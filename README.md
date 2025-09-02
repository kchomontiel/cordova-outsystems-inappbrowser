# Cordova OutSystems InAppBrowser Plugin

This plugin provides three different ways to open URLs in your Cordova application:

1. **Hidden Mode** (`open`) - Opens URLs in a hidden WebView for background processing
2. **External Browser** (`openInExternalBrowser`) - Opens URLs in the device's default browser
3. **WebView Mode** (`openInWebView`) - Opens URLs in a visible WebView within the app

## 🚀 Quick Start for OutSystems

### Installation from Repository

```bash
# Direct installation from Git
cordova plugin add https://github.com/[your-username]/cordova-outsystems-inappbrowser.git

# Or clone and install locally
git clone https://github.com/[your-username]/cordova-outsystems-inappbrowser.git
cd cordova-outsystems-inappbrowser
npm install && npm run build
cordova plugin add .
```

### Basic Usage in OutSystems

```javascript
// Wait for Cordova to be ready
document.addEventListener(
  "deviceready",
  function () {
    // Open visible WebView
    window.HiddenInAppBrowser.openInWebView("https://www.google.com", {
      success: function () {
        console.log("WebView opened");
      },
      error: function (error) {
        console.error("Error:", error);
      },
    });
  },
  false
);
```

📖 **For detailed OutSystems installation instructions, see [INSTALL_OUTSYSTEMS.md](INSTALL_OUTSYSTEMS.md)**

## Installation

```bash
cordova plugin add cordova-outsystems-inappbrowser
```

## Usage

### Hidden Mode (Default)

Opens a URL in a hidden WebView for background processing:

```javascript
// Modern API
HiddenInAppBrowser.open({
  url: "https://example.com",
  hidden: true,
  location: false,
  toolbar: false,
  zoom: false,
  hardwareback: true,
  mediaPlaybackRequiresUserAction: false,
  shouldPauseOnSuspend: false,
  clearsessioncache: true,
  cache: false,
  disallowoverscroll: true,
  hidenavigationbuttons: true,
  hideurlbar: true,
  fullscreen: true,
});

// Legacy API
HiddenInAppBrowser.open(
  "https://example.com",
  "_blank",
  "hidden=yes,location=no,toolbar=no"
);

// Simple URL
HiddenInAppBrowser.open("https://example.com");
```

### External Browser

Opens a URL in the device's default browser:

```javascript
// Modern API
HiddenInAppBrowser.openInExternalBrowser({
  url: "https://example.com",
  hidden: false,
  location: true,
  toolbar: true,
  zoom: true,
  hardwareback: true,
  mediaPlaybackRequiresUserAction: false,
  shouldPauseOnSuspend: false,
  clearsessioncache: false,
  cache: true,
  disallowoverscroll: false,
  hidenavigationbuttons: false,
  hideurlbar: false,
  fullscreen: false,
});

// Legacy API with different targets
HiddenInAppBrowser.openInExternalBrowser(
  "https://example.com",
  "_blank",
  "location=yes,toolbar=yes"
);
HiddenInAppBrowser.openInExternalBrowser(
  "https://example.com",
  "_system",
  "location=yes,toolbar=yes"
);

// Simple URL
HiddenInAppBrowser.openInExternalBrowser("https://example.com");
```

### WebView Mode

Opens a URL in a visible WebView within the app:

```javascript
// Modern API
HiddenInAppBrowser.openInWebView({
  url: "https://example.com",
  hidden: false,
  location: true,
  toolbar: true,
  zoom: true,
  hardwareback: true,
  mediaPlaybackRequiresUserAction: false,
  shouldPauseOnSuspend: false,
  clearsessioncache: false,
  cache: true,
  disallowoverscroll: false,
  hidenavigationbuttons: false,
  hideurlbar: false,
  fullscreen: false,
});

// Legacy API with different targets
HiddenInAppBrowser.openInWebView(
  "https://example.com",
  "_blank",
  "location=yes,toolbar=yes"
);
HiddenInAppBrowser.openInWebView(
  "https://example.com",
  "_self",
  "location=yes,toolbar=yes"
);

// Simple URL
HiddenInAppBrowser.openInWebView("https://example.com");
```

## Targets

The `target` parameter specifies how and where the URL should be opened. All methods support the following targets:

### Available Targets

| Target    | Description                   | Hidden Mode        | External Browser       | WebView Mode               |
| --------- | ----------------------------- | ------------------ | ---------------------- | -------------------------- |
| `_blank`  | Opens in a new window/context | ✅ Hidden WebView  | ✅ New external window | ✅ New WebView window      |
| `_self`   | Opens in the current window   | ✅ Current WebView | ✅ External browser    | ✅ Replace current content |
| `_system` | Opens in system browser       | ✅ Hidden WebView  | ✅ System browser      | ✅ New WebView window      |

