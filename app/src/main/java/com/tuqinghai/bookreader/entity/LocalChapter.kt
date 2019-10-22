package com.tuqinghai.bookreader.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique

@Entity
data class LocalChapter(
    @Id var id: Long = 0,
    @Unique var link: String,
    var title: String,
    var content: String,
    var lastLink: String,
    var nextLink: String,
    var bookLink:String,
    var isFirst :Boolean=false
)