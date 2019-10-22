package com.tuqinghai.bookreader.ui.book

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.tuqinghai.bookreader.R
import com.tuqinghai.bookreader.entity.LocalBook
import com.tuqinghai.bookreader.service.DataService
import com.tuqinghai.crawler.entity.Book
import kotlinx.android.synthetic.main.activity_book_info.*

class BookInfoActivity : AppCompatActivity() {

    private val book: LocalBook by lazy {
        var local = intent.getParcelableExtra("data") as LocalBook
        val book = DataService.INSTANCE.loadLocalBook(local.link)
        book ?: local
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_info)
    }

    override fun onContentChanged() {
        super.onContentChanged()
        initView()
    }


    private fun initView() {
        Glide.with(this)
            .load(book.cover)
            .placeholder(R.drawable.none)
            .into(ivCover)

        tvName.text = book.name
        tvDesc.text = book.desc
        tvNewChapter.text = book.newChapter
        tvNewChapterTime.text = book.newChapterTime

        ivBack.setOnClickListener {
            finish()
        }


        llNewChapter.setOnClickListener {
            val intent = Intent(this, ReadActivity::class.java)
            intent.putExtra("link", book.newChapterLink)
            intent.putExtra("book", book)
            startActivity(intent)
        }

        tvChapterList.setOnClickListener {
            val intent = Intent(this, ChapterListActivity::class.java)
            intent.putExtra("book", book)
            startActivity(intent)
        }


        btnAddInBookshelf.text = if (book.inBookshelf) "已加入书架" else "加入书架"
        btnAddInBookshelf.setBackgroundColor(
            if (book.inBookshelf)
                resources.getColor(android.R.color.darker_gray)
            else
                resources.getColor(android.R.color.holo_green_dark)
        )
        btnAddInBookshelf.isClickable = !book.inBookshelf
        btnAddInBookshelf.setOnClickListener {
            book.inBookshelf = true
            btnAddInBookshelf.text = "已加入书架"
            btnAddInBookshelf.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
            btnAddInBookshelf.isClickable = false
            DataService.INSTANCE.saveLocalBookSelf(book)
        }
    }


}
