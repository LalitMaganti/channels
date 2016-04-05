package com.tilal6991.channels.ui

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.databinding.ObservableList
import android.os.Binder
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.res.ResourcesCompat
import android.text.TextUtils
import com.tilal6991.channels.R
import com.tilal6991.channels.base.relayVM
import com.tilal6991.channels.collections.ObservableListChangedProxy
import com.tilal6991.channels.configuration.ChannelsConfiguration
import com.tilal6991.channels.viewmodel.ClientVM
import com.tilal6991.channels.viewmodel.RelayVM
import java.util.*

class NotificationService : Service() {

    private val listener = object : ObservableListChangedProxy<ChannelsConfiguration>() {
        override fun onListChanged(sender: ObservableList<ChannelsConfiguration>) {
            if (sender.isEmpty()) {
                sender.removeOnListChangedCallback(this)
            } else {
                updateNotifications()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        relayVM.activeConfigs.addOnListChangedCallback(listener)
        updateNotifications()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return Binder()
    }

    private fun updateNotifications() {
        val snapshot = StatusSnapshot.create(relayVM)
        startForeground(SERVICE_ID, buildServiceNotification(snapshot))
        updateWearNotification(snapshot)
    }

    private fun buildServiceNotification(snapshot: StatusSnapshot): Notification {
        val publicText = getNotificationContentText(snapshot)
        val builder = getPublicBuilder(publicText, snapshot)
        return getPrivateBuilder(builder, publicText, snapshot).build()
    }

    private fun getPublicBuilder(publicText: CharSequence,
                                 s: StatusSnapshot): NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(this)
                .setColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
                .setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_notification_small)
                .setContentIntent(getMainActivityIntent())
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setOngoing(true)
                .setLocalOnly(true)
                .setShowWhen(false)
                .setContentText(publicText)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        if (s.disconnected > 0) {
            builder.setGroup("serverstatus")
                    .setGroupSummary(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        } else {
            builder.setPriority(NotificationCompat.PRIORITY_MIN)
        }
        return builder
    }

    private fun getPrivateBuilder(builder: NotificationCompat.Builder,
                                  publicText: CharSequence,
                                  s: StatusSnapshot): NotificationCompat.Builder {
        val public = builder.build()
        val privateText = getPrivateText(publicText, s)
        builder.setContentText(privateText)
                .setTicker(privateText)
                .setPublicVersion(public)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)

        val reconnectAction = getReconnectAction(s, R.drawable.ic_refresh_light)
        if (reconnectAction != null) {
            builder.addAction(reconnectAction)
        }
        return builder.addAction(getDisconnectAction(s))
    }

    private fun updateWearNotification(snapshot: StatusSnapshot) {
        val nm = NotificationManagerCompat.from(this)
        val reconnectAction = getReconnectAction(snapshot, R.drawable.ic_refresh_action_wear)
        if (reconnectAction == null) {
            nm.cancel(WEARABLE_STATUS_ID)
            return
        }

        val extender = NotificationCompat.WearableExtender()
                .addAction(reconnectAction)
                .setContentAction(0)
                .setHintHideIcon(true)
        val wearBuilder = NotificationCompat.Builder(this)
                .setColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
                .setContentTitle(getString(R.string.notification_reconnect_wear_title))
                .setContentText(getString(
                        R.string.notification_reconnect_wear_content,
                        TextUtils.join(", ", snapshot.disconnectedNames)))
                .setSmallIcon(R.drawable.ic_notification_small)
                .setGroup("serverstatus")
                .extend(extender)
        nm.notify(WEARABLE_STATUS_ID, wearBuilder.build())
    }

    private fun getDisconnectAction(s: StatusSnapshot): NotificationCompat.Action {
        val intent = PendingIntent.getBroadcast(
                this, 0, Intent(DISCONNECT_ALL_INTENT), PendingIntent.FLAG_UPDATE_CURRENT)
        val stringRes: Int
        if (s.connected == 0 && s.connecting == 0) {
            if (s.total > 1) {
                stringRes = R.string.notification_action_close_all
            } else {
                stringRes = R.string.notification_action_close
            }
        } else {
            if (s.total > 1) {
                stringRes = R.string.notification_action_disconnect_all
            } else {
                stringRes = R.string.notification_action_disconnect
            }
        }
        return NotificationCompat.Action(R.drawable.ic_clear_light, getString(stringRes), intent)
    }

