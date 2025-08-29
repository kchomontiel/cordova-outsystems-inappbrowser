package com.outsystems.plugins.inappbrowser.osinappbrowser

import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaInterface
import org.apache.cordova.CordovaPlugin
import org.apache.cordova.CordovaWebView
import org.json.JSONArray
import org.json.JSONObject

class HiddenInAppBrowser: CordovaPlugin() {

    override fun initialize(cordova: CordovaInterface, webView: CordovaWebView) {
        super.initialize(cordova, webView)
    }

    override fun execute(
        action: String,
        args: JSONArray,
        callbackContext: CallbackContext
    ): Boolean {
        when(action) {
            "open" -> {
                open(args, callbackContext)
            }
            "openInExternalBrowser" -> {
                openInExternalBrowser(args, callbackContext)
            }
            "openInWebView" -> {
                openInWebView(args, callbackContext)
            }
            else -> {
                return false
            }
        }
        return true
    }

    /**
     * Opens a URL using the original InAppBrowser plugin with hidden=yes
     * @param args JSONArray that contains the parameters (url, options)
     * @param callbackContext CallbackContext the method should return to
     */
    private fun open(args: JSONArray, callbackContext: CallbackContext) {
        try {
            // Debug: Log the received arguments
            android.util.Log.d("HiddenInAppBrowser", "Received args: $args")
            
            val argumentsDictionary = args.getJSONObject(0)
            android.util.Log.d("HiddenInAppBrowser", "Arguments dictionary: $argumentsDictionary")
            
            val url = argumentsDictionary.getString("url")
            android.util.Log.d("HiddenInAppBrowser", "Extracted URL: $url")
            
            if (url.isNullOrEmpty()) {
                sendError(callbackContext, "URL is required")
                return
            }

            // Create options with hidden=yes
            val options = JSONObject().apply {
                put("hidden", "yes")
                put("location", "no")
                put("toolbar", "no")
                put("zoom", "no")
                put("hardwareback", "yes")
                put("mediaPlaybackRequiresUserAction", "no")
                put("shouldPauseOnSuspend", "no")
                put("clearsessioncache", "yes")
                put("cache", "no")
                put("disallowoverscroll", "yes")
                put("hidenavigationbuttons", "yes")
                put("hideurlbar", "yes")
                put("fullscreen", "yes")
            }

            // Create args for the original plugin
            val originalArgs = JSONArray().apply {
                put(url)
                put(options.toString())
            }
            
            // Check if hidden mode is requested
            val isHidden = options.optString("hidden", "no") == "yes"
            
            if (isHidden) {
                // Hidden mode: Load URL in background WebView
                try {
                    val activity = cordova.activity
                    if (activity == null) {
                        sendError(callbackContext, "Activity is not available")
                        return
                    }
                    
                    // Run WebView creation on UI thread
                    activity.runOnUiThread {
                        try {
                            // Create a background WebView (invisible)
                            val webView = android.webkit.WebView(activity)
                            webView.settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                builtInZoomControls = true
                                displayZoomControls = false
                                setSupportZoom(true)
                                mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            }
                            
                            // Set WebView to be invisible
                            webView.alpha = 0f
                            webView.visibility = android.view.View.GONE
                            
                            // Load the URL in background
                            webView.loadUrl(url)
                            
                            sendSuccess(callbackContext, "URL loaded in hidden WebView successfully")
                            
                        } catch (e: Exception) {
                            sendError(callbackContext, "Error loading URL in hidden mode: ${e.message}")
                        }
                    }
                    
                } catch (e: Exception) {
                    sendError(callbackContext, "Error setting up hidden WebView: ${e.message}")
                }
            } else {
                // Visible mode: Open in external browser
                try {
                    val activity = cordova.activity
                    if (activity == null) {
                        sendError(callbackContext, "Activity is not available")
                        return
                    }
                    
                    // Create intent to open URL in external browser
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                    
                    // Check if there's an app to handle this intent
                    if (intent.resolveActivity(activity.packageManager) != null) {
                        activity.startActivity(intent)
                        sendSuccess(callbackContext, "URL opened in external browser successfully")
                    } else {
                        sendError(callbackContext, "No app found to handle this URL")
                    }
                    
                } catch (e: Exception) {
                    sendError(callbackContext, "Error opening URL: ${e.message}")
                }
            }
            
        } catch (e: Exception) {
            sendError(callbackContext, "Error opening InAppBrowser: ${e.message}")
        }
    }

    private fun sendSuccess(callbackContext: CallbackContext, message: String) {
        val result = org.apache.cordova.PluginResult(org.apache.cordova.PluginResult.Status.OK, message)
        callbackContext.sendPluginResult(result)
    }

    private fun sendError(callbackContext: CallbackContext, message: String) {
        val result = org.apache.cordova.PluginResult(org.apache.cordova.PluginResult.Status.ERROR, message)
        callbackContext.sendPluginResult(result)
    }

    /**
     * Opens a URL in external browser using the original InAppBrowser plugin
     * @param args JSONArray that contains the parameters (url, options)
     * @param callbackContext CallbackContext the method should return to
     */
    private fun openInExternalBrowser(args: JSONArray, callbackContext: CallbackContext) {
        try {
            android.util.Log.d("HiddenInAppBrowser", "🔍 openInExternalBrowser - ===== INICIO DEL MÉTODO =====")
            android.util.Log.d("HiddenInAppBrowser", "openInExternalBrowser - Received args: $args")
            
            val argumentsDictionary = args.getJSONObject(0)
            android.util.Log.d("HiddenInAppBrowser", "openInExternalBrowser - Arguments dictionary: $argumentsDictionary")
            
            val url = argumentsDictionary.getString("url")
            android.util.Log.d("HiddenInAppBrowser", "openInExternalBrowser - Extracted URL: $url")
            
            if (url.isNullOrEmpty()) {
                android.util.Log.e("HiddenInAppBrowser", "openInExternalBrowser - URL is empty or null")
                sendError(callbackContext, "URL is required")
                return
            }

            // Get options from arguments if provided
            val options = argumentsDictionary.optJSONObject("options")
            android.util.Log.d("HiddenInAppBrowser", "openInExternalBrowser - Options received: $options")

            // Create options for external browser (similar to Apache InAppBrowser)
            val externalBrowserOptions = JSONObject().apply {
                put("hidden", "no")
                put("location", "yes")
                put("toolbar", "yes")
                put("zoom", "yes")
                put("hardwareback", "yes")
                put("mediaPlaybackRequiresUserAction", "no")
                put("shouldPauseOnSuspend", "no")
                put("clearsessioncache", "no")
                put("cache", "yes")
                put("disallowoverscroll", "no")
                put("hidenavigationbuttons", "no")
                put("hideurlbar", "no")
                put("fullscreen", "no")
            }

            // Merge with provided options if any
            if (options != null) {
                android.util.Log.d("HiddenInAppBrowser", "openInExternalBrowser - Merging with provided options")
                val iterator = options.keys()
                while (iterator.hasNext()) {
                    val key = iterator.next()
                    val value = options.get(key)
                    externalBrowserOptions.put(key, value)
                    android.util.Log.d("HiddenInAppBrowser", "openInExternalBrowser - Added option: $key = $value")
                }
            }
            
            android.util.Log.d("HiddenInAppBrowser", "openInExternalBrowser - Final options: $externalBrowserOptions")
            
            // Use the original InAppBrowser plugin to open in external browser
            try {
                val activity = cordova.activity
                if (activity == null) {
                    android.util.Log.e("HiddenInAppBrowser", "openInExternalBrowser - Activity is null")
                    sendError(callbackContext, "Activity is not available")
                    return
                }
                
                android.util.Log.d("HiddenInAppBrowser", "openInExternalBrowser - Activity is available, creating intent")
                
                // Create intent to open URL in external browser
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                android.util.Log.d("HiddenInAppBrowser", "openInExternalBrowser - Created intent: $intent")
                
                // Check if there's an app to handle this intent
                val resolveActivity = intent.resolveActivity(activity.packageManager)
                android.util.Log.d("HiddenInAppBrowser", "openInExternalBrowser - Resolve activity: $resolveActivity")
                
                if (resolveActivity != null) {
                    android.util.Log.d("HiddenInAppBrowser", "openInExternalBrowser - Starting activity")
                    activity.startActivity(intent)
                    android.util.Log.d("HiddenInAppBrowser", "openInExternalBrowser - Activity started successfully")
                    sendSuccess(callbackContext, "URL opened in external browser successfully")
                } else {
                    android.util.Log.e("HiddenInAppBrowser", "openInExternalBrowser - No app found to handle URL")
                    sendError(callbackContext, "No app found to handle this URL")
                }
                
            } catch (e: Exception) {
                android.util.Log.e("HiddenInAppBrowser", "openInExternalBrowser - Error opening URL: ${e.message}", e)
                sendError(callbackContext, "Error opening URL in external browser: ${e.message}")
            }
            
        } catch (e: Exception) {
            android.util.Log.e("HiddenInAppBrowser", "openInExternalBrowser - Error in method: ${e.message}", e)
            sendError(callbackContext, "Error opening external browser: ${e.message}")
        }
    }

    /**
     * Opens a URL in WebView using the original InAppBrowser plugin
     * @param args JSONArray that contains the parameters (url, options)
     * @param callbackContext CallbackContext the method should return to
     */
    private fun openInWebView(args: JSONArray, callbackContext: CallbackContext) {
        try {
            android.util.Log.d("HiddenInAppBrowser", "🔍 openInWebView - ===== INICIO DEL MÉTODO =====")
            android.util.Log.d("HiddenInAppBrowser", "openInWebView - Received args: $args")
            
            val argumentsDictionary = args.getJSONObject(0)
            android.util.Log.d("HiddenInAppBrowser", "openInWebView - Arguments dictionary: $argumentsDictionary")
            
            val url = argumentsDictionary.getString("url")
            android.util.Log.d("HiddenInAppBrowser", "openInWebView - Extracted URL: $url")
            
            if (url.isNullOrEmpty()) {
                android.util.Log.e("HiddenInAppBrowser", "openInWebView - URL is empty or null")
                sendError(callbackContext, "URL is required")
                return
            }

            // Get options from arguments if provided
            val options = argumentsDictionary.optJSONObject("options")
            android.util.Log.d("HiddenInAppBrowser", "openInWebView - Options received: $options")

            // Create options for WebView (similar to Apache InAppBrowser)
            val webViewOptions = JSONObject().apply {
                put("hidden", "no")
                put("location", "yes")
                put("toolbar", "yes")
                put("zoom", "yes")
                put("hardwareback", "yes")
                put("mediaPlaybackRequiresUserAction", "no")
                put("shouldPauseOnSuspend", "no")
                put("clearsessioncache", "no")
                put("cache", "yes")
                put("disallowoverscroll", "no")
                put("hidenavigationbuttons", "no")
                put("hideurlbar", "no")
                put("fullscreen", "no")
            }

            // Merge with provided options if any
            if (options != null) {
                android.util.Log.d("HiddenInAppBrowser", "openInWebView - Merging with provided options")
                val iterator = options.keys()
                while (iterator.hasNext()) {
                    val key = iterator.next()
                    val value = options.get(key)
                    webViewOptions.put(key, value)
                    android.util.Log.d("HiddenInAppBrowser", "openInWebView - Added option: $key = $value")
                }
            }
            
            android.util.Log.d("HiddenInAppBrowser", "openInWebView - Final options: $webViewOptions")
            
            // Use the original InAppBrowser plugin to open in WebView
            try {
                val activity = cordova.activity
                if (activity == null) {
                    android.util.Log.e("HiddenInAppBrowser", "openInWebView - Activity is null")
                    sendError(callbackContext, "Activity is not available")
                    return
                }
                
                android.util.Log.d("HiddenInAppBrowser", "openInWebView - Activity is available, creating WebView")
                
                // Run WebView creation on UI thread
                activity.runOnUiThread {
                    try {
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - Creating WebView on UI thread")
                        
                        // Create a WebView with proper configuration for visible mode
                        val webView = android.webkit.WebView(activity)
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - WebView created")
                        
                        webView.settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            builtInZoomControls = true
                            displayZoomControls = false
                            setSupportZoom(true)
                            mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            // Enable location bar and toolbar for visible mode
                            setSupportMultipleWindows(true)
                        }
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - WebView settings configured")
                        
                        // Create a custom WebViewClient to handle navigation
                        webView.webViewClient = object : android.webkit.WebViewClient() {
                            override fun shouldOverrideUrlLoading(view: android.webkit.WebView?, url: String?): Boolean {
                                android.util.Log.d("HiddenInAppBrowser", "openInWebView - shouldOverrideUrlLoading: $url")
                                url?.let { view?.loadUrl(it) }
                                return true
                            }
                            
                            override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                android.util.Log.d("HiddenInAppBrowser", "openInWebView - onPageFinished: $url")
                                sendSuccess(callbackContext, "URL loaded in WebView successfully")
                            }
                            
                            override fun onReceivedError(view: android.webkit.WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                                super.onReceivedError(view, errorCode, description, failingUrl)
                                android.util.Log.e("HiddenInAppBrowser", "openInWebView - onReceivedError: $description")
                                sendError(callbackContext, "Error loading URL: $description")
                            }
                        }
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - WebViewClient configured")
                        
                        // Create a custom WebChromeClient to handle dialogs and progress
                        webView.webChromeClient = object : android.webkit.WebChromeClient() {
                            override fun onProgressChanged(view: android.webkit.WebView?, newProgress: Int) {
                                super.onProgressChanged(view, newProgress)
                                android.util.Log.d("HiddenInAppBrowser", "openInWebView - Progress: $newProgress%")
                            }
                        }
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - WebChromeClient configured")
                        
                        // Create a layout for the WebView
                        val layout = android.widget.LinearLayout(activity).apply {
                            orientation = android.widget.LinearLayout.VERTICAL
                            layoutParams = android.view.ViewGroup.LayoutParams(
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            setBackgroundColor(android.graphics.Color.WHITE)
                        }
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - Layout created")
                        
                        // Create a dialog to show the WebView
                        val dialog = android.app.AlertDialog.Builder(activity)
                            .setView(layout)
                            .setCancelable(false)
                            .create()
                        
                        // Set dialog to full screen
                        dialog.window?.apply {
                            setLayout(
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.WHITE))
                        }
                        
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - Dialog created")
                        
                        // Add a toolbar with close button
                        val toolbar = android.widget.LinearLayout(activity).apply {
                            orientation = android.widget.LinearLayout.HORIZONTAL
                            setBackgroundColor(android.graphics.Color.parseColor("#F5F5F5"))
                            layoutParams = android.widget.LinearLayout.LayoutParams(
                                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                                android.util.TypedValue.applyDimension(
                                    android.util.TypedValue.COMPLEX_UNIT_DIP,
                                    56f,
                                    resources.displayMetrics
                                ).toInt()
                            )
                        }
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - Toolbar created")
                        
                        val closeButton = android.widget.Button(activity).apply {
                            text = "Close"
                            setOnClickListener {
                                android.util.Log.d("HiddenInAppBrowser", "openInWebView - Close button clicked")
                                // Close the dialog
                                dialog.dismiss()
                                sendSuccess(callbackContext, "WebView closed")
                            }
                            layoutParams = android.widget.LinearLayout.LayoutParams(
                                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                gravity = android.view.Gravity.CENTER_VERTICAL
                                marginStart = 16
                            }
                        }
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - Close button created")
                        
                        toolbar.addView(closeButton)
                        layout.addView(toolbar)
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - Toolbar added to layout")
                        
                        // Add the WebView to the layout
                        webView.layoutParams = android.widget.LinearLayout.LayoutParams(
                            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                            android.widget.LinearLayout.LayoutParams.MATCH_PARENT
                        )
                        layout.addView(webView)
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - WebView added to layout")
                        
                        // Show the dialog
                        dialog.show()
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - Dialog shown")
                        
                        // Load the URL
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - Loading URL: $url")
                        webView.loadUrl(url)
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - URL load initiated")
                        
                    } catch (e: Exception) {
                        android.util.Log.e("HiddenInAppBrowser", "openInWebView - Error creating WebView: ${e.message}", e)
                        sendError(callbackContext, "Error opening WebView: ${e.message}")
                    }
                }
                
            } catch (e: Exception) {
                android.util.Log.e("HiddenInAppBrowser", "openInWebView - Error setting up WebView: ${e.message}", e)
                sendError(callbackContext, "Error setting up WebView: ${e.message}")
            }
            
        } catch (e: Exception) {
            android.util.Log.e("HiddenInAppBrowser", "openInWebView - Error in method: ${e.message}", e)
            sendError(callbackContext, "Error opening WebView: ${e.message}")
        }
    }
}

