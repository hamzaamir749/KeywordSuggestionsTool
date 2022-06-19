package com.akstudios.KSTWV.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.webkit.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.akstudios.KSTWV.R


@SuppressLint("SetJavaScriptEnabled")
class MyWebView constructor(context: Context, attributeset: AttributeSet) :
    ConstraintLayout(context, attributeset) {

    private var progress: ProgressBar
    private var webview: WebView
    private var isLastLoadSuccess = false
    private var isError = false
    private var loadingDialog: LinearLayout

    init {
        val rootView =
            LayoutInflater.from(context).inflate(R.layout.layout_web_progress_view, this, true)
        progress = rootView.findViewById(R.id.progress)
        loadingDialog = rootView.findViewById(R.id.loadingDialog)
        webview = rootView.findViewById(R.id.my_web_view)
        webview.webChromeClient = MyWebChromeClient()
        webview.webViewClient = MyWebViewClient()
        webview.settings.javaScriptEnabled = true
        webview.settings.loadWithOverviewMode = true
        webview.settings.useWideViewPort = true
        webview.setInitialScale(1)
        webview.settings.cacheMode = WebSettings.LOAD_NO_CACHE
    }

    private inner class MyWebChromeClient : WebChromeClient() {

        override fun onProgressChanged(view: WebView, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            setProgress(newProgress)
        }

        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)
            if (title.contains("html")) {
                return
            }
            listener?.onTitle(title)
        }
    }

    private fun setProgress(newProgress: Int) {
        if (newProgress == 100) {
            webview.visibility = View.VISIBLE
            loadingDialog.visibility = View.GONE
        } else {
            progress.progress = newProgress
        }
    }

    private var i: Int = 0

    private inner class MyWebViewClient : WebViewClient() {

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)

            /*    if (!isError) {
                    isLastLoadSuccess = true
                    view.evaluateJavascript("document.getElementsByClassName('freebirdFormviewerViewResponseConfirmContentContainer').length > 0") {
                        listener?.onFormSubmitted()
                    }
                    listener?.success()
                }*/
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError
        ) {
            super.onReceivedError(view, request, error)

            isError = true
            if (!isLastLoadSuccess) {
                listener?.error()
            }
        }
    }


    @SuppressLint("JavascriptInterface")
    fun addJavascriptInterface(jsInterface: Any) {
        webview.addJavascriptInterface(jsInterface, "SSDJsBirdge")
    }

    fun reload() {
        isError = false
        webview.reload()
    }

    fun loadUrl(url: String) {
        isError = false
        try {
            webview.loadUrl(url)
        } catch (e: Exception) {

        }

    }

    fun canGoBack(): Boolean {
        val canGoBack = webview.canGoBack()
        if (canGoBack) {
            webview.goBack()
        }
        return canGoBack
    }

    /**
     * must be called on the main thread
     */
    fun destory() {
        try {
            webview.destroy()
        } catch (e: Exception) {
        }

    }

    private var listener: OnWebLoadStatusListener? = null

    fun setOnLoadStatueListener(listener: OnWebLoadStatusListener) {
        this.listener = listener
    }

    interface OnWebLoadStatusListener {
        fun error()

        fun success()

        fun onTitle(title: String)
        fun onFormSubmitted()
    }
}