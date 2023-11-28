package com.example.youtubepoc.views.activities

import android.accounts.AccountManager
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.youtubepoc.databinding.ActivityMainBinding
import com.example.youtubepoc.models.Common
import com.example.youtubepoc.models.ResponseCodes
import com.example.youtubepoc.models.YoutubeApi
import com.example.youtubepoc.viewModels.MainActivityViewModel
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel

    private val PREF_ACCOUNT_NAME = "accountName"
    val REQUEST_ACCOUNT_PICKER = 1000
    val REQUEST_AUTHORIZATION = 1001
    val REQUEST_GOOGLE_PLAY_SERVICES = 1002
    val REQUEST_PERMISSION_GET_ACCOUNTS = 1003


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        viewModel.initializeCredential()

        binding.btnSignIn.setOnClickListener {
            Log.d("AGNESH | MainActivity : ", "SIGN IN ATTEMPT")
            lifecycleScope.launch {
                viewModel.getResultsFromApi()
            }
        }

        viewModel.responseStatus.observe(this, Observer {
            when (it.status) {
                ResponseCodes.GOOGLE_PLAY_SERVICES_NOT_AVAILABLE -> {
                    val apiAvailability = GoogleApiAvailability.getInstance()
                    val dialog = apiAvailability.getErrorDialog(
                        this,
                        it.code,
                        REQUEST_GOOGLE_PLAY_SERVICES
                    )
                    dialog!!.show()
                }

                ResponseCodes.HAS_ACCOUNT_PICKER_PERMISSION -> {
//                    this.startActivityForResult(
//                        YoutubeApi.getCredential().newChooseAccountIntent(),
//                        REQUEST_ACCOUNT_PICKER
//                    )
                    resultLauncher.launch(YoutubeApi.getCredential().newChooseAccountIntent())
                }

                ResponseCodes.REQUEST_ACCOUNT_PICKER -> {
                    EasyPermissions.requestPermissions(
                        this,
                        "This app needs to access your Google account (via Contacts).",
                        REQUEST_PERMISSION_GET_ACCOUNTS,
                        android.Manifest.permission.GET_ACCOUNTS
                    )
                }

                ResponseCodes.UserRecoverableAuthIOException -> {
                    Log.w("AGNESH | MainActivity : ", "UserRecoverableAuthIOException occurred !!")
//                    this.startActivityForResult(
//                        viewModel.userRecoverableAuthIOExceptionIntent!!,
//                        REQUEST_AUTHORIZATION
//                    )
                    resultLauncher.launch(viewModel.userRecoverableAuthIOExceptionIntent)
                }

                else -> {}
            }
        })

        viewModel.isUserAuthenticated.observe(this, Observer {
            if (it) {
                Common.showSnackMessage(binding.root, "User signed in successfully !!", false)
                val i: Intent = Intent(this, HomeActivity::class.java)
                i.putExtra("NAME",YoutubeApi.getCredential().selectedAccountName)
                lifecycleScope.launch {
                    delay(1000)
                    startActivity(i)
                }
            }
        })
    }

//    override fun onActivityResult(
//        requestCode: Int, resultCode: Int, data: Intent?
//    ) {
//        super.onActivityResult(requestCode, resultCode, data)
//        when (requestCode) {
//
//            REQUEST_GOOGLE_PLAY_SERVICES -> {
//                Log.d("AGNESH | MainActivity : ", "ONACTIVITYRESULT ${requestCode}")
//                if (resultCode != RESULT_OK) {
//                    Common.showToast(
//                        applicationContext,
//                        "This app requires Google Play Services. Please install " +
//                                "Google Play Services on your device and relaunch this app."
//                    )
//                } else {
//                    lifecycleScope.launch {
//                        viewModel.getResultsFromApi()
//                    }
//                }
//            }
//
//            REQUEST_ACCOUNT_PICKER -> {
//                Log.d("AGNESH | MainActivity : ", "ONACTIVITYRESULT ${requestCode}")
//                if (resultCode == RESULT_OK && data != null && data.extras != null) {
//                    val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
//                    if (accountName != null) {
//                        val settings = getPreferences(MODE_PRIVATE)
//                        val editor = settings.edit()
//                        editor.putString(PREF_ACCOUNT_NAME, accountName)
//                        editor.apply()
//                        YoutubeApi.getCredential().selectedAccountName = accountName
//                        lifecycleScope.launch {
//                            viewModel.getResultsFromApi()
//                        }
//                    }
//                }
//            }
//
//            REQUEST_AUTHORIZATION -> {
//                Log.d("AGNESH | MainActivity : ", "ONACTIVITYRESULT ${requestCode}")
//                if (resultCode == RESULT_OK) {
//                    lifecycleScope.launch {
//                        viewModel.getResultsFromApi()
//                    }
//                }
//            }
//        }
//    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String?>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        EasyPermissions.onRequestPermissionsResult(
//            requestCode, permissions, grantResults, this
//        )
//    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val data: Intent? = result.data
                    Log.d("AGNESH | MainActivity : ", "Activity for result OK ${data?.extras}")
                    val accountName: String =
                        data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME).toString()
                    if (accountName != null) {
                        val settings = getPreferences(MODE_PRIVATE)
                        val editor = settings.edit()
                        editor.putString(PREF_ACCOUNT_NAME, accountName)
                        editor.apply()
                        YoutubeApi.getCredential().setSelectedAccountName(accountName)
                        viewModel.getResultsFromApi()
                    }
                }
            } else {
                Log.d("AGNESH | MainActivity : ", "Activity for result FAILED")
            }
        }
}



