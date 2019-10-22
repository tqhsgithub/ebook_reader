package com.tuqinghai.bookreader.ui.book

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.tuqinghai.bookreader.R
import com.tuqinghai.bookreader.entity.LocalBook
import com.tuqinghai.bookreader.entity.LocalChapter
import com.tuqinghai.bookreader.service.DataService
import com.tuqinghai.reader.ReaderView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_chapter_list.*
import kotlinx.android.synthetic.main.activity_read.*
import kotlinx.android.synthetic.main.activity_read.toolbar

class ReadActivity : AppCompatActivity() {

    private val book: LocalBook by lazy {
        intent.getParcelableExtra("book") as LocalBook
    }


    private val startIndex: Int by lazy {
        if (TextUtils.isEmpty(intent.getStringExtra("link"))) book.readIndex else 0
    }

    private var chapter: LocalChapter? = null
        set(value) {
            field = value
            title = value?.title
        }
    private var lastChapter: LocalChapter? = null
    private var nextChapter: LocalChapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read)
    }

    override fun onContentChanged() {
        super.onContentChanged()
        val link = intent.getStringExtra("link") ?: book.readLink
        initView()
        if (chapter == null) {
            loadChapter(link) {
                chapter = it
                readerView.text = it.content
                readerView.post {
                    readerView.startIndex = startIndex
                }
                loadChapter(it.nextLink) { next ->
                    nextChapter = next
                }
                loadChapter(it.lastLink) { last ->
                    lastChapter = last
                }

            }
        }
    }


    @SuppressLint("CheckResult")
    private fun loadChapter(link: String?, loadSuccess: (local: LocalChapter) -> Unit) {
        if (TextUtils.isEmpty(link) || link?.endsWith("html") != true) {
            return
        }
        DataService.INSTANCE.loadChapter(link, book)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                loadSuccess(it)
            }, {
                Toast.makeText(this, "加载失败！", Toast.LENGTH_SHORT).show()
            })
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when {
            item.itemId == android.R.id.home -> {
                finish()
                return true
            }
            item.title.startsWith("目录") -> {
                val intent = Intent(this, ChapterListActivity::class.java)
                intent.putExtra("book", book)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menu?.add("目录")?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return super.onCreateOptionsMenu(menu)
    }

    private fun initView() {

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title = chapter?.title

        readerView.onPageChangeListener = object : ReaderView.OnPageChangeListener {
            override fun change(before: Int, current: Int, count: Int, lastPage: Boolean) {
                if (before != current) {
                    return
                }

                if (lastPage) {

                    if (!TextUtils.isEmpty(chapter?.lastLink) && chapter!!.lastLink.endsWith("html")) {
                        last()
                    }
                } else {
                    if (!TextUtils.isEmpty(chapter?.nextLink)) {
                        next()
                    } else {
                        Toast.makeText(applicationContext, "已读完最新一章！", Toast.LENGTH_SHORT).show()
                    }
                }
            }


        }

        readerView.onMenuListener = object : ReaderView.OnMenuListener {
            override fun close() {
                toolbar.visibility = View.GONE
            }

            override fun open() {
                toolbar.visibility = View.VISIBLE
            }

        }
    }

    private fun next() {
        if (nextChapter != null) {
            lastChapter = chapter
            chapter = nextChapter
            nextChapter = null
            readerView.text = chapter!!.content
            loadChapter(chapter?.nextLink) {
                nextChapter = it
            }
        } else {
            loadChapter(chapter?.nextLink) {
                lastChapter = chapter
                chapter = it
                nextChapter = null
                readerView.text = chapter!!.content
                loadChapter(chapter?.nextLink) { next ->
                    nextChapter = next
                }
            }
        }
    }

    private fun last() {
        if (lastChapter != null) {
            nextChapter = chapter
            chapter = lastChapter
            lastChapter = null
            readerView.text = chapter!!.content
            readerView.post {
                readerView.startIndex = chapter!!.content.lastIndex
            }
            loadChapter(chapter?.lastLink) {
                lastChapter = it
            }
        } else {
            loadChapter(chapter?.lastLink) {
                nextChapter = chapter
                chapter = it
                lastChapter = null
                readerView.text = chapter!!.content
                readerView.post {
                    readerView.startIndex = chapter!!.content.lastIndex
                }
                loadChapter(chapter?.lastLink) { last ->
                    lastChapter = last
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        book.readLink = chapter?.link ?: ""
        book.readIndex = readerView.startIndex
        DataService.INSTANCE.saveBook(book)
    }
}
