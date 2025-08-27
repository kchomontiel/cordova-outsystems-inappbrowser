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
  const { exec } = require("cordova/exec");
  class HiddenInAppBrowserPlugin {
    async open(options) {
      const mergedOptions = { ...DEFAULT_OPEN_OPTIONS, ...options };
      return new Promise((resolve, reject) => {
        exec(
          () => resolve(),
          (error) => reject(new Error(error)),
          "HiddenInAppBrowser",
          "open",
          [{ url: mergedOptions.url }]
        );
      });
    }
  }
  const HiddenInAppBrowserInstance = new HiddenInAppBrowserPlugin();
  exports2.HiddenInAppBrowserInstance = HiddenInAppBrowserInstance;
  exports2.HiddenInAppBrowserPlugin = HiddenInAppBrowserPlugin;
  Object.defineProperty(exports2, Symbol.toStringTag, { value: "Module" });
});
