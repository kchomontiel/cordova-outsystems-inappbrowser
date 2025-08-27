# cordova-outsystems-inappbrowser

*This plugin is SUPPORTED by OutSystems. Customers entitled to Support Services may obtain assistance through Support.*

The InAppBrowser Plugin provides a web browser view that allows you to load any web page externally. It behaves as a standard web browser and is useful to load untrusted content without risking your application's security.

## Installation

```console
cordova plugin add <path-to-repo-local-clone>
```

It's also possible to install via the repo's URL directly.

```console
cordova plugin add https://github.com/OutSystems/cordova-outsystems-inappbrowser
```

## Supported Platforms

- iOS
- Android

## Methods

* [openInExternalBrowser](#open-in-external-browser)
* [openInSystemBrowser](#open-in-system-browser)
* [openInWebView](#open-in-webview)
* [openInWebViewHidden](#open-in-webview-hidden)
* [close](#close)

### Open in External Browser

```js
cordova.plugins.OSInAppBrowser.openInExternalBrowser(url, successCallback, errorCallback);
````

An action that opens the web content of the given URL in a separate browser, outside of your mobile application. 
In case of an error, it returns the associated error code and message ([check the error table](#errors)).

The action is composed of the following parameters:

- **url**: The URL to be opened. It must contain either 'http' or 'https' as the protocol prefix.
- **successCallback**: A structure indicating that the action was successful.
- **errorCallback**: A structure indicating that the action was not successful. It returns an "error" structure, composed of:
	- **code**: A text containing the error code.
	- **message**: A text containing the error message.

### Open in System Browser

```js
cordova.plugins.OSInAppBrowser.openInSystemBrowser(url, options, successCallback, errorCallback, browserCallbacks);
```

An action that opens the web content of the given URL in your mobile app, using SafariViewController for iOS and Custom Tabs for Android.
In case of an error, it returns the associated error code and message ([check the error table](#errors)).

The action is composed of the following parameters:

- **url**: The URL to be opened. It must contain either 'http' or 'https' as the protocol prefix.
- **options**: A structure containing some configurations to apply to the System Browser. It's composed of the following properties:
	- **android**: Android-specific System Browser options.
		- **showTitle**: A boolean that, if set to true, enables the title display.
		- **hideToolbarOnScroll**: A boolean that, if set to True, hides the toolbar when scrolling.
		- **viewStyle**: An `AndroidViewStyle` enumerator that sets the presentation style of CustomTabs.
		- **bottomSheetOptions**: An `AndroidBottomSheetOptions` structure that sets the options for the bottom sheet when this is selected as the `viewStyle`. If `viewStyle` is `FULL_SCREEN`, this will be ignored.
		- **startAnimation**: An `AndroidAnimation` enumerator that sets the start animation for when the browser appears.
		- **exitAnimation**: An `AndroidAnimation` enumerator that sets the exit animation for when the browser disappears.
	- **iOS**: iOS-specific System Browser options.
		- **closeButtonText**: A `DismissStyle` enumerator that sets a text to use as the close button's caption.
		- **viewStyle**: An `iOSViewstyle` enumerator that sets the presentation style of SafariViewController.
		- **animationEffect**: An `iOSAnimation` enumerator that sets the transition style of SafariViewController.
		- **enableBarsCollapsing**: A boolean that, if set to true, enables bars to collapse on scrolling down.
		- **enableReadersMode**: A boolean that, if set to true, enables readers mode.
- **successCallback**: A structure indicating that the action was successful.
- **errorCallback**: A structure indicating that the action was not successful. It returns an "error" structure, composed of:
	- **code**: A text containing the error code.
	- **message**: A text containing the error message.
- **browserCallbacks**: A `BrowserCallbacks` structure that contains all the callbacks that are triggered when opening a browser. The callbacks are based on the events defined on the `CallbackEvent` enumerator: `PAGE_CLOSED` and `PAGE_LOAD_COMPLETED`.

### Open in Web View

```js
cordova.plugins.OSInAppBrowser.openInWebView(url, options, successCallback, errorCallback, browserCallbacks);
```

An action that opens the web content of the given URL in your mobile app using a custom web view within your application.
In case of an error, it returns the associated error code and message ([check the error table](#errors)).

The action is composed of the following parameters:

- **url**: The URL to be opened. It must contain either 'http' or 'https' as the protocol prefix.
- **options**: A structure containing some configurations to apply to the Web View. It's composed of the following properties:
	- **showURL**: A boolean that, if set to true, displays the URL on the Web View.
	- **showToolbar**: A boolean that, if set to true, displays the toolbar on the Web View.
	- **closeButtonText**: Sets the text to display on the Close button on the Web View.
	- **toolbarPosition**: A `ToolbarPosition` enumerator that sets the position to display the Toolbar on the Web View.
	- **showNavigationButtons**: A boolean that, if set to true, displays the navigation buttons.
	- **leftToRight**: A boolean that, if set to true, swaps the positions of the navigation buttons and the close button. Specifically, the navigation buttons go to the left and the close button to the right.
	- **clearCache**: A boolean that, if set to true, has the Web View's cookie cache cleared before a new window is opened.
	- **clearSessionCache**: A boolean that, if set to true, has the session cookie cache cleared before a new window is opened.
	- **mediaPlaybackRequiresUserAction**: A boolean that, if set to true, prevents HTML5 audio or video from auto-playing.
	- **customWebViewUserAgent**: Sets a custom user agent to open the Web View with. If empty or not set, the parameter will be ignored.
	- **android**: Android-specific Web View options.
		- **allowZoom**: A boolean that, if set to true, shows the Android browser's zoom controls.
		- **hardwareBack**: A boolean that, if set to true, uses the hardware back button to navigate backwards through the Web View's history. If there is no previous page, the Web View will close.
		- **pauseMedia**: A boolean that, if set to true, makes the Web View pause/resume with the app to stop background audio. Note that this may be required to avoid Google Play issues like YouTube video playback while the application is in the background.
	- **iOS**: iOS-specific Web View options.
		- **allowOverScroll**:  A boolean that, if set to true, turns on the Web View bounce property.
		- **enableViewportScale**: A boolean that, if set to true, prevents viewport scaling through a meta tag.
		- **allowInLineMediaPlayback**: A boolean that, if set to true, allows in-line HTML5 media playback, displaying within the browser window rather than a device-specific playback interface. Note: The HTML's video element must also include the webkit-playsinline attribute.
		- **surpressIncrementalRendering**: A boolean that, if set to true, waits until all new view content is received before being rendered.
		- **viewStyle**: An `iOSViewstyle` enumerator that sets the presentation style of the Web View.
		- **animationEffect**: An `iOSAnimation` enumerator that sets the transition style of the Web View.
		- **allowsBackForwardNavigationGestures**:  A boolean that, if set to true, enables back and forward swipe gestures in the Web View.
- **successCallback**: A structure indicating that the action was successful.
- **errorCallback**: A structure indicating that the action was not successful. It returns an "error" structure, composed of:
	- **code**: A text containing the error code.
	- **message**: A text containing the error message.
- **browserCallbacks**: A `BrowserCallbacks` structure that contains all the callbacks that are triggered when opening a browser. The callbacks are based on the events defined on the `CallbackEvent` enumerator: `PAGE_CLOSED` and `PAGE_LOAD_COMPLETED`.

<<<<<<< HEAD
### Open in Web View Hidden

```js
cordova.plugins.OSInAppBrowser.openInWebViewHidden(url, options, successCallback, errorCallback, browserCallbacks);
```

An action that opens the web content of the given URL in your mobile app using a custom web view within your application, but hides the browser view from the user. This is useful for background operations like authentication, data fetching, or silent processing.
In case of an error, it returns the associated error code and message ([check the error table](#errors)).

The action is composed of the following parameters:

- **url**: The URL to be opened. It must contain either 'http' or 'https' as the protocol prefix.
- **options**: A structure containing some configurations to apply to the Hidden Web View. It includes all the properties from the regular Web View options plus:
	- **hidden**: A boolean that, if set to true, hides the Web View from the user interface. Default is true.
	- **autoClose**: A boolean that, if set to true, automatically closes the Web View after the page load is completed. Default is true.
	- **timeout**: A number that sets the timeout in milliseconds for the Web View operation. If set to 0, no timeout is applied. Default is 30000 (30 seconds).
	- **showURL**: A boolean that, if set to true, displays the URL on the Web View. Default is false for hidden Web View.
	- **showToolbar**: A boolean that, if set to true, displays the toolbar on the Web View. Default is false for hidden Web View.
	- **showNavigationButtons**: A boolean that, if set to true, displays the navigation buttons. Default is false for hidden Web View.
	- **closeButtonText**: Sets the text to display on the Close button on the Web View.
	- **toolbarPosition**: A `ToolbarPosition` enumerator that sets the position to display the Toolbar on the Web View.
	- **leftToRight**: A boolean that, if set to true, swaps the positions of the navigation buttons and the close button. Specifically, the navigation buttons go to the left and the close button to the right.
	- **clearCache**: A boolean that, if set to true, has the Web View's cookie cache cleared before a new window is opened.
	- **clearSessionCache**: A boolean that, if set to true, has the session cookie cache cleared before a new window is opened.
	- **mediaPlaybackRequiresUserAction**: A boolean that, if set to true, prevents HTML5 audio or video from auto-playing.
	- **customWebViewUserAgent**: Sets a custom user agent to open the Web View with. If empty or not set, the parameter will be ignored.
	- **android**: Android-specific Web View options.
		- **allowZoom**: A boolean that, if set to true, shows the Android browser's zoom controls.
		- **hardwareBack**: A boolean that, if set to true, uses the hardware back button to navigate backwards through the Web View's history. If there is no previous page, the Web View will close.
		- **pauseMedia**: A boolean that, if set to true, makes the Web View pause/resume with the app to stop background audio. Note that this may be required to avoid Google Play issues like YouTube video playback while the application is in the background.
	- **iOS**: iOS-specific Web View options.
		- **allowOverScroll**:  A boolean that, if set to true, turns on the Web View bounce property.
		- **enableViewportScale**: A boolean that, if set to true, prevents viewport scaling through a meta tag.
		- **allowInLineMediaPlayback**: A boolean that, if set to true, allows in-line HTML5 media playback, displaying within the browser window rather than a device-specific playback interface. Note: The HTML's video element must also include the webkit-playsinline attribute.
		- **surpressIncrementalRendering**: A boolean that, if set to true, waits until all new view content is received before being rendered.
		- **viewStyle**: An `iOSViewstyle` enumerator that sets the presentation style of the Web View.
		- **animationEffect**: An `iOSAnimation` enumerator that sets the transition style of the Web View.
		- **allowsBackForwardNavigationGestures**:  A boolean that, if set to true, enables back and forward swipe gestures in the Web View.
- **successCallback**: A structure indicating that the action was successful.
- **errorCallback**: A structure indicating that the action was not successful. It returns an "error" structure, composed of:
	- **code**: A text containing the error code.
	- **message**: A text containing the error message.
- **browserCallbacks**: A `BrowserCallbacks` structure that contains all the callbacks that are triggered when opening a browser. The callbacks are based on the events defined on the `CallbackEvent` enumerator: `PAGE_CLOSED` and `PAGE_LOAD_COMPLETED`.

**Example usage:**

```javascript
// Open a hidden WebView for background authentication
cordova.plugins.OSInAppBrowser.openInWebViewHidden(
    'https://example.com/auth',
    {
        hidden: true,
        autoClose: true,
        timeout: 30000,
        showURL: false,
        showToolbar: false,
        showNavigationButtons: false,
        clearCache: true,
        clearSessionCache: true
    },
    function() {
        console.log('Hidden WebView opened successfully');
    },
    function(error) {
        console.error('Error opening hidden WebView:', error);
    },
    {
        onbrowserClosed: function() {
            console.log('Hidden WebView closed');
        },
        onbrowserPageLoaded: function() {
            console.log('Page loaded in hidden WebView');
        }
    }
);
```

=======
>>>>>>> b38b391a03eed0314f62fc51f1de1dbb49f52900
### Close

```js
cordova.plugins.OSInAppBrowser.close(successCallback, errorCallback);
```

An action that closes the currently active browser. It can be used to close browsers launched through the `openInSystemBrowser` or `openInWebView` actions.
In case of an error, it returns the associated error code and message ([check the error table](#errors)).

The action is composed of the following parameters:

- **successCallback**: A structure indicating that the action was successful.
- **errorCallback**: A structure indicating that the action was not successful. It returns an "error" structure, composed of:
	- **code**: A text containing the error code.
	- **message**: A text containing the error message.

## Errors

|Code|Message|iOS|Android|
|:-|:-|:-:|:-:|
|OS-PLUG-IABP-0005|The 'openInExternalBrowser' input parameters aren't valid.|:white_check_mark:|:white_check_mark:|
|OS-PLUG-IABP-0006|The 'openInSystemBrowser' input parameters aren't valid.|:white_check_mark:|:white_check_mark:|
|OS-PLUG-IABP-0007|The 'openInWebView' input parameters aren't valid.|:white_check_mark:|:white_check_mark:|
|OS-PLUG-IABP-0008|External browser couldn't open the following URL: '\(url)'|:white_check_mark:|:white_check_mark:|
|OS-PLUG-IABP-0009|SafariViewController couldn't open the following URL: '\(url)'|:white_check_mark:|:x:|
|OS-PLUG-IABP-0010|Custom Tabs couldn't open the following URL: '\(url)'|:x:|:white_check_mark:|
|OS-PLUG-IABP-0011|The WebView couldn't open the following URL: '\(url)'|:white_check_mark:|:white_check_mark:|
|OS-PLUG-IABP-0012|There’s no browser view to close.|:white_check_mark:|:white_check_mark:|