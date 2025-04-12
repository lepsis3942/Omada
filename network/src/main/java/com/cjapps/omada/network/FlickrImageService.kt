package com.cjapps.omada.network

import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class FlickrImageService @Inject constructor(
    private val httpClient: HttpClient,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : IImageService {

}