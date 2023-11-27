package com.example.youtubepoc.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.youtubepoc.R
import com.example.youtubepoc.models.YoutubeSearchResult

class CustomSearchResultAdapter(
    private val listOfVideos: ArrayList<YoutubeSearchResult>,
    private val onItemClicked: (String) -> Unit
) :
    RecyclerView.Adapter<CustomSearchResultAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.custom_search_list, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.listOfVideos.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val result = listOfVideos[position]
        holder.resultTitle.text = result.title
        holder.resultDescription.text = result.description
        holder.resultTitle.setOnClickListener {
            onItemClicked(result.id)
        }
        Glide.with(holder.itemView)
            .load(result.thumbnail)
            .placeholder(R.drawable.multimedia) // Placeholder image resource
            .error(R.drawable.baseline_error_outline_24)           // Error image resource
            .into(holder.resultThumbnail)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val resultTitle: TextView = itemView.findViewById(R.id.tv_result_title);
        val resultDescription: TextView = itemView.findViewById(R.id.tv_result_description);
        val resultThumbnail: ImageView = itemView.findViewById(R.id.result_thumbnail);
    }
}

