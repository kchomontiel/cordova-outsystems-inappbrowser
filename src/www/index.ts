import { HiddenInAppBrowserOpenOptions } from "./definitions";
import {
  DEFAULT_OPEN_OPTIONS,
  DEFAULT_EXTERNAL_BROWSER_OPTIONS,
  DEFAULT_WEBVIEW_OPTIONS,
} from "./defaults";

// Export the open function directly for Cordova
export function open(
  urlOrOptions: string | HiddenInAppBrowserOpenOptions | any,
  target?: string,
  optionsString?: string,
  onSuccess?: () => void,
  onError?: (error: any) => void
): Promise<void> {
  // Debug: Log the raw parameters received
  console.log("HiddenInAppBrowser.open - Raw parameters received:", {
    urlOrOptions,
    target,
    optionsString,
    onSuccess,
    onError,
  });
  console.log("HiddenInAppBrowser.open - Parameters types:", {
    urlOrOptions: typeof urlOrOptions,
    target: typeof target,
    optionsString: typeof optionsString,
  });

  let url: string;
  let finalOptions: HiddenInAppBrowserOpenOptions;

  // Handle legacy API: open(url, target, options, success, error)
  if (typeof urlOrOptions === "string" && target !== undefined) {
    console.log("HiddenInAppBrowser.open - Using legacy API format");
    url = urlOrOptions;

    // Parse options string if provided
    let parsedOptions: any = {};
    if (optionsString) {
      console.log(
        "HiddenInAppBrowser.open - Parsing options string:",
        optionsString
      );
      const optionsArray = optionsString.split(",");
      optionsArray.forEach((option) => {
        const [key, value] = option.split("=");
        if (key && value) {
          parsedOptions[key.trim()] = value.trim();
        }
      });
      console.log("HiddenInAppBrowser.open - Parsed options:", parsedOptions);
    }

    finalOptions = { ...DEFAULT_OPEN_OPTIONS, ...parsedOptions, url };

    // If callbacks are provided, use them instead of Promise
    if (onSuccess || onError) {
      console.log("HiddenInAppBrowser.open - Using callback mode");
      return new Promise((resolve, reject) => {
        if (typeof cordova !== "undefined" && cordova.exec) {
          cordova.exec(
            () => {
              console.log("HiddenInAppBrowser.open - Success callback");
              if (onSuccess) onSuccess();
              resolve();
            },
            (error: string) => {
              console.log("HiddenInAppBrowser.open - Error callback:", error);
              if (onError) onError({ code: -1, message: error });
              reject(new Error(error));
            },
            "HiddenInAppBrowser",
            "open",
            [{ url: finalOptions.url }]
          );
        } else {
          const error = "Cordova is not available";
          if (onError) onError({ code: -1, message: error });
          reject(new Error(error));
        }
      });
    }
  } else {
    // Handle modern API: open(options) or open(url)
    console.log("HiddenInAppBrowser.open - Using modern API format");

    if (typeof urlOrOptions === "string") {
      console.log(
        "HiddenInAppBrowser.open - Options is a string, using as URL"
      );
      url = urlOrOptions;
      finalOptions = { ...DEFAULT_OPEN_OPTIONS, url };
    } else {
      console.log("HiddenInAppBrowser.open - Options is an object");
      console.log("HiddenInAppBrowser.open - Options.url:", urlOrOptions.url);
      console.log(
        "HiddenInAppBrowser.open - Options.url type:",
        typeof urlOrOptions.url
      );
      console.log(
        "HiddenInAppBrowser.open - Options.url constructor:",
        urlOrOptions.url?.constructor?.name
      );
      console.log(
        "HiddenInAppBrowser.open - Options.url keys:",
        Object.keys(urlOrOptions.url || {})
      );

      // Handle case where options.url might be an array of characters or malformed
      let urlFromOptions: string = urlOrOptions.url as string;

      // Debug: Log what we're trying to convert
      console.log(
        "HiddenInAppBrowser.open - Converting URL from:",
        urlFromOptions
      );
      console.log("HiddenInAppBrowser.open - URL type:", typeof urlFromOptions);

      if (typeof urlFromOptions !== "string") {
        console.log(
          "HiddenInAppBrowser.open - URL is not a string, attempting conversion..."
        );

        if (Array.isArray(urlFromOptions)) {
          console.log("HiddenInAppBrowser.open - URL is an array, joining...");
          urlFromOptions = (urlFromOptions as any[]).join("");
        } else if (
          typeof urlFromOptions === "object" &&
          urlFromOptions !== null
        ) {
          console.log(
            "HiddenInAppBrowser.open - URL is an object, reconstructing..."
          );
          // If it's an object with numeric keys, try to reconstruct the string
          const keys = Object.keys(urlFromOptions)
            .filter((key) => !isNaN(Number(key)))
            .sort((a, b) => Number(a) - Number(b));
          console.log("HiddenInAppBrowser.open - Found keys:", keys);
          if (keys.length > 0) {
            urlFromOptions = keys
              .map((key) => (urlFromOptions as any)[key])
              .join("");
          }
        }

        console.log(
          "HiddenInAppBrowser.open - Converted URL to:",
          urlFromOptions
        );
      }

      // Update options with the corrected URL
      const correctedOptions = { ...urlOrOptions, url: urlFromOptions };
      finalOptions = { ...DEFAULT_OPEN_OPTIONS, ...correctedOptions };
    }
  }

  // Debug: Log the parameters being sent
  console.log("HiddenInAppBrowser.open - Final options:", finalOptions);
  console.log("HiddenInAppBrowser.open - Final options.url:", finalOptions.url);
  console.log("Parameters being sent to cordova.exec:", [
    { url: finalOptions.url },
  ]);

  return new Promise((resolve, reject) => {
    // Use cordova.exec directly instead of require
    if (typeof cordova !== "undefined" && cordova.exec) {
      cordova.exec(
        () => resolve(),
        (error: string) => reject(new Error(error)),
        "HiddenInAppBrowser",
        "open",
        [{ url: finalOptions.url }]
      );
    } else {
      reject(new Error("Cordova is not available"));
    }
  });
}

