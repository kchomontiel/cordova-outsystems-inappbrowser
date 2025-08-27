import { require } from "cordova";
import {
  BrowserCallbacks,
  PluginError,
  SystemBrowserOptions,
  WebViewOptions,
  WebViewHiddenOptions,
  CallbackEvent,
  CallbackEventType,
} from "./definitions";
import {
  DefaultSystemBrowserOptions,
  DefaultWebViewOptions,
  DefaultWebViewHiddenOptions,
} from "./defaults";
var exec = require("cordova/exec");

function trigger(
  type: CallbackEventType,
  success: () => void,
  data?: any,
  onbrowserClosed: (() => void) | undefined = undefined,
  onbrowserPageLoaded: (() => void) | undefined = undefined,
  onbrowserPageNavigationCompleted:
    | ((data?: string) => void)
    | undefined = undefined
) {
  switch (type) {
    case CallbackEventType.SUCCESS:
      success();
      break;
    case CallbackEventType.PAGE_CLOSED:
      if (onbrowserClosed) {
        onbrowserClosed();
      }
      break;
    case CallbackEventType.PAGE_LOAD_COMPLETED:
      if (onbrowserPageLoaded) {
        onbrowserPageLoaded();
      }
      break;
    case CallbackEventType.PAGE_NAVIGATION_COMPLETED:
      if (onbrowserPageNavigationCompleted) {
        onbrowserPageNavigationCompleted(data);
      }
      break;
    default:
      break;
  }
}

function openInWebView(
  url: string,
  options: WebViewOptions,
  success: () => void,
  error: (error: PluginError) => void,
  browserCallbacks?: BrowserCallbacks,
  customHeaders?: { [key: string]: string } | null
): void {
  options = options || DefaultWebViewOptions;

  let triggerCorrectCallback = function (result: string) {
    const parsedResult: CallbackEvent = JSON.parse(result);
    if (parsedResult) {
      if (browserCallbacks) {
        trigger(
          parsedResult.eventType,
          success,
          parsedResult.data,
          browserCallbacks.onbrowserClosed,
          browserCallbacks.onbrowserPageLoaded,
          browserCallbacks.onbrowserPageNavigationCompleted
        );
      } else {
        trigger(parsedResult.eventType, success, parsedResult.data);
      }
    }
  };

  exec(triggerCorrectCallback, error, "OSInAppBrowser", "openInWebView", [
    { url, options, customHeaders },
  ]);
}

function openInSystemBrowser(
  url: string,
  options: SystemBrowserOptions,
  success: () => void,
  error: (error: PluginError) => void,
  browserCallbacks?: BrowserCallbacks
): void {
  options = options || DefaultSystemBrowserOptions;

  let triggerCorrectCallback = function (result: string) {
    const parsedResult: CallbackEvent = JSON.parse(result);
    if (parsedResult) {
      if (browserCallbacks) {
        trigger(
          parsedResult.eventType,
          success,
          parsedResult.data,
          browserCallbacks.onbrowserClosed,
          browserCallbacks.onbrowserPageLoaded
        );
      } else {
        trigger(parsedResult.eventType, success);
      }
    }
  };

  exec(triggerCorrectCallback, error, "OSInAppBrowser", "openInSystemBrowser", [
    { url, options },
  ]);
}

function openInExternalBrowser(
  url: string,
  success: () => void,
  error: (error: PluginError) => void
): void {
  exec(success, error, "OSInAppBrowser", "openInExternalBrowser", [{ url }]);
}

function open(
  url: string,
  target?: string,
  options?: string,
  success?: () => void,
  error?: (error: PluginError) => void
): void {
  target = target || "_blank";
  options = options || "";

  exec(success, error, "OSInAppBrowser", "open", [url, target, options]);
}

function openInWebViewHidden(
  url: string,
  options: WebViewHiddenOptions,
  success: () => void,
  error: (error: PluginError) => void,
  browserCallbacks?: BrowserCallbacks,
  customHeaders?: { [key: string]: string } | null
): void {
  options = options || DefaultWebViewHiddenOptions;

  let triggerCorrectCallback = function (result: string) {
    const parsedResult: CallbackEvent = JSON.parse(result);
    if (parsedResult) {
      if (browserCallbacks) {
        trigger(
          parsedResult.eventType,
          success,
          parsedResult.data,
          browserCallbacks.onbrowserClosed,
          browserCallbacks.onbrowserPageLoaded,
          browserCallbacks.onbrowserPageNavigationCompleted
        );
      } else {
        trigger(parsedResult.eventType, success, parsedResult.data);
      }
    }
  };

  exec(triggerCorrectCallback, error, "OSInAppBrowser", "openInWebViewHidden", [
    { url, options, customHeaders },
  ]);
}

function close(success: () => void, error: (error: PluginError) => void): void {
  exec(success, error, "OSInAppBrowser", "close", [{}]);
}

module.exports = {
  open,
  openInWebView,
  openInWebViewHidden,
  openInExternalBrowser,
  openInSystemBrowser,
  close,
};
