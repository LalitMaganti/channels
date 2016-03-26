package co.fusionx.channels.db

import android.content.Context
import android.database.Cursor
import co.fusionx.channels.configuration.ChannelsConfiguration
import co.fusionx.channels.configuration.ServerConfiguration
import co.fusionx.channels.configuration.UserConfiguration
import co.fusionx.channels.relay.HandshakeEventListener
import com.squareup.sqlbrite.BriteDatabase
import com.squareup.sqlbrite.SqlBrite
import rx.Observable

class ConnectionDatabase(private val context: Context) {
    private val briteDb: BriteDatabase

    init {
        briteDb = SqlBrite.create().wrapDatabaseHelper(ConnectionDBHelper.instance(context))
    }

    fun getConfigurations(): Observable<List<ChannelsConfiguration>> {
        return briteDb.createQuery(ConnectionTableConstants.TABLE_NAME, "SELECT * from ${ConnectionTableConstants.TABLE_NAME}")
                .mapToList { convertCursorToConfiguration(it) }
    }

    private fun convertCursorToConfiguration(cursor: Cursor): ChannelsConfiguration {
        val name = cursor.getString(ConnectionTableConstants.NAME)
        val connection = ServerConfiguration(
                cursor.getString(ConnectionTableConstants.HOSTNAME),
                cursor.getInt(ConnectionTableConstants.PORT),
                false,
                false
        )
        val handshake = UserConfiguration(
                cursor.getString(ConnectionTableConstants.USERNAME),
                cursor.getString(ConnectionTableConstants.SERVER_PASSWORD),
                getNicks(name),
                cursor.getString(ConnectionTableConstants.REAL_NAME)
        )
        return ChannelsConfiguration(name, connection, handshake)
    }

    private fun getNicks(name: String?): List<String> {
        val query = "SELECT * from ${NickTableConstants.TABLE_NAME} WHERE ${NickTableConstants.NAME} = '$name'"
        return briteDb.createQuery(NickTableConstants.TABLE_NAME, query)
                .mapToList { it.getString(NickTableConstants.NICK) }
                .toBlocking()
                .first()
    }

    companion object {
        private var instance: ConnectionDatabase? = null

        @Synchronized fun instance(ctx: Context): ConnectionDatabase {
            if (instance == null) {
                instance = ConnectionDatabase(ctx.applicationContext)
            }
            return instance!!
        }
    }

    fun Cursor.getInt(columnName: String) = getInt(getColumnIndex(columnName))
    fun Cursor.getString(columnName: String) = getString(getColumnIndex(columnName))
}