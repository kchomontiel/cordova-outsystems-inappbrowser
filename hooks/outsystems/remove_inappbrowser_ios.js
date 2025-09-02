#!/usr/bin/env node

const fs = require('fs');
const path = require('path');

module.exports = function(context) {
    // Only run for iOS platform
    if (context.opts.platforms.indexOf('ios') === -1) {
        return;
    }

    console.log('🔧 [HiddenInAppBrowser] Removing original InAppBrowser plugin from iOS...');

    const platformRoot = path.join(context.opts.projectRoot, 'platforms', 'ios');
    const pluginsDir = path.join(platformRoot, 'InAppBrowser_Test', 'Plugins');

    // Check if plugins directory exists
    if (!fs.existsSync(pluginsDir)) {
        console.log('📁 [HiddenInAppBrowser] Plugins directory not found, skipping...');
        return;
    }

    // List of plugins to remove
    const pluginsToRemove = [
        'com.outsystems.plugins.inappbrowser',
        'cordova-plugin-inappbrowser'
    ];

    pluginsToRemove.forEach(pluginName => {
        const pluginPath = path.join(pluginsDir, pluginName);
        if (fs.existsSync(pluginPath)) {
            console.log(`🗑️ [HiddenInAppBrowser] Removing plugin: ${pluginName}`);
            
            // Remove the plugin directory
            try {
                fs.rmSync(pluginPath, { recursive: true, force: true });
                console.log(`✅ [HiddenInAppBrowser] Successfully removed: ${pluginName}`);
            } catch (error) {
                console.log(`❌ [HiddenInAppBrowser] Error removing ${pluginName}: ${error.message}`);
            }
        } else {
            console.log(`ℹ️ [HiddenInAppBrowser] Plugin not found: ${pluginName}`);
        }
    });

    console.log('✅ [HiddenInAppBrowser] iOS InAppBrowser cleanup completed');
};
