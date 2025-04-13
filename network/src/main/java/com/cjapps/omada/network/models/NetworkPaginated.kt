package com.cjapps.omada.network.models

data class NetworkPaginated<T>(
    val page: Int,
    val pages: Int,
    val perPage: Int,
    val total: Int,
    val items: List<T>
)