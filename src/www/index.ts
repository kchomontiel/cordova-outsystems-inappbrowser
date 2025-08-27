import { HiddenInAppBrowserOpenOptions } from "./definitions";
import { DEFAULT_OPEN_OPTIONS } from "./defaults";

const { exec } = require("cordova/exec");

// Export the open function directly for Cordova
export function open(options: HiddenInAppBrowserOpenOptions): Promise<void> {
  const mergedOptions = { ...DEFAULT_OPEN_OPTIONS, ...options };

  return new Promise((resolve, reject) => {
    exec(
      () => resolve(),
      (error: string) => reject(new Error(error)),
      "HiddenInAppBrowser",
      "open",
      [{ url: mergedOptions.url }]
    );
  });
}

// Debug: Log to console to verify plugin is loaded
if (typeof console !== "undefined") {
  console.log("HiddenInAppBrowser plugin loaded with open function:", open);
}

// Export types for consumers
export type { HiddenInAppBrowserOpenOptions } from "./definitions";
