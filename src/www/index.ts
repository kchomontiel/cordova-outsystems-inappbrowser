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
): Promise<void> | void {
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
      if (typeof cordova !== "undefined" && cordova.exec) {
        cordova.exec(
          () => {
            console.log("HiddenInAppBrowser.open - Success callback");
            if (onSuccess) onSuccess();
          },
          (error: string) => {
            console.log("HiddenInAppBrowser.open - Error callback:", error);
            if (onError) onError({ code: -1, message: error.toString() });
          },
          "multibrowser",
          "open",
          [
            {
              url: finalOptions.url,
              options: {
                iOS: {
                  closeButtonText: "default",
                  viewStyle: "default",
                  animationEffect: "default",
                  enableBarsCollapsing: true,
                  enableReadersMode: false,
                  allowOverScroll: true,
                  enableViewportScale: false,
                  allowInLineMediaPlayback: false,
                  surpressIncrementalRendering: false,
                  allowsBackForwardNavigationGestures: true,
                },
                showURL: finalOptions.location,
                showToolbar: finalOptions.toolbar,
                clearCache: true,
                clearSessionCache: finalOptions.clearsessioncache,
                mediaPlaybackRequiresUserAction:
                  finalOptions.mediaPlaybackRequiresUserAction,
                closeButtonText: "Close",
                toolbarPosition: "default",
                leftToRight: false,
                showNavigationButtons: !finalOptions.hidenavigationbuttons,
                customWebViewUserAgent: null,
                hidden: finalOptions.hidden,
                autoClose: false,
                timeout: null,
              },
              customHeaders: null,
            },
          ]
        );
      } else {
        const error = "Cordova is not available";
        if (onError) onError({ code: -1, message: error.toString() });
      }
      return;
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

  // Return Promise only if no callbacks are provided
  return new Promise((resolve, reject) => {
    // Use cordova.exec directly instead of require
    if (typeof cordova !== "undefined" && cordova.exec) {
      cordova.exec(
        () => resolve(),
        (error: string) => reject(new Error(error)),
        "multibrowser",
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
  console.log(`🔍 ${methodName} - ===== INICIO DEL MÉTODO =====`);
  console.log(`${methodName} - Raw parameters received:`, {
    urlOrOptions,
    target,
    optionsString,
    onSuccess: typeof onSuccess,
    onError: typeof onError,
  });
  console.log(`${methodName} - Default options:`, defaultOptions);

  // OutSystems SIEMPRE usa la API legacy con callbacks
  console.log(`📋 ${methodName} - Using legacy API format (OutSystems)`);

  let url: string;
  let finalOptions: HiddenInAppBrowserOpenOptions;

  // Parse URL
  if (typeof urlOrOptions === "string") {
    url = urlOrOptions;
    console.log(`${methodName} - URL from legacy API:`, url);
  } else {
    url = "https://example.com"; // fallback
    console.log(`${methodName} - Invalid URL, using fallback:`, url);
  }

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
  console.log(`${methodName} - Final options after merge:`, finalOptions);

  // SIEMPRE usar callbacks (OutSystems)
  console.log(`📞 ${methodName} - Using callback mode (OutSystems)`);

  return new Promise((resolve, reject) => {
    if (typeof cordova !== "undefined" && cordova.exec) {
      console.log(`📱 ${methodName} - Cordova is available, calling exec...`);
      const execParams = [{ url: finalOptions.url, options: finalOptions }];
      console.log(`${methodName} - Exec parameters:`, execParams);

      cordova.exec(
        (result: any) => {
          console.log(`✅ ${methodName} - Success callback received:`, result);
          if (onSuccess) {
            console.log(`📞 ${methodName} - Calling onSuccess callback`);
            onSuccess();
          }
          resolve();
        },
        (error: string) => {
          console.log(`❌ ${methodName} - Error callback received:`, error);
          if (onError) {
            console.log(`📞 ${methodName} - Calling onError callback`);
            onError({ code: -1, message: error });
          }
          reject(new Error(error));
        },
        "multibrowser",
        methodName,
        execParams
      );
    } else {
      const error = "Cordova is not available";
      console.log(`❌ ${methodName} - ${error}`);
      if (onError) onError({ code: -1, message: error });
      reject(new Error(error));
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
): Promise<void> | void {
  // Debug: Log the raw parameters received
  console.log(
    "HiddenInAppBrowser.openInExternalBrowser - Raw parameters received:",
    {
      urlOrOptions,
      target,
      optionsString,
      onSuccess,
      onError,
    }
  );

  let url: string;
  let finalOptions: HiddenInAppBrowserOpenOptions;

  // Handle legacy API: openInExternalBrowser(url, target, options, success, error)
  if (typeof urlOrOptions === "string" && (onSuccess || onError)) {
    console.log(
      "HiddenInAppBrowser.openInExternalBrowser - Using legacy API format"
    );
    url = urlOrOptions;

    // Parse options string if provided
    let parsedOptions: any = {};
    if (optionsString) {
      console.log(
        "HiddenInAppBrowser.openInExternalBrowser - Parsing options string:",
        optionsString
      );
      const optionsArray = optionsString.split(",");
      optionsArray.forEach((option) => {
        const [key, value] = option.split("=");
        if (key && value) {
          parsedOptions[key.trim()] = value.trim();
        }
      });
      console.log(
        "HiddenInAppBrowser.openInExternalBrowser - Parsed options:",
        parsedOptions
      );
    }

    finalOptions = {
      ...DEFAULT_EXTERNAL_BROWSER_OPTIONS,
      ...parsedOptions,
      url,
    };

    // If callbacks are provided, use them instead of Promise
    if (onSuccess || onError) {
      console.log(
        "HiddenInAppBrowser.openInExternalBrowser - Using callback mode"
      );
      if (typeof cordova !== "undefined" && cordova.exec) {
        cordova.exec(
          () => {
            console.log(
              "HiddenInAppBrowser.openInExternalBrowser - Success callback"
            );
            if (onSuccess) onSuccess();
          },
          (error: string) => {
            console.log(
              "HiddenInAppBrowser.openInExternalBrowser - Error callback:",
              error
            );
            if (onError) onError({ code: -1, message: error.toString() });
          },
          "multibrowser",
          "openInExternalBrowser",
          [{ url: finalOptions.url, options: finalOptions }]
        );
      } else {
        const error = "Cordova is not available";
        if (onError) onError({ code: -1, message: error.toString() });
      }
      return; // Return without Promise when using callbacks
    }
  }

  // Handle modern API: openInExternalBrowser(options) or openInExternalBrowser(url) - use Promise
  console.log(
    "HiddenInAppBrowser.openInExternalBrowser - Using modern API format with Promise"
  );
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
): Promise<void> | void {
  // Debug: Log the raw parameters received
  console.log("HiddenInAppBrowser.openInWebView - Raw parameters received:", {
    urlOrOptions,
    target,
    optionsString,
    onSuccess,
    onError,
  });

  // OutSystems SIEMPRE usa la API legacy con callbacks
  console.log(
    "HiddenInAppBrowser.openInWebView - Using legacy API format (OutSystems)"
  );

  let url: string;
  let finalOptions: HiddenInAppBrowserOpenOptions;

  // Parse URL
  if (typeof urlOrOptions === "string") {
    url = urlOrOptions;
    console.log("HiddenInAppBrowser.openInWebView - URL from legacy API:", url);
  } else {
    url = "https://example.com"; // fallback
    console.log(
      "HiddenInAppBrowser.openInWebView - Invalid URL, using fallback:",
      url
    );
  }

  // Parse options string if provided
  let parsedOptions: any = {};
  if (optionsString) {
    console.log(
      "HiddenInAppBrowser.openInWebView - Parsing options string:",
      optionsString
    );
    const optionsArray = optionsString.split(",");
    optionsArray.forEach((option) => {
      const [key, value] = option.split("=");
      if (key && value) {
        parsedOptions[key.trim()] = value.trim();
      }
    });
    console.log(
      "HiddenInAppBrowser.openInWebView - Parsed options:",
      parsedOptions
    );
  }

  finalOptions = { ...DEFAULT_WEBVIEW_OPTIONS, ...parsedOptions, url };
  console.log(
    "HiddenInAppBrowser.openInWebView - Final options after merge:",
    finalOptions
  );

  // SIEMPRE usar callbacks (OutSystems)
  console.log(
    "HiddenInAppBrowser.openInWebView - Using callback mode (OutSystems)"
  );

  if (typeof cordova !== "undefined" && cordova.exec) {
    cordova.exec(
      () => {
        console.log("HiddenInAppBrowser.openInWebView - Success callback");
        if (onSuccess) {
          console.log(
            "HiddenInAppBrowser.openInWebView - Calling onSuccess callback"
          );
          onSuccess();
        }
      },
      (error: string) => {
        console.log(
          "HiddenInAppBrowser.openInWebView - Error callback:",
          error
        );
        if (onError) {
          console.log(
            "HiddenInAppBrowser.openInWebView - Calling onError callback"
          );
          onError({ code: -1, message: error.toString() });
        }
      },
      "multibrowser",
      "openInWebView",
      [{ url: finalOptions.url, options: finalOptions }]
    );
  } else {
    const error = "Cordova is not available";
    console.log(
      "HiddenInAppBrowser.openInWebView - Cordova not available:",
      error
    );
    if (onError) onError({ code: -1, message: error.toString() });
  }
}

export function closeWebView(): Promise<void> | void {
  return cordova.exec(
    () => {
      console.log("HiddenInAppBrowser: WebView closed successfully");
    },
    (error) => {
      console.error("HiddenInAppBrowser: Error closing WebView:", error);
    },
    "multibrowser",
    "closeWebView",
    []
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
