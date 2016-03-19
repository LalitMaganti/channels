package co.fusionx.channels.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.jetbrains.anko.db.*

class ConnectionDBHelper private constructor(private val context: Context) :
        SQLiteOpenHelper(context, ConnectionDBHelper.DB_NAME, null, 71) {
    override fun onCreate(db: SQLiteDatabase) {
        createTables(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS `${ConnectionTableConstants.TABLE_NAME}`;")
        db.execSQL("DROP TABLE IF EXISTS `${NickTableConstants.TABLE_NAME}`;")
        createTables(db)
    }

    private fun createTables(db: SQLiteDatabase) {
        db.execSQL(
                connectionTableColumns.map { "${it.first} ${it.second}" }.joinToString(", ",
                        prefix = "CREATE TABLE IF NOT EXISTS ${ConnectionTableConstants.TABLE_NAME}(",
                        postfix = ");"
                )
        )
        db.execSQL(
                nickTableColumns.map { "${it.first} ${it.second}" }.joinToString(", ",
                        prefix = "CREATE TABLE IF NOT EXISTS ${NickTableConstants.TABLE_NAME}(",
                        postfix = ");"
                )
        )

        // TODO(tilal6991) remove this
        db.insert(ConnectionTableConstants.TABLE_NAME,
                ConnectionTableConstants.NAME to "Freenode",
                ConnectionTableConstants.HOSTNAME to "irc.freenode.net",
                ConnectionTableConstants.PORT to 6667,
                ConnectionTableConstants.USERNAME to "tilal6993")
        db.insert(NickTableConstants.TABLE_NAME,
                NickTableConstants.NAME to "Freenode",
                NickTableConstants.NICK to "tilal6993")

        db.insert(ConnectionTableConstants.TABLE_NAME,
                ConnectionTableConstants.NAME to "Techtronix",
                ConnectionTableConstants.HOSTNAME to "irc.techtronix.net",
                ConnectionTableConstants.PORT to 6667,
                ConnectionTableConstants.USERNAME to "tilal6993")
        db.insert(NickTableConstants.TABLE_NAME,
                NickTableConstants.NAME to "Techtronix",
                NickTableConstants.NICK to "tilal6993")
    }

    companion object {
        val DB_NAME = "DB_CONNECTIONS"
        private var instance: ConnectionDBHelper? = null

        private val connectionTableColumns = arrayOf(
                ConnectionTableConstants._ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT + UNIQUE,
                ConnectionTableConstants.NAME to TEXT + NOT_NULL,

                ConnectionTableConstants.HOSTNAME to TEXT + NOT_NULL,
                ConnectionTableConstants.PORT to INTEGER + NOT_NULL,

                ConnectionTableConstants.USERNAME to TEXT + NOT_NULL,
                ConnectionTableConstants.SERVER_PASSWORD to TEXT,
                ConnectionTableConstants.REAL_NAME to TEXT
        )
        private val nickTableColumns = arrayOf(
                NickTableConstants._ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT + UNIQUE,
                NickTableConstants.NAME to TEXT + NOT_NULL,
                NickTableConstants.NICK to TEXT + NOT_NULL
        )

        @Synchronized fun instance(ctx: Context): ConnectionDBHelper {
            if (instance == null) {
                instance = ConnectionDBHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

    private fun SQLiteDatabase.insert(tableName: String, vararg values: Pair<String, Any?>): Long {
        return insert(tableName, null, values.toContentValues())
    }

    private fun Array<out Pair<String, Any?>>.toContentValues(): ContentValues {
        val values = ContentValues()
        for ((key, value) in this) {
            when (value) {
                is Boolean -> values.put(key, value)
                is Byte -> values.put(key, value)
                is ByteArray -> values.put(key, value)
                is Double -> values.put(key, value)
                is Float -> values.put(key, value)
                is Int -> values.put(key, value)
                is Long -> values.put(key, value)
                is Short -> values.put(key, value)
                is String -> values.put(key, value)
                else -> throw IllegalArgumentException("Non-supported value type: ${value?.javaClass?.name ?: "null"}")
            }
        }
        return values
    }
}