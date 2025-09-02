#!/bin/bash

echo "🚀 [HiddenInAppBrowser] Quick Build Script"
echo "=========================================="

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

# Step 2: Create test project if it doesn't exist
if [ ! -d "test_project" ]; then
    echo "📱 Step 2: Creating test project..."
    cordova create test_project com.test.app "Test App"
    cd test_project
    
    echo "📱 Adding iOS platform..."
    cordova platform add ios
    
    echo "📱 Adding Android platform..."
    cordova platform add android
else
    echo "📱 Using existing test project"
    cd test_project
fi

# Step 3: Install our plugin
echo "🔌 Step 3: Installing our plugin..."
if cordova plugin add ..; then
    echo "✅ Plugin installed successfully"
else
    echo "❌ Plugin installation failed"
    exit 1
fi

# Step 4: Build for iOS
echo "🍎 Step 4: Building for iOS..."
if cordova build ios; then
    echo "✅ iOS build completed successfully"
    
    # Check for conflicts
    if [ -d "platforms/ios/Test/Plugins/cordova-plugin-inappbrowser" ]; then
        echo "❌ [CONFLICT] Found cordova-plugin-inappbrowser in iOS - this will cause errors"
        exit 1
    else
        echo "✅ iOS platform is clean - no conflicts found"
    fi
else
    echo "❌ iOS build failed"
    exit 1
fi

# Step 5: Build for Android
echo "🤖 Step 5: Building for Android..."
if cordova build android; then
    echo "✅ Android build completed successfully"
else
    echo "❌ Android build failed"
    exit 1
fi

echo ""
echo "=========================================="
echo "🎉 BUILD COMPLETED SUCCESSFULLY!"
echo "=========================================="
echo ""
echo "📱 Test project location: $(pwd)"
echo "🔌 Plugin is ready for testing!"
echo ""
echo "Next steps:"
echo "1. Test the plugin functionality"
echo "2. If everything works, deploy to OutSystems"
echo "3. If there are issues, debug here first"
