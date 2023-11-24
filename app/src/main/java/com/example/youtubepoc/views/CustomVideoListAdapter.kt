package com.example.youtubepoc.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.youtubepoc.R
import com.example.youtubepoc.models.YoutubeVideo

class CustomVideoListAdapter(
    private val listOfVideos: ArrayList<YoutubeVideo>,
    private val onItemClicked: (String) -> Unit
) :
    RecyclerView.Adapter<MyViewHolder>() {

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
        holder.videoTitle.setOnClickListener {
            onItemClicked(video.id)
        }
    }
}

class MyViewHolder(itemView: View) : ViewHolder(itemView) {
    val videoTitle: TextView = itemView.findViewById(R.id.tv_video_title);
    val videoViews: TextView = itemView.findViewById(R.id.tv_video_views);
    val videoLikes: TextView = itemView.findViewById(R.id.tv_video_likes);
}