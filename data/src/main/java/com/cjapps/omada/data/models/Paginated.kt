package com.cjapps.omada.data.models

data class Paginated<T>(
    val page: Int,
    val pages: Int,
    val perPage: Int,
    val total: Int,
    val items: List<T>
)
