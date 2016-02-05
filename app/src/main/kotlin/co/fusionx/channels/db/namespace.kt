package co.fusionx.channels.db

import android.content.Context
import android.support.v4.app.Fragment
import android.view.View

object ConnectionDBColumns {
    const val TABLE_NAME: String = "connections"

    const val _ID: String = "_id"
    const val NAME: String = "name"
    const val HOSTNAME: String = "hostname"
    const val PORT: String = "port"
}

val Context.connectionDb: ConnectionDatabase
    get() = ConnectionDatabase.instance(this)
val Fragment.connectionDb: ConnectionDatabase
    get() = ConnectionDatabase.instance(activity)
val View.connectionDb: ConnectionDatabase
    get() = ConnectionDatabase.instance(context)