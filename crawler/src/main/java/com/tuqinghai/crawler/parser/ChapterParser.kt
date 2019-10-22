package com.tuqinghai.crawler.parser

import com.tuqinghai.crawler.entity.Chapter
import com.tuqinghai.crawler.entity.Source

object ChapterParser: IParser<Chapter>() {


    override fun parsing(url: String, source: Source):Chapter {


        val doc = getDoc(url, source)

        val titleRule = source.chapterParsingRule["title"]
        val title = Companion.parsing(doc,titleRule?:"")


        val contentRule = source.chapterParsingRule["content"]
        val content = Companion.parsing(doc,contentRule?:"")

        val lastRule = source.chapterParsingRule["last"]
        val last = Companion.parsing(doc,lastRule?:"")

        val nextRule = source.chapterParsingRule["next"]
        val next = Companion.parsing(doc,nextRule?:"")

       return Chapter(autoUrl(url, source),title, content, autoUrl(last,source), autoUrl(next,source))
    }




}