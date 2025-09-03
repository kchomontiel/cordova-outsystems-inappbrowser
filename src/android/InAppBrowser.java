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

public class InAppBrowser extends CordovaPlugin {

    private static final String TAG = "InAppBrowser";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("open")) {
            String url = args.getString(0);
            String target = args.getString(1);
            String options = args.getString(2);
            
            openInAppBrowser(url, target, options, callbackContext);
            return true;
        } else if (action.equals("close")) {
            callbackContext.success("InAppBrowser close method called");
            return true;
        } else if (action.equals("show")) {
            callbackContext.success("InAppBrowser show method called");
            return true;
        } else if (action.equals("hide")) {
            callbackContext.success("InAppBrowser hide method called");
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
            // Parse options to check if hidden mode is requested
            boolean isHidden = options != null && options.contains("hidden=yes");
            boolean showLocation = options != null && options.contains("location=yes");
            boolean showToolbar = options != null && options.contains("toolbar=yes");
            boolean hideNavigationButtons = options != null && options.contains("hidenavigationbuttons=yes");
            
            if (isHidden) {
                // Hidden mode - open in background without UI
                Log.d(TAG, "Opening URL in hidden mode: " + url);
                // For now, just return success (implementation will be added later)
                callbackContext.success("URL opened in hidden mode");
            } else {
                // Normal mode - open with UI
                Log.d(TAG, "Opening URL in normal mode: " + url);
                // For now, just return success (implementation will be added later)
                callbackContext.success("InAppBrowser open method called");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error opening InAppBrowser: " + e.getMessage());
            callbackContext.error("Error opening InAppBrowser: " + e.getMessage());
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
