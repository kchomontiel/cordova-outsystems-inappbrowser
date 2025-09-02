#!/bin/bash

echo "🔧 [MultiBrowser] Complete Manual Plugin Installation"
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
if [ -d "test_mb" ]; then
    echo "📱 Using existing test project in /tmp"
    cd test_mb
else
    echo "📱 Creating new test project in /tmp"
    cordova create test_mb com.test.mb "MultiBrowser Test"
    cd test_mb
    
    echo "📱 Adding iOS platform..."
    cordova platform add ios
    
    echo "📱 Adding Android platform..."
    cordova platform add android
fi

# Step 3: Manual plugin installation
echo "🔌 Step 3: Manual plugin installation..."
PLUGIN_DIR="/Users/carlosmontiel/Downloads/cordova-outsystems-inappbrowser-main"

# Create plugin directory structure
mkdir -p "plugins/multibrowser"

# Copy plugin files
echo "📋 Copying plugin files..."
cp -r "$PLUGIN_DIR/dist" "plugins/multibrowser/"
cp "$PLUGIN_DIR/plugin.xml" "plugins/multibrowser/"
cp -r "$PLUGIN_DIR/src" "plugins/multibrowser/"
cp "$PLUGIN_DIR/package.json" "plugins/multibrowser/"

# Step 4: Add plugin to config.xml
echo "📝 Step 4: Adding plugin to config.xml..."
if ! grep -q "multibrowser" config.xml; then
    # Add plugin reference to config.xml
    sed -i '' '/<platform name="ios">/a\
        <plugin name="multibrowser" spec="file:plugins/multibrowser" />' config.xml
    
    sed -i '' '/<platform name="android">/a\
        <plugin name="multibrowser" spec="file:plugins/multibrowser" />' config.xml
    
    echo "✅ Plugin added to config.xml"
else
    echo "✅ Plugin already in config.xml"
fi

# Step 5: Copy files directly to iOS platform
echo "🍎 Step 5: Copying files to iOS platform..."
IOS_PLUGIN_DIR="platforms/ios/MultiBrowser Test/Plugins/multibrowser"
mkdir -p "$IOS_PLUGIN_DIR"

# Copy iOS source files
cp "$PLUGIN_DIR/src/ios/HiddenInAppBrowser.swift" "$IOS_PLUGIN_DIR/"
cp "$PLUGIN_DIR/src/ios/HiddenInAppBrowserInputArgumentsModel.swift" "$IOS_PLUGIN_DIR/"

# Copy JavaScript files
cp "$PLUGIN_DIR/dist/plugin.js" "$IOS_PLUGIN_DIR/"

echo "✅ iOS plugin files copied"

# Step 6: Copy files directly to Android platform
echo "🤖 Step 6: Copying files to Android platform..."
ANDROID_PLUGIN_DIR="platforms/android/app/src/main/kotlin/com/outsystems/plugins/inappbrowser/osinappbrowser"
mkdir -p "$ANDROID_PLUGIN_DIR"

# Copy Android source files
cp "$PLUGIN_DIR/src/android/HiddenInAppBrowser.kt" "$ANDROID_PLUGIN_DIR/"

# Copy JavaScript files
cp "$PLUGIN_DIR/dist/plugin.js" "$ANDROID_PLUGIN_DIR/"

echo "✅ Android plugin files copied"

# Step 7: Update iOS config.xml
echo "📝 Step 7: Updating iOS config.xml..."
IOS_CONFIG="platforms/ios/MultiBrowser Test/config.xml"

# Add feature to iOS config
if ! grep -q "multibrowser" "$IOS_CONFIG"; then
    # Add feature before closing </widget> tag
    sed -i '' '/<\/widget>/i\
    <feature name="multibrowser">\
        <param name="ios-package" value="HiddenInAppBrowser" />\
    </feature>' "$IOS_CONFIG"
    
    echo "✅ iOS feature added to config.xml"
else
    echo "✅ iOS feature already in config.xml"
fi

# Step 8: Update Android config.xml
echo "📝 Step 8: Updating Android config.xml..."
ANDROID_CONFIG="platforms/android/app/src/main/res/xml/config.xml"

# Create Android config if it doesn't exist
mkdir -p "$(dirname "$ANDROID_CONFIG")"
if [ ! -f "$ANDROID_CONFIG" ]; then
    cat > "$ANDROID_CONFIG" << 'EOF'
<?xml version='1.0' encoding='utf-8'?>
<widget>
    <feature name="multibrowser">
        <param name="android-package" value="com.outsystems.plugins.inappbrowser.osinappbrowser.HiddenInAppBrowser" />
    </feature>
</widget>
EOF
    echo "✅ Android config.xml created"
else
    echo "✅ Android config.xml already exists"
fi

# Step 9: Prepare platforms
echo "🔨 Step 9: Preparing platforms..."
cordova prepare

# Step 10: Verify installation
echo "🔍 Step 10: Verifying installation..."
if [ -d "platforms/ios/MultiBrowser Test/Plugins/multibrowser" ]; then
    echo "✅ iOS plugin files found"
    ls -la "platforms/ios/MultiBrowser Test/Plugins/multibrowser/"
else
    echo "❌ iOS plugin files not found"
fi

if [ -d "platforms/android/app/src/main/kotlin/com/outsystems/plugins/inappbrowser/osinappbrowser" ]; then
    echo "✅ Android plugin files found"
    ls -la "platforms/android/app/src/main/kotlin/com/outsystems/plugins/inappbrowser/osinappbrowser/"
else
    echo "❌ Android plugin files not found"
fi

echo ""
echo "=================================================="
echo "🎉 COMPLETE MANUAL INSTALLATION COMPLETED!"
echo "=================================================="
echo ""
echo "📱 Test project location: $(pwd)"
echo "🔌 Plugin should be installed manually"
echo ""
echo "Next steps:"
echo "1. Open iOS project in Xcode: open platforms/ios/MultiBrowser Test.xcworkspace"
echo "2. Open Android project in Android Studio: open platforms/android/"
echo "3. Test the plugin functionality"
echo "4. If everything works, deploy to OutSystems"
