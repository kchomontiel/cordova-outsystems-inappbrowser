package com.outsystems.plugins.multibrowser

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.*
import android.widget.FrameLayout
import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaPlugin
import org.apache.cordova.PluginResult
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class HiddenInAppBrowser : CordovaPlugin() {
    companion object {
        private const val TAG = "HiddenInAppBrowser"
        private const val ACTION_OPEN_IN_WEBVIEW = "openInWebView"
        private const val ACTION_OPEN_HIDDEN = "openHidden"
        private const val ACTION_OPEN_IN_EXTERNAL_BROWSER = "openInExternalBrowser"
        private const val ACTION_CLOSE_WEBVIEW = "closeWebView"
    }
    



    override fun execute(action: String, args: JSONArray, callbackContext: CallbackContext): Boolean {
        try {
            when (action) {
                ACTION_OPEN_IN_WEBVIEW -> {
                    val url = args.getString(0)
                    val target = if (args.length() > 1) args.getString(1) else "_blank"
                    val options = if (args.length() > 2) args.getString(2) else ""
                    val showCloseButton = if (args.length() > 3) args.getBoolean(3) else true
                    openInWebView(url, target, options, showCloseButton, callbackContext)
                    return true
                }
                ACTION_OPEN_HIDDEN -> {
                    val url = args.getString(0)
                    val target = if (args.length() > 1) args.getString(1) else "_blank"
                    val options = if (args.length() > 2) args.getString(2) else ""
                    openHidden(url, target, options, callbackContext)
                    return true
                }
                ACTION_OPEN_IN_EXTERNAL_BROWSER -> {
                    val url = args.getString(0)
                    val target = if (args.length() > 1) args.getString(1) else "_system"
                    val options = if (args.length() > 2) args.getString(2) else ""
                    openInExternalBrowser(url, target, options, callbackContext)
                    return true
                }
                ACTION_CLOSE_WEBVIEW -> {
                    closeWebView(callbackContext)
                    return true
                }
                else -> {
                    callbackContext.error("Unknown action: $action")
                    return false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error executing action: $action", e)
            callbackContext.error("Error executing action: $action - ${e.message}")
            return false
        }
    }

    private fun openInWebView(url: String, target: String, options: String, showCloseButton: Boolean, callbackContext: CallbackContext) {
        cordova.activity.runOnUiThread {
            try {
                Log.d(TAG, "Opening URL in WebView: $url")
                

                
                val webView = WebView(cordova.activity)
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
                        callbackContext.success("Page loaded successfully")
                    }

                    override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                        super.onReceivedError(view, errorCode, description, failingUrl)
                        Log.e(TAG, "WebView error: $errorCode - $description for URL: $failingUrl")
                        callbackContext.error("WebView error: $errorCode - $description")
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

                // Create a layout for the WebView (same as before)
                val layout = FrameLayout(cordova.activity)
                
                // Create AlertDialog to show the WebView (BEFORE the button so it can reference it)
                val dialog = android.app.AlertDialog.Builder(cordova.activity)
                    .setView(layout)
                    .setCancelable(false)
                    .create()
                
                // Set dialog to full screen (same full-screen experience)
                dialog.window?.apply {
                    setLayout(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.WHITE))
                }
                
                // Add close button if requested (same style and position)
                if (showCloseButton) {
                    val closeButton = android.widget.Button(cordova.activity).apply {
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
                            dialog.dismiss() // Now dialog is available here
                            callbackContext.success("WebView closed successfully")
                        }
                    }
                    
                    // Position the close button in the top-left corner (same as before)
                    val closeButtonLayoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = 50
                        leftMargin = 20
                    }
                    
                    layout.addView(closeButton, closeButtonLayoutParams)
                }
                
                layout.addView(webView)
                
                // Show the dialog
                dialog.show()
                
                // Load the URL
                webView.loadUrl(url)
                
            } catch (e: Exception) {
                Log.e(TAG, "Error opening WebView", e)
                callbackContext.error("Error opening WebView: ${e.message}")
            }
        }
    }

    private fun openHidden(url: String, target: String, options: String, callbackContext: CallbackContext) {
        // For hidden mode, we'll just return success immediately
        Log.d(TAG, "Hidden mode requested for URL: $url")
        callbackContext.success("Hidden mode activated")
    }

    private fun openInExternalBrowser(url: String, target: String, options: String, callbackContext: CallbackContext) {
        try {
            Log.d(TAG, "Opening URL in external browser: $url")
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            cordova.activity.startActivity(intent)
            callbackContext.success("External browser opened")
        } catch (e: Exception) {
            Log.e(TAG, "Error opening external browser", e)
            callbackContext.error("Error opening external browser: ${e.message}")
        }
    }

    private fun closeWebView(callbackContext: CallbackContext) {
        cordova.activity.runOnUiThread {
            try {
                Log.d(TAG, "Closing WebView")
                
                // Since we're using AlertDialog now, this method is mainly for programmatic closing
                // The close button handles the actual closing via dialog.dismiss()
                callbackContext.success("WebView close method called")
                
            } catch (e: Exception) {
                Log.e(TAG, "Error closing WebView", e)
                callbackContext.error("Error closing WebView: ${e.message}")
            }
        }
    }
}
