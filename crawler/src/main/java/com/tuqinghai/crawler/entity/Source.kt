package com.tuqinghai.crawler.entity

data class Source (
    val title:String,                               //站点名称
    val url:String,                                 //站点网站
    val searchPath:String,                          //搜索路径，关键字使用%s占位符
    val searchParsingRule:Map<String,String>,       //搜索解析规则
    val chapterParsingRule:Map<String,String>,      //章节解析规则
    val chapterListParsingRule:Map<String,String>   //目录解析规则
)