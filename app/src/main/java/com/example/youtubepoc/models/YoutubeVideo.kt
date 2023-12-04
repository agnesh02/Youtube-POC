package com.example.youtubepoc.models

import java.math.BigInteger

data class YoutubeVideo(
    val id: String,
    val title: String,
    val viewCount: BigInteger?,
    val likeCount: BigInteger?,
    val duration: String,
    val thumbnail: String,
)
