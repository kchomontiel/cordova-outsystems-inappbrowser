package com.outsystems.plugins.inappbrowser.osinappbrowser

import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaInterface
import org.apache.cordova.CordovaPlugin
import org.apache.cordova.CordovaWebView
import org.json.JSONArray
import org.json.JSONObject

class HiddenInAppBrowser: CordovaPlugin() {
    
    // Agregar variables de clase para las referencias
    private var modalWebView: android.webkit.WebView? = null
    private var modalDialog: android.app.AlertDialog? = null

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
            "closeWebView" -> {
                closeWebView(args, callbackContext)
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
            android.util.Log.d("HiddenInAppBrowser", "🔍 open - ===== INICIO DEL MÉTODO =====")
            android.util.Log.d("HiddenInAppBrowser", "open - Received args: $args")
            
            val argumentsDictionary = args.getJSONObject(0)
            android.util.Log.d("HiddenInAppBrowser", "open - Arguments dictionary: $argumentsDictionary")
            
            val url = argumentsDictionary.getString("url")
            android.util.Log.d("HiddenInAppBrowser", "open - Extracted URL: $url")
            
            if (url.isNullOrEmpty()) {
                sendError(callbackContext, "URL is required")
                return
            }

            // Get options from arguments if provided
            val providedOptions = argumentsDictionary.optJSONObject("options")
            android.util.Log.d("HiddenInAppBrowser", "open - Provided options: $providedOptions")

            // Create options with hidden=yes (ALWAYS hidden for this method)
            val options = JSONObject().apply {
                put("hidden", "yes")  // Always hidden
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

            // Merge with provided options if any, but keep hidden=yes
            if (providedOptions != null) {
                android.util.Log.d("HiddenInAppBrowser", "open - Merging with provided options")
                val iterator = providedOptions.keys()
                while (iterator.hasNext()) {
                    val key = iterator.next()
                    val value = providedOptions.get(key)
                    // Don't override hidden=yes
                    if (key != "hidden") {
                        options.put(key, value)
                        android.util.Log.d("HiddenInAppBrowser", "open - Added option: $key = $value")
                    }
                }
            }
            
            android.util.Log.d("HiddenInAppBrowser", "open - Final options: $options")
            
            // ALWAYS use hidden mode for this method
            android.util.Log.d("HiddenInAppBrowser", "open - Using hidden mode")
            
            // Hidden mode: Load URL in background WebView
            try {
                val activity = cordova.activity
                if (activity == null) {
                    android.util.Log.e("HiddenInAppBrowser", "open - Activity is null")
                    sendError(callbackContext, "Activity is not available")
                    return
                }
                
                android.util.Log.d("HiddenInAppBrowser", "open - Activity is available, creating hidden WebView")
                
                // Run WebView creation on UI thread
                activity.runOnUiThread {
                    try {
                        android.util.Log.d("HiddenInAppBrowser", "open - Creating hidden WebView on UI thread")
                        
                        // Create a background WebView (invisible)
                        val webView = android.webkit.WebView(activity)
                        android.util.Log.d("HiddenInAppBrowser", "open - WebView created")
                        
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
                        android.util.Log.d("HiddenInAppBrowser", "open - WebView settings configured")
                        
                        // Set WebView to be invisible
                        webView.alpha = 0f
                        webView.visibility = android.view.View.GONE
                        android.util.Log.d("HiddenInAppBrowser", "open - WebView set to invisible")
                        
                        // Create a custom WebViewClient to handle navigation
                        webView.webViewClient = object : android.webkit.WebViewClient() {
                            override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                android.util.Log.d("HiddenInAppBrowser", "open - Page finished loading: $url")
                                sendSuccess(callbackContext, "URL loaded in hidden WebView successfully")
                            }
                            
                            override fun onReceivedError(view: android.webkit.WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                                super.onReceivedError(view, errorCode, description, failingUrl)
                                android.util.Log.e("HiddenInAppBrowser", "open - Error loading URL: $description")
                                sendError(callbackContext, "Error loading URL: $description")
                            }
                        }
                        android.util.Log.d("HiddenInAppBrowser", "open - WebViewClient configured")
                        
                        // Load the URL in background
                        android.util.Log.d("HiddenInAppBrowser", "open - Loading URL: $url")
                        webView.loadUrl(url)
                        android.util.Log.d("HiddenInAppBrowser", "open - URL load initiated")
                        
                    } catch (e: Exception) {
                        android.util.Log.e("HiddenInAppBrowser", "open - Error creating hidden WebView: ${e.message}", e)
                        sendError(callbackContext, "Error loading URL in hidden mode: ${e.message}")
                    }
                }
                
            } catch (e: Exception) {
                android.util.Log.e("HiddenInAppBrowser", "open - Error setting up hidden WebView: ${e.message}", e)
                sendError(callbackContext, "Error setting up hidden WebView: ${e.message}")
            }
            
        } catch (e: Exception) {
            android.util.Log.e("HiddenInAppBrowser", "open - Error in method: ${e.message}", e)
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
            android.util.Log.d("HiddenInAppBrowser", "openInWebView - CallbackContext ID: ${callbackContext.callbackId}")
            
            val argumentsDictionary = args.getJSONObject(0)
            android.util.Log.d("HiddenInAppBrowser", "openInWebView - Arguments dictionary: $argumentsDictionary")
            
            val url = argumentsDictionary.getString("url")
            android.util.Log.d("HiddenInAppBrowser", "openInWebView - Extracted URL: $url")
            
            if (url.isNullOrEmpty()) {
                android.util.Log.e("HiddenInAppBrowser", "openInWebView - URL is empty or null")
                sendError(callbackContext, "URL is required")
                return
            }
            
            android.util.Log.d("HiddenInAppBrowser", "✅ openInWebView - FASE 1 COMPLETADA: Validación de parámetros")

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
            
            android.util.Log.d("HiddenInAppBrowser", "✅ openInWebView - FASE 2 COMPLETADA: Configuración de opciones")

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
            android.util.Log.d("HiddenInAppBrowser", "✅ openInWebView - FASE 3 COMPLETADA: Fusión de opciones")
            
            // Use the original InAppBrowser plugin to open in WebView
            try {
                val activity = cordova.activity
                if (activity == null) {
                    android.util.Log.e("HiddenInAppBrowser", "openInWebView - Activity is null")
                    sendError(callbackContext, "Activity is not available")
                    return
                }
                
                android.util.Log.d("HiddenInAppBrowser", "openInWebView - Activity is available, creating WebView")
                android.util.Log.d("HiddenInAppBrowser", "✅ openInWebView - FASE 4 COMPLETADA: Validación de Activity")
                
                // Run WebView creation on UI thread
                activity.runOnUiThread {
                    try {
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - Creating WebView on UI thread")
                        android.util.Log.d("HiddenInAppBrowser", "✅ openInWebView - FASE 5 COMPLETADA: Inicio de creación en UI thread")
                        
                        // Create a WebView with proper configuration for visible mode
                        val webView = android.webkit.WebView(activity)
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - WebView created")
                        
                        // Configure WebView for full screen immediately
                        webView.layoutParams = android.widget.LinearLayout.LayoutParams(
                            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                            android.widget.LinearLayout.LayoutParams.MATCH_PARENT
                        )
                        
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
                            // Optimize for full screen
                            displayZoomControls = false
                            builtInZoomControls = false
                            // SSL and security settings for enterprise URLs
                            allowFileAccess = true
                            allowContentAccess = true
                            // Handle SSL errors gracefully
                            setSupportMultipleWindows(false)
                            // Cache settings
                            cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                        }
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - WebView settings configured")
                        android.util.Log.d("HiddenInAppBrowser", "✅ openInWebView - FASE 6 COMPLETADA: Configuración del WebView")
                        
                        // Create a custom WebViewClient to handle navigation and errors
                        webView.webViewClient = object : android.webkit.WebViewClient() {
                            override fun shouldOverrideUrlLoading(view: android.webkit.WebView?, url: String?): Boolean {
                                android.util.Log.d("HiddenInAppBrowser", "openInWebView - shouldOverrideUrlLoading: $url")
                                url?.let { view?.loadUrl(it) }
                                return true
                            }
                            
                            override fun onPageStarted(view: android.webkit.WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                android.util.Log.d("HiddenInAppBrowser", "openInWebView - onPageStarted: $url")
                                // Log to WebView console
                                view?.evaluateJavascript("console.log('📱 WebView: Page loading started - $url');", null)
                            }
                            
                            override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                android.util.Log.d("HiddenInAppBrowser", "openInWebView - onPageFinished: $url")
                                android.util.Log.d("HiddenInAppBrowser", "✅ openInWebView - FASE 15 COMPLETADA: Página cargada completamente")
                                // Log to WebView console
                                view?.evaluateJavascript("console.log('✅ WebView: Page loaded successfully - $url');", null)
                                // NO enviar callback aquí - esperar a que el diálogo esté visible
                            }
                            
                            override fun onReceivedError(view: android.webkit.WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                                super.onReceivedError(view, errorCode, description, failingUrl)
                                android.util.Log.e("HiddenInAppBrowser", "openInWebView - onReceivedError: $description")
                                android.util.Log.e("HiddenInAppBrowser", "openInWebView - Error code: $errorCode")
                                android.util.Log.e("HiddenInAppBrowser", "openInWebView - Failing URL: $failingUrl")
                                
                                // Log error to WebView console for debugging
                                val errorMessage = "🚨 WebView Error: $description (Code: $errorCode) - URL: $failingUrl"
                                view?.evaluateJavascript("console.error('$errorMessage');", null)
                                
                                // Handle specific HTTP error codes
                                when (errorCode) {
                                    405 -> {
                                        android.util.Log.e("HiddenInAppBrowser", "openInWebView - HTTP 405: Method Not Allowed")
                                        view?.evaluateJavascript("console.error('🚨 HTTP 405: Method Not Allowed - This URL requires specific HTTP method');", null)
                                        sendError(callbackContext, "HTTP 405: Method Not Allowed - This URL requires specific HTTP method")
                                    }
                                    403 -> {
                                        android.util.Log.e("HiddenInAppBrowser", "openInWebView - HTTP 403: Forbidden")
                                        view?.evaluateJavascript("console.error('🚨 HTTP 403: Forbidden - Access denied to this URL');", null)
                                        sendError(callbackContext, "HTTP 403: Forbidden - Access denied to this URL")
                                    }
                                    401 -> {
                                        android.util.Log.e("HiddenInAppBrowser", "openInAppBrowser - HTTP 401: Unauthorized")
                                        view?.evaluateJavascript("console.error('🚨 HTTP 401: Unauthorized - Authentication required');", null)
                                        sendError(callbackContext, "HTTP 401: Unauthorized - Authentication required")
                                    }
                                    else -> {
                                        view?.evaluateJavascript("console.error('🚨 WebView Error: $description (Code: $errorCode)');", null)
                                        sendError(callbackContext, "Error loading URL: $description (Code: $errorCode)")
                                    }
                                }
                            }
                            
                            override fun onReceivedHttpError(view: android.webkit.WebView?, request: android.webkit.WebResourceRequest?, errorResponse: android.webkit.WebResourceResponse?) {
                                super.onReceivedHttpError(view, request, errorResponse)
                                android.util.Log.e("HiddenInAppBrowser", "openInWebView - onReceivedHttpError: ${errorResponse?.statusCode}")
                                android.util.Log.e("HiddenInAppBrowser", "openInWebView - Request URL: ${request?.url}")
                                
                                // Log HTTP error to WebView console for debugging
                                val httpErrorMessage = "🚨 HTTP Error: ${errorResponse?.statusCode} - URL: ${request?.url}"
                                view?.evaluateJavascript("console.error('$httpErrorMessage');", null)
                                
                                // Handle HTTP errors specifically
                                errorResponse?.statusCode?.let { statusCode ->
                                    when (statusCode) {
                                        405 -> {
                                            android.util.Log.e("HiddenInAppBrowser", "openInWebView - HTTP 405: Method Not Allowed")
                                            view?.evaluateJavascript("console.error('🚨 HTTP 405: Method Not Allowed - This URL requires specific HTTP method');", null)
                                            sendError(callbackContext, "HTTP 405: Method Not Allowed - This URL requires specific HTTP method")
                                        }
                                        403 -> {
                                            android.util.Log.e("HiddenInAppBrowser", "openInAppBrowser", "openInWebView - HTTP 403: Forbidden")
                                            view?.evaluateJavascript("console.error('🚨 HTTP 403: Forbidden - Access denied to this URL');", null)
                                            sendError(callbackContext, "HTTP 403: Forbidden - Access denied to this URL")
                                        }
                                        401 -> {
                                            android.util.Log.e("HiddenInAppBrowser", "openInWebView - HTTP 401: Unauthorized")
                                            view?.evaluateJavascript("console.error('🚨 HTTP 401: Unauthorized - Authentication required');", null)
                                            sendError(callbackContext, "HTTP 401: Unauthorized - Authentication required")
                                        }
                                        else -> {
                                            view?.evaluateJavascript("console.error('🚨 HTTP Error: $statusCode');", null)
                                            sendError(callbackContext, "HTTP Error: $statusCode")
                                        }
                                    }
                                }
                            }
                        }
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - WebViewClient configured")
                        android.util.Log.d("HiddenInAppBrowser", "✅ openInWebView - FASE 7 COMPLETADA: Configuración de WebViewClient")
                        
                        // Create a custom WebChromeClient to handle dialogs and progress
                        webView.webChromeClient = object : android.webkit.WebChromeClient() {
                            override fun onProgressChanged(view: android.webkit.WebView?, newProgress: Int) {
                                super.onProgressChanged(view, newProgress)
                                android.util.Log.d("HiddenInAppBrowser", "openInWebView - Progress: $newProgress%")
                                // Log progress to WebView console
                                view?.evaluateJavascript("console.log('📊 WebView Progress: $newProgress%');", null)
                            }
                            
                            override fun onConsoleMessage(message: android.webkit.ConsoleMessage?): Boolean {
                                android.util.Log.d("HiddenInAppBrowser", "openInWebView - Console: ${message?.message()}")
                                return true
                            }
                        }
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - WebChromeClient configured")
                        android.util.Log.d("HiddenInAppBrowser", "✅ openInWebView - FASE 8 COMPLETADA: Configuración de WebChromeClient")
                        
                        // Create a layout for the WebView
                        val layout = android.widget.LinearLayout(activity).apply {
                            orientation = android.widget.LinearLayout.VERTICAL
                            layoutParams = android.view.ViewGroup.LayoutParams(
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            setBackgroundColor(android.graphics.Color.WHITE)
                            // Ensure layout takes full screen
                            setPadding(0, 0, 0, 0)
                        }
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - Layout created")
                        android.util.Log.d("HiddenInAppBrowser", "✅ openInWebView - FASE 9 COMPLETADA: Creación del Layout")
                        
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
                            // Ensure dialog takes full screen
                            setFlags(
                                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
                            )
                            // Remove any margins or padding
                            attributes = attributes.apply {
                                width = android.view.ViewGroup.LayoutParams.MATCH_PARENT
                                height = android.view.ViewGroup.LayoutParams.MATCH_PARENT
                            }
                        }
                        
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - Dialog created")
                        android.util.Log.d("HiddenInAppBrowser", "✅ openInWebView - FASE 10 COMPLETADA: Creación del Diálogo")
                        
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
                        android.util.Log.d("HiddenInAppBrowser", "✅ openInWebView - FASE 11 COMPLETADA: Creación de Toolbar y Botón Close")
                        
                        // Add the WebView to the layout
                        // WebView already has layoutParams set above
                        layout.addView(webView)
                        
                        // Ensure WebView fills the entire available space
                        webView.requestLayout()
                        
                        // Additional configuration for full screen behavior
                        webView.overScrollMode = android.view.View.OVER_SCROLL_NEVER
                        webView.isVerticalScrollBarEnabled = true
                        webView.isHorizontalScrollBarEnabled = true
                        
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - WebView added to layout")
                        android.util.Log.d("HiddenInAppBrowser", "✅ openInWebView - FASE 12 COMPLETADA: WebView agregado al Layout")
                        

                        
                        // Show the dialog
                        dialog.show()
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - Dialog shown")
                        android.util.Log.d("HiddenInAppBrowser", "✅ openInWebView - FASE 13 COMPLETADA: Diálogo mostrado")
                        
                        // Add welcome message to WebView console
                        webView.evaluateJavascript("""
                            console.log('🚀 MultiBrowser Plugin WebView Opened Successfully!');
                            console.log('📱 URL to load: $url');
                            console.log('🔧 Debug mode: Enabled');
                            console.log('📊 All errors and events will be logged here');
                        """.trimIndent(), null)
                        
                        // Send success callback AFTER dialog is shown (WebView is visible)
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - Sending success callback")
                        sendSuccess(callbackContext, "WebView opened successfully")
                        android.util.Log.d("HiddenInAppBrowser", "✅ openInWebView - FASE 14 COMPLETADA: Callback de éxito enviado")
                        
                        // Load the URL
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - Loading URL: $url")
                        webView.loadUrl(url)
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - URL load initiated")
                        android.util.Log.d("HiddenInAppBrowser", "✅ openInWebView - FASE 16 COMPLETADA: Carga de URL iniciada")
                        
                        // Después de crear el AlertDialog, guardar las referencias
                        modalWebView = webView
                        modalDialog = dialog
                        android.util.Log.d("HiddenInAppBrowser", "✅ openInWebView - FASE 17 COMPLETADA: Referencias guardadas")
                        
                    } catch (e: Exception) {
                        android.util.Log.e("HiddenInAppBrowser", "openInWebView - Error creating WebView: ${e.message}", e)
                        android.util.Log.e("HiddenInAppBrowser", "❌ openInWebView - ERROR en fase de creación del WebView")
                        sendError(callbackContext, "Error opening WebView: ${e.message}")
                    }
                }
                
            } catch (e: Exception) {
                android.util.Log.e("HiddenInAppBrowser", "openInWebView - Error setting up WebView: ${e.message}", e)
                android.util.Log.e("HiddenInAppBrowser", "❌ openInWebView - ERROR en fase de configuración del WebView")
                sendError(callbackContext, "Error setting up WebView: ${e.message}")
            }
            
        } catch (e: Exception) {
            android.util.Log.e("HiddenInAppBrowser", "openInWebView - Error in method: ${e.message}", e)
            android.util.Log.e("HiddenInAppBrowser", "❌ openInAppBrowser - ERROR GENERAL en método openInWebView")
            sendError(callbackContext, "Error opening WebView: ${e.message}")
        }
    }
    
    // Agregar el método closeWebView
    @JvmSuppressWildcards
    fun closeWebView(args: JSONArray, callbackContext: CallbackContext) {
        try {
            android.util.Log.d("HiddenInAppBrowser", "🔍 closeWebView - ===== INICIO DEL MÉTODO =====")
            android.util.Log.d("HiddenInAppBrowser", "closeWebView - Received args: $args")
            
            cordova.activity.runOnUiThread {
                try {
                    // Cerrar modal WebView
                    modalWebView?.let { webView ->
                        android.util.Log.d("HiddenInAppBrowser", "closeWebView - Closing modal WebView")
                        webView.stopLoading()
                        webView.destroy()
                        modalWebView = null
                    }
                    
                    // Cerrar diálogo modal
                    modalDialog?.let { dialog ->
                        android.util.Log.d("HiddenInAppBrowser", "closeWebView - Closing modal dialog")
                        dialog.dismiss()
                        modalDialog = null
                    }
                    
                    android.util.Log.d("HiddenInAppBrowser", "closeWebView - Modal WebView closed successfully")
                    callbackContext.success("Modal WebView closed successfully")
                    
                } catch (e: Exception) {
                    android.util.Log.e("HiddenInAppBrowser", "closeWebView - Error closing modal WebView", e)
                    callbackContext.error("Error closing modal WebView: ${e.message}")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("HiddenInAppBrowser", "closeWebView - Error in closeWebView method", e)
            callbackContext.error("Error in closeWebView method: ${e.message}")
        }
    }
}

