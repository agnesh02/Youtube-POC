package com.example.youtubepoc.views.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.youtubepoc.R
import com.example.youtubepoc.models.YoutubeVideo
import com.yausername.youtubedl_android.YoutubeDL.getInstance
import com.yausername.youtubedl_android.YoutubeDLException
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class CustomVideoListAdapter(
    private val listOfVideos: ArrayList<YoutubeVideo>,
    private val onItemClicked: (String) -> Unit
) :
    RecyclerView.Adapter<CustomVideoListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.custom_video_list, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.listOfVideos.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val video = listOfVideos[position]
        holder.videoTitle.text = video.title
        holder.videoViews.text = "${video.viewCount} views"
        holder.videoLikes.text = "${video.likeCount} likes"
        val duration = Duration.parse(video.duration)
        val startTime = LocalTime.of(0, 0).plus(duration)
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val formattedTime = startTime.format(formatter)
        holder.videoDuration.text = formattedTime
        holder.itemView.setOnClickListener {
            onItemClicked(video.id)
        }
        Glide.with(holder.itemView)
            .load(video.thumbnail)
            .placeholder(R.drawable.multimedia) // Placeholder image resource
            .error(R.drawable.baseline_error_outline_24)           // Error image resource
            .into(holder.videoThumbnail)
        holder.videoAudioDownload.setOnClickListener {
            Log.d("AGNESH","clicked")
        }
    }

    class MyViewHolder(itemView: View) : ViewHolder(itemView) {
        val videoTitle: TextView = itemView.findViewById(R.id.tv_video_title);
        val videoViews: TextView = itemView.findViewById(R.id.tv_video_views);
        val videoLikes: TextView = itemView.findViewById(R.id.tv_video_likes);
        val videoDuration: TextView = itemView.findViewById(R.id.tv_video_duration);
        val videoThumbnail: ImageView = itemView.findViewById(R.id.video_thumbnail);
        val videoAudioDownload: TextView = itemView.findViewById(R.id.tv_download_audio);
    }
}

