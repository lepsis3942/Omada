package com.cjapps.omada.network.models

data class NetworkImage(
    val id: String,
    val imageUrl: String,
    val title: String?,
    val description: String?,
    val dateUpload: Long?
)