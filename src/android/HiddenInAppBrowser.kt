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
            val argumentsDictionary = args.getJSONObject(0)
            val url = argumentsDictionary.getString("url")
            
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

            // Use the original InAppBrowser plugin directly
            val inAppBrowser = org.apache.cordova.inappbrowser.InAppBrowser()
            inAppBrowser.initialize(cordova, webView)
            
            // Create args for the original plugin
            val originalArgs = JSONArray().apply {
                put(url)
                put(options.toString())
            }
            
            // Call the original open method
            val result = inAppBrowser.execute("open", originalArgs, callbackContext)
            
            if (result) {
                sendSuccess(callbackContext, "InAppBrowser opened successfully")
            } else {
                sendError(callbackContext, "Failed to open InAppBrowser")
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
