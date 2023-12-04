package com.example.youtubepoc.views.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.youtubepoc.databinding.FragmentTrendingBinding
import com.example.youtubepoc.models.Common
import com.example.youtubepoc.models.ResponseCodes
import com.example.youtubepoc.models.YoutubeVideo
import com.example.youtubepoc.viewModels.HomeActivityViewModel
import com.example.youtubepoc.views.adapters.CustomVideoListAdapter
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Matcher


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


        val adapter = CustomVideoListAdapter(listOfTrendingVideos) {
//            binding.webViewYoutube.loadData(YoutubeApi.getJsYoutubeFrame(it), "text/html", "utf-8")
            ytPlayer.loadVideo(it, 0f);
            lifecycleScope.launch(Dispatchers.Default) {
//                Log.d("AGNESH URL",
//                    Uri.parse("https://www.youtube.com/watch?v=nPbcZswFjNQ").toString()
//                )
//                startDownload("https://www.youtube.com/watch?v=nPbcZswFjNQ")/////////
//                startDownload("https://www.youtube.com/watch?v=kAP8H9GS6Ks")
//                startDownload("https://www.youtube.com/watch?v=UTGoQNFGP68")
//                startDownload("https://www.youtube.com/watch?v=AnMhdn0wJ4I")

//                object : YouTubeExtractor(requireActivity()) {
//                    override fun onExtractionComplete(
//                        ytFiles: SparseArray<YtFile>?,
//                        vMeta: VideoMeta?
//                    ) {
//                        if (ytFiles != null) {
//                            val uniqueFiles: MutableSet<String> = HashSet()
//                            val videoStreams = java.util.ArrayList<YtFile>()
//
//                            // Iterate over ytFiles
//                            var i = 0
//                            var itag: Int
//                            while (i < ytFiles.size()) {
//                                itag = ytFiles.keyAt(i)
//                                val format =
//                                    ytFiles[itag].format.height.toString() + ytFiles[itag].format.ext
//                                val isAdded = uniqueFiles.add(format)
//                                if (isAdded) {
//                                    videoStreams.add(ytFiles[itag])
//                                }
//                                i++
//                            }
//                            Log.d("AGNESH", videoStreams.toString())
//                        } else {
//                            Toast.makeText(
//                                context,
//                                "No Download Files for this video",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//                }.extract("https://www.youtube.com/watch?v=" + "nPbcZswFjNQ", true, true)

//                YoutubeJExtractor().extract("nPbcZswFjNQ", object : JExtractorCallback {
//                    override fun onSuccess(videoData: VideoPlayerConfig?) {
//                        Log.d("AGNESH | SUCCESS", videoData?.streamingData?.hlsManifestUrl.toString())
//                    }
//
//                    override fun onNetworkException(e: YoutubeRequestException) {
//                        Log.d("AGNESH | exec ", e.toString())
//                    }
//
//                    override fun onError(exception: Exception) {
//                        Log.e("AGNESH", exception.toString())
//                    }
//                })
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

    private fun startDownload(videoUrl: String) {
//        if (downloading) {
//            Toast.makeText(
//                this@DownloadingExampleActivity,
//                "Cannot start download. A download is already in progress",
//                Toast.LENGTH_LONG
//            ).show()
//            return
//        }

        if (!isStoragePermissionGranted()) {
            Common.showSnackMessage(binding.root, "Grant storage permission and retry", true)
            EasyPermissions.requestPermissions(
                requireActivity(),
                "This app needs to access your storage",
                1004,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
//            startDownload(videoUrl)
//            return
        }

        try {
            YoutubeDL.getInstance().init(requireContext())
            FFmpeg.getInstance().init(requireContext());

        } catch (e: YoutubeDLException) {
            Log.e("AGNESH", "failed to initialize youtubedl-android", e)
        }

        val url = videoUrl
        val request = YoutubeDLRequest(url)
        val youtubeDLDir = getDownloadLocation()
        val config = File(youtubeDLDir, "config.txt")

//        request.addOption("--no-mtime")
//        request.addOption("--rm-cache-dir")
//        request.addOption(
//            "--user-agent",
//            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
//        )
//        request.addOption("--downloader", "libaria2c.so")
//        request.addOption("--external-downloader-args", "aria2c:\"--summary-interval=1\"")
//        request.addOption("-f", "bestvideo[ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]/best")
//        request.addOption("-o", "${youtubeDLDir.absolutePath}/%(title)s.%(ext)s")
//        YoutubeDL.getInstance().destroyProcessById("downloadProcess");
        request.addOption("-f", "b");
        request.addOption("-o", "${youtubeDLDir.absolutePath}/%(title)s.%(ext)s")
        YoutubeDL.getInstance().destroyProcessById("downloadProcess");
        Log.d("TEEEEEEST",
            YoutubeDL.getInstance().getInfo(videoUrl).url!! + YoutubeDL.getInstance()
                .getInfo(url).manifestUrl + YoutubeDL.getInstance().getInfo(url).playerUrl
        )
//        YoutubeDL.getInstance().execute(request, "downloadProcess", { a, b, c ->
//            Log.i("AGNESH REPORT", "$a $b $c")
//        })

//        if (useConfigFile.isChecked && config.exists()) {
//            request.addOption("--config-location", config.absolutePath)
//        } else {
//            request.addOption("--no-mtime")
//            request.addOption("--downloader", "libaria2c.so")
//            request.addOption("--external-downloader-args", "aria2c:\"--summary-interval=1\"")
//            request.addOption("-f", "bestvideo[ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]/best")
//            request.addOption("-o", "${youtubeDLDir.absolutePath}/%(title)s.%(ext)s")
//        }
    }

    fun isStoragePermissionGranted(): Boolean {
        return if (checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            === PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
            false
        }
    }

    private fun getDownloadLocation(): File {
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val youtubeDLDir = File(downloadsDir, "youtubedl-android")
        if (!youtubeDLDir.exists()) youtubeDLDir.mkdir()
        return youtubeDLDir
    }


}