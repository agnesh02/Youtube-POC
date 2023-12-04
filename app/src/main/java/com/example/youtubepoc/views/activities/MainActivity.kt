package com.example.youtubepoc.views.activities

import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
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
import com.maxrave.kotlinyoutubeextractor.State
import com.maxrave.kotlinyoutubeextractor.VideoMeta
import com.maxrave.kotlinyoutubeextractor.YTExtractor
import com.maxrave.kotlinyoutubeextractor.YtFile
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
//            lifecycleScope.launch {
//                viewModel.getResultsFromApi()
//            }


//            lifecycleScope.launch(Dispatchers.IO) {
//                var conn: HttpURLConnection? = null
//                val contents = StringBuilder()
//                try {
//                    conn =
//                        URL("https://www.youtube.com/watch?v=GN50IHHUc4U").openConnection() as HttpURLConnection
//                    conn!!.connectTimeout = 1000
//                    conn!!.readTimeout = 2000
//                    val `is` = conn!!.inputStream
//                    var enc = conn!!.contentEncoding
//                    if (enc == null) {
//                        val p: Pattern = Pattern.compile("charset=(.*)")
//                        val m: Matcher = p.matcher(conn!!.getHeaderField("Content-Type"))
//                        if (m.find()) {
//                            enc = m.group(1)
//                        }
//                    }
//                    if (enc == null) enc = "UTF-8"
//                    val br = BufferedReader(InputStreamReader(`is`, enc))
//                    var line: String? = null
//                    while (br.readLine().also { line = it } != null) {
//                        contents.append(line)
//                        contents.append("\n")
//                    }
//                } catch (e: IOException) {
//                    Log.d("AGNESH", e.toString())
//                }
//                Log.d("Agnesh", contents.toString())
//                val html = contents.toString()
//                val urlList = ArrayList<String>();
//                val urlencod: Pattern =
//                    Pattern.compile("\"url_encoded_fmt_stream_map\":\"([^\"]*)\"");
//                val urlencodMatch: Matcher = urlencod.matcher(html);
//                if (urlencodMatch.find()) {
//                    val url_encoded_fmt_stream_map: String
//                    url_encoded_fmt_stream_map = urlencodMatch.group(1)
//                    val encod = Pattern.compile("url=(.*)")
//                    val encodMatch = encod.matcher(url_encoded_fmt_stream_map)
//                    if (encodMatch.find()) {
//                        val sline = encodMatch.group(1)
//                        val urlStrings =
//                            sline.split("url=".toRegex()).dropLastWhile { it.isEmpty() }
//                                .toTypedArray()
//                        for (urlString in urlStrings) {
//                            var url: String? = null
//                            var urlString2 = ""
//                            urlString2 = StringEscapeUtils.unescapeJava(urlString)
//                            val link = Pattern.compile("([^&,]*)[&,]")
//                            val linkMatch = link.matcher(urlString2)
//                            if (linkMatch.find()) {
//                                url = linkMatch.group(1)
//                                url = URLDecoder.decode(url, "UTF8")
//                            }
//                            urlList.add(url!!)
//                        }
//                    }
//                    Log.d("URL LIST",urlList.toString())
//                }
//            }

//            lifecycleScope.launch (Dispatchers.IO){
//                try {
//                    val downloadsDir =
//                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                    val youtubeDLDir = File(downloadsDir, "youtubeTESST-android")
//                    if (!youtubeDLDir.exists()) {
//                        youtubeDLDir.mkdir()
//                        System.out.println("creating directory")
//                    }else{
//                        System.out.println("directory already exists")
//                    }
//                    val stop = AtomicBoolean(false)
//                    val web = URL("https://www.youtube.com/watch?v=pDftNJWcvuA")
//
//                    // [OPTIONAL] limit maximum quality, or do not call this function if
//                    // you wish maximum quality available.
//                    //
//                    // if youtube does not have video with requested quality, program
//                    // will raise en exception.
//                    var user: VGetParser? = null
//
//                    // create proper html parser depends on url
//                    user = VGet.parser(web)
//
//                    // download limited video quality from youtube
//                    // user = new YouTubeQParser(YoutubeQuality.p480);
//
//                    // download mp4 format only, fail if non exist
//                    // user = new YouTubeMPGParser();
//
//                    // create proper videoinfo to keep specific video information
//                    val videoinfo: VideoInfo = user.info(web)
//                    val v = VGet(videoinfo, youtubeDLDir)
//                    val notify = AppManagedDownload.VGetStatus(videoinfo)
//
//                    // [OPTIONAL] call v.extract() only if you d like to get video title
//                    // or download url link before start download. or just skip it.
//                    v.extract(user, stop, notify)
//                    System.out.println("Title: " + videoinfo.getTitle())
////                    val list: List<VideoFileInfo> = videoinfo.getInfo()
////                    if (list != null) {
////                        for (d in list) {
////                            // [OPTIONAL] setTarget file for each download source video/audio
////                            // use d.getContentType() to determine which or use
////                            // v.targetFile(dinfo, ext, conflict) to set name dynamically or
////                            // d.targetFile = new File("/Downloads/CustomName.mp3");
////                            // to set file name manually.
////                            println("Download URL: " + d.source)
////                        }
////                    }
//                    v.download(user, stop, notify)
//                } catch (e: DownloadInterruptedError) {
//                    throw e
//                } catch (e: RuntimeException) {
//                    throw e
//                } catch (e: Exception) {
//                    throw RuntimeException(e)
//                }
//            }
            val yt = YTExtractor(con = this, CACHING = true, LOGGING = true, retryCount = 3)

            lifecycleScope.launch(Dispatchers.IO) {
                var ytFiles: SparseArray<YtFile>? = null
                var videoMeta: VideoMeta? = null
                yt.extract("pDftNJWcvuA")
                //Before get YtFile or VideoMeta, you need to check state of yt object
                if (yt.state == State.SUCCESS) {
                    ytFiles = yt.getYTFiles()
                    videoMeta = yt.getVideoMeta()
                    Log.d("TEST",ytFiles.toString())
                }
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
                        android.Manifest.permission.READ_MEDIA_VIDEO
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
                i.putExtra("NAME", YoutubeApi.getCredential().selectedAccountName)
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





