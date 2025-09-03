#import <Cordova/CDV.h>

@interface HiddenInAppBrowser : CDVPlugin

- (void)openInWebView:(CDVInvokedUrlCommand*)command;
- (void)openHidden:(CDVInvokedUrlCommand*)command;
- (void)openInExternalBrowser:(CDVInvokedUrlCommand*)command;

@end
