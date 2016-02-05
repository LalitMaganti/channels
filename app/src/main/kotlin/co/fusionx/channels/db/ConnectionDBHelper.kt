package co.fusionx.channels.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.jetbrains.anko.db.*

class ConnectionDBHelper private constructor(private val context: Context) :
        SQLiteOpenHelper(context, ConnectionDBHelper.DB_NAME, null, 4) {
    override fun onCreate(db: SQLiteDatabase) {
        createTables(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE `${ConnectionDBColumns.TABLE_NAME}`;")
        createTables(db)
    }

    private fun createTables(db: SQLiteDatabase) {
        db.execSQL(
                arrayOf(
                        ConnectionDBColumns._ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT + UNIQUE,
                        ConnectionDBColumns.NAME to TEXT,
                        ConnectionDBColumns.HOSTNAME to TEXT,
                        ConnectionDBColumns.PORT to INTEGER
                ).map { col ->
                    "${col.first} ${col.second}"
                }.joinToString(", ", prefix = "CREATE TABLE IF NOT EXISTS ${ConnectionDBColumns.TABLE_NAME}(", postfix = ");")
        )

        // TODO(tilal6991) remove this
        db.insert(ConnectionDBColumns.TABLE_NAME,
                ConnectionDBColumns.NAME to "Freenode",
                ConnectionDBColumns.HOSTNAME to "irc.freenode.net",
                ConnectionDBColumns.PORT to 6667)
        db.insert(ConnectionDBColumns.TABLE_NAME,
                ConnectionDBColumns.NAME to "Techtronix",
                ConnectionDBColumns.HOSTNAME to "irc.techtronix.net",
                ConnectionDBColumns.PORT to 6667)
        db.insert(ConnectionDBColumns.TABLE_NAME,
                ConnectionDBColumns.NAME to "Freenode2",
                ConnectionDBColumns.HOSTNAME to "irc.freenode.net",
                ConnectionDBColumns.PORT to 6667)
    }

    companion object {
        val DB_NAME = "DB_CONNECTIONS"
        private var instance: ConnectionDBHelper? = null

        @Synchronized fun instance(ctx: Context): ConnectionDBHelper {
            if (instance == null) {
                instance = ConnectionDBHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }
}