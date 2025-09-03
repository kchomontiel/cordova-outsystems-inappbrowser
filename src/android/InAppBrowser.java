/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.cordova.inappbrowser;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import android.content.Intent;
import android.net.Uri;
import android.content.ActivityNotFoundException;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.app.Activity;
import android.view.ViewGroup;
import android.widget.Button;
import android.graphics.Color;
import android.view.View;
import android.graphics.drawable.GradientDrawable;
import android.app.AlertDialog;
import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;
import android.os.Build;
import android.view.WindowManager;
import java.util.List;

public class InAppBrowser extends CordovaPlugin {

    private static final String TAG = "InAppBrowser";
    private WebView webView;
    private AlertDialog webViewDialog;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            if (action.equals("open")) {
                String url = args.getString(0);
                String target = args.getString(1);
                String options = args.getString(2);
                
                Log.d(TAG, "open called with url: " + url + ", target: " + target + ", options: " + options);
                openInAppBrowser(url, target, options, callbackContext);
                return true;
            } else if (action.equals("close")) {
                Log.d(TAG, "close called");
                closeWebView(callbackContext);
                return true;
            } else if (action.equals("show")) {
                Log.d(TAG, "show called");
                showWebView(callbackContext);
                return true;
            } else if (action.equals("hide")) {
                Log.d(TAG, "hide called");
                hideWebView(callbackContext);
                return true;
            } else if (action.equals("addEventListener")) {
                String eventname = args.getString(0);
                Log.d(TAG, "addEventListener called with event: " + eventname);
                callbackContext.success("InAppBrowser addEventListener method called");
                return true;
            } else if (action.equals("removeEventListener")) {
                String eventname = args.getString(0);
                Log.d(TAG, "removeEventListener called with event: " + eventname);
                callbackContext.success("InAppBrowser removeEventListener method called");
                return true;
            } else if (action.equals("openExternal")) {
                String url = args.getString(0);
                Log.d(TAG, "openExternal called with url: " + url);
                openExternalBrowser(url, callbackContext);
                return true;
            }
            
