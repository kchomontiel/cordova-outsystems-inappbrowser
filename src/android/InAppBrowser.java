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

public class InAppBrowser extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("open")) {
            String url = args.getString(0);
            String target = args.getString(1);
            String options = args.getString(2);
            
            // For now, just return success
            callbackContext.success("InAppBrowser open method called");
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
        }
        
        return false;
    }
}
