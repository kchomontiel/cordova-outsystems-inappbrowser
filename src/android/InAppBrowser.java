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

public class InAppBrowser extends CordovaPlugin {

    private static final String TAG = "InAppBrowser";
    private WebView webView;
    private AlertDialog webViewDialog;
    private Activity activity;

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
            Log.d(TAG, "openInAppBrowser called with url: " + url);
            
            // Parse options
            boolean isHidden = options != null && options.contains("hidden=yes");
            boolean showLocation = options != null && options.contains("location=yes");
            boolean showToolbar = options != null && options.contains("toolbar=yes");
            boolean hideNavigationButtons = options != null && options.contains("hidenavigationbuttons=yes");
            
            Log.d(TAG, "Options parsed - hidden: " + isHidden + ", location: " + showLocation + ", toolbar: " + showToolbar);
            
            if (isHidden) {
                // Hidden mode - open in background without UI
                Log.d(TAG, "Opening URL in hidden mode: " + url);
                openHiddenWebView(url, callbackContext);
            } else {
                // Normal mode - open with UI
                Log.d(TAG, "Opening URL in normal mode: " + url);
                openVisibleWebView(url, callbackContext);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error in openInAppBrowser: " + e.getMessage(), e);
            callbackContext.error("Error opening InAppBrowser: " + e.getMessage());
        }
    }
    
    private void openHiddenWebView(String url, CallbackContext callbackContext) {
        try {
            Log.d(TAG, "openHiddenWebView called");
            
            // Create hidden WebView
            webView = new WebView(cordova.getActivity());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setAllowFileAccess(true);
            
            // Load URL
            webView.loadUrl(url);
            
            Log.d(TAG, "Hidden WebView created and loaded URL");
            callbackContext.success("URL opened in hidden mode");
            
        } catch (Exception e) {
            Log.e(TAG, "Error opening hidden WebView: " + e.getMessage(), e);
            callbackContext.error("Error opening hidden WebView: " + e.getMessage());
        }
    }
    
    private void openVisibleWebView(String url, CallbackContext callbackContext) {
        try {
            Log.d(TAG, "openVisibleWebView called");
            
            activity = cordova.getActivity();
            
            // Create WebView
            webView = new WebView(activity);
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
            
            // Create container with close button
            RelativeLayout container = createWebViewContainer();
            
            // Add WebView to container
            container.addView(webView);
            
            // Create and show dialog
            showWebViewDialog(container, callbackContext);
            
            Log.d(TAG, "Visible WebView created and shown");
            
        } catch (Exception e) {
            Log.e(TAG, "Error opening visible WebView: " + e.getMessage(), e);
            callbackContext.error("Error opening visible WebView: " + e.getMessage());
        }
    }
    
    private RelativeLayout createWebViewContainer() {
        Log.d(TAG, "createWebViewContainer called");
        
        RelativeLayout container = new RelativeLayout(activity);
        container.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));
        
        // Create close button
        Button closeButton = createCloseButton();
        
        // Position close button at top-left
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(80, 80);
        buttonParams.setMargins(20, 50, 0, 0);
        closeButton.setLayoutParams(buttonParams);
        
        // Add close button to container
        container.addView(closeButton);
        
        // Position WebView below close button
        RelativeLayout.LayoutParams webViewParams = new RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        );
        webViewParams.addRule(RelativeLayout.BELOW, closeButton.getId());
        webViewParams.setMargins(0, 100, 0, 0); // Add margin to avoid overlap with button
        webView.setLayoutParams(webViewParams);
        
        Log.d(TAG, "WebView container created successfully");
        return container;
    }
    
    private Button createCloseButton() {
        Log.d(TAG, "createCloseButton called");
        
        Button button = new Button(activity);
        button.setId(View.generateViewId());
        
        // Set button properties
        button.setText("✕");
        button.setTextColor(Color.WHITE);
        button.setTextSize(18);
        
        // Set button background
        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.parseColor("#FF4444"));
        background.setCornerRadius(40);
        button.setBackground(background);
        
        // Set click listener
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Close button clicked");
                closeWebView(null);
            }
        });
        
        Log.d(TAG, "Close button created successfully");
        return button;
    }
    
    private void showWebViewDialog(RelativeLayout container, CallbackContext callbackContext) {
        try {
            Log.d(TAG, "showWebViewDialog called");
            
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setView(container);
            
            // Create dialog
            webViewDialog = builder.create();
            webViewDialog.setCanceledOnTouchOutside(false);
            
            // Show dialog
            webViewDialog.show();
            
            Log.d(TAG, "WebView dialog shown successfully");
            
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
            
            if (webViewDialog != null && webViewDialog.isShowing()) {
                webViewDialog.dismiss();
                webViewDialog = null;
                Log.d(TAG, "WebView dialog dismissed");
            }
            
            if (webView != null) {
                webView = null;
                Log.d(TAG, "WebView cleared");
            }
            
            if (callbackContext != null) {
                callbackContext.success("WebView closed successfully");
            }
            
            Log.d(TAG, "WebView closed successfully");
            
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
            
            if (webViewDialog != null && !webViewDialog.isShowing()) {
                webViewDialog.show();
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
            
            if (webViewDialog != null && webViewDialog.isShowing()) {
                webViewDialog.hide();
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
            
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            if (intent.resolveActivity(cordova.getActivity().getPackageManager()) != null) {
                cordova.getActivity().startActivity(intent);
                callbackContext.success("URL opened in external browser");
                Log.d(TAG, "URL opened in external browser successfully");
            } else {
                callbackContext.error("No app available to handle this URL");
                Log.w(TAG, "No app available to handle URL: " + url);
            }
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "ActivityNotFoundException: " + e.getMessage());
            callbackContext.error("No app available to handle this URL");
        } catch (Exception e) {
            Log.e(TAG, "Error opening external browser: " + e.getMessage(), e);
            callbackContext.error("Error opening external browser: " + e.getMessage());
        }
    }
}
