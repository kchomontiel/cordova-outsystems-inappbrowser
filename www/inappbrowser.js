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

var exec = require('cordova/exec');

var InAppBrowser = function() {
    this.open = function(url, target, options, eventCallback) {
        // If no target specified, use _self
        target = target || "_self";
        
        // If no options specified, use empty object
        options = options || {};
        
        // If no eventCallback specified, use empty function
        eventCallback = eventCallback || function() {};
        
        // Call the native plugin
        exec(eventCallback, eventCallback, "InAppBrowser", "open", [url, target, options]);
    };
    
    this.close = function() {
        exec(null, null, "InAppBrowser", "close", []);
    };
    
    this.show = function() {
        exec(null, null, "InAppBrowser", "show", []);
    };
    
    this.hide = function() {
        exec(null, null, "InAppBrowser", "hide", []);
    };
    
    this.addEventListener = function(eventname, func) {
        exec(null, null, "InAppBrowser", "addEventListener", [eventname]);
    };
    
    this.removeEventListener = function(eventname, func) {
        exec(null, null, "InAppBrowser", "removeEventListener", [eventname]);
    };
};

module.exports = new InAppBrowser();
