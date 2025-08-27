import {
  HiddenInAppBrowser,
  HiddenInAppBrowserOpenOptions,
} from "./definitions";
import { DEFAULT_OPEN_OPTIONS } from "./defaults";

const { exec } = require("cordova/exec");

class HiddenInAppBrowserPlugin implements HiddenInAppBrowser {
  async open(options: HiddenInAppBrowserOpenOptions): Promise<void> {
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
}

// Create the plugin instance
const HiddenInAppBrowserInstance = new HiddenInAppBrowserPlugin();

// Export for module systems
export { HiddenInAppBrowserInstance, HiddenInAppBrowserPlugin };
export type { HiddenInAppBrowserOpenOptions } from "./definitions";
