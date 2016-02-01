package co.fusionx.channels.db

import android.content.Context
import android.support.v4.app.Fragment
import android.view.View

public object ConnectionDBColumns {
    public const val TABLE_NAME: String = "connections"

    public const val _ID: String = "_id"
    public const val NAME: String = "name"
    public const val HOSTNAME: String = "hostname"
    public const val PORT: String = "port"
}

val Context.connectionDb: ConnectionDatabase
    get() = ConnectionDatabase.instance(this)
val Fragment.connectionDb: ConnectionDatabase
    get() = ConnectionDatabase.instance(activity)
val View.connectionDb: ConnectionDatabase
    get() = ConnectionDatabase.instance(context)