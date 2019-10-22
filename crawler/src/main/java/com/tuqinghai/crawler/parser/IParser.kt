package com.tuqinghai.crawler.parser

import android.text.TextUtils
import com.tuqinghai.crawler.entity.Source
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

abstract class IParser<T> {

    abstract fun parsing(url: String, source: Source): T

    companion object {

        /**
         * 解析规则
         * "//"字符分割
         * 第一部分 text为内容，其他为属性 content为内容并换行
         * 第二部分 参照 https://jsoup.org/cookbook/extracting-data/selector-syntax
         */
        fun parsing(element: Element, rule: String): String{
            val r = rule.split("//")
            if (r.size != 2) {
                throw Throwable("错误的规则！")
            }
            val e = element.selectFirst(r[1]) ?: return ""
            return when (r[0]) {
                "text" -> {
                    e.text()
                }

                "content" -> {
                    parsing(e)
                }

                else -> {
                    e.attr(r[0]) ?: ""
                }
            }
        }

        private fun parsing(element: Element): String {
            val builder = StringBuilder()
            val lines = element.text().split(" ")
            for (l in lines) {
                val line = l.trim()
                if (!TextUtils.isEmpty(line)) {
                    builder.append("        ").append(line).append("\n\n")
                }
            }

            return builder.toString()
        }

        fun autoUrl(url: String, source: Source): String {
            return if (url.trim().startsWith("http")) {
                url.trim()
            } else {
                source.url + url.trim()
            }
        }

        fun getDoc(url: String, source: Source): Document {
            return Jsoup.connect(autoUrl(url, source)).timeout(15000).get()
        }

    }

}