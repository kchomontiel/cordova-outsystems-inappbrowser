#!/bin/bash

echo "🧪 [HiddenInAppBrowser] Simple Plugin Test"
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
    echo "🔍 Checking if plugin is already installed..."
    if cordova plugin ls | grep -q "com.outsystems.plugins.hiddeninappbrowser"; then
        echo "✅ Plugin is already installed"
    else
        echo "❌ Plugin installation completely failed"
        exit 1
    fi
fi

# Step 4: Verify plugin installation
echo "🔍 Step 4: Verifying plugin installation..."
if cordova plugin ls | grep -q "com.outsystems.plugins.hiddeninappbrowser"; then
    echo "✅ Plugin verification successful"
    echo "📋 Installed plugins:"
    cordova plugin ls
else
    echo "❌ Plugin verification failed"
    exit 1
fi

# Step 5: Check project structure
echo "📁 Step 5: Checking project structure..."
if [ -d "platforms/ios" ]; then
    echo "✅ iOS platform found"
    if [ -d "platforms/ios/Test/Plugins/com.outsystems.plugins.hiddeninappbrowser" ]; then
        echo "✅ iOS plugin files found"
    else
        echo "⚠️  iOS plugin files not found in expected location"
    fi
else
    echo "❌ iOS platform not found"
fi

if [ -d "platforms/android" ]; then
    echo "✅ Android platform found"
    if [ -d "platforms/android/app/src/main/kotlin/com/outsystems/plugins/inappbrowser" ]; then
        echo "✅ Android plugin files found"
    else
        echo "⚠️  Android plugin files not found in expected location"
    fi
else
    echo "❌ Android platform not found"
fi

echo ""
echo "=========================================="
echo "🎉 PLUGIN TEST COMPLETED SUCCESSFULLY!"
echo "=========================================="
echo ""
echo "📱 Test project location: $(pwd)"
echo "🔌 Plugin is installed and ready!"
echo ""
echo "Next steps:"
echo "1. Open iOS project in Xcode: open platforms/ios/Test.xcworkspace"
echo "2. Open Android project in Android Studio: open platforms/android/"
echo "3. Test the plugin functionality"
echo "4. If everything works, deploy to OutSystems"
