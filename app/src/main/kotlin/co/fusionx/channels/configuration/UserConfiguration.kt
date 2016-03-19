package co.fusionx.channels.configuration

import org.parceler.Parcel

@Parcel(Parcel.Serialization.BEAN)
class UserConfiguration @JvmOverloads constructor(
        var username: String? = "ChannelsUser",
        var password: String? = null,
        var nicks: List<String> = listOf("ChannelsUser"),
        var realName: String? = "ChannelsUser")