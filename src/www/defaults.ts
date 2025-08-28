import { HiddenInAppBrowserOpenOptions } from "./definitions";

export const DEFAULT_OPEN_OPTIONS: HiddenInAppBrowserOpenOptions = {
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
  fullscreen: true,
};

export const DEFAULT_EXTERNAL_BROWSER_OPTIONS: HiddenInAppBrowserOpenOptions = {
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
  fullscreen: false,
};

export const DEFAULT_WEBVIEW_OPTIONS: HiddenInAppBrowserOpenOptions = {
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
  fullscreen: false,
};
