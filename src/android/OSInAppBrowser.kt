package com.outsystems.plugins.inappbrowser.osinappbrowser

import com.google.gson.Gson
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.OSIABEngine
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.models.OSIABAnimation
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.models.OSIABCustomTabsOptions
import androidx.lifecycle.lifecycleScope
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.OSIABClosable
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.OSIABRouter
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.helpers.OSIABFlowHelper
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.models.OSIABToolbarPosition
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.models.OSIABViewStyle
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.models.OSIABWebViewOptions
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.routeradapters.OSIABCustomTabsRouterAdapter
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.routeradapters.OSIABExternalBrowserRouterAdapter
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.routeradapters.OSIABWebViewRouterAdapter
import android.content.Intent
import android.net.Uri
import android.provider.Browser
import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaInterface
import org.apache.cordova.CordovaPlugin
import org.apache.cordova.CordovaWebView
import org.apache.cordova.PluginResult
import org.json.JSONArray
import org.json.JSONObject
import java.util.StringTokenizer

class OSInAppBrowser: CordovaPlugin() {
    private var engine: OSIABEngine? = null
    private var activeRouter: OSIABRouter<Boolean>? = null
    private val gson by lazy { Gson() }
    
    // Constants for the original InAppBrowser compatibility
    private val SELF = "_self"
    private val SYSTEM = "_system"
    private val BLANK = "_blank"
    private val NULL = "null"

