package com.example.youtubepoc.models

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.youtube.YouTube

object YoutubeApi {

    private lateinit var credential: GoogleAccountCredential
    private lateinit var youtubeService: YouTube

    fun setCredential(credential: GoogleAccountCredential) {
        this.credential = credential
    }

    fun getCredential(): GoogleAccountCredential {
        return this.credential
    }

    fun setYoutubeService(service: YouTube) {
        this.youtubeService = service
    }

    fun getYoutubeService(): YouTube {
        return this.youtubeService
    }

    fun getJsYoutubeFrame(videoId: String): String {
        val JS_YOUTUBE_FRAME = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "  <body>\n" +
                "    <!-- 1. The <iframe> (and video player) will replace this <div> tag. -->\n" +
                "    <div id=\"player\"></div>\n" +
                "\n" +
                "    <script>\n" +
                "      // 2. This code loads the IFrame Player API code asynchronously.\n" +
                "      var tag = document.createElement('script');\n" +
                "\n" +
                "      tag.src = \"https://www.youtube.com/iframe_api\";\n" +
                "      var firstScriptTag = document.getElementsByTagName('script')[0];\n" +
                "      firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);\n" +
                "\n" +
                "      // 3. This function creates an <iframe> (and YouTube player)\n" +
                "      //    after the API code downloads.\n" +
                "      var player;\n" +
                "      function onYouTubeIframeAPIReady() {\n" +
                "        player = new YT.Player('player', {\n" +
                "          height: '300',\n" +
                "          width: '380',\n" +
                "          videoId: '${videoId}',\n" +
                "          playerVars: {\n" +
                "            'playsinline': 1\n" +
                "          },\n" +
                "          events: {\n" +
                "            'onReady': onPlayerReady,\n" +
                "            'onStateChange': onPlayerStateChange\n" +
                "          },\n" +
                "          referrerPolicy: 'strict-origin-when-cross-origin'\n"+
                "        });\n" +
                "      }\n" +
                "\n" +
                "      // 4. The API will call this function when the video player is ready.\n" +
                "      function onPlayerReady(event) {\n" +
                "        event.target.playVideo();\n" +
                "      }\n" +
                "\n" +
                "      // 5. The API calls this function when the player's state changes.\n" +
                "      //    The function indicates that when playing a video (state=1),\n" +
                "      //    the player should play for six seconds and then stop.\n" +
                "      var done = false;\n" +
                "      function onPlayerStateChange(event) {\n" +
                "        // if (event.data == YT.PlayerState.PLAYING && !done) {\n" +
                "        //   setTimeout(stopVideo, 6000);\n" +
                "        //   done = true;\n" +
                "        // }\n" +
                "      }\n" +
                "      function stopVideo() {\n" +
                "        player.stopVideo();\n" +
                "      }\n" +
                "    </script>\n" +
                "  </body>\n" +
                "</html>"
        return JS_YOUTUBE_FRAME
    }
}