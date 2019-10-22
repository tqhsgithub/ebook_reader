package com.tuqinghai.bookreader.ui.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import com.tuqinghai.bookreader.R
import com.tuqinghai.bookreader.adapter.BookAdapter
import com.tuqinghai.bookreader.service.DataService
import com.tuqinghai.bookreader.ui.book.BookInfoActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {

    private val adapter = BookAdapter()
    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
    }

    override fun onContentChanged() {
        super.onContentChanged()
        initView()
    }

    private fun initView() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                loadSearchList(s?.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        rvSearchList.adapter = adapter
//        rvSearchList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener { _, _, position ->
            val intent = Intent(this, BookInfoActivity::class.java)
            intent.putExtra("data", adapter.data[position])
            startActivity(intent)
        }

        tvCancel.setOnClickListener {
            finish()
        }

        srl.setOnRefreshListener {
            loadSearchList(etSearch.text.toString())
        }
    }

    private fun loadSearchList(keyword: String?) {
        srl.isRefreshing = true
        adapter.keyword = keyword ?: ""
        adapter.setNewData(null)
        disposable?.dispose()
        if (TextUtils.isEmpty(keyword)) {
            srl.isRefreshing = false
            return
        }
        disposable = DataService.INSTANCE.search(keyword!!)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                adapter.setNewData(it)
                srl.isRefreshing = false
            }, {
                srl.isRefreshing = false
                Toast.makeText(applicationContext, "查询失败请重试！", Toast.LENGTH_SHORT).show()
            })


    }
}
