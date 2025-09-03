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
import android.app.Activity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Button;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.graphics.drawable.GradientDrawable;

public class InAppBrowser extends CordovaPlugin {

    private static final String TAG = "InAppBrowser";
    private WebView webView;
    private FrameLayout webViewContainer;
    private Activity activity;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("open")) {
            String url = args.getString(0);
            String target = args.getString(1);
            String options = args.getString(2);
            
            openInAppBrowser(url, target, options, callbackContext);
            return true;
        } else if (action.equals("close")) {
            closeWebView(callbackContext);
            return true;
        } else if (action.equals("show")) {
            showWebView(callbackContext);
            return true;
        } else if (action.equals("hide")) {
            hideWebView(callbackContext);
            return true;
        } else if (action.equals("addEventListener")) {
            String eventname = args.getString(0);
            callbackContext.success("InAppBrowser addEventListener method called");
            return true;
        } else if (action.equals("removeEventListener")) {
            String eventname = args.getString(0);
            callbackContext.success("InAppBrowser removeEventListener method called");
            return true;
        } else if (action.equals("openExternal")) {
            String url = args.getString(0);
            openExternalBrowser(url, callbackContext);
            return true;
        }
        
        return false;
    }
    
    private void openInAppBrowser(String url, String target, String options, CallbackContext callbackContext) {
        try {
            // Parse options
            boolean isHidden = options != null && options.contains("hidden=yes");
            boolean showLocation = options != null && options.contains("location=yes");
            boolean showToolbar = options != null && options.contains("toolbar=yes");
            boolean hideNavigationButtons = options != null && options.contains("hidenavigationbuttons=yes");
            
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
            Log.e(TAG, "Error opening InAppBrowser: " + e.getMessage());
            callbackContext.error("Error opening InAppBrowser: " + e.getMessage());
        }
    }
    
    private void openHiddenWebView(String url, CallbackContext callbackContext) {
        try {
            // Create hidden WebView
            webView = new WebView(cordova.getActivity());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(url);
            
            callbackContext.success("URL opened in hidden mode");
        } catch (Exception e) {
            Log.e(TAG, "Error opening hidden WebView: " + e.getMessage());
            callbackContext.error("Error opening hidden WebView: " + e.getMessage());
        }
    }
    
    private void openVisibleWebView(String url, CallbackContext callbackContext) {
        try {
            activity = cordova.getActivity();
            
            // Create WebView
            webView = new WebView(activity);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(url);
            
            // Create container
            webViewContainer = new FrameLayout(activity);
            webViewContainer.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ));
            
            // Add close button
            Button closeButton = createCloseButton();
            webViewContainer.addView(closeButton);
            
            // Add WebView
            webViewContainer.addView(webView);
            
            // Set as content view
            activity.setContentView(webViewContainer);
            
            callbackContext.success("WebView opened successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error opening visible WebView: " + e.getMessage());
            callbackContext.error("Error opening visible WebView: " + e.getMessage());
        }
    }
    
    private Button createCloseButton() {
        Button button = new Button(activity);
        
        // Set button properties
        button.setText("✕");
        button.setTextColor(Color.WHITE);
        button.setTextSize(18);
        
        // Set button size and position
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(80, 80);
        params.setMargins(20, 50, 0, 0);
        button.setLayoutParams(params);
        
        // Set button background
        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.parseColor("#FF4444"));
        background.setCornerRadius(40);
        button.setBackground(background);
        
        // Set click listener
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeWebView(null);
            }
        });
        
        return button;
    }
    
    private void closeWebView(CallbackContext callbackContext) {
        try {
            if (webViewContainer != null && activity != null) {
                // Restore original content view
                activity.setContentView(activity.findViewById(android.R.id.content));
                webViewContainer = null;
                webView = null;
                
                if (callbackContext != null) {
                    callbackContext.success("WebView closed successfully");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error closing WebView: " + e.getMessage());
            if (callbackContext != null) {
                callbackContext.error("Error closing WebView: " + e.getMessage());
            }
        }
    }
    
    private void showWebView(CallbackContext callbackContext) {
        if (webViewContainer != null) {
            webViewContainer.setVisibility(View.VISIBLE);
            callbackContext.success("WebView shown successfully");
        } else {
            callbackContext.error("No WebView to show");
        }
    }
    
    private void hideWebView(CallbackContext callbackContext) {
        if (webViewContainer != null) {
            webViewContainer.setVisibility(View.GONE);
            callbackContext.success("WebView hidden successfully");
        } else {
            callbackContext.error("No WebView to hide");
        }
    }
    
    private void openExternalBrowser(String url, CallbackContext callbackContext) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            if (intent.resolveActivity(cordova.getActivity().getPackageManager()) != null) {
                cordova.getActivity().startActivity(intent);
                callbackContext.success("URL opened in external browser");
            } else {
                callbackContext.error("No app available to handle this URL");
            }
        } catch (ActivityNotFoundException e) {
            callbackContext.error("No app available to handle this URL");
        } catch (Exception e) {
            callbackContext.error("Error opening external browser: " + e.getMessage());
        }
    }
}
