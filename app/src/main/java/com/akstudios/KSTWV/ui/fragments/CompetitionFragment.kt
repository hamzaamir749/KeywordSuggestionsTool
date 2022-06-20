package com.akstudios.KSTWV.ui.fragments

import android.Manifest
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.akstudios.KSTWV.databinding.FragmentCompetitionBinding
import com.akstudios.KSTWV.ui.activities.MainActivity
import com.akstudios.KSTWV.utils.GlobalVariables
import com.akstudios.KSTWV.utils.createBannerAd
import com.akstudios.KSTWV.utils.openBrowser
import com.applovin.sdk.AppLovinSdk
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompetitionFragment : Fragment() {


    lateinit var binding: FragmentCompetitionBinding


    private val STORAGE_PERMISSION_CODE = 1

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            AlertDialog.Builder(requireContext())
                .setTitle("Permission needed")
                .setMessage("This permission is needed to download files")
                .setPositiveButton(
                    "ok"
                ) { dialog: DialogInterface?, which: Int ->
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        STORAGE_PERMISSION_CODE
                    )
                }
                .setNegativeButton(
                    "cancel"
                ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
                .create().show()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompetitionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        requestStoragePermission()
    }

    private fun clickListeners() {

    }

    private fun initViews() {
        val webSettings = binding.activityMainWebview.settings
        webSettings.javaScriptEnabled = true
        binding.activityMainWebview.webViewClient = HelloWebViewClient()
        binding.activityMainWebview.webChromeClient = MyWebChromeClient()
        binding.activityMainWebview.setDownloadListener { url: String?, userAgent: String?, contentDisposition: String?, mimeType: String?, contentLength: Long ->
            val source = Uri.parse(url)
            val request = DownloadManager.Request(source)
            val cookies = CookieManager.getInstance().getCookie(url)
            request.addRequestHeader("cookie", cookies)
            request.addRequestHeader("User-Agent", userAgent)
            request.setDescription("Downloading File...")
            request.setTitle(
                URLUtil.guessFileName(
                    url,
                    contentDisposition,
                    mimeType
                )
            )
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                URLUtil.guessFileName(url, contentDisposition, mimeType)
            )
            val dm =
                requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
            Toast.makeText(requireContext(), "Downloading File", Toast.LENGTH_LONG).show()
        }
        binding.activityMainWebview.loadUrl(GlobalVariables.COMPETITION_WEBSITE) //Replace The Link Here
        loadApplovinSdk()
    }

    private fun loadApplovinSdk() {
        AppLovinSdk.getInstance(requireContext()).mediationProvider = "max"
        AppLovinSdk.getInstance(requireContext()).initializeSdk {
            requireContext().createBannerAd(binding.bannerAdLayout)
        }
    }

    inner class HelloWebViewClient : WebViewClient() {
        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            var isReturn: Boolean = true
            isReturn = if (url.contains("seostudios.xyz")) {
                false
            } else {
                (requireActivity() as MainActivity).loadAd(true) {
                    requireContext().openBrowser(url)
                }
                true
            }
            return isReturn
        }
    }

    private inner class MyWebChromeClient : WebChromeClient() {

        override fun onProgressChanged(view: WebView, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            setProgress(newProgress)
        }

    }

    private fun setProgress(newProgress: Int) {
        if (newProgress == 100) {
            binding.progress.visibility = View.GONE
        } else {
            binding.progress.progress = newProgress
        }
    }
}