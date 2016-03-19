package co.fusionx.channels.configuration

import org.parceler.Parcel

@Parcel(Parcel.Serialization.BEAN)
class ServerConfiguration @JvmOverloads constructor(
        var name: String? = null,
        var hostname: String? = null,
        var port: Int = 6667,
        var ssl: Boolean = false,
        var sslAllCerts: Boolean = false)