# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

The changes documented here do not include those from the original repository.

## [Unreleased]

### Features

- Add new `openInWebViewHidden` method that allows opening WebView in hidden state for background operations
- Support for hidden WebView with configurable timeout and auto-close functionality
- Enhanced WebView options with hidden-specific configurations

## 1.5.0

### Features

- Android: Add support for PDF files in the WebView via PDF.js (only for the OpenInWebView option) [RMET-2053](https://outsystemsrd.atlassian.net/browse/RMET-2053)

## 1.4.1

### Features

- Added support for predictive back navigation for Android 13+ (https://outsystemsrd.atlassian.net/browse/RMET-4335)

## 1.4.0

#### Features

- Add support for passing custom headers to web view (only for the OpenInWebView option). [RMET-4287](https://outsystemsrd.atlassian.net/browse/RMET-4287).

## 1.3.1

### iOS

#### Features

- Added support for back and forward swipe navigation gestures in `WKWebView` via the `allowsBackForwardNavigationGestures` option. (for openInWebView option only) (https://outsystemsrd.atlassian.net/browse/RMET-4216).

## 1.3.0

#### Features

- Users now receive an event when the navigation occurs (for openInWebView option only) (https://outsystemsrd.atlassian.net/browse/RMET-4120).

### Android

#### Chores

- Update dependency to Android native library (https://outsystemsrd.atlassian.net/browse/RMET-3982).

## 1.2.1

### Android

#### Fixes

- Remove unnecessary permissions from AndroidManifest (https://outsystemsrd.atlassian.net/browse/RMET-3987).

## 1.2.0

### Android

#### Chores

- Bumps Kotlin and Gradle versions (https://outsystemsrd.atlassian.net/browse/RMET-3887).

## 1.1.0

### Android

#### Chores
- Remove unnecesary `kotlin-kapt` plugin from build.gradle file (https://outsystemsrd.atlassian.net/browse/RMET-3804).

### Features
- Handle Edge-to-Edge on all Android versions.

## 1.0.2
- Android: Fix issue where the custom tabs browser wasn't being closed when navigating back to the app
- Android: Fix race condition that caused the `BrowserFinished` event to not be fired in some instances with the system browser

### Fixes
- Android: Fix issue where some URLs weren't being open in Custom Tabs and the External Browser (https://outsystemsrd.atlassian.net/browse/RMET-3680)

## 1.0.0

### Features
- Add `Close` feature for WebView and System Browser on Android (https://outsystemsrd.atlassian.net/browse/RMET-3428).
- Add error codes and messages on iOS (https://outsystemsrd.atlassian.net/browse/RMET-3465).
- Format error codes and messages on Android (https://outsystemsrd.atlassian.net/browse/RMET-3466).
- Add permissions requests and opening file chooser to `OpenInWebView` feature on Android (https://outsystemsrd.atlassian.net/browse/RMET-3534).
- Add error and loading screens for `OpenInWebView` feature for Android (https://outsystemsrd.atlassian.net/browse/RMET-3492).
- Add custom error page for `OpenInWebView` feature (https://outsystemsrd.atlassian.net/browse/RMET-3491).
- Add browser events to `OpenInSystemBrowser` feature on Android (https://outsystemsrd.atlassian.net/browse/RMET-3431).
- Add `OpenInSystemBrowser`'s features on Android (https://outsystemsrd.atlassian.net/browse/RMET-3424).
- Add possibility to override the user agent used in `OpenInWebView`'s webview (https://outsystemsrd.atlassian.net/browse/RMET-3490).
- Add browser events to `OpenInWebView` feature (https://outsystemsrd.atlassian.net/browse/RMET-3432).
- Add `OpenInWebView` with current features and default UI on Android (https://outsystemsrd.atlassian.net/browse/RMET-3426).
- Add `Close` feature on iOS (https://outsystemsrd.atlassian.net/browse/RMET-3427).
- Add `OpenInWebView`'s interface customisations on iOS (https://outsystemsrd.atlassian.net/browse/RMET-3489).
- Add `OpenInWebView`'s event listeners on iOS (https://outsystemsrd.atlassian.net/browse/RMET-3430).
- Add `OpenInWebView`'s features on iOS (https://outsystemsrd.atlassian.net/browse/RMET-3425).
- Add `OpenInSystemBrowser`'s event listeners on iOS (https://outsystemsrd.atlassian.net/browse/RMET-3429).
- Add `OpenInSystemBrowser`'s features on iOS (https://outsystemsrd.atlassian.net/browse/RMET-3423).
- Add `OpenInExternalBrowser`'s features on Android (https://outsystemsrd.atlassian.net/browse/RMET-3422).
- Add `OpenInExternalBrowser` on iOS (https://outsystemsrd.atlassian.net/browse/RMET-3421).
- [Bridge] Adds cordova bridge, with types (https://outsystemsrd.atlassian.net/browse/RMET-3419).
- Add content to `README` (https://outsystemsrd.atlassian.net/browse/RMET-3473).
