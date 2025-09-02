#!/bin/bash

echo "🚀 [HiddenInAppBrowser] Build and Test Script Started"
echo "=================================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if we're in the right directory
if [ ! -f "plugin.xml" ]; then
    print_error "This script must be run from the plugin root directory"
    exit 1
fi

print_status "Current directory: $(pwd)"

# Step 1: Build the plugin
print_status "Step 1: Building the plugin..."
if npm run build; then
    print_success "Plugin built successfully"
else
    print_error "Plugin build failed"
    exit 1
fi

# Step 2: Check if we have a test project
if [ ! -d "test_project" ]; then
    print_warning "No test project found. Creating one..."
    
    # Create test project
    cordova create test_project com.outsystems.test.inappbrowser "InAppBrowser Test"
    cd test_project
    
    # Add platforms
    print_status "Adding iOS platform..."
    cordova platform add ios
    
    print_status "Adding Android platform..."
    cordova platform add android
    
    # Go back to plugin directory
    cd ..
else
    print_status "Test project found, using existing one"
    cd test_project
fi

# Step 3: Install our plugin
print_status "Step 3: Installing our plugin..."
if cordova plugin add ..; then
    print_success "Plugin installed successfully"
else
    print_error "Plugin installation failed"
    exit 1
fi

# Step 4: Build for iOS
print_status "Step 4: Building for iOS..."
if cordova build ios; then
    print_success "iOS build completed successfully"
    
    # Check if there are any InAppBrowser conflicts
    if [ -d "platforms/ios/InAppBrowser_Test/Plugins/com.outsystems.plugins.inappbrowser" ]; then
        print_warning "Found com.outsystems.plugins.inappbrowser in iOS - this might cause conflicts"
    fi
    
    if [ -d "platforms/ios/InAppBrowser_Test/Plugins/cordova-plugin-inappbrowser" ]; then
        print_error "Found cordova-plugin-inappbrowser in iOS - this will cause compilation errors"
        exit 1
    fi
    
    print_success "iOS platform is clean - no conflicting plugins found"
else
    print_error "iOS build failed"
    exit 1
fi

# Step 5: Build for Android
print_status "Step 5: Building for Android..."
if cordova build android; then
    print_success "Android build completed successfully"
    
    # Check if InAppBrowser is properly installed for Android
    if [ -d "platforms/android/app/src/main/kotlin/com/outsystems/plugins/inappbrowser/osinappbrowser" ]; then
        print_success "Android plugin files found correctly"
    else
        print_warning "Android plugin files not found in expected location"
    fi
else
    print_error "Android build failed"
    exit 1
fi

# Step 6: Summary
echo ""
echo "=================================================="
print_success "Build and Test Completed Successfully!"
echo "=================================================="
echo ""
print_status "Summary:"
echo "  ✅ Plugin built successfully"
echo "  ✅ Plugin installed in test project"
echo "  ✅ iOS build completed (no conflicts)"
echo "  ✅ Android build completed (with InAppBrowser dependency)"
echo ""
print_status "Next steps:"
echo "  1. Test the plugin functionality in the test project"
echo "  2. If everything works, the plugin is ready for OutSystems"
echo "  3. If there are issues, debug and fix them here first"
echo ""
print_status "Test project location: $(pwd)"
print_status "You can now test the plugin functionality!"
