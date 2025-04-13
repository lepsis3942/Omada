package com.cjapps.omada.data.models

import java.util.Date

data class Image(
    val id: String,
    val imageUrl: String,
    val title: String?,
    val description: String?,
    val dateUpload: Date?
)
