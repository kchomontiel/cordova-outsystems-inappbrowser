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

#import "CDVInAppBrowser.h"
#import <SafariServices/SafariServices.h>
#import <WebKit/WebKit.h>

@interface CDVInAppBrowser ()
@property (nonatomic, strong) WKWebView *webView;
@property (nonatomic, strong) UIViewController *webViewController;
@property (nonatomic, strong) UINavigationController *navigationController;
@end

@implementation CDVInAppBrowser

- (void)open:(CDVInvokedUrlCommand*)command
{
    NSString* urlString = [command.arguments objectAtIndex:0];
    NSString* target = [command.arguments objectAtIndex:1];
    NSString* options = [command.arguments objectAtIndex:2];
    
    NSURL* url = [NSURL URLWithString:urlString];
    if (!url) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Invalid URL"];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return;
    }
    
    // Parse options
    BOOL isHidden = [options containsString:@"hidden=yes"];
    BOOL showLocation = [options containsString:@"location=yes"];
    BOOL showToolbar = [options containsString:@"toolbar=yes"];
    BOOL hideNavigationButtons = [options containsString:@"hidenavigationbuttons=yes"];
    
    if (isHidden) {
        // Hidden mode - open in background without UI
        NSLog(@"Opening URL in hidden mode: %@", urlString);
        [self openHiddenWebView:url withOptions:options command:command];
    } else {
        // Normal mode - open with UI
        NSLog(@"Opening URL in normal mode: %@", urlString);
        [self openVisibleWebView:url withOptions:options command:command];
    }
}

- (void)openHiddenWebView:(NSURL*)url withOptions:(NSString*)options command:(CDVInvokedUrlCommand*)command
{
    // Create hidden WebView
    WKWebViewConfiguration *config = [[WKWebViewConfiguration alloc] init];
    self.webView = [[WKWebView alloc] initWithFrame:CGRectZero configuration:config];
    
    // Load URL
    NSURLRequest *request = [NSURLRequest requestWithURL:url];
    [self.webView loadRequest:request];
    
    // Return success
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"URL opened in hidden mode"];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)openVisibleWebView:(NSURL*)url withOptions:(NSString*)options command:(CDVInvokedUrlCommand*)command
{
    // Create WebView
    WKWebViewConfiguration *config = [[WKWebViewConfiguration alloc] init];
    self.webView = [[WKWebView alloc] initWithFrame:CGRectZero configuration:config];
    
    // Create view controller
    self.webViewController = [[UIViewController alloc] init];
    self.webViewController.view = self.webView;
    self.webViewController.title = @"WebView";
    
    // Create navigation controller
    self.navigationController = [[UINavigationController alloc] initWithRootViewController:self.webViewController];
    
    // Add close button
    UIBarButtonItem *closeButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemClose target:self action:@selector(closeWebView)];
    self.webViewController.navigationItem.leftBarButtonItem = closeButton;
    
    // Load URL
    NSURLRequest *request = [NSURLRequest requestWithURL:url];
    [self.webView loadRequest:request];
    
    // Present the WebView
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.viewController presentViewController:self.navigationController animated:YES completion:^{
            CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"WebView opened successfully"];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }];
    });
}

- (void)closeWebView
{
    if (self.navigationController) {
        [self.navigationController dismissViewControllerAnimated:YES completion:nil];
        self.navigationController = nil;
        self.webViewController = nil;
        self.webView = nil;
    }
}

- (void)close:(CDVInvokedUrlCommand*)command
{
    [self closeWebView];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"WebView closed successfully"];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)show:(CDVInvokedUrlCommand*)command
{
    if (self.webView && self.navigationController) {
        self.navigationController.modalPresentationStyle = UIModalPresentationFullScreen;
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"WebView shown successfully"];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"No WebView to show"];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void)hide:(CDVInvokedUrlCommand*)command
{
    if (self.navigationController) {
        [self.navigationController dismissViewControllerAnimated:YES completion:nil];
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"WebView hidden successfully"];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"No WebView to hide"];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void)addEventListener:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"InAppBrowser addEventListener method called"];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)removeEventListener:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"InAppBrowser removeEventListener method called"];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)openExternal:(CDVInvokedUrlCommand*)command
{
    NSString* urlString = [command.arguments objectAtIndex:0];
    NSURL* url = [NSURL URLWithString:urlString];
    
    if (!url) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Invalid URL"];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return;
    }
    
    // Open in external browser (Safari)
    if ([[UIApplication sharedApplication] canOpenURL:url]) {
        [[UIApplication sharedApplication] openURL:url options:@{} completionHandler:^(BOOL success) {
            CDVPluginResult* pluginResult;
            if (success) {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"URL opened in external browser"];
            } else {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Failed to open URL in external browser"];
            }
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Cannot open URL"];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

@end
