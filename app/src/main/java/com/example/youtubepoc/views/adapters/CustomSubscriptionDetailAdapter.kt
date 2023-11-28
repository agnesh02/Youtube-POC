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
import com.example.youtubepoc.models.YoutubeSubscriptionDetails

class CustomSubscriptionDetailAdapter(
    private val listOfSubscriptions: ArrayList<YoutubeSubscriptionDetails>,
    private val onItemClicked: (String) -> Unit
) :
    RecyclerView.Adapter<CustomSubscriptionDetailAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.custom_subscription_list, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.listOfSubscriptions.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val subscription = listOfSubscriptions[position]
        holder.subscriptionTitle.text = subscription.title
        holder.subscriptionDescription.text = subscription.description
        holder.itemView.setOnClickListener {
            onItemClicked(subscription.id)
        }
        Glide.with(holder.itemView)
            .load(subscription.thumbnail)
            .placeholder(R.drawable.multimedia) // Placeholder image resource
            .error(R.drawable.baseline_error_outline_24)           // Error image resource
            .into(holder.subscriptionThumbnail)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subscriptionTitle: TextView = itemView.findViewById(R.id.tv_subscription_title);
        val subscriptionDescription: TextView = itemView.findViewById(R.id.tv_subscription_description);
        val subscriptionThumbnail: ImageView = itemView.findViewById(R.id.subscription_thumbnail);
    }
}

