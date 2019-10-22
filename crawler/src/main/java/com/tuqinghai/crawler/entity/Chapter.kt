package com.tuqinghai.crawler.entity

data class Chapter(
    val link: String,
    val title: String? = null,
    val content: String? = null,
    val lastLink: String? = null,
    val nextLink: String? = null
)