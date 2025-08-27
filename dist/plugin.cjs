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
  const mergedOptions = { ...DEFAULT_OPEN_OPTIONS, ...options };
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
exports.open = open;
