var multibrowser = {
  openInWebView: function (
    url,
    target,
    options,
    successCallback,
    errorCallback
  ) {
    cordova.exec(
      successCallback,
      errorCallback,
      "multibrowser",
      "openInWebView",
      [url, target, options]
    );
  },

  openHidden: function (url, target, options, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "multibrowser", "openHidden", [
      url,
      target,
      options,
    ]);
  },

  openInExternalBrowser: function (
    url,
    target,
    options,
    successCallback,
    errorCallback
  ) {
    cordova.exec(
      successCallback,
      errorCallback,
      "multibrowser",
      "openInExternalBrowser",
      [url, target, options]
    );
  },

  closeWebView: function (successCallback, errorCallback) {
    cordova.exec(
      successCallback,
      errorCallback,
      "multibrowser",
      "closeWebView",
      []
    );
  },
};

// Exponer globalmente para Cordova
if (typeof module !== "undefined" && module.exports) {
  module.exports = multibrowser;
}

// Exponer en window para uso global
if (typeof window !== "undefined") {
  window.multibrowser = multibrowser;
}

// Exponer en cordova.plugins
if (typeof cordova !== "undefined" && cordova.plugins) {
  cordova.plugins.multibrowser = multibrowser;
}
