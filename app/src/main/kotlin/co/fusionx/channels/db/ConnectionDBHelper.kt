package co.fusionx.channels.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

public class ConnectionDBHelper(private val context: Context) : ManagedSQLiteOpenHelper(
        context, ConnectionDBHelper.DB_NAME, null, 1) {

    companion object {
        private val DB_NAME = "DB_CONNECTIONS"
        private var instance: ConnectionDBHelper? = null

        @Synchronized fun instance(ctx: Context): ConnectionDBHelper {
            if (instance == null) {
                instance = ConnectionDBHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        createTables(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.use {
            db.dropTable("connections")
        }
        createTables(db)
    }

    private fun createTables(db: SQLiteDatabase) {
        db.use {
            db.createTable(
                    ConnectionDBColumns.TABLE_NAME,
                    true /* ifNotExists */,
                    ConnectionDBColumns._ID to INTEGER + PRIMARY_KEY + UNIQUE + AUTOINCREMENT,
                    ConnectionDBColumns.TITLE to TEXT
            )
            db.insert(
                    ConnectionDBColumns.TABLE_NAME,
                    ConnectionDBColumns.TITLE to "Test"
            )
        }
    }
}