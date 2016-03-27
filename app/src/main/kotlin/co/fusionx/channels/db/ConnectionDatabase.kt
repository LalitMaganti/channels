package co.fusionx.channels.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import co.fusionx.channels.configuration.ChannelsConfiguration
import co.fusionx.channels.configuration.ServerConfiguration
import co.fusionx.channels.configuration.UserConfiguration
import com.squareup.sqlbrite.BriteDatabase
import com.squareup.sqlbrite.SqlBrite
import org.jetbrains.anko.db.transaction
import rx.Completable
import rx.Observable
import rx.schedulers.Schedulers
import timber.log.Timber

class ConnectionDatabase private constructor(private val context: Context) :
        SQLiteOpenHelper(context, ConnectionDatabase.DB_NAME, null, 1) {

    private val briteDb = SqlBrite.create().wrapDatabaseHelper(this)

    fun getConfigurations(): Observable<List<ChannelsConfiguration>> {
        return briteDb.createQuery(ConnectionTable.TABLE_NAME, "SELECT * from ${ConnectionTable.TABLE_NAME}")
                .mapToList { convertCursorToConfiguration(it) }
    }

    fun insert(configuration: ChannelsConfiguration): Completable {
        return Completable.fromAction {
            val transaction = briteDb.newTransaction()
            try {
                val id = consumeConfigurationAsContentValues(configuration) {
                    briteDb.insert(ConnectionTable.TABLE_NAME, it)
                }
                consumeNicksAsContentValues(id.toInt(), configuration.user.nicks) {
                    briteDb.insert(NickTable.TABLE_NAME, it)
                }
                transaction.markSuccessful()
            } catch (ex: SQLException) {
                Timber.e(ex, "Error when inserting configuration.")
            } finally {
                transaction.end()
            }
        }
    }

    fun update(id: Int, configuration: ChannelsConfiguration): Completable {
        return Completable.fromAction {
            val transaction = briteDb.newTransaction()
            try {
                consumeConfigurationAsContentValues(configuration) {
                    briteDb.update(ConnectionTable.TABLE_NAME, it, "${ConnectionTable._ID} = $id")
                }

                // Delete all the old nicks before replacing with the new one.
                briteDb.delete(NickTable.TABLE_NAME, "${NickTable.CONNECTION_ID} = $id")
                consumeNicksAsContentValues(id, configuration.user.nicks) {
                    briteDb.insert(NickTable.TABLE_NAME, it)
                }

                transaction.markSuccessful()
            } catch (ex: SQLException) {
                Timber.e(ex, "Error when updating configuration.")
            } finally {
                transaction.end()
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        createTables(db)

        val freenode = ChannelsConfiguration(-1, "Freenode",
                ServerConfiguration("irc.freenode.net", 6667, false, "ChannelsUser", null),
                UserConfiguration(
                        listOf("ChannelsUser"), true, "ChannelsUser",
                        UserConfiguration.NONE_AUTH_TYPE, null, null))
        rawInsertConfiguration(db, freenode)

        val techtronix = ChannelsConfiguration(-1, "Techtronix",
                ServerConfiguration("irc.techtronix.net", 6667, false, "ChannelsUser", null),
                UserConfiguration(
                        listOf("ChannelsUser"), true, "ChannelsUser",
                        UserConfiguration.NONE_AUTH_TYPE, null, null))
        rawInsertConfiguration(db, techtronix)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    private fun createTables(db: SQLiteDatabase) {
        db.execSQL(
                ConnectionTable.COLUMNS.map { "${it.first} ${it.second}" }.joinToString(", ",
                        prefix = "CREATE TABLE IF NOT EXISTS ${ConnectionTable.TABLE_NAME}(",
                        postfix = ");"
                )
        )
        db.execSQL(
                NickTable.COLUMNS.map { "${it.first} ${it.second}" }.joinToString(", ",
                        prefix = "CREATE TABLE IF NOT EXISTS ${NickTable.TABLE_NAME}(",
                        postfix = ");"
                )
        )
    }

    private fun getNicks(id: Int): List<String> {
        val query = "SELECT * from ${NickTable.TABLE_NAME} WHERE ${NickTable.CONNECTION_ID} = '$id'"
        return briteDb.createQuery(NickTable.TABLE_NAME, query)
                .mapToList { it.getString(NickTable.NICK) }
                .toBlocking()
                .first()
    }

    private fun convertCursorToConfiguration(cursor: Cursor): ChannelsConfiguration {
        val id = cursor.getInt(ConnectionTable._ID)
        val name = cursor.getString(ConnectionTable.NAME)
        val connection = ServerConfiguration(
                cursor.getString(ConnectionTable.HOSTNAME),
                cursor.getInt(ConnectionTable.PORT),
                cursor.getInt(ConnectionTable.SSL) == 1,
                cursor.getString(ConnectionTable.SERVER_USERNAME),
                cursor.getString(ConnectionTable.SERVER_PASSWORD))
        val handshake = UserConfiguration(
                getNicks(id),
                cursor.getInt(ConnectionTable.AUTO_NICK_CHANGE) == 1,
                cursor.getString(ConnectionTable.REAL_NAME),
                cursor.getInt(ConnectionTable.AUTH_TYPE),
                cursor.getString(ConnectionTable.AUTH_USERNAME),
                cursor.getString(ConnectionTable.AUTH_PASSWORD))
        return ChannelsConfiguration(id, name, connection, handshake)
    }

    private fun rawInsertConfiguration(db: SQLiteDatabase, configuration: ChannelsConfiguration) {
        db.beginTransaction()
        try {
            val id = consumeConfigurationAsContentValues(configuration) {
                db.insertOrThrow(ConnectionTable.TABLE_NAME, null, it)
            }
            consumeNicksAsContentValues(id.toInt(), configuration.user.nicks) {
                db.insertOrThrow(NickTable.TABLE_NAME, null, it)
            }
            db.setTransactionSuccessful()
        } catch (ex: SQLException) {
            Timber.e(ex, "Error when inserting raw configuration.")
        } finally {
            db.endTransaction()
        }
    }

    private fun <T> consumeConfigurationAsContentValues(configuration: ChannelsConfiguration,
                                                        function: (ContentValues) -> T): T {
        val values = ContentValues()
        values.put(ConnectionTable.NAME, configuration.name)
        values.put(ConnectionTable.HOSTNAME, configuration.server.hostname)
        values.put(ConnectionTable.PORT, configuration.server.port)
        values.put(ConnectionTable.SSL, configuration.server.ssl)
        values.put(ConnectionTable.SERVER_USERNAME, configuration.server.username)
        values.put(ConnectionTable.SERVER_PASSWORD, configuration.server.password)

        values.put(ConnectionTable.AUTO_NICK_CHANGE, configuration.user.autoChangeNick)
        values.put(ConnectionTable.REAL_NAME, configuration.user.realName)

        val authType = configuration.user.authType
        values.put(ConnectionTable.AUTH_TYPE, authType)
        if (authType == UserConfiguration.SASL_AUTH_TYPE) {
            values.put(ConnectionTable.AUTH_USERNAME, configuration.user.authUsername)
            values.put(ConnectionTable.AUTH_PASSWORD, configuration.user.authPassword)
        } else if (authType == UserConfiguration.NICKSERV_AUTH_TYPE) {
            values.put(ConnectionTable.AUTH_PASSWORD, configuration.user.authPassword)
        }
        return function(values)
    }

    private fun consumeNicksAsContentValues(id: Int,
                                            nicks: List<String>,
                                            function: (ContentValues) -> Unit) {
        val nick = ContentValues()
        for (n in nicks) {
            nick.clear()
            nick.put(NickTable.CONNECTION_ID, id)
            nick.put(NickTable.NICK, n)
            function(nick)
        }
    }

    companion object {
        val DB_NAME = "DB_CONNECTIONS"
        private var instance: ConnectionDatabase? = null

        @Synchronized fun instance(ctx: Context): ConnectionDatabase {
            if (instance == null) {
                instance = ConnectionDatabase(ctx.applicationContext)
            }
            return instance!!
        }
    }
}