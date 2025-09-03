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
        try {
            Log.d(TAG, "Opening URL in WebView: $url")
            
            // Create intent to launch WebViewActivity
            val intent = android.content.Intent(cordova.activity, com.outsystems.plugins.multibrowser.WebViewActivity::class.java).apply {
                putExtra(com.outsystems.plugins.multibrowser.WebViewActivity.EXTRA_URL, url)
                putExtra(com.outsystems.plugins.multibrowser.WebViewActivity.EXTRA_SHOW_CLOSE_BUTTON, showCloseButton)
            }
            
            // Launch the WebViewActivity
            cordova.activity.startActivity(intent)
            
            // Send success callback immediately
            callbackContext.success("WebView opened successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error opening WebView", e)
            callbackContext.error("Error opening WebView: ${e.message}")
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
        try {
            Log.d(TAG, "Closing WebView programmatically")
            
            // Send broadcast to close WebViewActivity
            val closeIntent = android.content.Intent(com.outsystems.plugins.multibrowser.WebViewActivity.ACTION_CLOSE_WEBVIEW)
            cordova.activity.sendBroadcast(closeIntent)
            
            callbackContext.success("WebView close command sent")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error closing WebView", e)
            callbackContext.error("Error closing WebView: ${e.message}")
        }
    }
}
