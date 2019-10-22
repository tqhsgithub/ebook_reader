package com.tuqinghai.bookreader.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.tuqinghai.bookreader.R
import com.tuqinghai.bookreader.entity.LocalChapter
import kotlinx.android.synthetic.main.item_chapter_name.view.*

class ChapterListAdapter : BaseQuickAdapter<LocalChapter, BaseViewHolder> {
    constructor() : super(R.layout.item_chapter_name)

    override fun convert(helper: BaseViewHolder, item: LocalChapter?) {
        helper.itemView.tvChapterName.text = item?.title
    }
}