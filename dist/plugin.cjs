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
            if (onError) onError({ code: -1, message: error });
          },
          "HiddenInAppBrowser",
          "open",
          [{ url: finalOptions.url }]
        );
      } else {
        const error = "Cordova is not available";
        if (onError) onError({ code: -1, message: error });
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
        "HiddenInAppBrowser",
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
  let url;
  let finalOptions;
  if (typeof urlOrOptions === "string" && target !== void 0) {
    console.log(`📋 ${methodName} - Using legacy API format`);
    url = urlOrOptions;
    console.log(`${methodName} - URL from legacy API:`, url);
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
    if (onSuccess || onError) {
      console.log(`📞 ${methodName} - Using callback mode`);
      return new Promise((resolve, reject) => {
        if (typeof cordova !== "undefined" && cordova.exec) {
          console.log(
            `📱 ${methodName} - Cordova is available, calling exec...`
          );
          const execParams2 = [{ url: finalOptions.url, options: finalOptions }];
          console.log(`${methodName} - Exec parameters:`, execParams2);
          cordova.exec(
            (result) => {
              console.log(
                `✅ ${methodName} - Success callback received:`,
                result
              );
              if (onSuccess) onSuccess();
              resolve();
            },
            (error) => {
              console.log(`❌ ${methodName} - Error callback received:`, error);
              if (onError) onError({ code: -1, message: error });
              reject(new Error(error));
            },
            "HiddenInAppBrowser",
            methodName,
            execParams2
          );
        } else {
          const error = "Cordova is not available";
          console.log(`❌ ${methodName} - ${error}`);
          if (onError) onError({ code: -1, message: error });
          reject(new Error(error));
        }
      });
    }
  } else {
    console.log(`📋 ${methodName} - Using modern API format`);
    if (typeof urlOrOptions === "string") {
      console.log(`${methodName} - Options is a string, using as URL`);
      url = urlOrOptions;
      finalOptions = { ...defaultOptions, url };
    } else {
      console.log(`${methodName} - Options is an object`);
      console.log(`${methodName} - Options.url:`, urlOrOptions.url);
      console.log(`${methodName} - Options.url type:`, typeof urlOrOptions.url);
      let urlFromOptions = urlOrOptions.url;
      if (typeof urlFromOptions !== "string") {
        console.log(
          `${methodName} - URL is not a string, attempting conversion...`
        );
        if (Array.isArray(urlFromOptions)) {
          console.log(`${methodName} - URL is an array, joining...`);
          urlFromOptions = urlFromOptions.join("");
        } else if (typeof urlFromOptions === "object" && urlFromOptions !== null) {
          console.log(`${methodName} - URL is an object, reconstructing...`);
          const keys = Object.keys(urlFromOptions).filter((key) => !isNaN(Number(key))).sort((a, b) => Number(a) - Number(b));
          console.log(`${methodName} - Found keys:`, keys);
          if (keys.length > 0) {
            urlFromOptions = keys.map((key) => urlFromOptions[key]).join("");
          }
        }
        console.log(`${methodName} - Converted URL to:`, urlFromOptions);
      }
      const correctedOptions = { ...urlOrOptions, url: urlFromOptions };
      finalOptions = { ...defaultOptions, ...correctedOptions };
    }
  }
  console.log(`📤 ${methodName} - Final options:`, finalOptions);
  console.log(`${methodName} - Final options.url:`, finalOptions.url);
  const execParams = [{ url: finalOptions.url, options: finalOptions }];
  console.log(
    `📤 ${methodName} - Parameters being sent to cordova.exec:`,
    execParams
  );
  return new Promise((resolve, reject) => {
    if (typeof cordova !== "undefined" && cordova.exec) {
      console.log(`📱 ${methodName} - Cordova is available, calling exec...`);
      cordova.exec(
        (result) => {
          console.log(`✅ ${methodName} - Success callback received:`, result);
          resolve();
        },
        (error) => {
          console.log(`❌ ${methodName} - Error callback received:`, error);
          reject(new Error(error));
        },
        "HiddenInAppBrowser",
        methodName,
        execParams
      );
    } else {
      const error = "Cordova is not available";
      console.log(`❌ ${methodName} - ${error}`);
      reject(new Error(error));
    }
  });
}
function openInExternalBrowser(urlOrOptions, target, optionsString, onSuccess, onError) {
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
  return processOptionsAndExecute(
    urlOrOptions,
    target,
    optionsString,
    onSuccess,
    onError,
    DEFAULT_WEBVIEW_OPTIONS,
    "openInWebView"
  );
}
if (typeof console !== "undefined") {
  console.log("HiddenInAppBrowser plugin loaded with functions:", {
    open,
    openInExternalBrowser,
    openInWebView
  });
}
exports.open = open;
exports.openInExternalBrowser = openInExternalBrowser;
exports.openInWebView = openInWebView;
