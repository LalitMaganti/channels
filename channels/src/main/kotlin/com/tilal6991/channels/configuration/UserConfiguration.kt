package com.tilal6991.channels.configuration

import nz.bradcampbell.paperparcel.PaperParcel

@PaperParcel
data class UserConfiguration(
        val nicks: List<String>,
        val autoChangeNick: Boolean,
        val realName: String,
        val authType: Int,
        val authUsername: String?,
        val authPassword: String?) {

    companion object {
        const val NONE_AUTH_TYPE = 0
        const val SASL_AUTH_TYPE = 1
        const val NICKSERV_AUTH_TYPE = 2
    }
}