// Helper function to process options and call cordova.exec
function processOptionsAndExecute(
  urlOrOptions: string | HiddenInAppBrowserOpenOptions | any,
  target: string | undefined,
  optionsString: string | undefined,
  onSuccess: (() => void) | undefined,
  onError: ((error: any) => void) | undefined,
  defaultOptions: HiddenInAppBrowserOpenOptions,
  methodName: string
): Promise<void> {
  console.log(`${methodName} - Raw parameters received:`, {
    urlOrOptions,
    target,
    optionsString,
    onSuccess,
    onError,
  });

  let url: string;
  let finalOptions: HiddenInAppBrowserOpenOptions;

  // Handle legacy API: method(url, target, options, success, error)
  if (typeof urlOrOptions === "string" && target !== undefined) {
    console.log(`${methodName} - Using legacy API format`);
    url = urlOrOptions;

    // Parse options string if provided
    let parsedOptions: any = {};
    if (optionsString) {
      console.log(`${methodName} - Parsing options string:`, optionsString);
      const optionsArray = optionsString.split(",");
      optionsArray.forEach((option) => {
        const [key, value] = option.split("=");
        if (key && value) {
          parsedOptions[key.trim()] = value.trim();
        }
      });
      console.log(`${methodName} - Parsed options:`, parsedOptions);
    }

    finalOptions = { ...defaultOptions, ...parsedOptions, url };

    // If callbacks are provided, use them instead of Promise
    if (onSuccess || onError) {
      console.log(`${methodName} - Using callback mode`);
      return new Promise((resolve, reject) => {
        if (typeof cordova !== "undefined" && cordova.exec) {
          cordova.exec(
            () => {
              console.log(`${methodName} - Success callback`);
              if (onSuccess) onSuccess();
              resolve();
            },
            (error: string) => {
              console.log(`${methodName} - Error callback:`, error);
              if (onError) onError({ code: -1, message: error });
              reject(new Error(error));
            },
            "HiddenInAppBrowser",
            methodName,
            [{ url: finalOptions.url, options: finalOptions }]
          );
        } else {
          const error = "Cordova is not available";
          if (onError) onError({ code: -1, message: error });
          reject(new Error(error));
        }
      });
    }
  } else {
    // Handle modern API: method(options) or method(url)
    console.log(`${methodName} - Using modern API format`);

    if (typeof urlOrOptions === "string") {
      console.log(`${methodName} - Options is a string, using as URL`);
      url = urlOrOptions;
      finalOptions = { ...defaultOptions, url };
    } else {
      console.log(`${methodName} - Options is an object`);
      console.log(`${methodName} - Options.url:`, urlOrOptions.url);
      console.log(`${methodName} - Options.url type:`, typeof urlOrOptions.url);

      // Handle case where options.url might be an array of characters or malformed
      let urlFromOptions: string = urlOrOptions.url as string;

      if (typeof urlFromOptions !== "string") {
        console.log(
          `${methodName} - URL is not a string, attempting conversion...`
        );

        if (Array.isArray(urlFromOptions)) {
          console.log(`${methodName} - URL is an array, joining...`);
          urlFromOptions = (urlFromOptions as any[]).join("");
        } else if (
          typeof urlFromOptions === "object" &&
          urlFromOptions !== null
        ) {
          console.log(`${methodName} - URL is an object, reconstructing...`);
          // If it's an object with numeric keys, try to reconstruct the string
          const keys = Object.keys(urlFromOptions)
            .filter((key) => !isNaN(Number(key)))
            .sort((a, b) => Number(a) - Number(b));
          console.log(`${methodName} - Found keys:`, keys);
          if (keys.length > 0) {
            urlFromOptions = keys
              .map((key) => (urlFromOptions as any)[key])
              .join("");
          }
        }

        console.log(`${methodName} - Converted URL to:`, urlFromOptions);
      }

      // Update options with the corrected URL
      const correctedOptions = { ...urlOrOptions, url: urlFromOptions };
      finalOptions = { ...defaultOptions, ...correctedOptions };
    }
  }

  // Debug: Log the parameters being sent
  console.log(`${methodName} - Final options:`, finalOptions);
  console.log(`${methodName} - Final options.url:`, finalOptions.url);
  console.log(`Parameters being sent to cordova.exec:`, [
    { url: finalOptions.url, options: finalOptions },
  ]);

  return new Promise((resolve, reject) => {
    // Use cordova.exec directly instead of require
    if (typeof cordova !== "undefined" && cordova.exec) {
      cordova.exec(
        () => resolve(),
        (error: string) => reject(new Error(error)),
        "HiddenInAppBrowser",
        methodName,
        [{ url: finalOptions.url, options: finalOptions }]
      );
    } else {
      reject(new Error("Cordova is not available"));
    }
  });
}

