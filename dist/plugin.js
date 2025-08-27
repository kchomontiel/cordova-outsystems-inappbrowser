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
  function open(urlOrOptions, target, optionsString, onSuccess, onError) {
    console.log("HiddenInAppBrowser.open - Raw parameters received:", { urlOrOptions, target, optionsString, onSuccess, onError });
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
        console.log("HiddenInAppBrowser.open - Parsing options string:", optionsString);
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
        return new Promise((resolve, reject) => {
          if (typeof cordova !== "undefined" && cordova.exec) {
            cordova.exec(
              () => {
                console.log("HiddenInAppBrowser.open - Success callback");
                if (onSuccess) onSuccess();
                resolve();
              },
              (error) => {
                console.log("HiddenInAppBrowser.open - Error callback:", error);
                if (onError) onError({ code: -1, message: error });
                reject(new Error(error));
              },
              "HiddenInAppBrowser",
              "open",
              [{ url: finalOptions.url }]
            );
          } else {
            const error = "Cordova is not available";
            if (onError) onError({ code: -1, message: error });
            reject(new Error(error));
          }
        });
      }
    } else {
      console.log("HiddenInAppBrowser.open - Using modern API format");
      if (typeof urlOrOptions === "string") {
        console.log("HiddenInAppBrowser.open - Options is a string, using as URL");
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
  if (typeof console !== "undefined") {
    console.log("HiddenInAppBrowser plugin loaded with open function:", open);
  }
  exports2.open = open;
  Object.defineProperty(exports2, Symbol.toStringTag, { value: "Module" });
});
