package com.tilal6991.channels.configuration

import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable

@PaperParcel
data class ChannelsConfiguration(
        val id: Int,
        val name: String,
        val server: ServerConfiguration,
        val user: UserConfiguration) : Comparable<ChannelsConfiguration>, PaperParcelable {

    override fun compareTo(other: ChannelsConfiguration): Int {
        return name.compareTo(other.name)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ChannelsConfiguration) return false
        return other.id == id
    }

    override fun hashCode(): Int {
        return id
    }

    companion object {
      @JvmField val CREATOR = PaperParcelable.Creator(ChannelsConfiguration::class.java)
    }
}