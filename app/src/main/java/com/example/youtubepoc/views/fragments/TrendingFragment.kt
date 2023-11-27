package com.example.youtubepoc.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.youtubepoc.databinding.FragmentTrendingBinding
import com.example.youtubepoc.models.ResponseCodes
import com.example.youtubepoc.models.YoutubeVideo
import com.example.youtubepoc.viewModels.HomeActivityViewModel
import com.example.youtubepoc.views.adapters.CustomVideoListAdapter
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener

class TrendingFragment : Fragment() {

    private lateinit var binding: FragmentTrendingBinding
    private lateinit var viewModel: HomeActivityViewModel
    private var listOfTrendingVideos = ArrayList<YoutubeVideo>()
    private lateinit var ytPlayer: YouTubePlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTrendingBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(requireActivity()).get(HomeActivityViewModel::class.java)

        val adapter: CustomVideoListAdapter = CustomVideoListAdapter(listOfTrendingVideos, {
//            binding.webViewYoutube.loadData(YoutubeApi.getJsYoutubeFrame(it), "text/html", "utf-8")
            ytPlayer.loadVideo(it, 0f);
        })

        binding.recyclerViewTrendingVideos.adapter = adapter
        binding.recyclerViewTrendingVideos.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTrendingVideos.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )

        binding.btnGetTrending.setOnClickListener {
            viewModel.getTrendingVideos()
//            viewModel.getChannelInfo()
//            viewModel.getUserSubscriptions()
        }

        viewModel.trendingVideosStatus.observe(viewLifecycleOwner, Observer {
            if (it == ResponseCodes.REQUEST_ATTEMPT) {
                binding.btnGetTrending.isEnabled = false
                binding.progressBarTrendingVideos.visibility = View.VISIBLE
            } else {
                binding.btnGetTrending.isEnabled = true
                binding.progressBarTrendingVideos.visibility = View.INVISIBLE
            }
        })

        viewModel.trendingVideosInfo.observe(viewLifecycleOwner, Observer {
            listOfTrendingVideos.clear()
            listOfTrendingVideos.addAll(it)
            adapter.notifyDataSetChanged()
        })

//        binding.webViewYoutube.settings.javaScriptEnabled = true
//        binding.webViewYoutube.webChromeClient = WebChromeClient()

//        val player = ExoPlayer.Builder(applicationContext).build()
//        binding.playerView.player = player
//        val mediaItem = MediaItem.fromUri("https://www.youtube.com/watch?v=8FkLRUJj-o0.mp4")
//        player.setMediaItem(mediaItem)
//        player.prepare()
//        player.play()

        lifecycle.addObserver(binding.youtubePlayerView)
        binding.youtubePlayerView.addYouTubePlayerListener(object :
            AbstractYouTubePlayerListener() {
            override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                ytPlayer = youTubePlayer
            }
        });


        return binding.root
    }
}