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
                        
                        // Configure advanced WebView settings for better compatibility
                        webView.settings.apply {
                            // Basic settings
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            
                            // Display settings
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            builtInZoomControls = false
                            displayZoomControls = false
                            setSupportZoom(false)
                            
                            // Security and SSL
                            mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            setSupportMultipleWindows(false)
                            allowFileAccess = true
                            allowContentAccess = true
                            cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                            
                            // Advanced settings for complex pages
                            javaScriptCanOpenWindowsAutomatically = true
                            allowFileAccessFromFileURLs = true
                            allowUniversalAccessFromFileURLs = true
                            setPluginState(android.webkit.WebSettings.PluginState.ON)
                            setRenderPriority(android.webkit.WebSettings.RenderPriority.HIGH)
                            setEnableSmoothTransition(true)
                            
                            // Font and display settings
                            setDefaultFontSize(16)
                            setDefaultFixedFontSize(13)
                            setMinimumFontSize(8)
                            setMinimumLogicalFontSize(8)
                            
                            // Network settings
                            setBlockNetworkImage(false)
                            setBlockNetworkLoads(false)
                            // setAppCacheMaxSize was deprecated and removed in modern Android
                        }
                        
                        // Set custom User-Agent to mimic desktop browser completely
                        webView.settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 MultiBrowserPlugin/1.0"
                        
                        // Log the User-Agent for debugging
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - User-Agent configured: ${webView.settings.userAgentString}")
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - User-Agent length: ${webView.settings.userAgentString.length}")
                        
                        // Verify User-Agent is actually set
                        val actualUserAgent = webView.settings.userAgentString
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - Actual User-Agent after setting: $actualUserAgent")
                        
                        // Advanced WebView settings for Google Tag Manager and complex pages
                        webView.settings.apply {
                            javaScriptCanOpenWindowsAutomatically = true
                            allowFileAccessFromFileURLs = true
                            allowUniversalAccessFromFileURLs = true
                            databaseEnabled = true
                            setPluginState(android.webkit.WebSettings.PluginState.ON)
                            setRenderPriority(android.webkit.WebSettings.RenderPriority.HIGH)
                            setEnableSmoothTransition(true)
                            
                            // DISABLE COOKIES to match cURL/Postman behavior
                            android.webkit.CookieManager.getInstance().apply { 
                                setAcceptCookie(false)
                                setAcceptThirdPartyCookies(webView, false)
                            }
                            android.util.Log.d("HiddenInAppBrowser", "openInWebView - Cookies DISABLED to match cURL/Postman behavior")
                        }
                        
                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - Advanced WebView settings configured")
                        android.util.Log.d("HiddenInAppBrowser", "✅ openInWebView - FASE 6 COMPLETADA: Configuración avanzada del WebView")

                        // Create a custom WebViewClient to handle navigation and errors
                        webView.webViewClient = object : android.webkit.WebViewClient() {
                            override fun shouldOverrideUrlLoading(view: android.webkit.WebView?, request: android.webkit.WebResourceRequest?): Boolean {
                                val url = request?.url?.toString()
                                android.util.Log.d("HiddenInAppBrowser", "openInWebView - shouldOverrideUrlLoading: $url")
                                // Log to WebView console
                                safeEvaluateJavascript(view, "console.log('🔗 WebView: Loading URL - $url');")
                                url?.let { view?.loadUrl(it) }
                                return true
                            }
                            
                            override fun shouldInterceptRequest(view: android.webkit.WebView?, request: android.webkit.WebResourceRequest?): android.webkit.WebResourceResponse? {
                                // Log resource requests for debugging
                                request?.url?.let { url ->
                                    android.util.Log.d("HiddenInAppBrowser", "openInWebView - Resource request: $url")
                                    
                                    // Log cookie state
                                    android.webkit.CookieManager.getInstance().let { cookieManager ->
                                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - Cookie state: AcceptCookie=${cookieManager.acceptCookie()}")
                                    }
                                    
                                    // Log all request headers for debugging
                                    request.requestHeaders?.let { headers ->
                                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - Request headers count: ${headers.size}")
                                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - ORIGINAL headers (before modification):")
                                        headers.forEach { (key, value) ->
                                            android.util.Log.d("HiddenInAppBrowser", "openInWebView - Original Header [$key]: $value")
                                        }
                                    }
                                }
                                
                                // Add custom headers to make WebView look more like a real browser
                                request?.requestHeaders?.let { headers ->
                                    // Add Host header for proper server identification
                                    request.url?.host?.let { host ->
                                        headers["Host"] = host
                                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - Host header set: $host")
                                    }
                                    
                                    // Use the actual request URL as referer instead of fake Google URL
                                    request.url?.let { url ->
                                        headers["Referer"] = url.toString()
                                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - Referer set to actual URL: $url")
                                    }
                                    
                                    // Add common browser headers with more realistic values
                                    headers["Accept"] = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"
                                    headers["Accept-Language"] = "en-US,en;q=0.9"
                                    headers["Accept-Encoding"] = "gzip, deflate, br"
                                    headers["DNT"] = "1"
                                    headers["Connection"] = "keep-alive"
                                    headers["Upgrade-Insecure-Requests"] = "1"
                                    
                                    // More realistic Sec-Fetch headers
                                    headers["Sec-Fetch-Dest"] = "document"
                                    headers["Sec-Fetch-Mode"] = "navigate"
                                    headers["Sec-Fetch-Site"] = "same-origin"  // Changed from "none" to "same-origin"
                                    headers["Sec-Fetch-User"] = "?1"
                                    
                                    // Cache control
                                    headers["Cache-Control"] = "max-age=0"
                                    
                                    // Log all headers for debugging
                                    android.util.Log.d("HiddenInAppBrowser", "openInWebView - All headers configured: ${headers.keys.joinToString(", ")}")
                                    
                                    // Log final headers after modification
                                    android.util.Log.d("HiddenInAppBrowser", "openInWebView - Final headers count: ${headers.size}")
                                    android.util.Log.d("HiddenInAppBrowser", "openInWebView - FINAL headers (after modification):")
                                    headers.forEach { (key, value) ->
                                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - Final Header [$key]: $value")
                                    }
                                }
                                
                                return super.shouldInterceptRequest(view, request)
                            }
                            
                            override fun onPageStarted(view: android.webkit.WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                android.util.Log.d("HiddenInAppBrowser", "openInWebView - onPageStarted: $url")
                                safeEvaluateJavascript(view, "console.log('📱 WebView: Page loading started - $url');")
                                
                                // Log current WebView state
                                view?.let { webView ->
                                    android.util.Log.d("HiddenInAppBrowser", "openInWebView - Current WebView URL: ${webView.url}")
                                    android.util.Log.d("HiddenInAppBrowser", "openInWebView - WebView Title: ${webView.title}")
                                    android.util.Log.d("HiddenInAppBrowser", "openInWebView - WebView User-Agent: ${webView.settings.userAgentString}")
                                    
                                    safeEvaluateJavascript(webView, "console.log('🔍 WebView: Current URL - ${webView.url}');")
                                    safeEvaluateJavascript(webView, "console.log('📋 WebView: Title - ${webView.title}');")
                                    safeEvaluateJavascript(webView, "console.log('🖥️ WebView: User-Agent - ${webView.settings.userAgentString}');")
                                }
                            }
                            
                            override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                android.util.Log.d("HiddenInAppBrowser", "openInWebView - onPageFinished: $url")
                                safeEvaluateJavascript(view, "console.log('✅ WebView: Page loaded successfully - $url');")
                                
                                // Log final WebView state
                                view?.let { webView ->
                                    android.util.Log.d("HiddenInAppBrowser", "openInWebView - Final WebView URL: ${webView.url}")
                                    android.util.Log.d("HiddenInAppBrowser", "openInWebView - Final WebView Title: ${webView.title}")
                                    android.util.Log.d("HiddenInAppBrowser", "openInWebView - Final WebView User-Agent: ${webView.settings.userAgentString}")
                                    android.util.Log.d("HiddenInAppBrowser", "✅ openInWebView - FASE 15 COMPLETADA: Página cargada completamente")
                                    
                                    // Get page content length to detect empty content
                                    safeEvaluateJavascript(webView, "console.log('📊 WebView: Content Length - ' + document.documentElement.innerHTML.length);")
                                    safeEvaluateJavascript(webView, "console.log('🔍 WebView: Final URL - ${webView.url}');")
                                    safeEvaluateJavascript(webView, "console.log('📋 WebView: Final Title - ${webView.title}');")
                                    safeEvaluateJavascript(webView, "console.log('🖥️ WebView: Final User-Agent - ${webView.settings.userAgentString}');")
                                    
                                    // Log if page content is empty
                                    safeEvaluateJavascript(webView, """
                                        if (document.documentElement.innerHTML.length < 100) {
                                            console.log('🚨 WARNING: Page content appears to be empty or very short!');
                                            console.log('📄 Document HTML length:', document.documentElement.innerHTML.length);
                                            console.log('📄 Document body length:', document.body ? document.body.innerHTML.length : 'No body');
                                            console.log('🔍 Document ready state:', document.readyState);
                                        }
                                    """.trimIndent())
                                }
                            }
                            
                                                                override fun onReceivedError(view: android.webkit.WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                                        super.onReceivedError(view, errorCode, description, failingUrl)
                                        android.util.Log.e("HiddenInAppBrowser", "openInWebView - onReceivedError: $description")
                                        android.util.Log.e("HiddenInAppBrowser", "openInAppBrowser - Error code: $errorCode")
                                        android.util.Log.e("HiddenInAppBrowser", "openInWebView - Failing URL: $failingUrl")
                                        
                                        // Log error to WebView console for debugging
                                        val errorMessage = "🚨 WebView Error: $description (Code: $errorCode) - URL: $failingUrl"
                                        safeEvaluateJavascript(view, "console.error('$errorMessage');")
                                        
                                        // Handle specific HTTP error codes
                                        when (errorCode) {
                                            405 -> {
                                                android.util.Log.e("HiddenInAppBrowser", "openInWebView - HTTP 405: Method Not Allowed")
                                                safeEvaluateJavascript(view, "console.error('🚨 HTTP 405: Method Not Allowed - This URL requires specific HTTP method');")
                                                sendError(callbackContext, "HTTP 405: Method Not Allowed - This URL requires specific HTTP method")
                                            }
                                            403 -> {
                                                android.util.Log.e("HiddenInAppBrowser", "openInWebView - HTTP 403: Forbidden")
                                                safeEvaluateJavascript(view, "console.error('🚨 HTTP 403: Forbidden - Access denied to this URL');")
                                                sendError(callbackContext, "HTTP 403: Forbidden - Access denied to this URL")
                                            }
                                            401 -> {
                                                android.util.Log.e("HiddenInAppBrowser", "openInAppBrowser - HTTP 401: Unauthorized")
                                                safeEvaluateJavascript(view, "console.error('🚨 HTTP 401: Unauthorized - Authentication required');")
                                                sendError(callbackContext, "HTTP 401: Unauthorized - Authentication required")
                                            }
                                            else -> {
                                                safeEvaluateJavascript(view, "console.error('🚨 WebView Error: $description (Code: $errorCode)');")
                                                sendError(callbackContext, "Error loading URL: $description (Code: $errorCode)")
                                            }
                                        }
                                    }
                            
                            override fun onReceivedHttpError(view: android.webkit.WebView?, request: android.webkit.WebResourceRequest?, errorResponse: android.webkit.WebResourceResponse?) {
                                super.onReceivedHttpError(view, request, errorResponse)
                                android.util.Log.e("HiddenInAppBrowser", "openInWebView - onReceivedHttpError: ${errorResponse?.statusCode}")
                                android.util.Log.e("HiddenInAppBrowser", "openInWebView - Request URL: ${request?.url}")
                                
                                // Check if this is a non-critical resource that can be ignored
                                val url = request?.url?.toString() ?: ""
                                val statusCode = errorResponse?.statusCode ?: 0
                                
                                // List of non-critical resources that can fail without affecting page functionality
                                val nonCriticalResources = listOf(
                                    "favicon.ico",
                                    ".ico",
                                    ".png",
                                    ".jpg",
                                    ".jpeg",
                                    ".gif",
                                    ".svg",
                                    ".woff",
                                    ".woff2",
                                    ".ttf",
                                    ".eot"
                                )
                                
                                // Check if this is a non-critical resource with 404 error
                                val isNonCritical404 = statusCode == 404 && nonCriticalResources.any { url.contains(it) }
                                
                                if (isNonCritical404) {
                                    // Log but don't send error callback for non-critical 404s
                                    android.util.Log.d("HiddenInAppBrowser", "openInWebView - Ignoring non-critical 404: $url")
                                    safeEvaluateJavascript(view, "console.log('ℹ️ Ignoring non-critical 404: $url');")
                                    return // Don't proceed with error handling
                                }
                                
                                // Log HTTP error to WebView console for debugging
                                val httpErrorMessage = "🚨 HTTP Error: $statusCode - URL: $url"
                                safeEvaluateJavascript(view, "console.error('$httpErrorMessage');")
                                
                                // Handle HTTP errors specifically - only for critical errors
                                when (statusCode) {
                                    405 -> {
                                        android.util.Log.e("HiddenInAppBrowser", "openInWebView - HTTP 405: Method Not Allowed")
                                        safeEvaluateJavascript(view, "console.error('🚨 HTTP 405: Method Not Allowed - This URL requires specific HTTP method');")
                                        sendError(callbackContext, "HTTP 405: Method Not Allowed - This URL requires specific HTTP method")
                                    }
                                    403 -> {
                                        android.util.Log.e("HiddenInAppBrowser", "openInWebView - HTTP 403: Forbidden")
                                        safeEvaluateJavascript(view, "console.error('🚨 HTTP 403: Forbidden - Access denied to this URL');")
                                        sendError(callbackContext, "HTTP 403: Forbidden - Access denied to this URL")
                                    }
                                    401 -> {
                                        android.util.Log.e("HiddenInAppBrowser", "openInAppBrowser - HTTP 401: Unauthorized")
                                        safeEvaluateJavascript(view, "console.error('🚨 HTTP 401: Unauthorized - Authentication required');")
                                        sendError(callbackContext, "HTTP 401: Unauthorized - Authentication required")
                                    }
                                    500, 502, 503, 504 -> {
                                        // Server errors - these are critical
                                        android.util.Log.e("HiddenInAppBrowser", "openInWebView - HTTP $statusCode: Server Error")
                                        safeEvaluateJavascript(view, "console.error('🚨 HTTP $statusCode: Server Error');")
                                        sendError(callbackContext, "HTTP $statusCode: Server Error")
                                    }
                                    else -> {
                                        // For other status codes, only log but don't send error callback
                                        // unless it's a critical resource
                                        android.util.Log.d("HiddenInAppBrowser", "openInWebView - HTTP $statusCode for URL: $url (logged but not critical)")
                                        safeEvaluateJavascript(view, "console.log('ℹ️ HTTP $statusCode for URL: $url (not critical)');")
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
                                safeEvaluateJavascript(view, "console.log('📊 WebView Progress: $newProgress%');")
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
                        safeEvaluateJavascript(webView, """
                            console.log('🚀 MultiBrowser Plugin WebView Opened Successfully!');
                            console.log('📱 URL to load: $url');
                            console.log('🔧 Debug mode: Enabled');
                            console.log('📊 All errors and events will be logged here');
                        """.trimIndent())
                        
                        // Send success callback AFTER dialog is shown (WebView is visible)
                        // Add delay to allow Google Tag Manager and page content to load
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            android.util.Log.d("HiddenInAppBrowser", "openInWebView - Sending success callback after delay")
                            sendSuccess(callbackContext, "WebView opened successfully")
                            android.util.Log.d("HiddenInAppBrowser", "✅ openInWebView - FASE 14 COMPLETADA: Callback de éxito enviado")
                        }, 2000) // 2 second delay for GTM and content to load
                        
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

    // Helper function to safely execute JavaScript on the main thread
    private fun safeEvaluateJavascript(webView: android.webkit.WebView?, script: String) {
        webView?.let { view ->
            if (android.os.Looper.myLooper() == android.os.Looper.getMainLooper()) {
                // Already on main thread, execute directly
                view.evaluateJavascript(script, null)
            } else {
                // Not on main thread, post to main thread
                view.post {
                    view.evaluateJavascript(script, null)
                }
            }
        }
    }
}

