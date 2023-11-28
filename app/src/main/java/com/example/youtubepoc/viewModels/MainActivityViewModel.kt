package com.example.youtubepoc.viewModels

import android.app.Application
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.youtubepoc.models.Common
import com.example.youtubepoc.models.ResponseCodes
import com.example.youtubepoc.models.YoutubeApi
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.YouTubeScopes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    data class ResponseContainer(val status: ResponseCodes, val code: Int)

    private lateinit var credential: GoogleAccountCredential
    private val SCOPES = arrayOf(YouTubeScopes.YOUTUBE_READONLY)


    val responseStatus = MutableLiveData<ResponseContainer>()
    var userRecoverableAuthIOExceptionIntent: Intent? = null

    val isUserAuthenticated = MutableLiveData(false)

    fun initializeCredential() {
        credential = GoogleAccountCredential.usingOAuth2(
            getApplication(), listOf(*SCOPES)
        )
        YoutubeApi.setCredential(credential)
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(getApplication())
        return connectionStatusCode == ConnectionResult.SUCCESS
    }

    fun getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices()
        } else if (credential.selectedAccountName == null) {
            chooseAccount()
        } else if (!isDeviceOnline()) {
            Log.d("AGNESH | MainActivityViewModel : ", "Network connection unavailable")
        } else {
            getDataFromApi(credential)
        }
    }

    private fun acquireGooglePlayServices() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(getApplication())
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            responseStatus.value =
                ResponseContainer(
                    ResponseCodes.GOOGLE_PLAY_SERVICES_NOT_AVAILABLE,
                    connectionStatusCode
                )
        }
    }

    private fun chooseAccount() {
        if (EasyPermissions.hasPermissions(
                getApplication(), android.Manifest.permission.GET_ACCOUNTS
            )
        ) {
            responseStatus.value =
                ResponseContainer(ResponseCodes.HAS_ACCOUNT_PICKER_PERMISSION, -1)

        } else {
            responseStatus.value = ResponseContainer(ResponseCodes.REQUEST_ACCOUNT_PICKER, -1)
        }
    }

    private fun isDeviceOnline(): Boolean {
        val connectionManager =
            getApplication<Application>().getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectionManager.activeNetwork
        val capabilities: NetworkCapabilities? =
            connectionManager.getNetworkCapabilities(network)
        var isAvailable = false

        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                isAvailable = true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                isAvailable = true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                isAvailable = true
            }
        }
        return network != null && isAvailable
    }

    private fun getDataFromApi(credential: GoogleAccountCredential) {

        val credential: GoogleAccountCredential = credential

        val transport = AndroidHttp.newCompatibleTransport()
        val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()

        // Might need this
//        val mService = YouTube.Builder(
//            transport, jsonFactory, credential
//        )
//            .setApplicationName("YoutubePOC")
//            .build()
//
//        val mService = YouTube.Builder(
//            transport, jsonFactory
//        ) { request ->
//            //initialize method helps to add any extra details that may be required to process the query
//            //setting package name and sha1 certificate to identify request by server
//            request.headers["X-Android-Package"] = "com.example.youtubepoc"
//            request.headers["X-Android-Cert"] =
//                "E7:FA:DD:B8:9A:1B:C0:F9:4F:0A:36:55:E6:01:41:60:4A:AE:6E:5A"
//
//        }.setApplicationName("YoutubePOC").build();

        val service = YouTube.Builder(transport, jsonFactory, credential)
            .setApplicationName("YoutubePOC").build();

        YoutubeApi.setCredential(credential)
        YoutubeApi.setYoutubeService(service)

        viewModelScope.launch(Dispatchers.IO) {

            try {

                Log.d(
                    "AGNESH | MainActivityViewModel",
                    "FETCHING DATA FROM API ... email: ${credential.selectedAccount.name} token: ${credential.token}"
                )

                CoroutineScope(Dispatchers.Main).launch {
                    isUserAuthenticated.value = true
                }

            } catch (e: Exception) {
                if (e is UserRecoverableAuthException) {
                    userRecoverableAuthIOExceptionIntent = e.intent
                    CoroutineScope(Dispatchers.Main).launch {
                        responseStatus.value =
                            ResponseContainer(ResponseCodes.UserRecoverableAuthIOException, -1)
                    }
                } else if (e is UserRecoverableAuthIOException) {
                    userRecoverableAuthIOExceptionIntent = e.intent
                    CoroutineScope(Dispatchers.Main).launch {
                        responseStatus.value =
                            ResponseContainer(ResponseCodes.UserRecoverableAuthIOException, -1)
                    }
                } else {
                    Log.d("AGNESH | MainActivityViewModel | getDataFromApi : ", e.toString())
                }
            }
        }
    }
}