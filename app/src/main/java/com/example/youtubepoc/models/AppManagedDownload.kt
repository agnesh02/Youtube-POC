package com.github.axet.vget

import com.github.axet.vget.info.VGetParser
import com.github.axet.vget.info.VideoFileInfo
import com.github.axet.vget.info.VideoInfo
import com.github.axet.vget.vhs.VimeoInfo
import com.github.axet.vget.vhs.YouTubeInfo
import com.github.axet.wget.SpeedInfo
import com.github.axet.wget.info.DownloadInfo
import com.github.axet.wget.info.ex.DownloadInterruptedError
import java.io.File
import java.net.URL
import java.util.concurrent.atomic.AtomicBoolean

object AppManagedDownload {
    fun formatSpeed(s: Long): String {
        if (s > 0.1 * 1024 * 1024 * 1024) {
            val f = s / 1024f / 1024f / 1024f
            return String.format("%.1f GB/s", f)
        } else if (s > 0.1 * 1024 * 1024) {
            val f = s / 1024f / 1024f
            return String.format("%.1f MB/s", f)
        } else {
            val f = s / 1024f
            return String.format("%.1f kb/s", f)
        }
    }

//    @JvmStatic
//    fun main(args: Array<String>) {
//        // ex: http://www.youtube.com/watch?v=Nj6PFaDmp6c
//        val url = args[0]
//        // ex: /Users/axet/Downloads/
//        val path = File(args[1])
//        try {
//            val stop = AtomicBoolean(false)
//            val web = URL(url)
//
//            // [OPTIONAL] limit maximum quality, or do not call this function if
//            // you wish maximum quality available.
//            //
//            // if youtube does not have video with requested quality, program
//            // will raise en exception.
//            var user: VGetParser? = null
//
//            // create proper html parser depends on url
//            user = VGet.parser(web)
//
//            // download limited video quality from youtube
//            // user = new YouTubeQParser(YoutubeQuality.p480);
//
//            // download mp4 format only, fail if non exist
//            // user = new YouTubeMPGParser();
//
//            // create proper videoinfo to keep specific video information
//            val videoinfo = user.info(web)
//            val v = VGet(videoinfo, path)
//            val notify = VGetStatus(videoinfo)
//
//            // [OPTIONAL] call v.extract() only if you d like to get video title
//            // or download url link before start download. or just skip it.
//            v.extract(user, stop, notify)
//            println("Title: " + videoinfo.title)
//            val list = videoinfo.info
//            if (list != null) {
//                for (d: VideoFileInfo in list) {
//                    // [OPTIONAL] setTarget file for each download source video/audio
//                    // use d.getContentType() to determine which or use
//                    // v.targetFile(dinfo, ext, conflict) to set name dynamically or
//                    // d.targetFile = new File("/Downloads/CustomName.mp3");
//                    // to set file name manually.
//                    println("Download URL: " + d.source)
//                }
//            }
//            v.download(user, stop, notify)
//        } catch (e: DownloadInterruptedError) {
//            throw e
//        } catch (e: RuntimeException) {
//            throw e
//        } catch (e: Exception) {
//            throw RuntimeException(e)
//        }
//    }

    internal class VGetStatus(var videoinfo: VideoInfo) : Runnable {
        var last: Long = 0
        var map: MutableMap<VideoFileInfo, SpeedInfo> = HashMap()
        fun getSpeedInfo(dinfo: VideoFileInfo): SpeedInfo {
            var speedInfo = map[dinfo]
            if (speedInfo == null) {
                speedInfo = SpeedInfo()
                speedInfo.start(dinfo.count)
                map[dinfo] = speedInfo
            }
            return speedInfo
        }

        override fun run() {
            val dinfoList = videoinfo.info
            when (videoinfo.state) {
                VideoInfo.States.EXTRACTING, VideoInfo.States.EXTRACTING_DONE, VideoInfo.States.DONE -> {
                    if (videoinfo is YouTubeInfo) {
                        val i = videoinfo as YouTubeInfo
                        println(videoinfo.state.toString() + " " + i.videoQuality)
                    } else if (videoinfo is VimeoInfo) {
                        val i = videoinfo as VimeoInfo
                        println(videoinfo.state.toString() + " " + i.videoQuality)
                    } else {
                        println("downloading unknown quality")
                    }
                    for (d: VideoFileInfo in videoinfo.info) {
                        val speedInfo = getSpeedInfo(d)
                        speedInfo.end(d.count)
                        println(
                            String.format(
                                "file:%d - %s (%s)", dinfoList!!.indexOf(d), d.targetFile,
                                formatSpeed(speedInfo.averageSpeed.toLong())
                            )
                        )
                    }
                }

                VideoInfo.States.ERROR -> {
                    println(videoinfo.state.toString() + " " + videoinfo.delay)
                    if (dinfoList != null) {
                        for (dinfo: DownloadInfo in dinfoList) {
                            println(
                                "file:" + dinfoList.indexOf(dinfo) + " - " + dinfo.exception + " delay:"
                                        + dinfo.delay
                            )
                        }
                    }
                }

                VideoInfo.States.RETRYING -> {
                    println(videoinfo.state.toString() + " " + videoinfo.delay + videoinfo.exception.message)
                    if (dinfoList != null) {
                        for (dinfo: DownloadInfo in dinfoList) {
                            println(
                                ("file:" + dinfoList.indexOf(dinfo) + " - " + dinfo.state + " "
                                        + dinfo.exception + " delay:" + dinfo.delay)
                            )
                        }
                    }
                }

                VideoInfo.States.DOWNLOADING -> {
                    val now = System.currentTimeMillis()
                    if (now - 1000 > last) {
                        last = now
                        var parts: String = ""
                        for (dinfo: VideoFileInfo in dinfoList!!) {
                            val speedInfo = getSpeedInfo(dinfo)
                            speedInfo.step(dinfo.count)
                            val pp = dinfo.parts
                            if (pp != null) {
                                // multipart download
                                for (p: DownloadInfo.Part in pp) {
                                    if ((p.state == DownloadInfo.Part.States.DOWNLOADING)) {
                                        parts += String.format(
                                            "part#%d(%.2f) ", p.number,
                                            p.count / p.length.toFloat()
                                        )
                                    }
                                }
                            }
                            println(
                                String.format(
                                    "file:%d - %s %.2f %s (%s)", dinfoList.indexOf(dinfo),
                                    videoinfo.state, dinfo.count / dinfo.length.toFloat(), parts,
                                    formatSpeed(speedInfo.currentSpeed.toLong())
                                )
                            )
                        }
                    }
                }

                else -> {}
            }
        }
    }
}