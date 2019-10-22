package com.tuqinghai.bookreader.source

import com.tuqinghai.crawler.entity.Source
import java.util.HashMap

object SourceManage {
    val sourceList: ArrayList<Source> by lazy {
        val list = ArrayList<Source>()

        val chapterMap1 = HashMap<String, String>()
        chapterMap1["content"] = "content//div#content"
        chapterMap1["title"] = "text//h1"
        chapterMap1["next"] = "href//a:contains(下一章)"
        chapterMap1["last"] = "href//a:contains(上一章)"

        val bookMap1 = HashMap<String, String>()
        bookMap1["list"] = "div.result-item"
        bookMap1["cover"] = "src//img"
        bookMap1["title"] = "text//a.result-game-item-title-link"
        bookMap1["link"] = "href//a.result-game-item-title-link"
        bookMap1["author"] = "text//p.result-game-item-info-tag"
        bookMap1["desc"] = "text//p.result-game-item-desc"
        bookMap1["newChapter"] = "text//a.result-game-item-info-tag-item"
        bookMap1["newChapterTime"] = "text//p:contains(更新时间) > span:matches(^(?!更新时间).*$)"
        bookMap1["newChapterLink"] = "href//a.result-game-item-info-tag-item"

        val chapterListMap1 = HashMap<String, String>()
        chapterListMap1["list"] = "div#list > dl > dd"
        chapterListMap1["title"] = "text//a"
        chapterListMap1["link"] = "href//a"

        list.add(
            Source(
                "新笔趣阁",
                "https://www.xbiquge6.com",
                "/search.php?keyword=%s",
                bookMap1,
                chapterMap1,
                chapterListMap1
            )
        )



        list
    }
}