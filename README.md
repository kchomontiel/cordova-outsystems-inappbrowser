# HiddenInAppBrowser Plugin

A simplified Cordova plugin that provides a hidden in-app browser functionality, specifically designed for OutSystems applications. This plugin allows you to open URLs in a hidden WebView without interrupting the user experience.

## Overview

This plugin is a simplified version of the original InAppBrowser plugin, focused specifically on the `hidden` functionality. It's designed to work seamlessly with OutSystems applications and provides a clean, minimal API.

## Features

- **Hidden Mode**: Open URLs in a background WebView without showing any UI
- **External Browser Mode**: Open URLs in the device's default browser
- **OutSystems Compatibility**: Designed to work with OutSystems mobile applications
- **Cross-Platform**: Supports both Android and iOS
- **Dual API Support**: Compatible with both legacy and modern API formats

## Installation

### From GitHub

```bash
cordova plugin add https://github.com/kchomontiel/cordova-outsystems-inappbrowser.git
```

### From Local Path

```bash
cordova plugin add <path-to-repo-local-clone>
```

## Supported Platforms

- ✅ Android
- ✅ iOS

## API Reference

### Open Method

The plugin provides a single `open` method that supports both legacy and modern API formats.

#### Legacy API (OutSystems Compatible)

```javascript
cordova.plugins.HiddenInAppBrowser.open(
  url, // String: The URL to open
  target, // String: Target (e.g., '_blank')
  optionsString, // String: Comma-separated options
  successCallback, // Function: Success callback
  errorCallback // Function: Error callback
);
```

**Example:**

```javascript
cordova.plugins.HiddenInAppBrowser.open(
  "https://example.com",
  "_blank",
  "hidden=yes,location=no,toolbar=no,hidenavigationbuttons=yes",
  function () {
    console.log("Success");
  },
  function (error) {
    console.error("Error:", error);
  }
);
```

#### Modern API

```javascript
cordova.plugins.HiddenInAppBrowser.open(options);
```

**Example:**

```javascript
cordova.plugins.HiddenInAppBrowser.open({
  url: "https://example.com",
  hidden: true,
  location: false,
  toolbar: false,
});
```

### Options

| Option                            | Type           | Default  | Description                    |
| --------------------------------- | -------------- | -------- | ------------------------------ |
| `url`                             | String         | Required | The URL to open                |
| `hidden`                          | Boolean/String | `true`   | Whether to open in hidden mode |
| `location`                        | Boolean/String | `false`  | Show/hide location bar         |
| `toolbar`                         | Boolean/String | `false`  | Show/hide toolbar              |
| `zoom`                            | Boolean/String | `false`  | Enable/disable zoom            |
| `hardwareback`                    | Boolean/String | `true`   | Use hardware back button       |
| `mediaPlaybackRequiresUserAction` | Boolean/String | `false`  | Require user action for media  |
| `shouldPauseOnSuspend`            | Boolean/String | `false`  | Pause on app suspend           |
| `clearsessioncache`               | Boolean/String | `true`   | Clear session cache            |
| `cache`                           | Boolean/String | `false`  | Enable/disable cache           |
| `disallowoverscroll`              | Boolean/String | `true`   | Disable overscroll             |
| `hidenavigationbuttons`           | Boolean/String | `true`   | Hide navigation buttons        |
| `hideurlbar`                      | Boolean/String | `true`   | Hide URL bar                   |
| `fullscreen`                      | Boolean/String | `true`   | Enable fullscreen              |

## Usage in OutSystems

### JavaScript Action

```javascript
require(["PluginManager"], function (module) {
  function onSuccess() {
    $parameters.Success = true;
    $resolve();
  }

  function onError(error) {
    $parameters.Success = false;
    $parameters.ErrorCode = error.code;
    $parameters.ErrorMessage = error.message;
    $resolve();
  }

  cordova.plugins.HiddenInAppBrowser.open(
    $parameters.URL,
    "_blank",
    "hidden=yes,location=no,toolbar=no,hidenavigationbuttons=yes",
    onSuccess,
    onError
  );
});
```

### Parameters

- **URL**: The URL to open (e.g., `https://www.example.com`)
- **Success**: Boolean indicating if the operation was successful
- **ErrorCode**: Error code if the operation failed
- **ErrorMessage**: Error message if the operation failed

## Behavior

### Hidden Mode (`hidden=yes`)

When `hidden` is set to `yes` or `true`:

- ✅ URL loads in a background WebView
- ✅ No UI is shown to the user
- ✅ User remains in the current app
- ✅ No interruption to user experience
- ✅ Useful for background operations like authentication

### Visible Mode (`hidden=no`)

When `hidden` is set to `no` or `false`:

- ✅ URL opens in the device's default browser
- ✅ User sees the browser interface
- ✅ Traditional browser experience

## Technical Details

### Architecture

- **JavaScript Layer**: Handles API compatibility and parameter parsing
- **Native Layer**:
  - **Android**: Uses WebView with UI thread safety
  - **iOS**: Uses WKWebView for hidden operations

### Thread Safety

The plugin ensures all UI operations are performed on the main UI thread to prevent crashes and threading issues.

### Error Handling

The plugin provides comprehensive error handling:

- Invalid URLs
- Network connectivity issues
- Platform-specific errors
- Threading errors

## Development

### Building

```bash
npm install
npm run build
```

### Project Structure

```
src/
├── android/
│   └── HiddenInAppBrowser.kt    # Android native implementation
├── ios/
│   └── HiddenInAppBrowser.swift # iOS native implementation
└── www/
    ├── index.ts                 # Main JavaScript entry point
    ├── definitions.ts           # TypeScript definitions
    ├── defaults.ts              # Default options
    └── web.ts                   # Web platform implementation
```

## Troubleshooting

### Common Issues

1. **"URL is required" error**: Ensure the URL parameter is properly passed
2. **Thread errors**: The plugin automatically handles UI thread operations
3. **Plugin not found**: Ensure the plugin is properly installed in your Cordova project

### Debug Logs

The plugin provides extensive debug logging. Check the browser console for logs starting with `HiddenInAppBrowser.open`.

## License

This project is licensed under the Apache License 2.0.

## Support

For issues and questions:

1. Check the troubleshooting section
2. Review the debug logs
3. Ensure compatibility with your OutSystems version
