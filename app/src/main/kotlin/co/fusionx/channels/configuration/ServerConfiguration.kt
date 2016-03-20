package co.fusionx.channels.configuration

import co.fusionx.relay.RelayClient
import org.parceler.Parcel
import org.parceler.ParcelConstructor
import org.parceler.ParcelProperty

@Parcel(Parcel.Serialization.BEAN)
class ServerConfiguration @ParcelConstructor constructor(
        @param:ParcelProperty("hostname") override val hostname: String,
        @param:ParcelProperty("port") override val port: Int,
        @param:ParcelProperty("ssl") val ssl: Boolean,
        @param:ParcelProperty("sslAllCerts") val sslAllCerts: Boolean) : RelayClient.Configuration