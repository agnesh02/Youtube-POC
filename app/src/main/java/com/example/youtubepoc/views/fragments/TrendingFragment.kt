package com.example.youtubepoc.views.fragments

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS
import com.arthenica.mobileffmpeg.FFmpeg
import com.example.youtubepoc.databinding.FragmentTrendingBinding
import com.example.youtubepoc.models.Common
import com.example.youtubepoc.models.ResponseCodes
import com.example.youtubepoc.models.YoutubeVideo
import com.example.youtubepoc.viewModels.HomeActivityViewModel
import com.example.youtubepoc.views.adapters.CustomVideoListAdapter
import com.maxrave.kotlinyoutubeextractor.State
import com.maxrave.kotlinyoutubeextractor.VideoMeta
import com.maxrave.kotlinyoutubeextractor.YTExtractor
import com.maxrave.kotlinyoutubeextractor.YtFile
import com.maxrave.kotlinyoutubeextractor.getAudioOnly
import com.maxrave.kotlinyoutubeextractor.getVideoOnly
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.coroutines.launch
import java.io.File


class DownloadCompleteReceiver(val isDownloadFiished: MutableLiveData<Boolean>) :
    BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            // Handle the download completion here
            // You can execute your code or trigger an event after the download is finished
            // For example, you can show a notification or update UI
            // Note: You may need to check the download status in the intent to ensure it was successful
            // You can get the download ID from intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            isDownloadFiished.value = true
        }
    }
}


class TrendingFragment : Fragment() {

    private lateinit var binding: FragmentTrendingBinding
    private lateinit var viewModel: HomeActivityViewModel
    private var listOfTrendingVideos = ArrayList<YoutubeVideo>()
    private lateinit var ytPlayer: YouTubePlayer
    val isDownloadFiished = MutableLiveData(false)
    private val downloadCompleteReceiver = DownloadCompleteReceiver(isDownloadFiished)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        requireActivity().registerReceiver(downloadCompleteReceiver, filter)

        binding = FragmentTrendingBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(requireActivity()).get(HomeActivityViewModel::class.java)

//        val adapter: CustomVideoListAdapter = CustomVideoListAdapter(listOfTrendingVideos) {
//            ytPlayer.loadVideo(it.id, 0f);
//        }

        val adapter = CustomVideoListAdapter(listOfTrendingVideos) {
            ytPlayer.loadVideo(it.id, 0f);
            val yt =
                YTExtractor(con = requireActivity(), CACHING = true, LOGGING = true, retryCount = 3)
            var ytFiles: SparseArray<YtFile>? = null
            var videoMeta: VideoMeta? = null
            lifecycleScope.launch {
                yt.extract(it.id)
                if (yt.state == State.SUCCESS) {
                    ytFiles = yt.getYTFiles()
                    videoMeta = yt.getVideoMeta()
                    val videoYtFiles =
                        ytFiles?.getAudioOnly() // Return ArrayList<YtFile> of only video
                    val audioYtFiles =
                        ytFiles?.getVideoOnly() // Return ArrayList<YtFile> of only audio
//                    Log.d("YT VIDEO RESULT (url)", audioYtFiles.toString())
                    val ytFile = ytFiles?.get(18)
                    val ytFileVideoUrl = ytFile?.url.toString()
                    Log.d("YT VIDEO RESULT (url)", ytFileVideoUrl)

                    val downloadsDir =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val youtubeDLDir = File(downloadsDir, "youtubeVids-android")
                    if (!youtubeDLDir.exists()) youtubeDLDir.mkdir()

                    val videoFilePath = "file://$youtubeDLDir/testVideo.mp4"
                    val audioFilePath = "file://$youtubeDLDir/testAudio"
                    val downloadUri = Uri.parse(ytFileVideoUrl.trim())
                    val req = DownloadManager.Request(downloadUri)
                    req.setDestinationUri(Uri.parse(videoFilePath))
                    req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    val dm =
                        requireActivity()!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    dm.enqueue(req)

                    isDownloadFiished.observe(viewLifecycleOwner, Observer {
                        if (it) {
                            Common.showSnackMessage(binding.root, "Starting to extract", true)
                            val rc = FFmpeg.execute(
                                " -i" +
                                        " ${videoFilePath}" +
                                        " -map" +
                                        " 0:a" +
                                        " -c" +
                                        " copy" +
                                        " ${audioFilePath}.m4a"
                            )
                            if (rc == RETURN_CODE_SUCCESS) {
                                Log.i("FFFFF", "Command execution completed successfully.")
                            } else if (rc == RETURN_CODE_CANCEL) {
                                Log.i("FFFFF", "Command execution cancelled by user.")
                            } else {
                                Log.i(
                                    "FFFFF",
                                    String.format(
                                        "Command execution failed with rc=%d and the output below.",
                                        rc
                                    )
                                )
                                Config.printLastCommandOutput(Log.INFO)
                            }
                        }
                    })

                }
            }
        }


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

        viewModel.trendingVideosInfo.observe(viewLifecycleOwner, {
            listOfTrendingVideos.clear()
            listOfTrendingVideos.addAll(it)
            adapter.notifyDataSetChanged()
        })

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