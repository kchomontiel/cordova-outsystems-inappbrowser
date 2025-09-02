"use strict";
Object.defineProperty(exports, Symbol.toStringTag, { value: "Module" });
const DEFAULT_OPEN_OPTIONS = {
  url: "",
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
  fullscreen: true
};
const DEFAULT_EXTERNAL_BROWSER_OPTIONS = {
  url: "",
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
  fullscreen: false
};
const DEFAULT_WEBVIEW_OPTIONS = {
  url: "",
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
  fullscreen: false
};
function open(urlOrOptions, target, optionsString, onSuccess, onError) {
  console.log("HiddenInAppBrowser.open - Raw parameters received:", {
    urlOrOptions,
    target,
    optionsString,
    onSuccess,
    onError
  });
  console.log("HiddenInAppBrowser.open - Parameters types:", {
    urlOrOptions: typeof urlOrOptions,
    target: typeof target,
    optionsString: typeof optionsString
  });
  let url;
  let finalOptions;
  if (typeof urlOrOptions === "string" && target !== void 0) {
    console.log("HiddenInAppBrowser.open - Using legacy API format");
    url = urlOrOptions;
    let parsedOptions = {};
    if (optionsString) {
      console.log(
        "HiddenInAppBrowser.open - Parsing options string:",
        optionsString
      );
      const optionsArray = optionsString.split(",");
      optionsArray.forEach((option) => {
        const [key, value] = option.split("=");
        if (key && value) {
          parsedOptions[key.trim()] = value.trim();
        }
      });
      console.log("HiddenInAppBrowser.open - Parsed options:", parsedOptions);
    }
    finalOptions = { ...DEFAULT_OPEN_OPTIONS, ...parsedOptions, url };
    if (onSuccess || onError) {
      console.log("HiddenInAppBrowser.open - Using callback mode");
      if (typeof cordova !== "undefined" && cordova.exec) {
        cordova.exec(
          () => {
            console.log("HiddenInAppBrowser.open - Success callback");
            if (onSuccess) onSuccess();
          },
          (error) => {
            console.log("HiddenInAppBrowser.open - Error callback:", error);
            if (onError) onError({ code: -1, message: error.toString() });
          },
          "multibrowser",
          "open",
          [
            {
              url: finalOptions.url,
              options: {
                iOS: {
                  closeButtonText: "default",
                  viewStyle: "default",
                  animationEffect: "default",
                  enableBarsCollapsing: true,
                  enableReadersMode: false,
                  allowOverScroll: true,
                  enableViewportScale: false,
                  allowInLineMediaPlayback: false,
                  surpressIncrementalRendering: false,
                  allowsBackForwardNavigationGestures: true
                },
                showURL: finalOptions.location,
                showToolbar: finalOptions.toolbar,
                clearCache: true,
                clearSessionCache: finalOptions.clearsessioncache,
                mediaPlaybackRequiresUserAction: finalOptions.mediaPlaybackRequiresUserAction,
                closeButtonText: "Close",
                toolbarPosition: "default",
                leftToRight: false,
                showNavigationButtons: !finalOptions.hidenavigationbuttons,
                customWebViewUserAgent: null,
                hidden: finalOptions.hidden,
                autoClose: false,
                timeout: null
              },
              customHeaders: null
            }
          ]
        );
      } else {
        const error = "Cordova is not available";
        if (onError) onError({ code: -1, message: error.toString() });
      }
      return;
    }
  } else {
    console.log("HiddenInAppBrowser.open - Using modern API format");
    if (typeof urlOrOptions === "string") {
      console.log(
        "HiddenInAppBrowser.open - Options is a string, using as URL"
      );
      url = urlOrOptions;
      finalOptions = { ...DEFAULT_OPEN_OPTIONS, url };
    } else {
      console.log("HiddenInAppBrowser.open - Options is an object");
      console.log("HiddenInAppBrowser.open - Options.url:", urlOrOptions.url);
      console.log(
        "HiddenInAppBrowser.open - Options.url type:",
        typeof urlOrOptions.url
      );
      console.log(
        "HiddenInAppBrowser.open - Options.url constructor:",
        urlOrOptions.url?.constructor?.name
      );
      console.log(
        "HiddenInAppBrowser.open - Options.url keys:",
        Object.keys(urlOrOptions.url || {})
      );
      let urlFromOptions = urlOrOptions.url;
      console.log(
        "HiddenInAppBrowser.open - Converting URL from:",
        urlFromOptions
      );
      console.log("HiddenInAppBrowser.open - URL type:", typeof urlFromOptions);
      if (typeof urlFromOptions !== "string") {
        console.log(
          "HiddenInAppBrowser.open - URL is not a string, attempting conversion..."
        );
        if (Array.isArray(urlFromOptions)) {
          console.log("HiddenInAppBrowser.open - URL is an array, joining...");
          urlFromOptions = urlFromOptions.join("");
        } else if (typeof urlFromOptions === "object" && urlFromOptions !== null) {
          console.log(
            "HiddenInAppBrowser.open - URL is an object, reconstructing..."
          );
          const keys = Object.keys(urlFromOptions).filter((key) => !isNaN(Number(key))).sort((a, b) => Number(a) - Number(b));
          console.log("HiddenInAppBrowser.open - Found keys:", keys);
          if (keys.length > 0) {
            urlFromOptions = keys.map((key) => urlFromOptions[key]).join("");
          }
        }
        console.log(
          "HiddenInAppBrowser.open - Converted URL to:",
          urlFromOptions
        );
      }
      const correctedOptions = { ...urlOrOptions, url: urlFromOptions };
      finalOptions = { ...DEFAULT_OPEN_OPTIONS, ...correctedOptions };
    }
  }
  console.log("HiddenInAppBrowser.open - Final options:", finalOptions);
  console.log("HiddenInAppBrowser.open - Final options.url:", finalOptions.url);
  console.log("Parameters being sent to cordova.exec:", [
    { url: finalOptions.url }
  ]);
  return new Promise((resolve, reject) => {
    if (typeof cordova !== "undefined" && cordova.exec) {
      cordova.exec(
        () => resolve(),
        (error) => reject(new Error(error)),
        "multibrowser",
        "open",
        [{ url: finalOptions.url }]
      );
    } else {
      reject(new Error("Cordova is not available"));
    }
  });
}
function processOptionsAndExecute(urlOrOptions, target, optionsString, onSuccess, onError, defaultOptions, methodName) {
  console.log(`🔍 ${methodName} - ===== INICIO DEL MÉTODO =====`);
  console.log(`${methodName} - Raw parameters received:`, {
    urlOrOptions,
    target,
    optionsString,
    onSuccess: typeof onSuccess,
    onError: typeof onError
  });
  console.log(`${methodName} - Default options:`, defaultOptions);
  console.log(`📋 ${methodName} - Using legacy API format (OutSystems)`);
  let url;
  let finalOptions;
  if (typeof urlOrOptions === "string") {
    url = urlOrOptions;
    console.log(`${methodName} - URL from legacy API:`, url);
  } else {
    url = "https://example.com";
    console.log(`${methodName} - Invalid URL, using fallback:`, url);
  }
  let parsedOptions = {};
  if (optionsString) {
    console.log(`${methodName} - Parsing options string:`, optionsString);
    const optionsArray = optionsString.split(",");
    optionsArray.forEach((option) => {
      const [key, value] = option.split("=");
      if (key && value) {
        parsedOptions[key.trim()] = value.trim();
      }
    });
    console.log(`${methodName} - Parsed options:`, parsedOptions);
  }
  finalOptions = { ...defaultOptions, ...parsedOptions, url };
  console.log(`${methodName} - Final options after merge:`, finalOptions);
  console.log(`📞 ${methodName} - Using callback mode (OutSystems)`);
  return new Promise((resolve, reject) => {
    if (typeof cordova !== "undefined" && cordova.exec) {
      console.log(`📱 ${methodName} - Cordova is available, calling exec...`);
      const execParams = [{ url: finalOptions.url, options: finalOptions }];
      console.log(`${methodName} - Exec parameters:`, execParams);
      cordova.exec(
        (result) => {
          console.log(`✅ ${methodName} - Success callback received:`, result);
          if (onSuccess) {
            console.log(`📞 ${methodName} - Calling onSuccess callback`);
            onSuccess();
          }
          resolve();
        },
        (error) => {
          console.log(`❌ ${methodName} - Error callback received:`, error);
          if (onError) {
            console.log(`📞 ${methodName} - Calling onError callback`);
            onError({ code: -1, message: error });
          }
          reject(new Error(error));
        },
        "multibrowser",
        methodName,
        execParams
      );
    } else {
      const error = "Cordova is not available";
      console.log(`❌ ${methodName} - ${error}`);
      if (onError) onError({ code: -1, message: error });
      reject(new Error(error));
    }
  });
}
function openInExternalBrowser(urlOrOptions, target, optionsString, onSuccess, onError) {
  console.log(
    "HiddenInAppBrowser.openInExternalBrowser - Raw parameters received:",
    {
      urlOrOptions,
      target,
      optionsString,
      onSuccess,
      onError
    }
  );
  let url;
  let finalOptions;
  if (typeof urlOrOptions === "string" && (onSuccess || onError)) {
    console.log(
      "HiddenInAppBrowser.openInExternalBrowser - Using legacy API format"
    );
    url = urlOrOptions;
    let parsedOptions = {};
    if (optionsString) {
      console.log(
        "HiddenInAppBrowser.openInExternalBrowser - Parsing options string:",
        optionsString
      );
      const optionsArray = optionsString.split(",");
      optionsArray.forEach((option) => {
        const [key, value] = option.split("=");
        if (key && value) {
          parsedOptions[key.trim()] = value.trim();
        }
      });
      console.log(
        "HiddenInAppBrowser.openInExternalBrowser - Parsed options:",
        parsedOptions
      );
    }
    finalOptions = {
      ...DEFAULT_EXTERNAL_BROWSER_OPTIONS,
      ...parsedOptions,
      url
    };
    if (onSuccess || onError) {
      console.log(
        "HiddenInAppBrowser.openInExternalBrowser - Using callback mode"
      );
      if (typeof cordova !== "undefined" && cordova.exec) {
        cordova.exec(
          () => {
            console.log(
              "HiddenInAppBrowser.openInExternalBrowser - Success callback"
            );
            if (onSuccess) onSuccess();
          },
          (error) => {
            console.log(
              "HiddenInAppBrowser.openInExternalBrowser - Error callback:",
              error
            );
            if (onError) onError({ code: -1, message: error.toString() });
          },
          "multibrowser",
          "openInExternalBrowser",
          [{ url: finalOptions.url, options: finalOptions }]
        );
      } else {
        const error = "Cordova is not available";
        if (onError) onError({ code: -1, message: error.toString() });
      }
      return;
    }
  }
  console.log(
    "HiddenInAppBrowser.openInExternalBrowser - Using modern API format with Promise"
  );
  return processOptionsAndExecute(
    urlOrOptions,
    target,
    optionsString,
    onSuccess,
    onError,
    DEFAULT_EXTERNAL_BROWSER_OPTIONS,
    "openInExternalBrowser"
  );
}
function openInWebView(urlOrOptions, target, optionsString, onSuccess, onError) {
  console.log("HiddenInAppBrowser.openInWebView - Raw parameters received:", {
    urlOrOptions,
    target,
    optionsString,
    onSuccess,
    onError
  });
  console.log(
    "HiddenInAppBrowser.openInWebView - Using legacy API format (OutSystems)"
  );
  let url;
  let finalOptions;
  if (typeof urlOrOptions === "string") {
    url = urlOrOptions;
    console.log("HiddenInAppBrowser.openInWebView - URL from legacy API:", url);
  } else {
    url = "https://example.com";
    console.log(
      "HiddenInAppBrowser.openInWebView - Invalid URL, using fallback:",
      url
    );
  }
  let parsedOptions = {};
  if (optionsString) {
    console.log(
      "HiddenInAppBrowser.openInWebView - Parsing options string:",
      optionsString
    );
    const optionsArray = optionsString.split(",");
    optionsArray.forEach((option) => {
      const [key, value] = option.split("=");
      if (key && value) {
        parsedOptions[key.trim()] = value.trim();
      }
    });
    console.log(
      "HiddenInAppBrowser.openInWebView - Parsed options:",
      parsedOptions
    );
  }
  finalOptions = { ...DEFAULT_WEBVIEW_OPTIONS, ...parsedOptions, url };
  console.log(
    "HiddenInAppBrowser.openInWebView - Final options after merge:",
    finalOptions
  );
  console.log(
    "HiddenInAppBrowser.openInWebView - Using callback mode (OutSystems)"
  );
  if (typeof cordova !== "undefined" && cordova.exec) {
    cordova.exec(
      () => {
        console.log("HiddenInAppBrowser.openInWebView - Success callback");
        if (onSuccess) {
          console.log(
            "HiddenInAppBrowser.openInWebView - Calling onSuccess callback"
          );
          onSuccess();
        }
      },
      (error) => {
        console.log(
          "HiddenInAppBrowser.openInWebView - Error callback:",
          error
        );
        if (onError) {
          console.log(
            "HiddenInAppBrowser.openInWebView - Calling onError callback"
          );
          onError({ code: -1, message: error.toString() });
        }
      },
      "multibrowser",
      "openInWebView",
      [{ url: finalOptions.url, options: finalOptions }]
    );
  } else {
    const error = "Cordova is not available";
    console.log(
      "HiddenInAppBrowser.openInWebView - Cordova not available:",
      error
    );
    if (onError) onError({ code: -1, message: error.toString() });
  }
}
function closeWebView() {
  return cordova.exec(
    () => {
      console.log("HiddenInAppBrowser: WebView closed successfully");
    },
    (error) => {
      console.error("HiddenInAppBrowser: Error closing WebView:", error);
    },
    "multibrowser",
    "closeWebView",
    []
  );
}
if (typeof console !== "undefined") {
  console.log("HiddenInAppBrowser plugin loaded with functions:", {
    open,
    openInExternalBrowser,
    openInWebView
  });
}
exports.closeWebView = closeWebView;
exports.open = open;
exports.openInExternalBrowser = openInExternalBrowser;
exports.openInWebView = openInWebView;
