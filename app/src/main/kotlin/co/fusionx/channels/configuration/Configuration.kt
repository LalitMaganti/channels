package co.fusionx.channels.configuration

import org.parceler.Parcel
import org.parceler.ParcelConstructor
import org.parceler.ParcelProperty

@Parcel(Parcel.Serialization.BEAN)
class Configuration @ParcelConstructor constructor(
        @param:ParcelProperty("connection") val connection: ServerConfiguration,
        @param:ParcelProperty("user") val user: UserConfiguration) {

    override fun equals(other: Any?): Boolean {
        if (other !is Configuration) return false
        return other.connection.name == connection.name
    }

    override fun hashCode(): Int {
        return connection.name?.hashCode() ?: 0
    }
}