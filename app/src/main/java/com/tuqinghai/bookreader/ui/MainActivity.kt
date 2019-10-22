package com.tuqinghai.bookreader.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.tuqinghai.bookreader.R
import com.tuqinghai.bookreader.adapter.BookshelfAdapter
import com.tuqinghai.bookreader.entity.LocalBook
import com.tuqinghai.bookreader.service.DataService
import com.tuqinghai.bookreader.ui.book.BookInfoActivity
import com.tuqinghai.bookreader.ui.book.ReadActivity
import com.tuqinghai.bookreader.ui.search.SearchActivity
import com.tuqinghai.crawler.entity.Book
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val adapter: BookshelfAdapter by lazy { BookshelfAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onContentChanged() {
        super.onContentChanged()
        initView()
    }

    private fun initView() {
        tvSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        rvBookshelf.adapter = adapter

        adapter.setOnItemClickListener { _, _, position ->
            val book = adapter.data[position]
            if (adapter.edit) {
                adapter.checkedMap[book.id] = !(adapter.checkedMap[book.id] ?: false)
                adapter.notifyDataSetChanged()
            } else {

                if (TextUtils.isEmpty(book.readLink) || !book.readLink.endsWith("html")) {
                    val intent = Intent(this, BookInfoActivity::class.java)
                    intent.putExtra("data", book)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, ReadActivity::class.java)
                    intent.putExtra("book", book)
                    startActivity(intent)
                }
            }
        }

        tvEdit.setOnClickListener {
            adapter.edit = !adapter.edit
            tvEdit.text = if (adapter.edit) "取消" else "编辑"
            adapter.notifyDataSetChanged()
            llEdit.visibility = if (adapter.edit) View.VISIBLE else View.GONE
        }

        tvRemove.setOnClickListener {
            val checked = adapter.data.filter { adapter.checkedMap[it.id] ?: false }
            checked.forEach {
                it.inBookshelf = false
                DataService.INSTANCE.saveBook(it)
            }

            adapter.data.removeAll(checked)
            adapter.notifyDataSetChanged()
        }

        tvLocal.setOnClickListener {
            val checked = adapter.data.filter { adapter.checkedMap[it.id] ?: false }

            Observable.fromIterable(checked)
                .flatMap { DataService.INSTANCE.saveBookAllChapter(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe { p ->
                    tvLocal.text = "离线中$p %"
                }

        }
    }

    override fun onResume() {
        super.onResume()
        loadBookshelf()
    }

    @SuppressLint("CheckResult")
    private fun loadBookshelf() {
        DataService.INSTANCE.loadBookshelf()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                adapter.setNewData(it)
            }

    }
}
