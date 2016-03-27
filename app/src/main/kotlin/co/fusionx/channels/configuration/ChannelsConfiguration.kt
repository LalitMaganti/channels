package co.fusionx.channels.configuration

import org.parceler.Parcel
import org.parceler.ParcelConstructor
import org.parceler.ParcelProperty

@Parcel(Parcel.Serialization.BEAN)
class ChannelsConfiguration @ParcelConstructor constructor(
        @param:ParcelProperty("id") val id: Int,
        @param:ParcelProperty("name") val name: String,
        @param:ParcelProperty("server") val server: ServerConfiguration,
        @param:ParcelProperty("user") val user: UserConfiguration) {

    override fun equals(other: Any?): Boolean {
        if (other !is ChannelsConfiguration) return false
        return other.id == id
    }

    override fun hashCode(): Int {
        return id
    }
}