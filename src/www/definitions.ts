export interface HiddenInAppBrowserOpenOptions {
  url: string;
  hidden?: boolean;
  location?: boolean;
  toolbar?: boolean;
  zoom?: boolean;
  hardwareback?: boolean;
  mediaPlaybackRequiresUserAction?: boolean;
  shouldPauseOnSuspend?: boolean;
  clearsessioncache?: boolean;
  cache?: boolean;
  disallowoverscroll?: boolean;
  hidenavigationbuttons?: boolean;
  hideurlbar?: boolean;
  fullscreen?: boolean;
}

export interface HiddenInAppBrowser {
  open(options: string | HiddenInAppBrowserOpenOptions): Promise<void>;
}
