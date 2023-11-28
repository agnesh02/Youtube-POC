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
import com.example.youtubepoc.models.YoutubeSearchResult
import com.example.youtubepoc.databinding.FragmentSearchBinding
import com.example.youtubepoc.models.Common
import com.example.youtubepoc.models.ResponseCodes
import com.example.youtubepoc.viewModels.HomeActivityViewModel
import com.example.youtubepoc.views.adapters.CustomSearchResultAdapter
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: HomeActivityViewModel
    private var listOfSearchResult = ArrayList<YoutubeSearchResult>()
    private lateinit var ytPlayer: YouTubePlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSearchBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(requireActivity()).get(HomeActivityViewModel::class.java)

        val adapter = CustomSearchResultAdapter(listOfSearchResult) {
            if (it == "null") {
                Common.showSnackMessage(binding.root, "Cannot play this content as this is not a video", true)
            } else {
                ytPlayer.loadVideo(it, 0f);
            }
        }

        binding.recyclerViewSearch.adapter = adapter
        binding.recyclerViewSearch.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewSearch.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )

        binding.btnGetSearchResult.setOnClickListener {
            val query = binding.searchInputChild.text.toString().trim()
            viewModel.getSearchResults(query)
        }

        viewModel.searchResultsStatus.observe(viewLifecycleOwner, Observer {
            if (it == ResponseCodes.REQUEST_ATTEMPT) {
                binding.btnGetSearchResult.isEnabled = false
                binding.progressBarSearch.visibility = View.VISIBLE
            } else {
                binding.btnGetSearchResult.isEnabled = true
                binding.progressBarSearch.visibility = View.INVISIBLE
            }
        })

        viewModel.searchResultsInfo.observe(viewLifecycleOwner, Observer {
            listOfSearchResult.clear()
            listOfSearchResult.addAll(it)
            adapter.notifyDataSetChanged()
        })

        lifecycle.addObserver(binding.youtubePlayerViewSearch)
        binding.youtubePlayerViewSearch.addYouTubePlayerListener(object :
            AbstractYouTubePlayerListener() {
            override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                ytPlayer = youTubePlayer
            }
        });

        return binding.root
    }
}