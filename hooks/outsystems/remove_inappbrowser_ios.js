#!/usr/bin/env node

const fs = require("fs");
const path = require("path");

module.exports = function (context) {
  try {
    console.log(
      "🔧 [HiddenInAppBrowser] Hook started with context:",
      context ? "available" : "undefined"
    );

    // Only run for iOS platform
    if (
      !context ||
      !context.opts ||
      !context.opts.platforms ||
      context.opts.platforms.indexOf("ios") === -1
    ) {
      console.log(
        "🔧 [HiddenInAppBrowser] Skipping hook - not iOS platform or context not available"
      );
      return;
    }

    console.log(
      "🔧 [HiddenInAppBrowser] Removing original InAppBrowser plugin from iOS..."
    );
    console.log(
      "🔧 [HiddenInAppBrowser] Context:",
      JSON.stringify(
        {
          projectRoot: context.opts.projectRoot,
          platforms: context.opts.platforms,
        },
        null,
        2
      )
    );

    const platformRoot = path.join(
      context.opts.projectRoot,
      "platforms",
      "ios"
    );
    const pluginsDir = path.join(platformRoot, "InAppBrowser_Test", "Plugins");

    // Check if plugins directory exists
    if (!fs.existsSync(pluginsDir)) {
      console.log(
        "📁 [HiddenInAppBrowser] Plugins directory not found, skipping..."
      );
      return;
    }

    // List of plugins to remove
    const pluginsToRemove = [
      "com.outsystems.plugins.inappbrowser",
      "cordova-plugin-inappbrowser",
    ];

    pluginsToRemove.forEach((pluginName) => {
      const pluginPath = path.join(pluginsDir, pluginName);
      if (fs.existsSync(pluginPath)) {
        console.log(`🗑️ [HiddenInAppBrowser] Removing plugin: ${pluginName}`);

        // Remove the plugin directory
        try {
          fs.rmSync(pluginPath, { recursive: true, force: true });
          console.log(
            `✅ [HiddenInAppBrowser] Successfully removed: ${pluginName}`
          );
        } catch (error) {
          console.log(
            `❌ [HiddenInAppBrowser] Error removing ${pluginName}: ${error.message}`
          );
        }
      } else {
        console.log(`ℹ️ [HiddenInAppBrowser] Plugin not found: ${pluginName}`);
      }
    });

    console.log("✅ [HiddenInAppBrowser] iOS InAppBrowser cleanup completed");
  } catch (error) {
    console.log("❌ [HiddenInAppBrowser] Hook error:", error.message);
    console.log("❌ [HiddenInAppBrowser] Stack trace:", error.stack);
  }
};
