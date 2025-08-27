(function(global, factory) {
  typeof exports === "object" && typeof module !== "undefined" ? factory(exports) : typeof define === "function" && define.amd ? define(["exports"], factory) : (global = typeof globalThis !== "undefined" ? globalThis : global || self, factory(global.HiddenInAppBrowser = {}));
})(this, function(exports2) {
  "use strict";
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
    console.log("HiddenInAppBrowser.open - Options.url:", options.url);
    console.log(
      "HiddenInAppBrowser.open - Options.url type:",
      typeof options.url
    );
    console.log("HiddenInAppBrowser.open - Options.url constructor:", options.url?.constructor?.name);
    console.log("HiddenInAppBrowser.open - Options.url keys:", Object.keys(options.url || {}));
    let url = options.url;
    console.log("HiddenInAppBrowser.open - Converting URL from:", url);
    console.log("HiddenInAppBrowser.open - URL type:", typeof url);
    if (typeof url !== "string") {
      console.log("HiddenInAppBrowser.open - URL is not a string, attempting conversion...");
      if (Array.isArray(url)) {
        console.log("HiddenInAppBrowser.open - URL is an array, joining...");
        url = url.join("");
      } else if (typeof url === "object" && url !== null) {
        console.log("HiddenInAppBrowser.open - URL is an object, reconstructing...");
        const keys = Object.keys(url).filter((key) => !isNaN(Number(key))).sort((a, b) => Number(a) - Number(b));
        console.log("HiddenInAppBrowser.open - Found keys:", keys);
        if (keys.length > 0) {
          url = keys.map((key) => url[key]).join("");
        }
      }
      console.log("HiddenInAppBrowser.open - Converted URL to:", url);
    }
    const correctedOptions = { ...options, url };
    const mergedOptions = { ...DEFAULT_OPEN_OPTIONS, ...correctedOptions };
    console.log("HiddenInAppBrowser.open - Merged options:", mergedOptions);
    console.log(
      "HiddenInAppBrowser.open - Merged options.url:",
      mergedOptions.url
    );
    console.log("Parameters being sent to cordova.exec:", [
      { url: mergedOptions.url }
    ]);
    return new Promise((resolve, reject) => {
      if (typeof cordova !== "undefined" && cordova.exec) {
        cordova.exec(
          () => resolve(),
          (error) => reject(new Error(error)),
          "HiddenInAppBrowser",
          "open",
          [{ url: mergedOptions.url }]
        );
      } else {
        reject(new Error("Cordova is not available"));
      }
    });
  }
  if (typeof console !== "undefined") {
    console.log("HiddenInAppBrowser plugin loaded with open function:", open);
  }
  exports2.open = open;
  Object.defineProperty(exports2, Symbol.toStringTag, { value: "Module" });
});
