import { HiddenInAppBrowserOpenOptions } from "./definitions";
import { DEFAULT_OPEN_OPTIONS } from "./defaults";

// Export the open function directly for Cordova
export function open(options: HiddenInAppBrowserOpenOptions): Promise<void> {
  // Debug: Log the raw options received
  console.log("HiddenInAppBrowser.open - Raw options received:", options);
  console.log("HiddenInAppBrowser.open - Options type:", typeof options);
  console.log("HiddenInAppBrowser.open - Options.url:", options.url);
  console.log(
    "HiddenInAppBrowser.open - Options.url type:",
    typeof options.url
  );
  console.log("HiddenInAppBrowser.open - Options.url constructor:", options.url?.constructor?.name);
  console.log("HiddenInAppBrowser.open - Options.url keys:", Object.keys(options.url || {}));

  // Handle case where options.url might be an array of characters or malformed
  let url: string = options.url as string;
  
  // Debug: Log what we're trying to convert
  console.log("HiddenInAppBrowser.open - Converting URL from:", url);
  console.log("HiddenInAppBrowser.open - URL type:", typeof url);
  
  if (typeof url !== "string") {
    console.log("HiddenInAppBrowser.open - URL is not a string, attempting conversion...");
    
    if (Array.isArray(url)) {
      console.log("HiddenInAppBrowser.open - URL is an array, joining...");
      url = (url as any[]).join("");
    } else if (typeof url === "object" && url !== null) {
      console.log("HiddenInAppBrowser.open - URL is an object, reconstructing...");
      // If it's an object with numeric keys, try to reconstruct the string
      const keys = Object.keys(url)
        .filter((key) => !isNaN(Number(key)))
        .sort((a, b) => Number(a) - Number(b));
      console.log("HiddenInAppBrowser.open - Found keys:", keys);
      if (keys.length > 0) {
        url = keys.map((key) => (url as any)[key]).join("");
      }
    }
    
    console.log("HiddenInAppBrowser.open - Converted URL to:", url);
  }

  // Update options with the corrected URL
  const correctedOptions = { ...options, url };

  const mergedOptions = { ...DEFAULT_OPEN_OPTIONS, ...correctedOptions };

  // Debug: Log the parameters being sent
  console.log("HiddenInAppBrowser.open - Merged options:", mergedOptions);
  console.log(
    "HiddenInAppBrowser.open - Merged options.url:",
    mergedOptions.url
  );
  console.log("Parameters being sent to cordova.exec:", [
    { url: mergedOptions.url },
  ]);

  return new Promise((resolve, reject) => {
    // Use cordova.exec directly instead of require
    if (typeof cordova !== "undefined" && cordova.exec) {
      cordova.exec(
        () => resolve(),
        (error: string) => reject(new Error(error)),
        "HiddenInAppBrowser",
        "open",
        [{ url: mergedOptions.url }]
      );
    } else {
      reject(new Error("Cordova is not available"));
    }
  });
}

// Debug: Log to console to verify plugin is loaded
if (typeof console !== "undefined") {
  console.log("HiddenInAppBrowser plugin loaded with open function:", open);
}

// Export types for consumers
export type { HiddenInAppBrowserOpenOptions } from "./definitions";
