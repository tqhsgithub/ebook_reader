package com.tuqinghai.crawler.entity

import android.os.Parcel
import android.os.Parcelable

data class Book(
    val cover: String?,
    val title: String,
    val link: String,
    val author: String? = null,
    val desc: String? = null,
    val newChapter: String? = null,
    val newChapterTime: String? = null,
    val newChapterLink: String? = null,
    val source:String
)