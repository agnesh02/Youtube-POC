package com.example.youtubepoc.views.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.youtubepoc.R
import com.example.youtubepoc.databinding.ActivityHomeBinding
import com.example.youtubepoc.models.Common
import com.example.youtubepoc.viewModels.HomeActivityViewModel
import com.example.youtubepoc.views.fragments.SearchFragment
import com.example.youtubepoc.views.fragments.SubscriptionFragment
import com.example.youtubepoc.views.fragments.TrendingFragment


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(HomeActivityViewModel::class.java)

        val bundle = this.intent.extras
        if (bundle != null) {
            val name = bundle.getString("NAME")
            Common.showToast(this, name.toString())
        }

        // Initializing a screen
        supportActionBar?.title = "Trending"
        changeScreen(TrendingFragment())
        binding.bottomNavigationView.itemIconTintList = null
        binding.bottomNavigationView.selectedItemId = R.id.menu_trending

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_trending -> {
                    supportActionBar?.title = "Trending"
                    changeScreen(TrendingFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.menu_search -> {
                    supportActionBar?.title = "Search"
                    changeScreen(SearchFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.menu_subscription -> {
                    supportActionBar?.title = "Subscriptions"
                    changeScreen(SubscriptionFragment())
                    return@setOnItemSelectedListener true
                }
            }
            return@setOnItemSelectedListener true
        }
    }

    private fun changeScreen(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(binding.homeActivityFrame.id, fragment)
        transaction.commit()
    }

}