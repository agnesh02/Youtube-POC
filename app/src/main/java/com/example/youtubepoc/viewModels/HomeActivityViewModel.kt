package com.example.youtubepoc.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.youtubepoc.models.ResponseCodes
import com.example.youtubepoc.models.YoutubeApi
import com.example.youtubepoc.models.YoutubeSearchResult
import com.example.youtubepoc.models.YoutubeVideo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivityViewModel(application: Application) : AndroidViewModel(application) {

    val trendingVideosInfo = MutableLiveData<ArrayList<YoutubeVideo>>()
    val trendingVideosStatus = MutableLiveData<ResponseCodes>()

    val searchResultsInfo = MutableLiveData<ArrayList<YoutubeSearchResult>>()
    val searchResultsStatus = MutableLiveData<ResponseCodes>()

    fun getTrendingVideos() {

        val trendingVideosResult = ArrayList<YoutubeVideo>()

        viewModelScope.launch(Dispatchers.Main) {
            trendingVideosInfo.value = arrayListOf()
            trendingVideosStatus.value = ResponseCodes.REQUEST_ATTEMPT
        }

        viewModelScope.launch(Dispatchers.IO) {
            val result =
                YoutubeApi.getYoutubeService().videos().list("snippet,statistics")
                    .setChart("mostPopular")
                    .setOauthToken(YoutubeApi.getCredential().token)
                    .setRegionCode("IN")
                    .setMaxResults(20)
                    .execute()
            val trendingVideos = result.items
            for (video in trendingVideos) {
//                val msg =
//                    "Title:'${video.snippet.title}' | Views: ${video.statistics.viewCount} | Likes: ${video.statistics.likeCount}"
                val videoObj = YoutubeVideo(
                    video.id,
                    video.snippet.title,
                    video.statistics.viewCount,
                    video.statistics.likeCount,
                    video.snippet.thumbnails.default.url
                )
                trendingVideosResult.add(videoObj)
            }

            viewModelScope.launch(Dispatchers.Main) {
                trendingVideosInfo.value = trendingVideosResult
                trendingVideosStatus.value = ResponseCodes.REQUEST_SUCCESS
            }

            for (result in trendingVideosResult) {
                Log.i("TRENDING VIDEO: ", result.toString())
            }
        }
    }

    fun getSearchResults(query: String) {
        val searchResultsResult = ArrayList<YoutubeSearchResult>()

        viewModelScope.launch(Dispatchers.Main) {
            searchResultsInfo.value = arrayListOf()
            searchResultsStatus.value = ResponseCodes.REQUEST_ATTEMPT
        }

        viewModelScope.launch(Dispatchers.IO) {
            val result =
                YoutubeApi.getYoutubeService().search().list("snippet").setQ(query)
                    .setRegionCode("IN")
                    .setOauthToken(YoutubeApi.getCredential().token)
                    .setMaxResults(20).execute()

            val searchResults = result.items
            for (video in searchResults) {
                val videoObj = YoutubeSearchResult(
                    video.id?.videoId.toString(),
                    video.snippet.title,
                    video.snippet.description,
                    video.snippet.thumbnails.default.url
                )
                searchResultsResult.add(videoObj)
            }

            viewModelScope.launch(Dispatchers.Main) {
                searchResultsInfo.value = searchResultsResult
                searchResultsStatus.value = ResponseCodes.REQUEST_SUCCESS
            }

            for (result in searchResultsResult) {
                Log.i("SEARCH RESULT: ", result.toString())
            }
        }
    }

    fun getChannelInfo() {

        viewModelScope.launch(Dispatchers.IO) {
            val channelInfo = ArrayList<String>()
            val result =
                YoutubeApi.getYoutubeService().channels().list("snippet,contentDetails,statistics")
                    .setForUsername("GoogleDevelopers")
                    .setKey("AIzaSyBPWzsXmR_EogkLIPod6nZmaifw7Jh-V4M")
                    .execute()
            val channels = result.items
            for (channel in channels) {
                channelInfo.add("Title:'${channel.snippet.title}' | Views: ${channel.statistics.viewCount} | Subscribers: ${channel.statistics.subscriberCount}")
            }

            for (result in channelInfo) {
                Log.i("CHANNEL: ", result)
            }
        }
    }

    fun getUserSubscriptions() {

        viewModelScope.launch(Dispatchers.IO) {
            val subscriptionsInfo = ArrayList<String>()
            val result =
                YoutubeApi.getYoutubeService().subscriptions().list("snippet,contentDetails")
                    .setMine(true)
                    .setOauthToken(YoutubeApi.getCredential().token)
                    .execute()

            val subscriptions = result.items
            for (subscription in subscriptions) {
                subscriptionsInfo.add("Title:'${subscription.snippet.title}' | Description: ${subscription.snippet.description}")
            }

            for (result in subscriptionsInfo) {
                Log.i("SUBSCRIPTION: ", result)
            }
        }
    }

}