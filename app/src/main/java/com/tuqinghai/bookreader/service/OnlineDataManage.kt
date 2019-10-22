package com.tuqinghai.bookreader.service

import android.util.Log
import com.tuqinghai.bookreader.source.SourceManage
import com.tuqinghai.crawler.Crawler
import com.tuqinghai.crawler.entity.Book
import com.tuqinghai.crawler.entity.Chapter
import com.tuqinghai.crawler.entity.Source
import com.tuqinghai.reader.BuildConfig
import io.reactivex.Observable

class OnlineDataManage {



        fun searchBooks(keyword:String): Observable<List<Book>> {
            return Observable.create<List<Book>> {
                SourceManage.sourceList.forEach { source ->
                    try {
                        val list = Crawler.search(keyword, source)
                        it.onNext(list)
                    } catch (e: Throwable) {
                        if (BuildConfig.DEBUG)
                            Log.e("书籍查找失败","${source.title}:$e")
                    }
                }
            }

        }

    fun loadChapterList(link:String,source: Source):List<Chapter>{
        return Crawler.loadChapterList(link, source)
    }

    fun loadChapter(link:String,source: Source):Chapter{
        return Crawler.loadChapter(link,source)
    }
}