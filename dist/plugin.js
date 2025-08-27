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
    const mergedOptions = { ...DEFAULT_OPEN_OPTIONS, ...options };
    console.log("HiddenInAppBrowser.open called with:", mergedOptions);
    console.log("Parameters being sent to cordova.exec:", [{ url: mergedOptions.url }]);
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
