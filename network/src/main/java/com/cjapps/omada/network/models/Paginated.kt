package com.cjapps.omada.network.models

data class Paginated<T>(
    val page: Int,
    val pages: Int,
    val perPage: Int,
    val total: Int,
    val item: T
)