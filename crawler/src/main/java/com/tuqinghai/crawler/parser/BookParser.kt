package com.tuqinghai.crawler.parser

import com.google.gson.Gson
import com.tuqinghai.crawler.entity.Book
import com.tuqinghai.crawler.entity.Source
import org.jsoup.Jsoup

object BookParser : IParser<List<Book>>() {

    override fun parsing(url: String, source: Source): List<Book> {
        val books = ArrayList<Book>()
        val doc = getDoc(url, source)

        val listRule = source.searchParsingRule["list"]
        val list = doc.select(listRule)

        val coverRule = source.searchParsingRule["cover"] ?: ""
        val titleRule = source.searchParsingRule["title"] ?: ""
        val linkRule = source.searchParsingRule["link"] ?: ""
        val authorRule = source.searchParsingRule["author"] ?: ""
        val descRule = source.searchParsingRule["desc"] ?: ""
        val newChapter = source.searchParsingRule["newChapter"] ?: ""
        val newChapterTime = source.searchParsingRule["newChapterTime"] ?: ""
        val newChapterLink = source.searchParsingRule["newChapterLink"] ?: ""

        list?.forEach {
            val cover = Companion.parsing(it, coverRule)
            val title = Companion.parsing(it, titleRule)
            val link = Companion.parsing(it, linkRule)
            val author = Companion.parsing(it, authorRule)
            val desc = Companion.parsing(it, descRule)
            val chapter = Companion.parsing(it, newChapter)
            val chapterTime = Companion.parsing(it, newChapterTime)
            val chapterLink = Companion.parsing(it, newChapterLink)
            val s = Gson().toJson(source)
            val book = Book(cover, title, autoUrl(link, source), author, desc, chapter, chapterTime, autoUrl(chapterLink, source), s)
            books.add(book)
        }

        return books
    }

}