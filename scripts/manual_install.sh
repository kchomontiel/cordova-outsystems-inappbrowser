#!/bin/bash

echo "🔧 [HiddenInAppBrowser] Manual Plugin Installation"
echo "=================================================="

# Check if we're in the right directory
if [ ! -f "plugin.xml" ]; then
    echo "❌ [ERROR] This script must be run from the plugin root directory"
    exit 1
fi

echo "📁 Current directory: $(pwd)"

# Step 1: Build the plugin
echo "🔨 Step 1: Building the plugin..."
if npm run build; then
    echo "✅ Plugin built successfully"
else
    echo "❌ Plugin build failed"
    exit 1
fi

# Step 2: Create test project in /tmp
echo "📱 Step 2: Creating test project in /tmp..."
cd /tmp
if [ -d "test_plugin" ]; then
    echo "📱 Using existing test project in /tmp"
    cd test_plugin
else
    echo "📱 Creating new test project in /tmp"
    cordova create test_plugin com.test.app "Test App"
    cd test_plugin
    
    echo "📱 Adding iOS platform..."
    cordova platform add ios
    
    echo "📱 Adding Android platform..."
    cordova platform add android
fi

# Step 3: Manual plugin installation
echo "🔌 Step 3: Manual plugin installation..."
PLUGIN_DIR="/Users/carlosmontiel/Downloads/cordova-outsystems-inappbrowser-main"

# Create plugin directory structure
mkdir -p "plugins/com.outsystems.plugins.hiddeninappbrowser"

# Copy plugin files
echo "📋 Copying plugin files..."
cp -r "$PLUGIN_DIR/dist" "plugins/com.outsystems.plugins.hiddeninappbrowser/"
cp "$PLUGIN_DIR/plugin.xml" "plugins/com.outsystems.plugins.hiddeninappbrowser/"
cp -r "$PLUGIN_DIR/src" "plugins/com.outsystems.plugins.hiddeninappbrowser/"

# Copy package.json
cp "$PLUGIN_DIR/package.json" "plugins/com.outsystems.plugins.hiddeninappbrowser/"

# Step 4: Add plugin to config.xml
echo "📝 Step 4: Adding plugin to config.xml..."
if ! grep -q "com.outsystems.plugins.hiddeninappbrowser" config.xml; then
    # Add plugin reference to config.xml
    sed -i '' '/<platform name="ios">/a\
        <plugin name="com.outsystems.plugins.hiddeninappbrowser" spec="file:plugins/com.outsystems.plugins.hiddeninappbrowser" />' config.xml
    
    sed -i '' '/<platform name="android">/a\
        <plugin name="com.outsystems.plugins.hiddeninappbrowser" spec="file:plugins/com.outsystems.plugins.hiddeninappbrowser" />' config.xml
    
    echo "✅ Plugin added to config.xml"
else
    echo "✅ Plugin already in config.xml"
fi

# Step 5: Prepare platforms
echo "🔨 Step 5: Preparing platforms..."
cordova prepare

# Step 6: Verify installation
echo "🔍 Step 6: Verifying installation..."
if [ -d "platforms/ios/Test App/Plugins/com.outsystems.plugins.hiddeninappbrowser" ]; then
    echo "✅ iOS plugin files found"
    ls -la "platforms/ios/Test App/Plugins/com.outsystems.plugins.hiddeninappbrowser/"
else
    echo "❌ iOS plugin files not found"
fi

if [ -d "platforms/android/app/src/main/kotlin/com/outsystems/plugins/inappbrowser" ]; then
    echo "✅ Android plugin files found"
    ls -la "platforms/android/app/src/main/kotlin/com/outsystems/plugins/inappbrowser/"
else
    echo "❌ Android plugin files not found"
fi

echo ""
echo "=================================================="
echo "🎉 MANUAL INSTALLATION COMPLETED!"
echo "=================================================="
echo ""
echo "📱 Test project location: $(pwd)"
echo "🔌 Plugin should be installed manually"
echo ""
echo "Next steps:"
echo "1. Open iOS project in Xcode: open platforms/ios/Test.xcworkspace"
echo "2. Open Android project in Android Studio: open platforms/android/"
echo "3. Test the plugin functionality"
echo "4. If everything works, deploy to OutSystems"
