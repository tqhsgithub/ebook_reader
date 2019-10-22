package com.tuqinghai.bookreader.ui.book

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import com.tuqinghai.bookreader.R
import com.tuqinghai.bookreader.adapter.ChapterListAdapter
import com.tuqinghai.bookreader.entity.LocalBook
import com.tuqinghai.bookreader.service.DataService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_chapter_list.*

class ChapterListActivity : AppCompatActivity() {

    private val book: LocalBook by lazy {
        intent.getParcelableExtra("book") as LocalBook
    }

    private val adapter = ChapterListAdapter()
    private var isInverse  = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapter_list)


    }

    override fun onContentChanged() {
        super.onContentChanged()
        initView()
        loadChapterList()
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title = book.name

        rvChapterList.adapter = adapter
        rvChapterList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener { _, _, position ->
            val intent = Intent(this, ReadActivity::class.java)
            intent.putExtra("book", book)
            intent.putExtra("link", adapter.data[position].link)
            startActivity(intent)
        }

        srl.setOnRefreshListener {
            loadChapterList()
        }
    }

    @SuppressLint("CheckResult")
    private fun loadChapterList() {
        srl.isRefreshing = true
        DataService.INSTANCE.loadChapterList(book)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                srl.isRefreshing = false

                adapter.setNewData(if (isInverse) it.reversed() else it)
            }, {
                srl.isRefreshing = false
                adapter.setNewData(null)
                Toast.makeText(applicationContext, "加载失败！", Toast.LENGTH_SHORT).show()
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when  {
            item.itemId==android.R.id.home -> {
                finish()
                return true
            }
            item.title.startsWith("目录")->{
                isInverse = !isInverse
                adapter.setNewData(adapter.data.reversed())
                item.title = if(isInverse) "目录↑️" else "目录↓"
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
       menu?.clear()
        menu?.add(if(isInverse) "目录↑️" else "目录↓")?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return super.onCreateOptionsMenu(menu)
    }
}
