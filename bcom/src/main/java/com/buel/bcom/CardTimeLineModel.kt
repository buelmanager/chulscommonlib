package com.buel.bcom


data class CardTimeLineModel(
    var uid: String? = "",
    var storageUid: String? = "",
    var userUid: String? = "",
    var title: String? = "",
    var desc: String? = "",
    var content_img: String? = "",
    var userName: String? = "",
    var userImage: String? = "",
    var comments: String? = "",
    var likes: String? = "",
    var type: String? = "",
    var clickType: String? = "",
    var timeStamp: String? = "",
    var starCount: Int = 0,
    var stars: MutableMap<String, Boolean> = HashMap()
)