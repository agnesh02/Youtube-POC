package com.example.youtubepoc.views

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.youtubepoc.databinding.ActivityHomeBinding
import com.example.youtubepoc.models.Common
import com.example.youtubepoc.models.ResponseCodes
import com.example.youtubepoc.models.YoutubeApi
import com.example.youtubepoc.models.YoutubeVideo
import com.example.youtubepoc.viewModels.HomeActivityViewModel

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeActivityViewModel
    private var listOfTrendingVideos = ArrayList<YoutubeVideo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(HomeActivityViewModel::class.java)

        val bundle = this.intent.extras
        if (bundle != null) {
            val name = bundle.getString("NAME")
            Common.showToast(this, name.toString())
        }

        val adapter: CustomVideoListAdapter = CustomVideoListAdapter(listOfTrendingVideos, {
            binding.webViewYoutube.loadData(YoutubeApi.getJsYoutubeFrame(it), "text/html", "utf-8")
        })

        binding.recyclerViewTrendingVideos.adapter = adapter
        binding.recyclerViewTrendingVideos.layoutManager = LinearLayoutManager(applicationContext)
        binding.recyclerViewTrendingVideos.addItemDecoration(
            DividerItemDecoration(
                applicationContext,
                LinearLayoutManager.VERTICAL
            )
        )

        binding.btnGetTrending.setOnClickListener {
            viewModel.getTrendingVideos()
//            viewModel.getChannelInfo()
//            viewModel.getUserSubscriptions()
        }

        viewModel.trendingVideosStatus.observe(this, Observer {
            if (it == ResponseCodes.REQUEST_ATTEMPT) {
                binding.btnGetTrending.isEnabled = false
                binding.progressBarTrendingVideos.visibility = View.VISIBLE
            } else {
                binding.btnGetTrending.isEnabled = true
                binding.progressBarTrendingVideos.visibility = View.INVISIBLE
            }
        })

        viewModel.trendingVideosInfo.observe(this, Observer {
            listOfTrendingVideos.clear()
            listOfTrendingVideos.addAll(it)
            adapter.notifyDataSetChanged()
        })

        binding.webViewYoutube.settings.javaScriptEnabled = true
        binding.webViewYoutube.webChromeClient = WebChromeClient()

//        val player = ExoPlayer.Builder(applicationContext).build()
//        binding.playerView.player = player
//        val mediaItem = MediaItem.fromUri("https://www.youtube.com/watch?v=8FkLRUJj-o0.mp4")
//        player.setMediaItem(mediaItem)
//        player.prepare()
//        player.play()
    }
}