// Export the openInExternalBrowser function
export function openInExternalBrowser(
  urlOrOptions: string | HiddenInAppBrowserOpenOptions | any,
  target?: string,
  optionsString?: string,
  onSuccess?: () => void,
  onError?: (error: any) => void
): Promise<void> {
  return processOptionsAndExecute(
    urlOrOptions,
    target,
    optionsString,
    onSuccess,
    onError,
    DEFAULT_EXTERNAL_BROWSER_OPTIONS,
    "openInExternalBrowser"
  );
}

// Export the openInWebView function
export function openInWebView(
  urlOrOptions: string | HiddenInAppBrowserOpenOptions | any,
  target?: string,
  optionsString?: string,
  onSuccess?: () => void,
  onError?: (error: any) => void
): Promise<void> {
  return processOptionsAndExecute(
    urlOrOptions,
    target,
    optionsString,
    onSuccess,
    onError,
    DEFAULT_WEBVIEW_OPTIONS,
    "openInWebView"
  );
}

// Debug: Log to console to verify plugin is loaded
if (typeof console !== "undefined") {
  console.log("HiddenInAppBrowser plugin loaded with functions:", {
    open,
    openInExternalBrowser,
    openInWebView,
  });
}

// Export types for consumers
export type { HiddenInAppBrowserOpenOptions } from "./definitions";