    private fun getReconnectAction(s: StatusSnapshot, drawable: Int): NotificationCompat.Action? {
        if (s.disconnected == 0) {
            return null
        }

        val intent = PendingIntent.getBroadcast(
                this, 0, Intent(RECONNECT_ALL_INTENT), PendingIntent.FLAG_UPDATE_CURRENT)
        val reconnectActionResId: Int
        if (s.disconnected > 1) {
            reconnectActionResId = R.string.notification_action_reconnect_all
        } else {
            reconnectActionResId = R.string.notification_action_reconnect
        }
        return NotificationCompat.Action(drawable, getString(reconnectActionResId), intent)
    }

    private fun getMainActivityIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        return PendingIntent.getActivity(this, 0, intent, 0)
    }

    private fun getPrivateText(public: CharSequence, s: StatusSnapshot): CharSequence {
        if (s.total != 1) {
            return public
        }

        val active = relayVM.activeConfigs
        val formatResId = when {
            s.connected > 0 -> R.string.notification_connected_title
            s.connecting > 0 -> R.string.notification_connecting_title
            s.reconnecting > 0 -> R.string.notification_reconnecting_title
            else -> R.string.notification_disconnected_title
        }
        return getString(formatResId, active.first().name)
    }

    private fun getNotificationContentText(s: StatusSnapshot): CharSequence {
        val publicText = StringBuilder()
        if (s.connected > 0) {
            publicText.append(resources.getQuantityString(
                    R.plurals.client_connected, s.connected, s.connected))
        }
        if (s.connecting > 0) {
            if (publicText.length > 0) {
                publicText.append(", ")
            }
            publicText.append(resources.getQuantityString(
                    R.plurals.client_connecting, s.connecting, s.connecting))
        }
        if (s.reconnecting > 0) {
            if (publicText.length > 0) {
                publicText.append(", ")
            }
            publicText.append(resources.getQuantityString(
                    R.plurals.client_connected, s.reconnecting, s.reconnecting))
        }
        if (s.disconnected > 0) {
            if (publicText.length > 0) {
                publicText.append(", ")
            }
            publicText.append(resources.getQuantityString(
                    R.plurals.client_disconnected, s.disconnected, s.disconnected))
        }
        return publicText
    }

    private data class StatusSnapshot private constructor(val connected: Int,
                                                          val connecting: Int,
                                                          val reconnecting: Int,
                                                          val disconnected: Int,
                                                          val disconnectedNames: List<String>) {
        val total: Int = connected + connecting + reconnecting + disconnected

        companion object {
            fun create(relayVM: RelayVM): StatusSnapshot {
                val disconnectedNames = ArrayList<String>()
                var connected = 0
                var disconnected = 0
                var connecting = 0
                var reconnecting = 0

                val active = relayVM.activeConfigs
                for (a in active) {
                    val client = relayVM.configActiveClients[a]
                    when (client.statusInt) {
                        ClientVM.DISCONNECTED -> {
                            disconnectedNames.add(a.name)
                            disconnected++
                        }
                        ClientVM.CONNECTING, ClientVM.SOCKET_CONNECTED -> connecting++
                        ClientVM.CONNECTED -> connected++
                        ClientVM.RECONNECTING -> reconnecting++
                    }
                }
                return StatusSnapshot(connected, connecting, reconnecting, disconnected,
                        disconnectedNames)
            }
        }
    }

    companion object {
        private const val SERVICE_ID = 1;
        private const val WEARABLE_STATUS_ID = 2;

        private const val DISCONNECT_ALL_INTENT = "com.tilal6991.channels.disconnect_all";
        private const val RECONNECT_ALL_INTENT = "com.tilal6991.channels.reconnect_all";
    }
}