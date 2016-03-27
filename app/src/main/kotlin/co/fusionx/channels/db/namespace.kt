package co.fusionx.channels.db

import android.content.Context
import android.database.Cursor
import org.jetbrains.anko.db.*

object ConnectionTable {
    const val TABLE_NAME: String = "connections"

    const val _ID: String = "_id"
    const val NAME: String = "name"

    const val HOSTNAME: String = "hostname"
    const val PORT: String = "port"
    const val SSL: String = "ssl"
    const val SSL_ACCEPT_ALL: String = "ssl_accept_all"
    const val SERVER_USERNAME: String = "server_username"
    const val SERVER_PASSWORD: String = "server_password"

    const val AUTO_NICK_CHANGE: String = "auto_nick_change"
    const val REAL_NAME: String = "real_name"
    const val AUTH_TYPE: String = "auth_type"
    const val AUTH_USERNAME: String = "auth_username"
    const val AUTH_PASSWORD: String = "auth_password"

    val COLUMNS = arrayOf(
            _ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT + UNIQUE,
            NAME to TEXT + NOT_NULL + UNIQUE,

            HOSTNAME to TEXT + NOT_NULL,
            PORT to INTEGER + NOT_NULL,
            SSL to INTEGER + NOT_NULL,
            SSL_ACCEPT_ALL to INTEGER + NOT_NULL,
            SERVER_USERNAME to TEXT + NOT_NULL,
            SERVER_PASSWORD to TEXT,

            AUTO_NICK_CHANGE to INTEGER + NOT_NULL,
            REAL_NAME to TEXT + NOT_NULL,
            AUTH_TYPE to INTEGER + NOT_NULL,
            AUTH_USERNAME to TEXT,
            AUTH_PASSWORD to TEXT)
}

object NickTable {
    const val TABLE_NAME: String = "nicks"

    const val _ID: String = "_id"
    const val CONNECTION_ID: String = "connection_id"

    const val NICK: String = "nick"

    val COLUMNS = arrayOf(
            _ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT + UNIQUE,
            CONNECTION_ID to INTEGER + NOT_NULL + UNIQUE,
            NICK to TEXT + NOT_NULL,
            "" to FOREIGN_KEY(CONNECTION_ID, ConnectionTable.TABLE_NAME, ConnectionTable._ID))
}

fun Cursor.getInt(columnName: String) = getInt(getColumnIndex(columnName))
fun Cursor.getString(columnName: String) = getString(getColumnIndex(columnName))

val Context.connectionDb: ConnectionDatabase
    get() = ConnectionDatabase.instance(this)