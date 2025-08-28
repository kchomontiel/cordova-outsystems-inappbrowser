import { HiddenInAppBrowser, HiddenInAppBrowserOpenOptions } from "./definitions";
import { DEFAULT_OPEN_OPTIONS, DEFAULT_EXTERNAL_BROWSER_OPTIONS, DEFAULT_WEBVIEW_OPTIONS } from "./defaults";

export class HiddenInAppBrowserWeb implements HiddenInAppBrowser {
  async open(options: HiddenInAppBrowserOpenOptions): Promise<void> {
    const mergedOptions = { ...DEFAULT_OPEN_OPTIONS, ...options };

    // For web, we'll use window.open with the specified options
    const features = this.buildFeaturesString(mergedOptions);
    const windowRef = window.open(mergedOptions.url, "_blank", features);

    if (!windowRef) {
      throw new Error("Failed to open InAppBrowser window");
    }
  }

  async openInExternalBrowser(options: HiddenInAppBrowserOpenOptions): Promise<void> {
    const mergedOptions = { ...DEFAULT_EXTERNAL_BROWSER_OPTIONS, ...options };

    // For web, external browser is the same as regular window.open
    const features = this.buildFeaturesString(mergedOptions);
    const windowRef = window.open(mergedOptions.url, "_blank", features);

    if (!windowRef) {
      throw new Error("Failed to open external browser window");
    }
  }

  async openInWebView(options: HiddenInAppBrowserOpenOptions): Promise<void> {
    const mergedOptions = { ...DEFAULT_WEBVIEW_OPTIONS, ...options };

    // For web, webview is similar to regular window.open but with different defaults
    const features = this.buildFeaturesString(mergedOptions);
    const windowRef = window.open(mergedOptions.url, "_blank", features);

    if (!windowRef) {
      throw new Error("Failed to open WebView window");
    }
  }

  private buildFeaturesString(options: HiddenInAppBrowserOpenOptions): string {
    const features: string[] = [];

    if (options.location === false) features.push("location=no");
    if (options.toolbar === false) features.push("toolbar=no");
    if (options.zoom === false) features.push("scrollbars=no");
    if (options.fullscreen === true) features.push("fullscreen=yes");

    return features.join(",");
  }
}
