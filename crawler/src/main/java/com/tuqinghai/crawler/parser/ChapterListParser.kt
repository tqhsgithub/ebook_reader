package com.tuqinghai.crawler.parser

import com.tuqinghai.crawler.entity.Chapter
import com.tuqinghai.crawler.entity.Source
import org.jsoup.Jsoup

object ChapterListParser : IParser<List<Chapter>>() {

    override fun parsing(url: String, source: Source): List<Chapter> {


        val doc = getDoc(url, source)

        val listRule = source.chapterListParsingRule["list"]
        val list = doc.select(listRule)


        val titleRule = source.chapterListParsingRule["title"] ?: ""
        val linkRule = source.chapterListParsingRule["link"] ?: ""

        return list.map {
            val title = Companion.parsing(it, titleRule)
            val link = Companion.parsing(it, linkRule)
            Chapter(autoUrl(link,source),title)
        }
    }
}