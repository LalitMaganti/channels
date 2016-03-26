package co.fusionx.channels.configuration

import org.parceler.Parcel
import org.parceler.ParcelConstructor
import org.parceler.ParcelProperty

@Parcel(Parcel.Serialization.BEAN)
class UserConfiguration @ParcelConstructor constructor(
        @param:ParcelProperty("username") val username: String,
        @param:ParcelProperty("password") val password: String?,
        @param:ParcelProperty("nicks") val nicks: List<String>,
        @param:ParcelProperty("realName") val realName: String)