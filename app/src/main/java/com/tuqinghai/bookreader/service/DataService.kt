package com.tuqinghai.bookreader.service

import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.tuqinghai.bookreader.BuildConfig
import com.tuqinghai.bookreader.entity.*
import com.tuqinghai.crawler.entity.Book
import com.tuqinghai.crawler.entity.Chapter
import com.tuqinghai.crawler.entity.Source
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers


class DataService {

    private val LocalBook.sourceObj: Source
        get() {
            return Gson().fromJson<Source>(source, Source::class.java)
        }

    private constructor()

    companion object {
        val INSTANCE: DataService by lazy {
            DataService()
        }

    }

    private val localManage: LocalDataManage by lazy {
        LocalDataManage()
    }

    private val onlineManage: OnlineDataManage by lazy {
        OnlineDataManage()
    }

    /**
     * 查询
     */
    fun search(keyword: String): Observable<List<LocalBook>> {
        return Observable.create<List<LocalBook>> {
            val list = ArrayList<LocalBook>()
            val localBooks = localManage.search(keyword)
            it.onNext(localBooks)
            onlineManage.searchBooks(keyword)
                .subscribeOn(Schedulers.newThread())
                .subscribe({ onlineList ->

                    val onlineBooks = onlineList.map { book -> bookToLocal(book) }
                    list.addAll(onlineBooks)
                    it.onNext(list)
                    onlineBooks.forEach { book ->
                        localManage.cacheBook(book)
                    }


                }, {

                })
            it.onNext(list)
        }
    }

    /**
     * 在线书籍对象转为本地书籍对象
     */
    private fun bookToLocal(book: Book): LocalBook {
        return LocalBook(
            0,
            book.title,
            book.link,
            "",
            book.cover ?: "",
            book.author ?: "",
            book.desc ?: "",
            book.newChapter,
            book.newChapterTime,
            book.newChapterLink,
            0,
            book.source,
            false
        )
    }


    /**
     * 加载目录
     */
    fun loadChapterList(book: LocalBook): Observable<List<LocalChapter>> {
        return Observable.create<List<LocalChapter>> {


            val localList = localManage.getLocalChapterList(book.link)
            it.onNext(localList)
            val list = onlineManage.loadChapterList(book.link, book.sourceObj)
                .mapIndexed { index, chapter ->
                    chapterToLocal(chapter, book.link, index == 0)
                }
            it.onNext(list)
            list.forEach { chapter ->
                localManage.cacheChapter(chapter)
            }


        }
    }

    fun chapterToLocal(chapter: Chapter, bookLink: String, isFirst: Boolean = false): LocalChapter {
        return LocalChapter(
            0,
            chapter.link,
            chapter.title ?: "",
            chapter.content ?: "",
            chapter.lastLink ?: "",
            chapter.nextLink ?: "",
            bookLink,
            isFirst
        )
    }


    /**
     * 加载章节
     *
     */
    fun loadChapter(link: String, book: LocalBook): Observable<LocalChapter> {
        return Observable.create<LocalChapter> {

            var local = localManage.getLocalChapter(link)
            if (!TextUtils.isEmpty(local?.content)) {
                it.onNext(local!!)
            }

            if (TextUtils.isEmpty(local?.content)
                || TextUtils.isEmpty(local?.nextLink)
                || local?.nextLink?.endsWith("html") != true
            ) {
                val online = onlineManage.loadChapter(link, book.sourceObj)
                val chapter = chapterToLocal(online, book.link, local?.isFirst ?: false)
                it.onNext(chapter)
                localManage.cacheChapter(chapter)
            }


        }
    }


    /**
     * 加载本地书籍
     */
    fun loadLocalBook(link: String): LocalBook? {
        return localManage.getLocalBook(link)
    }


    /**
     * 加载书架书籍
     */
    fun loadBookshelf(): Observable<List<LocalBook>> {
        return Observable.create {
            val list = localManage.getLocalBookList()
            it.onNext(list)
        }

    }

    /**
     * 保存到书架
     */
    fun saveLocalBookSelf(book: LocalBook) {
        book.inBookshelf = true
        localManage.saveBook(book)
    }

    /**
     * 从书架移出
     */
    fun removeLocalBookSelf(id: Long) {
        localManage.removeLocalBookSelf(id)
    }

    /**
     * 保存书籍
     */
    fun saveBook(book: LocalBook) {
        localManage.saveBook(book)
    }

    /**
     * 离线所有章节
     */
    fun saveBookAllChapter(book: LocalBook): Observable<Int> {
        var count = 0
        var finsh = 0
        return Observable.create<LocalChapter> {
            val list = onlineManage.loadChapterList(book.link, book.sourceObj)

            count = list.size

            list.mapIndexed { index, chapter ->
                chapterToLocal(chapter, book.link, index == 0)
            }.forEach { local ->
                it.onNext(local)
            }
            it.onComplete()
        }
            .map {
                if (BuildConfig.DEBUG)
                    Log.d("章节解析", it.title)
                var local = localManage.getLocalChapter(it.link)
                if (!TextUtils.isEmpty(local?.content)) {
                    local!!
                } else {
                    val online = onlineManage.loadChapter(it.link, book.sourceObj)
                    val chapter = chapterToLocal(online, book.link, local?.isFirst ?: false)
                    localManage.cacheChapter(chapter)
                    chapter
                }
            }.map {
                ++finsh
                ((finsh.toFloat() / count) * 100).toInt()
            }

    }


}