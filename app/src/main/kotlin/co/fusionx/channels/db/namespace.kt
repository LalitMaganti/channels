package co.fusionx.channels.db

import android.content.Context
import android.support.v4.app.Fragment
import android.view.View

object ConnectionTableConstants {
    const val TABLE_NAME: String = "connections"

    const val _ID: String = "_id"
    const val NAME: String = "name"

    const val HOSTNAME: String = "hostname"
    const val PORT: String = "port"
    const val SSL: String = "ssl"
    const val SSL_AUTO_CHANGE: String = "ssl_auto_change"
    const val SERVER_USERNAME: String = "server_username"
    const val SERVER_PASSWORD: String = "server_password"

    const val AUTO_NICK_CHANGE: String = "auto_nick_change"
    const val REAL_NAME: String = "real_name"
    const val AUTH_TYPE: String = "auth_type"
    const val AUTH_USERNAME: String = "auth_username"
    const val AUTH_PASSWORD: String = "auth_password"
}

object NickTableConstants {
    const val TABLE_NAME: String = "nicks"

    const val _ID: String = "_id"
    const val NAME: String = "name"

    const val NICK: String = "nick"
}

val Context.connectionDb: ConnectionDatabase
    get() = ConnectionDatabase.instance(this)
val Fragment.connectionDb: ConnectionDatabase
    get() = ConnectionDatabase.instance(activity)
val View.connectionDb: ConnectionDatabase
    get() = ConnectionDatabase.instance(context)