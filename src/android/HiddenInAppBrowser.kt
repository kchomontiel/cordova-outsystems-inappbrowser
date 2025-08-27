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
            
            // Open URL directly using WebView
            try {
                val activity = cordova.activity
                if (activity == null) {
                    sendError(callbackContext, "Activity is not available")
                    return
                }
                
                // Create a WebView and load the URL
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
                
                // Load the URL
                webView.loadUrl(url)
                
                sendSuccess(callbackContext, "URL loaded in WebView successfully")
                
            } catch (e: Exception) {
                sendError(callbackContext, "Error opening WebView: ${e.message}")
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

