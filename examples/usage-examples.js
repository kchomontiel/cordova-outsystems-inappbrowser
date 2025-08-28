// Examples of using the HiddenInAppBrowser plugin with the new methods

// Example 1: Hidden Mode (Default)
async function openHiddenExample() {
  try {
    // Modern API
    await HiddenInAppBrowser.open({
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
    console.log("URL opened in hidden mode successfully");
  } catch (error) {
    console.error("Error opening URL in hidden mode:", error);
  }
}

// Example 2: External Browser
async function openExternalBrowserExample() {
  try {
    // Modern API
    await HiddenInAppBrowser.openInExternalBrowser({
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
    console.log("URL opened in external browser successfully");
  } catch (error) {
    console.error("Error opening URL in external browser:", error);
  }
}

// Example 3: WebView Mode
async function openWebViewExample() {
  try {
    // Modern API
    await HiddenInAppBrowser.openInWebView({
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
    console.log("URL opened in WebView successfully");
  } catch (error) {
    console.error("Error opening URL in WebView:", error);
  }
}

// Example 4: Legacy API Examples
function legacyApiExamples() {
  // Hidden mode with legacy API
  HiddenInAppBrowser.open(
    "https://example.com",
    "_blank",
    "hidden=yes,location=no,toolbar=no,hidenavigationbuttons=yes",
    function () {
      console.log("Hidden mode opened successfully");
    },
    function (error) {
      console.error("Error opening hidden mode:", error);
    }
  );

  // External browser with legacy API
  HiddenInAppBrowser.openInExternalBrowser(
    "https://example.com",
    "_blank",
    "location=yes,toolbar=yes,zoom=yes",
    function () {
      console.log("External browser opened successfully");
    },
    function (error) {
      console.error("Error opening external browser:", error);
    }
  );

  // WebView with legacy API
  HiddenInAppBrowser.openInWebView(
    "https://example.com",
    "_blank",
    "location=yes,toolbar=yes,zoom=yes",
    function () {
      console.log("WebView opened successfully");
    },
    function (error) {
      console.error("Error opening WebView:", error);
    }
  );
}

// Example 5: Simple URL Examples
async function simpleUrlExamples() {
  try {
    // Simple hidden mode
    await HiddenInAppBrowser.open("https://example.com");
    console.log("Simple hidden mode opened");

    // Simple external browser
    await HiddenInAppBrowser.openInExternalBrowser("https://example.com");
    console.log("Simple external browser opened");

    // Simple WebView
    await HiddenInAppBrowser.openInWebView("https://example.com");
    console.log("Simple WebView opened");
  } catch (error) {
    console.error("Error in simple examples:", error);
  }
}

// Example 6: OutSystems Integration
function outSystemsIntegrationExample() {
  // This is how you would use it in an OutSystems JavaScript action
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

    // Choose the appropriate method based on your needs
    if ($parameters.Mode === "hidden") {
      HiddenInAppBrowser.open(
        $parameters.URL,
        "_blank",
        "hidden=yes,location=no,toolbar=no,hidenavigationbuttons=yes",
        onSuccess,
        onError
      );
    } else if ($parameters.Mode === "external") {
      HiddenInAppBrowser.openInExternalBrowser(
        $parameters.URL,
        "_blank",
        "location=yes,toolbar=yes",
        onSuccess,
        onError
      );
    } else if ($parameters.Mode === "webview") {
      HiddenInAppBrowser.openInWebView(
        $parameters.URL,
        "_blank",
        "location=yes,toolbar=yes",
        onSuccess,
        onError
      );
    }
  });
}

// Export functions for use in other modules
if (typeof module !== "undefined" && module.exports) {
  module.exports = {
    openHiddenExample,
    openExternalBrowserExample,
    openWebViewExample,
    legacyApiExamples,
    simpleUrlExamples,
    outSystemsIntegrationExample,
  };
}
