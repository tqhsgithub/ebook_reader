package com.tuqinghai.bookreader.adapter

import android.view.View
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.tuqinghai.bookreader.entity.LocalBook
import com.tuqinghai.bookreader.R
import kotlinx.android.synthetic.main.item_book.view.ivCover
import kotlinx.android.synthetic.main.item_book.view.tvName
import kotlinx.android.synthetic.main.item_bookshelf.view.*

class BookshelfAdapter : BaseQuickAdapter<LocalBook, BaseViewHolder> {

    var edit = false

    var checkedMap = HashMap<Long, Boolean>()

    constructor() : super(R.layout.item_bookshelf)


    override fun convert(helper: BaseViewHolder, item: LocalBook?) {

        Glide.with(helper.itemView)
            .load(item?.cover)
            .placeholder(R.drawable.none)
            .into(helper.itemView.ivCover)

        helper.itemView.tvName.text = item?.name

        helper.itemView.checkbox.visibility = if (edit) View.VISIBLE else View.INVISIBLE
        helper.itemView.checkbox.isChecked = checkedMap[item?.id ?: 0] ?: false


    }
}