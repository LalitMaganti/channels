package com.tilal6991.channels.configuration

import nz.bradcampbell.paperparcel.PaperParcel

@PaperParcel
data class ServerConfiguration constructor(
        val hostname: String,
        val port: Int,
        val ssl: Boolean,
        val username: String,
        val password: String?)