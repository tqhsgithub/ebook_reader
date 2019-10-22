package com.tuqinghai.crawler;

import com.tuqinghai.crawler.entity.Chapter;
import com.tuqinghai.crawler.entity.Source;
import com.tuqinghai.crawler.parser.ChapterParser;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {

        Map<String, String> chapterMap = new HashMap<>();
        chapterMap.put("content", "text//div#content");
        chapterMap.put("title", "text//h1");
        chapterMap.put("next", "href//a:contains(下一章)");
        chapterMap.put("last", "href//a:contains(上一章)");

        Map<String, String> bookMap = new HashMap<>();
        bookMap.put("list", ".result-game-item-detail");
        bookMap.put("cover", "src//img");
        bookMap.put("title", "text//a.result-game-item-title-link");
        bookMap.put("link", "href//a.result-game-item-title-link");
        bookMap.put("author", "text//p.result-game-item-info-tag");
        bookMap.put("desc", "text//p.result-game-item-desc");

        Map<String, String> chapterListMap = new HashMap<>();
        chapterListMap.put("list", "div#list > dd");;
        chapterListMap.put("title", "text//a");
        chapterListMap.put("link", "href//a");

        Source source =  new Source(
                        "",
                        "https://www.xbiquge6.com",
                        "/search.php?keyword=%s",
                        bookMap,
                        chapterMap,
                        chapterListMap
                );

//        List li = Crawler.INSTANCE.search("大",
//
//                );
//        System.out.println(li.size());

        Crawler.INSTANCE.search("超神",source);
    }
}