package com.tuqinghai.crawler

import android.util.Log
import com.tuqinghai.crawler.entity.Book
import com.tuqinghai.crawler.entity.Chapter
import com.tuqinghai.crawler.entity.Source
import com.tuqinghai.crawler.parser.BookParser
import com.tuqinghai.crawler.parser.ChapterListParser
import com.tuqinghai.crawler.parser.ChapterParser

object Crawler {

    fun search(keyword:String,source: Source):List<Book>{

        val list = ArrayList<Book>()
        val url = String.format(source.searchPath, keyword)
        list.addAll(BookParser.parsing(url,source))
        return list
    }

    fun loadChapter(url:String,source: Source): Chapter {
        return ChapterParser.parsing(url, source)
    }

    fun loadChapterList(url: String,source: Source):List<Chapter>{
        return ChapterListParser.parsing(url, source)
    }
}