### Target Usage Examples

```javascript
// _blank - New window (recommended for most cases)
HiddenInAppBrowser.openInWebView(
  "https://example.com",
  "_blank",
  "location=yes,toolbar=yes"
);

// _self - Replace current content
HiddenInAppBrowser.openInWebView(
  "https://example.com",
  "_self",
  "location=yes,toolbar=yes"
);

// _system - System browser
HiddenInAppBrowser.openInExternalBrowser(
  "https://example.com",
  "_system",
  "location=yes,toolbar=yes"
);
```

### Target Recommendations by Method

#### Hidden Mode (`open`)

```javascript
// Recommended: _blank for background operations
HiddenInAppBrowser.open("https://example.com", "_blank", "hidden=yes");
```

#### External Browser (`openInExternalBrowser`)

```javascript
// Recommended: _blank or _system for external navigation
HiddenInAppBrowser.openInExternalBrowser("https://example.com", "_blank");
HiddenInAppBrowser.openInExternalBrowser("https://example.com", "_system");
```

#### WebView Mode (`openInWebView`)

```javascript
// Recommended: _blank for new window, _self to replace content
HiddenInAppBrowser.openInWebView(
  "https://example.com",
  "_blank",
  "location=yes"
);
HiddenInAppBrowser.openInWebView(
  "https://example.com",
  "_self",
  "location=yes"
);
```

## Options

All methods support the following options:

| Option                            | Type    | Default (Hidden) | Default (External) | Default (WebView) | Description                            |
| --------------------------------- | ------- | ---------------- | ------------------ | ----------------- | -------------------------------------- |
| `url`                             | string  | -                | -                  | -                 | The URL to open                        |
| `hidden`                          | boolean | true             | false              | false             | Whether to hide the browser            |
| `location`                        | boolean | false            | true               | true              | Show/hide the location bar             |
| `toolbar`                         | boolean | false            | true               | true              | Show/hide the toolbar                  |
| `zoom`                            | boolean | false            | true               | true              | Enable/disable zoom controls           |
| `hardwareback`                    | boolean | true             | true               | true              | Enable/disable hardware back button    |
| `mediaPlaybackRequiresUserAction` | boolean | false            | false              | false             | Require user action for media playback |
| `shouldPauseOnSuspend`            | boolean | false            | false              | false             | Pause on app suspend                   |
| `clearsessioncache`               | boolean | true             | false              | false             | Clear session cache                    |
| `cache`                           | boolean | false            | true               | true              | Enable/disable cache                   |
| `disallowoverscroll`              | boolean | true             | false              | false             | Disable overscroll                     |
| `hidenavigationbuttons`           | boolean | true             | false              | false             | Hide navigation buttons                |
| `hideurlbar`                      | boolean | true             | false              | false             | Hide URL bar                           |
| `fullscreen`                      | boolean | true             | false              | false             | Open in fullscreen mode                |

## Platform Support

- **Android**: All three modes supported
- **iOS**: All three modes supported
- **Web**: All three modes supported (uses window.open)

### Platform-Specific Target Behavior

#### Android

- `_blank`: Opens in a new activity/window
- `_self`: Replaces current content
- `_system`: Opens in system browser

#### iOS

- `_blank`: Opens in a new view controller
- `_self`: Replaces current content
- `_system`: Opens in Safari or default browser

#### Web

- `_blank`: Opens in a new browser tab/window
- `_self`: Opens in the same tab
- `_system`: Similar behavior to `_blank`

## Error Handling

All methods return a Promise and support both Promise and callback patterns:

```javascript
// Promise pattern
try {
  await HiddenInAppBrowser.open("https://example.com");
  console.log("URL opened successfully");
} catch (error) {
  console.error("Error opening URL:", error);
}

// Callback pattern
HiddenInAppBrowser.open(
  "https://example.com",
  "_blank",
  "hidden=yes",
  function () {
    console.log("URL opened successfully");
  },
  function (error) {
    console.error("Error opening URL:", error);
  }
);

// OutSystems Integration with different targets
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

  // Choose target based on requirements
  const target = $parameters.Target || "_blank";

  if ($parameters.Mode === "hidden") {
    HiddenInAppBrowser.open(
      $parameters.URL,
      target,
      "hidden=yes,location=no,toolbar=no",
      onSuccess,
      onError
    );
  } else if ($parameters.Mode === "external") {
    HiddenInAppBrowser.openInExternalBrowser(
      $parameters.URL,
      target,
      "location=yes,toolbar=yes",
      onSuccess,
      onError
    );
  } else if ($parameters.Mode === "webview") {
    HiddenInAppBrowser.openInWebView(
      $parameters.URL,
      target,
      "location=yes,toolbar=yes",
      onSuccess,
      onError
    );
  }
});
```

## License

This project is licensed under the MIT License.
