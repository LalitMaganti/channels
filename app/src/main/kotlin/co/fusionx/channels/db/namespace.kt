package co.fusionx.channels.db

import android.content.Context
import android.support.v4.app.Fragment
import android.view.View

public object ConnectionDBColumns {
    public val TABLE_NAME: String = "connections"

    public val _ID: String = "_id"
    public val TITLE: String = "title"
}

val Context.connectionDb: ConnectionDBHelper
    get() = ConnectionDBHelper.instance(applicationContext)
val Fragment.connectionDb: ConnectionDBHelper
    get() = ConnectionDBHelper.instance(activity.applicationContext)
val View.connectionDb: ConnectionDBHelper
    get() = ConnectionDBHelper.instance(context.applicationContext)