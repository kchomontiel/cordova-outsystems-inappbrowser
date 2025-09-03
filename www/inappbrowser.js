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
    cordova.exec(successCallback, errorCallback, "InAppBrowser", "open", [
      url,
      target,
      options,
    ]);
  };

  this.close = function (successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "InAppBrowser", "close", []);
  };

  this.show = function (successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "InAppBrowser", "show", []);
  };

  this.hide = function (successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "InAppBrowser", "hide", []);
  };

  this.addEventListener = function (eventname, callback) {
    cordova.exec(callback, null, "InAppBrowser", "addEventListener", [
      eventname,
    ]);
  };

  this.removeEventListener = function (eventname, callback) {
    cordova.exec(callback, null, "InAppBrowser", "removeEventListener", [
      eventname,
    ]);
  };

  this.openExternal = function (url, successCallback, errorCallback) {
    cordova.exec(
      successCallback,
      errorCallback,
      "InAppBrowser",
      "openExternal",
      [url]
    );
  };
};

module.exports = new InAppBrowser();
