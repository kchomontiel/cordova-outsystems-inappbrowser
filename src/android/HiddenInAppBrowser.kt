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
}

