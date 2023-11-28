package com.example.youtubepoc.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.youtubepoc.databinding.FragmentSubscriptionBinding
import com.example.youtubepoc.models.Common
import com.example.youtubepoc.models.ResponseCodes
import com.example.youtubepoc.models.YoutubeSubscriptionDetails
import com.example.youtubepoc.viewModels.HomeActivityViewModel
import com.example.youtubepoc.views.adapters.CustomSubscriptionDetailAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SubscriptionFragment : Fragment() {

    private lateinit var binding: FragmentSubscriptionBinding
    private lateinit var viewModel: HomeActivityViewModel
    private var listOfSubscriptions = ArrayList<YoutubeSubscriptionDetails>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubscriptionBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(requireActivity()).get(HomeActivityViewModel::class.java)

        binding.btnGetUserSubscriptions.setOnClickListener {
            viewModel.getUserSubscriptions()
        }

        val adapter = CustomSubscriptionDetailAdapter(listOfSubscriptions) {
            viewModel.getChannelInfo(it) { data ->
                lifecycleScope.launch(Dispatchers.Main) {
                    Common.showSnackMessage(
                        binding.root,
                        "Views: ${data?.viewCount}\nSubscribers: ${data?.subCount}\nVideos: ${data?.videoCount}",
                        true
                    )
                }
            }
        }

        binding.recyclerViewUserSubscriptions.adapter = adapter
        binding.recyclerViewUserSubscriptions.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewUserSubscriptions.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )

        viewModel.userSubscriptionInfoStatus.observe(viewLifecycleOwner, Observer {
            if (it == ResponseCodes.REQUEST_ATTEMPT) {
                binding.btnGetUserSubscriptions.isEnabled = false
                binding.progressBarUserSubscriptions.visibility = View.VISIBLE
            } else {
                binding.btnGetUserSubscriptions.isEnabled = true
                binding.progressBarUserSubscriptions.visibility = View.INVISIBLE
            }
        })

        viewModel.userSubscriptionInfo.observe(viewLifecycleOwner, Observer {
            listOfSubscriptions.clear()
            listOfSubscriptions.addAll(it)
            adapter.notifyDataSetChanged()
        })

        return binding.root
    }

}