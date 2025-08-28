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
  open(
    urlOrOptions: string | HiddenInAppBrowserOpenOptions,
    target?: string,
    optionsString?: string,
    onSuccess?: () => void,
    onError?: (error: any) => void
  ): Promise<void>;
  
  openInExternalBrowser(
    urlOrOptions: string | HiddenInAppBrowserOpenOptions,
    target?: string,
    optionsString?: string,
    onSuccess?: () => void,
    onError?: (error: any) => void
  ): Promise<void>;
  
  openInWebView(
    urlOrOptions: string | HiddenInAppBrowserOpenOptions,
    target?: string,
    optionsString?: string,
    onSuccess?: () => void,
    onError?: (error: any) => void
  ): Promise<void>;
}
