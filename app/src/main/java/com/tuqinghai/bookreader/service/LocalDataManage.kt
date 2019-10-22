package com.tuqinghai.bookreader.service

import android.text.TextUtils
import com.tuqinghai.bookreader.BuildConfig
import com.tuqinghai.bookreader.entity.*
import com.tuqinghai.bookreader.utils.ApplicationUtils
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser

class LocalDataManage {


    private val boxStore: BoxStore by lazy {

        val store = MyObjectBox.builder()
            .androidContext(ApplicationUtils.context)
            .build()
        if (BuildConfig.DEBUG) {
            AndroidObjectBrowser(store).start(ApplicationUtils.context)
        }
        store
    }

    private val localBookBox: Box<LocalBook> by lazy {
        boxStore.boxFor(LocalBook::class.java)
    }

    private val localChapterBox: Box<LocalChapter> by lazy {
        boxStore.boxFor(LocalChapter::class.java)
    }

    fun search(keyword: String): List<LocalBook> {
        return localBookBox.query()
            .contains(LocalBook_.name, keyword)
            .or()
            .contains(LocalBook_.author, keyword)
            .build()
            .find()
    }

    fun getLocalBook(link: String): LocalBook? {
        return localBookBox.query().equal(LocalBook_.link, link).build().findUnique()
    }

    /**
     * 缓存书籍
     */
    fun cacheBook(book: LocalBook) {
        val local = getLocalBook(book.link)
        book.id = local?.id ?: 0
        book.inBookshelf = local?.inBookshelf ?: false
        book.readIndex = local?.readIndex ?: 0
        book.readLink = local?.readLink ?: ""
        localBookBox.put(book)
    }

    fun saveBook(book: LocalBook) {
        val local = getLocalBook(book.link)
        book.id = local?.id ?: 0
        localBookBox.put(book)
    }

    fun removeLocalBookSelf(id: Long) {
        val book = localBookBox[id]
        book.inBookshelf = false
        localBookBox.put(book)
    }

    fun getLocalChapterList(link: String): List<LocalChapter> {
        return localChapterBox.query()
            .equal(LocalChapter_.bookLink, link)
            .build()
            .find()
    }

    fun getLocalBookList(inBookself: Boolean = true): List<LocalBook> {
        return localBookBox.query()
            .equal(LocalBook_.inBookshelf, inBookself)
            .build()
            .find()
    }

    fun getLocalChapter(link: String): LocalChapter? {
        return localChapterBox.query().equal(LocalChapter_.link, link).build().findUnique()
    }

    fun cacheChapter(chapter: LocalChapter) {
        val local = getLocalChapter(chapter.link)
        chapter.id = local?.id ?: 0
        if (TextUtils.isEmpty(chapter.nextLink) || !chapter!!.nextLink.endsWith("html"))
            chapter.nextLink = local?.nextLink ?: ""
        localChapterBox.put(chapter)
    }

}