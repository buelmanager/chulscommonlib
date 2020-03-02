package com.recycler.data


enum class TYPE{
    TITLE_CONTENT,
    DIV_LINE,
    SECTION_TITLE,

}
data class DataVO(val title:String,val content:String , val type: TYPE = TYPE.TITLE_CONTENT,val timestamp: String,val id:String)