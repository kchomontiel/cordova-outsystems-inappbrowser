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
export {
  HiddenInAppBrowserInstance,
  HiddenInAppBrowserPlugin
};
