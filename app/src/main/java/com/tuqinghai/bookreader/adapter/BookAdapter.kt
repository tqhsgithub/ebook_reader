package com.tuqinghai.bookreader.adapter

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.tuqinghai.bookreader.R
import com.tuqinghai.bookreader.entity.LocalBook
import kotlinx.android.synthetic.main.item_book.view.*

class BookAdapter:BaseQuickAdapter<LocalBook,BaseViewHolder> {

    var keyword = ""

    constructor():super(R.layout.item_book)

    override fun convert(helper: BaseViewHolder, item: LocalBook?) {
        setSpannableString(item?.name?:"",helper.itemView.tvName)
        setSpannableString(item?.author?:"",helper.itemView.tvAuthor)
        helper.itemView.tvDesc.text  = item?.desc

        Glide.with(helper.itemView)
            .load(item?.cover)
            .placeholder(R.drawable.none)
            .into(helper?.itemView.ivCover)
    }

    private fun setSpannableString(str:String, tv:TextView){
        val start = str.indexOf(keyword)
        if(start!=-1) {
            val spannableString = SpannableString(str)
            spannableString.setSpan(ForegroundColorSpan(Color.RED),start,start+keyword.length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            tv.setText(spannableString, TextView.BufferType.SPANNABLE)
        }else{
            tv.text = str
        }
    }

}