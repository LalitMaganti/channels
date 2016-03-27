package co.fusionx.channels.configuration

import org.parceler.Parcel
import org.parceler.ParcelConstructor
import org.parceler.ParcelProperty

@Parcel(Parcel.Serialization.BEAN)
data class UserConfiguration @ParcelConstructor constructor(
        @param:ParcelProperty("nicks") val nicks: List<String>,
        @param:ParcelProperty("autoChangeNick") val autoChangeNick: Boolean,
        @param:ParcelProperty("realName") val realName: String,
        @param:ParcelProperty("authType") val authType: Int,
        @param:ParcelProperty("authUsername") val authUsername: String?,
        @param:ParcelProperty("authPassword") val authPassword: String?) {

    companion object {
        const val NONE_AUTH_TYPE = 0
        const val SASL_AUTH_TYPE = 1
        const val NICKSERV_AUTH_TYPE = 2
    }
}