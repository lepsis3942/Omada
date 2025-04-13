package com.cjapps.omada.network.models.internal

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
internal data class FlickrImage(
    val id: String,
    val secret: String,
    val server: String,
    val title: String? = null,
    val description: PhotoDescription? = null,
    @SerialName("dateupload") val dateUpload: Long? = null
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
internal data class PhotoDescription(@SerialName("_content") val content: String?)