            Log.w(TAG, "Unknown action: " + action);
            callbackContext.error("Unknown action: " + action);
            return false;
            
        } catch (Exception e) {
            Log.e(TAG, "Error in execute: " + e.getMessage(), e);
            callbackContext.error("Error in execute: " + e.getMessage());
            return false;
        }
    }
    
    private void openInAppBrowser(String url, String target, String options, CallbackContext callbackContext) {
        try {
            Log.d(TAG, "openInAppBrowser called with url: " + url + ", target: " + target);
            
            // Parse options
            boolean isHidden = options != null && options.contains("hidden=yes");
            boolean showLocation = options != null && options.contains("location=yes");
            boolean showToolbar = options != null && options.contains("toolbar=yes");
            boolean hideNavigationButtons = options != null && options.contains("hidenavigationbuttons=yes");
            
            Log.d(TAG, "Options parsed - hidden: " + isHidden + ", location: " + showLocation + ", toolbar: " + showToolbar);
            Log.d(TAG, "Target: " + target);
            
            // Check target parameter
            if ("_system".equals(target)) {
                // External browser - use openExternal
                Log.d(TAG, "Opening URL in external browser (target: _system): " + url);
                openExternalBrowser(url, callbackContext);
            } else if ("_blank".equals(target) || "_self".equals(target)) {
                // Internal WebView - check if hidden or visible
                if (isHidden) {
                    // Hidden mode - open in background without UI
                    Log.d(TAG, "Opening URL in hidden WebView (target: " + target + "): " + url);
                    openHiddenWebView(url, callbackContext);
                } else {
                    // Normal mode - open with UI
                    Log.d(TAG, "Opening URL in visible WebView (target: " + target + "): " + url);
                    openVisibleWebView(url, callbackContext);
                }
            } else {
                // Default behavior - treat as internal WebView
                Log.d(TAG, "Unknown target '" + target + "', defaulting to internal WebView");
                if (isHidden) {
                    openHiddenWebView(url, callbackContext);
                } else {
                    openVisibleWebView(url, callbackContext);
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error in openInAppBrowser: " + e.getMessage(), e);
            callbackContext.error("Error opening InAppBrowser: " + e.getMessage());
        }
    }
    
    private void openHiddenWebView(String url, CallbackContext callbackContext) {
        try {
            Log.d(TAG, "openHiddenWebView called");
            
            // Get activity reference
            final Activity currentActivity = cordova.getActivity();
            if (currentActivity == null) {
                Log.e(TAG, "openHiddenWebView - Activity is null");
                callbackContext.error("Activity is not available");
                return;
            }
            
            Log.d(TAG, "openHiddenWebView - Activity is available, creating hidden WebView");
            
            // Run UI operations on main thread
            currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d(TAG, "openHiddenWebView - Creating hidden WebView on UI thread");
                        
                        // Create a background WebView (invisible)
                        webView = new WebView(currentActivity);
                        Log.d(TAG, "openHiddenWebView - WebView created");
                        
                        // Configure WebView settings
                        WebSettings settings = webView.getSettings();
                        settings.setJavaScriptEnabled(true);
                        settings.setDomStorageEnabled(true);
                        settings.setLoadWithOverviewMode(true);
                        settings.setUseWideViewPort(true);
                        settings.setBuiltInZoomControls(true);
                        settings.setDisplayZoomControls(false);
                        settings.setSupportZoom(true);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                        }
                        Log.d(TAG, "openHiddenWebView - WebView settings configured");
                        
                        // Set WebView to be invisible
                        webView.setAlpha(0f);
                        webView.setVisibility(View.GONE);
                        Log.d(TAG, "openHiddenWebView - WebView set to invisible");
                        
                        // Create a custom WebViewClient to handle navigation
                        webView.setWebViewClient(new WebViewClient() {
                            @Override
                            public void onPageFinished(WebView view, String url) {
                                super.onPageFinished(view, url);
                                Log.d(TAG, "openHiddenWebView - Page finished loading: " + url);
                                callbackContext.success("URL loaded in hidden WebView successfully");
                            }
                            
                            @Override
                            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                                super.onReceivedError(view, errorCode, description, failingUrl);
                                Log.e(TAG, "openHiddenWebView - Error loading URL: " + description);
                                callbackContext.error("Error loading URL: " + description);
                            }
                        });
                        Log.d(TAG, "openHiddenWebView - WebViewClient configured");
                        
                        // Load the URL in background
                        Log.d(TAG, "openHiddenWebView - Loading URL: " + url);
                        webView.loadUrl(url);
                        Log.d(TAG, "openHiddenWebView - URL load initiated");
                        
                    } catch (Exception e) {
                        Log.e(TAG, "openHiddenWebView - Error creating hidden WebView: " + e.getMessage(), e);
                        callbackContext.error("Error loading URL in hidden mode: " + e.getMessage());
                    }
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "openHiddenWebView - Error setting up hidden WebView: " + e.getMessage(), e);
            callbackContext.error("Error setting up hidden WebView: " + e.getMessage());
        }
    }
    
    private void openVisibleWebView(String url, CallbackContext callbackContext) {
        try {
            Log.d(TAG, "openVisibleWebView called");
            
            // Get activity reference
            final Activity currentActivity = cordova.getActivity();
            
            // Run UI operations on main thread
            currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d(TAG, "Running UI operations on main thread");
                        
                        // Create WebView
                        webView = new WebView(currentActivity);
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.getSettings().setDomStorageEnabled(true);
                        webView.getSettings().setAllowFileAccess(true);
                        webView.getSettings().setBuiltInZoomControls(true);
                        webView.getSettings().setDisplayZoomControls(false);
                        
                        // Set WebViewClient to handle navigation
                        webView.setWebViewClient(new WebViewClient() {
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                view.loadUrl(url);
                                return true;
                            }
                        });
                        
                        // Load URL
                        webView.loadUrl(url);
                        
                        // Create container with close button (WebView ya está incluido)
                        RelativeLayout container = createWebViewContainer(currentActivity);
                        
                        // Create and show dialog
                        showWebViewDialog(container, callbackContext, currentActivity);
                        
                        Log.d(TAG, "Visible WebView created and shown on UI thread");
                        
                    } catch (Exception e) {
                        Log.e(TAG, "Error in UI thread: " + e.getMessage(), e);
                        callbackContext.error("Error creating WebView: " + e.getMessage());
                    }
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error opening visible WebView: " + e.getMessage(), e);
            callbackContext.error("Error opening visible WebView: " + e.getMessage());
        }
    }
    
    private RelativeLayout createWebViewContainer(Activity currentActivity) {
        Log.d(TAG, "createWebViewContainer called");
        
        // Create main container that takes full screen with NO margins
        RelativeLayout container = new RelativeLayout(currentActivity);
        container.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));
        container.setBackgroundColor(Color.BLACK); // Black background
        container.setPadding(0, 0, 0, 0); // NO padding
        container.setClipToPadding(false); // Don't clip content to padding
        
        // Create close button (floating overlay)
        Button closeButton = createCloseButton(currentActivity);
        
        // Position close button as floating overlay at top-left
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(100, 100);
        buttonParams.setMargins(30, 50, 0, 0); // Top-left corner with status bar consideration
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        closeButton.setLayoutParams(buttonParams);
        
        // Add close button to main container (on top)
        container.addView(closeButton);
        Log.d(TAG, "Close button added as floating overlay");
        
        // Create WebView that takes full screen with NO margins
        RelativeLayout.LayoutParams webViewParams = new RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        );
        webViewParams.setMargins(0, 0, 0, 0); // NO margins at all
        webView.setLayoutParams(webViewParams);
        
        // Add WebView to container (behind the close button)
        container.addView(webView);
        
        Log.d(TAG, "WebView takes full screen with absolutely NO margins");
        Log.d(TAG, "WebView container created successfully with floating close button");
        return container;
    }
    
    private Button createCloseButton(Activity currentActivity) {
        Log.d(TAG, "createCloseButton called");
        
        Button button = new Button(currentActivity);
        button.setId(View.generateViewId());
        
        // Set button properties - white background with black X
        button.setText("✕");
        button.setTextColor(Color.BLACK); // Black X
        button.setTextSize(20); // Smaller text to fit in circle
        
        // Set button background - white circle
        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.WHITE); // White background
        background.setCornerRadius(50); // Rounded corners
        background.setStroke(2, Color.GRAY); // Thin gray border for definition
        button.setBackground(background);
        
        // Set button size and padding - smaller for better fit
        button.setPadding(20, 20, 20, 20);
        
        // Add shadow/elevation for better visibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            button.setElevation(8f); // Slightly less elevation
        }
        
        // Set click listener
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Close button clicked");
                closeWebView(null);
            }
        });
        
        Log.d(TAG, "Close button created successfully with white background and black X");
        return button;
    }
    
    private void showWebViewDialog(RelativeLayout container, CallbackContext callbackContext, Activity currentActivity) {
        try {
            Log.d(TAG, "showWebViewDialog called");
            
            // Create a full-screen dialog with no margins
            AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            builder.setView(container);
            
            // Create dialog
            webViewDialog = builder.create();
            webViewDialog.setCanceledOnTouchOutside(false);
            
            // Set dialog to full screen with no margins
            webViewDialog.getWindow().setFlags(
                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
            
            // Remove any padding/margins from dialog
            webViewDialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            );
            
            // Show dialog
            webViewDialog.show();
            
            Log.d(TAG, "WebView dialog shown successfully in full screen mode");
            
            // Return success
            callbackContext.success("WebView opened successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error showing WebView dialog: " + e.getMessage(), e);
            callbackContext.error("Error showing WebView dialog: " + e.getMessage());
        }
    }
    
    private void closeWebView(CallbackContext callbackContext) {
        try {
            Log.d(TAG, "closeWebView called");
            
            // Run UI operations on main thread
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (webViewDialog != null && webViewDialog.isShowing()) {
                            webViewDialog.dismiss();
                            webViewDialog = null;
                            Log.d(TAG, "WebView dialog dismissed");
                        }
                        
                        if (webView != null) {
                            webView = null;
                            Log.d(TAG, "WebView cleared");
                        }
                        
                        Log.d(TAG, "WebView closed successfully on UI thread");
                        
                    } catch (Exception e) {
                        Log.e(TAG, "Error closing WebView on UI thread: " + e.getMessage(), e);
                    }
                }
            });
            
            if (callbackContext != null) {
                callbackContext.success("WebView closed successfully");
            }
            
            Log.d(TAG, "WebView close operation initiated");
            
        } catch (Exception e) {
            Log.e(TAG, "Error closing WebView: " + e.getMessage(), e);
            if (callbackContext != null) {
                callbackContext.error("Error closing WebView: " + e.getMessage());
            }
        }
    }
    
    private void showWebView(CallbackContext callbackContext) {
        try {
            Log.d(TAG, "showWebView called");
            
            // Run UI operations on main thread
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (webViewDialog != null && !webViewDialog.isShowing()) {
                            webViewDialog.show();
                            Log.d(TAG, "WebView shown successfully on UI thread");
                        } else {
                            Log.w(TAG, "No WebView to show");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error showing WebView on UI thread: " + e.getMessage(), e);
                    }
                }
            });
            
            if (webViewDialog != null && !webViewDialog.isShowing()) {
                callbackContext.success("WebView shown successfully");
                Log.d(TAG, "WebView shown successfully");
            } else {
                callbackContext.error("No WebView to show");
                Log.w(TAG, "No WebView to show");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing WebView: " + e.getMessage(), e);
            callbackContext.error("Error showing WebView: " + e.getMessage());
        }
    }
    
    private void hideWebView(CallbackContext callbackContext) {
        try {
            Log.d(TAG, "hideWebView called");
            
            // Run UI operations on main thread
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (webViewDialog != null && webViewDialog.isShowing()) {
                            webViewDialog.hide();
                            Log.d(TAG, "WebView hidden successfully on UI thread");
                        } else {
                            Log.w(TAG, "No WebView to hide");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error hiding WebView on UI thread: " + e.getMessage(), e);
                    }
                }
            });
            
            if (webViewDialog != null && webViewDialog.isShowing()) {
                callbackContext.success("WebView hidden successfully");
                Log.d(TAG, "WebView hidden successfully");
            } else {
                callbackContext.error("No WebView to hide");
                Log.w(TAG, "No WebView to hide");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error hiding WebView: " + e.getMessage(), e);
            callbackContext.error("Error hiding WebView: " + e.getMessage());
        }
    }
    
    private void openExternalBrowser(String url, CallbackContext callbackContext) {
        try {
            Log.d(TAG, "openExternalBrowser called with url: " + url);
            
            // Validate URL format
            if (url == null || url.trim().isEmpty()) {
                Log.e(TAG, "URL is null or empty");
                callbackContext.error("URL is null or empty");
                return;
            }
            
            // Ensure URL has protocol
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
                Log.d(TAG, "Added https:// protocol to URL: " + url);
            }
            
            // Create intent for external browser
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            
            // Add flags for better compatibility
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            
            // Log the intent details for debugging
            Log.d(TAG, "Intent created: " + intent.toString());
            Log.d(TAG, "Intent data: " + intent.getDataString());
            
            // Get package manager
            Activity currentActivity = cordova.getActivity();
            if (currentActivity == null) {
                Log.e(TAG, "Current activity is null");
                callbackContext.error("Activity not available");
                return;
            }
            
            // Check if there's an app to handle this URL
            if (intent.resolveActivity(currentActivity.getPackageManager()) != null) {
                try {
                    Log.d(TAG, "Starting external browser activity");
                    // Start the external browser activity
                    currentActivity.startActivity(intent);
                    callbackContext.success("URL opened in external browser");
                    Log.d(TAG, "URL opened in external browser successfully: " + url);
                } catch (SecurityException se) {
                    Log.e(TAG, "SecurityException opening external browser: " + se.getMessage());
                    callbackContext.error("Permission denied to open external browser");
                } catch (Exception e) {
                    Log.e(TAG, "Exception starting external browser: " + e.getMessage());
                    callbackContext.error("Error starting external browser: " + e.getMessage());
                }
            } else {
                Log.w(TAG, "No app available to handle URL: " + url);
                Log.d(TAG, "Available apps for this intent: " + getAvailableAppsForIntent(intent, currentActivity));
                
                // Try alternative approach - use default browser
                try {
                    Log.d(TAG, "Trying alternative approach with default browser");
                    Intent alternativeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    alternativeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    
                    // Try to start without checking resolveActivity
                    currentActivity.startActivity(alternativeIntent);
                    callbackContext.success("URL opened in default browser");
                    Log.d(TAG, "URL opened in default browser successfully: " + url);
                } catch (Exception e) {
                    Log.e(TAG, "Alternative approach also failed: " + e.getMessage());
                    callbackContext.error("No app available to handle this URL");
                }
            }
        } catch (IllegalArgumentException iae) {
            Log.e(TAG, "Invalid URL format: " + url + " - " + iae.getMessage());
            callbackContext.error("Invalid URL format: " + url);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "ActivityNotFoundException: " + e.getMessage());
            callbackContext.error("No app available to handle this URL");
        } catch (Exception e) {
            Log.e(TAG, "Error opening external browser: " + e.getMessage(), e);
            callbackContext.error("Error opening external browser: " + e.getMessage());
        }
    }
    
    private String getAvailableAppsForIntent(Intent intent, Activity activity) {
        try {
            List<ResolveInfo> resolveInfoList = activity.getPackageManager().queryIntentActivities(intent, 0);
            StringBuilder apps = new StringBuilder();
            for (ResolveInfo resolveInfo : resolveInfoList) {
                if (resolveInfo.activityInfo != null) {
                    apps.append(resolveInfo.activityInfo.packageName).append(", ");
                }
            }
            return apps.toString();
        } catch (Exception e) {
            Log.e(TAG, "Error getting available apps: " + e.getMessage());
            return "Error getting available apps";
        }
    }
}
