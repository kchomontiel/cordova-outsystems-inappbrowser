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

var InAppBrowser = function () {
  this.open = function (url, target, options, successCallback, errorCallback) {
    if (typeof cordova !== "undefined" && cordova.exec) {
      cordova.exec(successCallback, errorCallback, "InAppBrowser", "open", [
        url,
        target,
        options,
      ]);
    } else {
      console.error("Cordova not available");
      if (errorCallback) errorCallback("Cordova not available");
    }
  };

  this.close = function (successCallback, errorCallback) {
    if (typeof cordova !== "undefined" && cordova.exec) {
      cordova.exec(successCallback, errorCallback, "InAppBrowser", "close", []);
    } else {
      console.error("Cordova not available");
      if (errorCallback) errorCallback("Cordova not available");
    }
  };

  this.show = function (successCallback, errorCallback) {
    if (typeof cordova !== "undefined" && cordova.exec) {
      cordova.exec(successCallback, errorCallback, "InAppBrowser", "show", []);
    } else {
      console.error("Cordova not available");
      if (errorCallback) errorCallback("Cordova not available");
    }
  };

  this.hide = function (successCallback, errorCallback) {
    if (typeof cordova !== "undefined" && cordova.exec) {
      cordova.exec(successCallback, errorCallback, "InAppBrowser", "hide", []);
    } else {
      console.error("Cordova not available");
      if (errorCallback) errorCallback("Cordova not available");
    }
  };

  this.addEventListener = function (eventname, callback) {
    if (typeof cordova !== "undefined" && cordova.exec) {
      cordova.exec(callback, null, "InAppBrowser", "addEventListener", [
        eventname,
      ]);
    } else {
      console.error("Cordova not available");
    }
  };

  this.removeEventListener = function (eventname, callback) {
    if (typeof cordova !== "undefined" && cordova.exec) {
      cordova.exec(callback, null, "InAppBrowser", "removeEventListener", [
        eventname,
      ]);
    } else {
      console.error("Cordova not available");
    }
  };

  this.openExternal = function (url, successCallback, errorCallback) {
    if (typeof cordova !== "undefined" && cordova.exec) {
      cordova.exec(
        successCallback,
        errorCallback,
        "InAppBrowser",
        "openExternal",
        [url]
      );
    } else {
      console.error("Cordova not available");
      if (errorCallback) errorCallback("Cordova not available");
    }
  };
};

// Create the plugin instance
var inAppBrowser = new InAppBrowser();

// Function to expose the plugin globally
function exposePlugin() {
  if (typeof window !== "undefined") {
    // Primary location (standard Apache way)
    if (window.cordova) {
      window.cordova.InAppBrowser = inAppBrowser;

      // Secondary location (for compatibility)
      if (window.cordova.plugins) {
        window.cordova.plugins.InAppBrowser = inAppBrowser;
      }
    }

    // Tertiary location (direct global)
    window.InAppBrowser = inAppBrowser;

    // Legacy location (for old code)
    window.multibrowser = inAppBrowser;

    console.log("InAppBrowser plugin exposed globally");
  }
}

// Expose immediately if possible
exposePlugin();

// Also expose when deviceready fires (in case Cordova loads later)
if (typeof document !== "undefined") {
  document.addEventListener(
    "deviceready",
    function () {
      exposePlugin();
    },
    false
  );
}

// Expose for module systems
if (typeof module !== "undefined" && module.exports) {
  module.exports = inAppBrowser;
}