    override fun initialize(cordova: CordovaInterface, webView: CordovaWebView) {
        super.initialize(cordova, webView)
        this.engine = OSIABEngine()
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
            "openInSystemBrowser" -> {
                openInSystemBrowser(args, callbackContext)
            }
            "openInWebView" -> {
                openInWebView(args, callbackContext)
            }
            "openInWebViewHidden" -> {
                openInWebViewHidden(args, callbackContext)
            }
            "close" -> {
                close(callbackContext)
            }
        }
        return true
    }

    /**
     * Calls the openExternalBrowser method of OSIABEngine to open the url in the device's browser app
     * @param args JSONArray that contains the parameters to parse (e.g. url to open)
     * @param callbackContext CallbackContext the method should return to
     */
    private fun openInExternalBrowser(args: JSONArray, callbackContext: CallbackContext) {
        val url: String?

        try {
            val argumentsDictionary = args.getJSONObject(0)
            url = argumentsDictionary.getString("url")
            if(url.isNullOrEmpty()) throw IllegalArgumentException()
        }
        catch (e: Exception) {
            sendError(callbackContext, OSInAppBrowserError.InputArgumentsIssue(OSInAppBrowserTarget.EXTERNAL_BROWSER))
            return
        }

        try {
            val externalBrowserRouter = OSIABExternalBrowserRouterAdapter(cordova.context)

            engine?.openExternalBrowser(externalBrowserRouter, url) { success ->
                if (success) {
                    sendSuccess(callbackContext, OSIABEventType.SUCCESS)
                } else {
                    sendError(callbackContext, OSInAppBrowserError.OpenFailed(url, OSInAppBrowserTarget.EXTERNAL_BROWSER))
                }
            }
        }
        catch (e: Exception) {
            sendError(callbackContext, OSInAppBrowserError.OpenFailed(url, OSInAppBrowserTarget.EXTERNAL_BROWSER))
        }
    }

    /**
     * Original InAppBrowser open method for compatibility
     * @param args JSONArray containing [url, target, features]
     * @param callbackContext CallbackContext the method should return to
     */
    private fun open(args: JSONArray, callbackContext: CallbackContext) {
        try {
            val url = args.getString(0)
            var target = args.optString(1)
            if (target.isNullOrEmpty() || target == NULL) {
                target = SELF
            }
            val features = parseFeature(args.optString(2))

            cordova.activity.runOnUiThread {
                var result = ""
                
                when (target) {
                    SELF -> {
                        // Load in the main WebView
                        webView.loadUrl(url)
                        result = ""
                    }
                    SYSTEM -> {
                        // Open in external browser
                        result = openExternal(url)
                    }
                    else -> {
                        // BLANK or anything else - open in InAppBrowser
                        result = showWebPage(url, features)
                    }
                }

                val pluginResult = PluginResult(PluginResult.Status.OK, result)
                pluginResult.keepCallback = true
                callbackContext.sendPluginResult(pluginResult)
            }
        } catch (e: Exception) {
            sendError(callbackContext, OSInAppBrowserError.InputArgumentsIssue(OSInAppBrowserTarget.WEB_VIEW))
        }
    }

    /**
     * Calls the openCustomTabs method of OSIABEngine to open the url in Custom Tabs
     * @param args JSONArray that contains the parameters to parse (e.g. url to open)
     * @param callbackContext CallbackContext the method should return to
     */
    private fun openInSystemBrowser(args: JSONArray, callbackContext: CallbackContext) {
        val url: String?
        val customTabsOptions: OSIABCustomTabsOptions?

        try {
            val argumentsDictionary = args.getJSONObject(0)
            url = argumentsDictionary.getString("url")
            if(url.isNullOrEmpty()) throw IllegalArgumentException()
            customTabsOptions = buildCustomTabsOptions(argumentsDictionary.optString("options", "{}"))
        }
        catch (e: Exception) {
            sendError(callbackContext, OSInAppBrowserError.InputArgumentsIssue(OSInAppBrowserTarget.SYSTEM_BROWSER))
            return
        }

        try {
            close {
                val customTabsRouter = OSIABCustomTabsRouterAdapter(
                    context = cordova.context,
                    lifecycleScope = cordova.activity.lifecycleScope,
                    options = customTabsOptions,
                    flowHelper = OSIABFlowHelper(),
                    onBrowserPageLoaded = {
                        sendSuccess(callbackContext, OSIABEventType.BROWSER_PAGE_LOADED)
                    },
                    onBrowserFinished = {
                        sendSuccess(callbackContext, OSIABEventType.BROWSER_FINISHED)
                    }
                )

                engine?.openCustomTabs(customTabsRouter, url) { success ->
                    if (success) {
                        activeRouter = customTabsRouter
                        sendSuccess(callbackContext, OSIABEventType.SUCCESS)
                    } else {
                        sendError(callbackContext, OSInAppBrowserError.OpenFailed(url, OSInAppBrowserTarget.SYSTEM_BROWSER))
                    }
                }
            }
        }
        catch (e: Exception) {
            sendError(callbackContext, OSInAppBrowserError.OpenFailed(url, OSInAppBrowserTarget.SYSTEM_BROWSER))
        }
    }

    /**
     * Calls the openWebView method of OSIABEngine to open the url in a WebView
     * @param args JSONArray that contains the parameters to parse (e.g. url to open)
     * @param callbackContext CallbackContext the method should return to
     */
    private fun openInWebView(args: JSONArray, callbackContext: CallbackContext) {
        val url: String?
        val webViewOptions: OSIABWebViewOptions?
        var customHeaders: Map<String, String>? = null

        try {
            val argumentsDictionary = args.getJSONObject(0)
            url = argumentsDictionary.getString("url")
            if(url.isNullOrEmpty()) throw IllegalArgumentException()
            webViewOptions = buildWebViewOptions(argumentsDictionary.optString("options", "{}"))
            if (argumentsDictionary.has("customHeaders")) {
                customHeaders = argumentsDictionary.getJSONObject("customHeaders").let { jsObject ->
                    val result = mutableMapOf<String, String>()
                    jsObject.keys().forEach { key ->
                        when (val value = jsObject.opt(key)) {
                            is String -> result[key] = value
                            is Number -> result[key] = value.toString()
                        }
                    }
                    result
                }
            }
        }
        catch (e: Exception) {
            sendError(callbackContext, OSInAppBrowserError.InputArgumentsIssue(OSInAppBrowserTarget.WEB_VIEW))
            return
        }

        try {
            close {
                val webViewRouter = OSIABWebViewRouterAdapter(
                    context = cordova.context,
                    lifecycleScope = cordova.activity.lifecycleScope,
                    options = webViewOptions,
                    customHeaders = customHeaders,
                    flowHelper = OSIABFlowHelper(),
                    onBrowserPageLoaded = {
                        sendSuccess(callbackContext, OSIABEventType.BROWSER_PAGE_LOADED)
                    },
                    onBrowserFinished = {
                        sendSuccess(callbackContext, OSIABEventType.BROWSER_FINISHED)
                    },
                    onBrowserPageNavigationCompleted = { data ->
                        sendSuccess(callbackContext, OSIABEventType.BROWSER_PAGE_NAVIGATION_COMPLETED, data)
                    }
                )

                engine?.openWebView(webViewRouter, url) { success ->
                    if (success) {
                        activeRouter = webViewRouter
                        sendSuccess(callbackContext, OSIABEventType.SUCCESS)
                    } else {
                        sendError(callbackContext, OSInAppBrowserError.OpenFailed(url, OSInAppBrowserTarget.WEB_VIEW))
                    }
                }
            }
        }
        catch (e: Exception) {
            sendError(callbackContext, OSInAppBrowserError.OpenFailed(url, OSInAppBrowserTarget.WEB_VIEW))
        }
    }

    /**
     * Calls the openWebView method of OSIABEngine to open the url in a hidden WebView
     * @param args JSONArray that contains the parameters to parse (e.g. url to open)
     * @param callbackContext CallbackContext the method should return to
     */
    private fun openInWebViewHidden(args: JSONArray, callbackContext: CallbackContext) {
        val url: String?
        val webViewOptions: OSIABWebViewOptions?
        var customHeaders: Map<String, String>? = null

        try {
            val argumentsDictionary = args.getJSONObject(0)
            url = argumentsDictionary.getString("url")
            if(url.isNullOrEmpty()) throw IllegalArgumentException()
            
            webViewOptions = buildWebViewHiddenOptions(argumentsDictionary.optString("options", "{}"))
            if (argumentsDictionary.has("customHeaders")) {
                customHeaders = argumentsDictionary.getJSONObject("customHeaders").let { jsObject ->
                    val result = mutableMapOf<String, String>()
                    jsObject.keys().forEach { key ->
                        when (val value = jsObject.opt(key)) {
                            is String -> result[key] = value
                            is Number -> result[key] = value.toString()
                        }
                    }
                    result
                }
            }
        }
        catch (e: Exception) {
            sendError(callbackContext, OSInAppBrowserError.InputArgumentsIssue(OSInAppBrowserTarget.WEB_VIEW))
            return
        }

        try {
            close {
                val webViewRouter = OSIABWebViewRouterAdapter(
                    context = cordova.context,
                    lifecycleScope = cordova.activity.lifecycleScope,
                    options = webViewOptions,
                    customHeaders = customHeaders,
                    flowHelper = OSIABFlowHelper(),
                    onBrowserPageLoaded = {
                        sendSuccess(callbackContext, OSIABEventType.BROWSER_PAGE_LOADED)
                    },
                    onBrowserFinished = {
                        sendSuccess(callbackContext, OSIABEventType.BROWSER_FINISHED)
                    },
                    onBrowserPageNavigationCompleted = { data ->
                        sendSuccess(callbackContext, OSIABEventType.BROWSER_PAGE_NAVIGATION_COMPLETED, data)
                    }
                )

                engine?.openWebView(webViewRouter, url) { success ->
                    if (success) {
                        activeRouter = webViewRouter
                        sendSuccess(callbackContext, OSIABEventType.SUCCESS)
                    } else {
                        sendError(callbackContext, OSInAppBrowserError.OpenFailed(url, OSInAppBrowserTarget.WEB_VIEW))
                    }
                }
            }
        }
        catch (e: Exception) {
            sendError(callbackContext, OSInAppBrowserError.OpenFailed(url, OSInAppBrowserTarget.WEB_VIEW))
        }
    }

    /**
     * Calls the close method of OSIABEngine to close the currently opened view
     * @param callbackContext CallbackContext the method should return to
     */
    private fun close(callbackContext: CallbackContext) {
        close { success ->
            if (success) {
                sendSuccess(callbackContext, OSIABEventType.SUCCESS)
            } else {
                sendError(callbackContext, OSInAppBrowserError.CloseFailed)
            }
        }
    }

    private fun close(callback: (Boolean) -> Unit) {
        (activeRouter as? OSIABClosable)?.let { closableRouter ->
            closableRouter.close { success ->
                if (success) {
                    activeRouter = null
                }
                callback(success)
            }
        } ?: callback(false)
    }

    /**
     * Parses options that come in a JSObject to create a 'OSInAppBrowserSystemBrowserInputArguments' object.
     * Then, it uses the newly created object to create a 'OSIABCustomTabsOptions' object.
     * @param options The options to open the URL in the system browser (Custom Tabs) , in a JSON string.
     */
    private fun buildCustomTabsOptions(options: String): OSIABCustomTabsOptions {
        return gson.fromJson(options, OSInAppBrowserSystemBrowserInputArguments::class.java).let {
            OSIABCustomTabsOptions(
                showTitle = it.android?.showTitle ?: true,
                hideToolbarOnScroll = it.android?.hideToolbarOnScroll ?: false,
                viewStyle = it.android?.viewStyle ?: OSIABViewStyle.FULL_SCREEN,
                bottomSheetOptions = it.android?.bottomSheetOptions,
                startAnimation = it.android?.startAnimation ?: OSIABAnimation.FADE_IN,
                exitAnimation = it.android?.exitAnimation ?: OSIABAnimation.FADE_OUT
            )
        }
    }

    /**
     * Parses options that come in JSON to a 'OSInAppBrowserWebViewInputArguments'.
     * Then, it uses the newly created object to create a 'OSIABWebViewOptions' object.
     * @param options The options to open the URL in a WebView, in a JSON string.
     */
    private fun buildWebViewOptions(options: String): OSIABWebViewOptions {
        return gson.fromJson(options, OSInAppBrowserWebViewInputArguments::class.java).let {
            OSIABWebViewOptions(
                it.showURL ?: true,
                it.showToolbar ?: true,
                it.clearCache ?: true,
                it.clearSessionCache ?: true,
                it.mediaPlaybackRequiresUserAction ?: false,
                it.closeButtonText ?: "Close",
                it.toolbarPosition ?: OSIABToolbarPosition.TOP,
                it.leftToRight ?: false,
                it.showNavigationButtons ?: true,
                it.android.allowZoom ?: true,
                it.android.hardwareBack ?: true,
                it.android.pauseMedia ?: true,
                it.customWebViewUserAgent
            )
        }
    }

    /**
     * Parses options that come in JSON to a 'OSInAppBrowserWebViewHiddenInputArguments'.
     * Then, it uses the newly created object to create a 'OSIABWebViewOptions' object.
     * @param options The options to open the URL in a hidden WebView, in a JSON string.
     */
    private fun buildWebViewHiddenOptions(options: String): OSIABWebViewOptions {
        return gson.fromJson(options, OSInAppBrowserWebViewHiddenInputArguments::class.java).let {
            OSIABWebViewOptions(
                it.showURL ?: false,  // Hidden WebView should not show URL by default
                it.showToolbar ?: false,  // Hidden WebView should not show toolbar by default
                it.clearCache ?: true,
                it.clearSessionCache ?: true,
                it.mediaPlaybackRequiresUserAction ?: false,
                it.closeButtonText ?: "Close",
                it.toolbarPosition ?: OSIABToolbarPosition.TOP,
                it.leftToRight ?: false,
                it.showNavigationButtons ?: false,  // Hidden WebView should not show navigation buttons by default
                it.android.allowZoom ?: false,  // Hidden WebView should not allow zoom by default
                it.android.hardwareBack ?: true,
                it.android.pauseMedia ?: true,
                it.customWebViewUserAgent
            )
        }
    }

    /**
     * Helper method to send a success result
     * @param callbackContext CallbackContext to send the result to
     * @param event Event to be sent (SUCCESS, BROWSER_PAGE_LOADED, or BROWSER_FINISHED)
     */
    private fun sendSuccess(callbackContext: CallbackContext, event: OSIABEventType, data: Any? = null) {
        val dataToSend: Map<String, Any?> = mapOf("eventType" to event.value, "data" to data);
        val jsonString = gson.toJson(dataToSend)

        val pluginResult = PluginResult(PluginResult.Status.OK, jsonString)
        pluginResult.keepCallback = true
        callbackContext.sendPluginResult(pluginResult)
    }

    /**
     * Parse features string into HashMap (from original InAppBrowser)
     * @param optString Features string in format "key1=value1,key2=value2"
     * @return HashMap with parsed features
     */
    private fun parseFeature(optString: String): HashMap<String, String>? {
        return if (optString == NULL) {
            null
        } else {
            val map = HashMap<String, String>()
            val features = StringTokenizer(optString, ",")
            while (features.hasMoreElements()) {
                val option = StringTokenizer(features.nextToken(), "=")
                if (option.hasMoreElements()) {
                    val key = option.nextToken()
                    var value = option.nextToken()
                    // For boolean options, default to "yes" if not "yes" or "no"
                    if (!customizableOptions.contains(key)) {
                        value = if (value == "yes" || value == "no") value else "yes"
                    }
                    map[key] = value
                }
            }
            map
        }
    }

    /**
     * Open URL in external browser (from original InAppBrowser)
     * @param url URL to open
     * @return Empty string if successful, error message otherwise
     */
    private fun openExternal(url: String): String {
        return try {
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = Uri.parse(url)
            if ("file" == uri.scheme) {
                intent.setDataAndType(uri, webView.resourceApi.getMimeType(uri))
            } else {
                intent.data = uri
            }
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, cordova.activity.packageName)
            openExternalExcludeCurrentApp(intent)
            ""
        } catch (e: RuntimeException) {
            e.toString()
        }
    }

    /**
     * Open external intent excluding current app (from original InAppBrowser)
     * @param intent Intent to open
     */
    private fun openExternalExcludeCurrentApp(intent: Intent) {
        val currentPackage = cordova.activity.packageName
        var hasCurrentPackage = false

        val pm = cordova.activity.packageManager
        val activities = pm.queryIntentActivities(intent, 0)
        val targetIntents = ArrayList<Intent>()

        for (ri in activities) {
            if (currentPackage != ri.activityInfo.packageName) {
                val targetIntent = intent.clone() as Intent
                targetIntent.setPackage(ri.activityInfo.packageName)
                targetIntents.add(targetIntent)
            } else {
                hasCurrentPackage = true
            }
        }

        when {
            !hasCurrentPackage || targetIntents.isEmpty() -> {
                cordova.activity.startActivity(intent)
            }
            targetIntents.size == 1 -> {
                cordova.activity.startActivity(targetIntents[0])
            }
            targetIntents.isNotEmpty() -> {
                val chooser = Intent.createChooser(targetIntents.removeAt(targetIntents.size - 1), null)
                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetIntents.toTypedArray())
                cordova.activity.startActivity(chooser)
            }
        }
    }

    /**
     * Show web page with features (from original InAppBrowser)
     * @param url URL to load
     * @param features Features HashMap
     * @return Empty string if successful, error message otherwise
     */
    private fun showWebPage(url: String, features: HashMap<String, String>?): String {
        // Parse features to determine behavior
        val showLocationBar = features?.get("location")?.equals("yes") ?: true
        val showZoomControls = features?.get("zoom")?.equals("yes") ?: true
        val openWindowHidden = features?.get("hidden")?.equals("yes") ?: false
        val hardwareBack = features?.get("hardwareback")?.equals("yes") ?: true
        val mediaPlaybackRequiresUserAction = features?.get("mediaPlaybackRequiresUserAction")?.equals("yes") ?: false
        val clearAllCache = features?.get("clearcache")?.equals("yes") ?: false
        val clearSessionCache = features?.get("clearsessioncache")?.equals("yes") ?: false
        val closeButtonCaption = features?.get("closebuttoncaption") ?: ""
        val leftToRight = features?.get("lefttoright")?.equals("yes") ?: false
        val hideNavigationButtons = features?.get("hidenavigationbuttons")?.equals("yes") ?: false
        val hideUrlBar = features?.get("hideurlbar")?.equals("yes") ?: false
        val fullscreen = features?.get("fullscreen")?.equals("yes") ?: true

        // Convert features to our WebView options format
        val webViewOptions = OSIABWebViewOptions(
            showLocationBar && !hideUrlBar,  // showURL
            showLocationBar,  // showToolbar
            clearAllCache,  // clearCache
            clearSessionCache,  // clearSessionCache
            mediaPlaybackRequiresUserAction,  // mediaPlaybackRequiresUserAction
            closeButtonCaption.ifEmpty { "Close" },  // closeButtonText
            OSIABToolbarPosition.TOP,  // toolbarPosition
            leftToRight,  // leftToRight
            showLocationBar && !hideNavigationButtons,  // showNavigationButtons
            showZoomControls,  // allowZoom
            hardwareBack,  // hardwareBack
            true,  // pauseMedia
            ""  // customWebViewUserAgent - empty string instead of null
        )

        try {
            close {
                val webViewRouter = OSIABWebViewRouterAdapter(
                    context = cordova.context,
                    lifecycleScope = cordova.activity.lifecycleScope,
                    options = webViewOptions,
                    customHeaders = null,
                    flowHelper = OSIABFlowHelper(),
                    onBrowserPageLoaded = {
                        // Handle page loaded event
                    },
                    onBrowserFinished = {
                        // Handle browser finished event
                    },
                    onBrowserPageNavigationCompleted = { data ->
                        // Handle navigation completed event
                    }
                )

                engine?.openWebView(webViewRouter, url) { success ->
                    if (success) {
                        activeRouter = webViewRouter
                        
                        // If hidden is true, hide the dialog after opening
                        if (openWindowHidden) {
                            cordova.activity.runOnUiThread {
                                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                    // Hide the dialog but keep it active
                                    // Note: This would need to be implemented in the router adapter
                                }, 100)
                            }
                        }
                    }
                }
            }
            return ""
        } catch (e: Exception) {
            return e.toString()
        }
    }

    // List of customizable options (from original InAppBrowser)
    private val customizableOptions = listOf(
        "closebuttoncaption", "toolbarcolor", "navigationbuttoncolor", 
        "closebuttoncolor", "footercolor"
    )

    /**
     * Helper method to send an error result
     * @param callbackContext CallbackContext to send the result to
     * @param error Error to be sent in the result
     */
    private fun sendError(callbackContext: CallbackContext, error: OSInAppBrowserError) {
        val pluginResult = PluginResult(
            PluginResult.Status.ERROR,
            JSONObject().apply {
                put("code", error.code)
                put("message", error.message)
            }
        )
        callbackContext.sendPluginResult(pluginResult)
    }

}

enum class OSIABEventType(val value: Int) {
    SUCCESS(1),
    BROWSER_FINISHED(2),
    BROWSER_PAGE_LOADED(3),
    BROWSER_PAGE_NAVIGATION_COMPLETED(4)
}
