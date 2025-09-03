package com.outsystems.plugins.multibrowser

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.FrameLayout

class WebViewActivity : Activity() {
    companion object {
        private const val TAG = "WebViewActivity"
        const val EXTRA_URL = "extra_url"
        const val EXTRA_SHOW_CLOSE_BUTTON = "extra_show_close_button"
        const val ACTION_CLOSE_WEBVIEW = "com.outsystems.plugins.multibrowser.CLOSE_WEBVIEW"
    }
    
    private lateinit var webView: WebView
    private var showCloseButton: Boolean = true
    private lateinit var closeReceiver: BroadcastReceiver
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get parameters from intent
        val url = intent.getStringExtra(EXTRA_URL) ?: ""
        showCloseButton = intent.getBooleanExtra(EXTRA_SHOW_CLOSE_BUTTON, true)
        
        Log.d(TAG, "Creating WebView Activity for URL: $url, ShowCloseButton: $showCloseButton")
        
        // Create the layout
        val layout = FrameLayout(this)
        
        // Create WebView
        webView = WebView(this)
        setupWebView(webView)
        
        // Add close button if requested
        if (showCloseButton) {
            val closeButton = createCloseButton()
            layout.addView(closeButton, createCloseButtonLayoutParams())
        }
        
        // Add WebView to layout
        layout.addView(webView)
        
        // Set as content view
        setContentView(layout)
        
        // Load the URL
        webView.loadUrl(url)
        
        // Register broadcast receiver for programmatic closing
        setupCloseReceiver()
    }
    
    private fun setupWebView(webView: WebView) {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            loadsImagesAutomatically = true
            blockNetworkImage = false
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d(TAG, "Page started loading: $url")
            }
            
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d(TAG, "Page finished loading: $url")
            }
            
            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                Log.e(TAG, "WebView error: $errorCode - $description for URL: $failingUrl")
            }
            
            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                request?.let {
                    Log.d(TAG, "Intercepting request: ${it.url}")
                }
                return super.shouldInterceptRequest(view, request)
            }
        }
        
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                Log.d(TAG, "Loading progress: $newProgress%")
            }
        }
    }
    
    private fun createCloseButton(): Button {
        return Button(this).apply {
            text = "✕"
            setTextColor(android.graphics.Color.BLACK)
            setBackgroundColor(android.graphics.Color.WHITE)
            setPadding(20, 20, 20, 20)
            
            // Create rounded white background
            val backgroundDrawable = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                cornerRadius = 25f // Rounded corners
                setColor(android.graphics.Color.WHITE)
                setStroke(2, android.graphics.Color.LTGRAY) // Optional: thin gray border
            }
            background = backgroundDrawable
            
            setOnClickListener {
                Log.d(TAG, "Close button clicked")
                finish() // Close this activity, return to previous
            }
        }
    }
    
    private fun createCloseButtonLayoutParams(): FrameLayout.LayoutParams {
        return FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = 50
            leftMargin = 20
        }
    }
    
    override fun onBackPressed() {
        // Handle back button press
        Log.d(TAG, "Back button pressed")
        finish() // Close this activity, return to previous
        super.onBackPressed()
    }
    
    private fun setupCloseReceiver() {
        closeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == ACTION_CLOSE_WEBVIEW) {
                    Log.d(TAG, "Received close command via broadcast")
                    finish() // Close the activity programmatically
                }
            }
        }
        
        // Register the receiver
        val filter = IntentFilter(ACTION_CLOSE_WEBVIEW)
        registerReceiver(closeReceiver, filter)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Unregister the receiver to avoid memory leaks
        try {
            unregisterReceiver(closeReceiver)
        } catch (e: Exception) {
            Log.w(TAG, "Error unregistering receiver", e)
        }
    }
}
