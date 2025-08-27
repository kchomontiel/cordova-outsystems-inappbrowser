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
function open(options) {
  console.log("HiddenInAppBrowser.open - Raw options received:", options);
  console.log("HiddenInAppBrowser.open - Options type:", typeof options);
  let url;
  let finalOptions;
  if (typeof options === "string") {
    console.log("HiddenInAppBrowser.open - Options is a string, using as URL");
    url = options;
    finalOptions = { ...DEFAULT_OPEN_OPTIONS, url };
  } else {
    console.log("HiddenInAppBrowser.open - Options is an object");
    console.log("HiddenInAppBrowser.open - Options.url:", options.url);
    console.log(
      "HiddenInAppBrowser.open - Options.url type:",
      typeof options.url
    );
    console.log(
      "HiddenInAppBrowser.open - Options.url constructor:",
      options.url?.constructor?.name
    );
    console.log(
      "HiddenInAppBrowser.open - Options.url keys:",
      Object.keys(options.url || {})
    );
    let urlFromOptions = options.url;
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
    const correctedOptions = { ...options, url: urlFromOptions };
    finalOptions = { ...DEFAULT_OPEN_OPTIONS, ...correctedOptions };
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
if (typeof console !== "undefined") {
  console.log("HiddenInAppBrowser plugin loaded with open function:", open);
}
exports.open = open;
