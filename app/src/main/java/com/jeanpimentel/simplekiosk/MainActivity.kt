package com.jeanpimentel.simplekiosk

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        configWebview()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configWebview() {

        webView.webViewClient = WebViewClient()

        webView.settings.javaScriptEnabled = true
        webView.settings.setSupportZoom(false)

        webView.loadUrl("http://google.com")
    }

}
