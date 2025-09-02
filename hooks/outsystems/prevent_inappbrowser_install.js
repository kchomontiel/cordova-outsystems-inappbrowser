#!/usr/bin/env node

const fs = require("fs");
const path = require("path");

module.exports = function (context) {
  try {
    console.log("🔧 [HiddenInAppBrowser] Prevent installation hook started");

    // Get project root
    const projectRoot = context.opts.projectRoot || process.cwd();
    console.log("🔧 [HiddenInAppBrowser] Project root:", projectRoot);

    // Check if we're in an iOS context
    const platformsDir = path.join(projectRoot, "platforms");
    if (!fs.existsSync(platformsDir)) {
      console.log(
        "📁 [HiddenInAppBrowser] No platforms directory found, skipping"
      );
      return;
    }

    const iosDir = path.join(platformsDir, "ios");
    if (!fs.existsSync(iosDir)) {
      console.log("📁 [HiddenInAppBrowser] No iOS platform found, skipping");
      return;
    }

    // Find all iOS project directories
    const iosProjects = fs.readdirSync(iosDir).filter((dir) => {
      const projectPath = path.join(iosDir, dir);
      return (
        fs.statSync(projectPath).isDirectory() &&
        fs.existsSync(path.join(projectPath, "project.pbxproj"))
      );
    });

    console.log("🔧 [HiddenInAppBrowser] Found iOS projects:", iosProjects);

    // Remove InAppBrowser plugin from all iOS projects BEFORE installation
    iosProjects.forEach((projectName) => {
      const pluginsDir = path.join(iosDir, projectName, "Plugins");
      if (!fs.existsSync(pluginsDir)) {
        console.log(
          `📁 [HiddenInAppBrowser] No plugins directory in ${projectName}`
        );
        return;
      }

      console.log(`🔧 [HiddenInAppBrowser] Processing project: ${projectName}`);

      // List of plugins to remove
      const pluginsToRemove = [
        "com.outsystems.plugins.inappbrowser",
        "cordova-plugin-inappbrowser",
      ];

      pluginsToRemove.forEach((pluginName) => {
        const pluginPath = path.join(pluginsDir, pluginName);
        if (fs.existsSync(pluginPath)) {
          console.log(
            `🗑️ [HiddenInAppBrowser] Removing plugin: ${pluginName} from ${projectName}`
          );

          try {
            // Remove the plugin directory
            fs.rmSync(pluginPath, { recursive: true, force: true });
            console.log(
              `✅ [HiddenInAppBrowser] Successfully removed: ${pluginName} from ${projectName}`
            );

            // Also remove from config.xml if it exists
            const configPath = path.join(iosDir, projectName, "config.xml");
            if (fs.existsSync(configPath)) {
              let configContent = fs.readFileSync(configPath, "utf8");
              const pluginRegex = new RegExp(
                `<feature name="${pluginName}">[\\s\\S]*?<\\/feature>`,
                "g"
              );
              configContent = configContent.replace(pluginRegex, "");
              fs.writeFileSync(configPath, configContent);
              console.log(
                `✅ [HiddenInAppBrowser] Removed ${pluginName} from config.xml`
              );
            }
          } catch (error) {
            console.log(
              `❌ [HiddenInAppBrowser] Error removing ${pluginName} from ${projectName}: ${error.message}`
            );
          }
        } else {
          console.log(
            `ℹ️ [HiddenInAppBrowser] Plugin not found: ${pluginName} in ${projectName}`
          );
        }
      });
    });

    console.log(
      "✅ [HiddenInAppBrowser] Prevention completed for all iOS projects"
    );
  } catch (error) {
    console.log(
      "❌ [HiddenInAppBrowser] Prevention hook error:",
      error.message
    );
    console.log("❌ [HiddenInAppBrowser] Stack trace:", error.stack);
  }
};
