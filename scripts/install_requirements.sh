#!/bin/bash

echo "🔧 [HiddenInAppBrowser] Cordova Build Requirements Installation"
echo "=============================================================="

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

# Check if we're on macOS
if [[ "$OSTYPE" == "darwin"* ]]; then
    print_status "Detected macOS - installing requirements for iOS and Android"
    PLATFORM="macos"
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    print_status "Detected Linux - installing requirements for Android only"
    PLATFORM="linux"
    print_warning "iOS development requires macOS with Xcode"
else
    print_error "Unsupported operating system: $OSTYPE"
    print_warning "This script is designed for macOS and Linux"
    exit 1
fi

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to install Homebrew (macOS)
install_homebrew() {
    if ! command_exists brew; then
        print_status "Installing Homebrew..."
        /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
        
        # Add Homebrew to PATH for M1 Macs
        if [[ -f "/opt/homebrew/bin/brew" ]]; then
            echo 'eval "$(/opt/homebrew/bin/brew shellenv)"' >> ~/.zprofile
            eval "$(/opt/homebrew/bin/brew shellenv)"
        fi
        
        print_success "Homebrew installed successfully"
    else
        print_success "Homebrew already installed"
    fi
}

# Function to install Node.js
install_nodejs() {
    if ! command_exists node; then
        print_status "Installing Node.js..."
        if [[ "$PLATFORM" == "macos" ]]; then
            brew install node
        else
            curl -fsSL https://deb.nodesource.com/setup_lts.x | sudo -E bash -
            sudo apt-get install -y nodejs
        fi
        print_success "Node.js installed successfully"
    else
        print_success "Node.js already installed: $(node --version)"
    fi
    
    if ! command_exists npm; then
        print_error "npm not found after Node.js installation"
        exit 1
    else
        print_success "npm already installed: $(npm --version)"
    fi
}

# Function to install Cordova
install_cordova() {
    if ! command_exists cordova; then
        print_status "Installing Cordova CLI..."
        npm install -g cordova
        print_success "Cordova CLI installed successfully"
    else
        print_success "Cordova CLI already installed: $(cordova --version)"
    fi
}

# Function to install Xcode (macOS only)
install_xcode() {
    if [[ "$PLATFORM" == "macos" ]]; then
        if ! command_exists xcode-select; then
            print_status "Installing Xcode Command Line Tools..."
            xcode-select --install
            print_success "Xcode Command Line Tools installed"
        else
            print_success "Xcode Command Line Tools already installed"
        fi
        
        # Check if Xcode is installed
        if [ -d "/Applications/Xcode.app" ]; then
            print_success "Xcode.app found in Applications"
        else
            print_warning "Xcode.app not found in Applications"
            print_status "You can download Xcode from the Mac App Store"
            print_status "Or install it manually from developer.apple.com"
        fi
    fi
}

# Function to install Android Studio and SDK
install_android() {
    if [[ "$PLATFORM" == "macos" ]]; then
        if ! command_exists brew; then
            print_error "Homebrew required for Android installation on macOS"
            exit 1
        fi
        
        # Install Java
        if ! command_exists java; then
            print_status "Installing Java JDK..."
            brew install openjdk@11
            print_success "Java JDK installed"
        else
            print_success "Java already installed: $(java -version 2>&1 | head -n 1)"
        fi
        
        # Install Android Studio
        if [ ! -d "/Applications/Android Studio.app" ]; then
            print_status "Installing Android Studio..."
            brew install --cask android-studio
            print_success "Android Studio installed"
            print_warning "Please open Android Studio and complete the setup wizard"
            print_warning "This will install the Android SDK and tools"
        else
            print_success "Android Studio already installed"
        fi
    else
        # Linux installation
        if ! command_exists java; then
            print_status "Installing Java JDK..."
            sudo apt-get update
            sudo apt-get install -y openjdk-11-jdk
            print_success "Java JDK installed"
        fi
        
        if [ ! -d "$HOME/Android/Sdk" ]; then
            print_status "Installing Android SDK..."
            sudo apt-get install -y android-sdk
            print_success "Android SDK installed"
        fi
    fi
}

# Function to set up environment variables
setup_environment() {
    print_status "Setting up environment variables..."
    
    # Create .bashrc or .zshrc if it doesn't exist
    SHELL_RC="$HOME/.zshrc"
    if [ ! -f "$SHELL_RC" ]; then
        SHELL_RC="$HOME/.bashrc"
    fi
    
    # Add Android SDK to PATH
    if [[ "$PLATFORM" == "macos" ]]; then
        ANDROID_HOME="$HOME/Library/Android/sdk"
    else
        ANDROID_HOME="$HOME/Android/Sdk"
    fi
    
    if [ -d "$ANDROID_HOME" ]; then
        if ! grep -q "ANDROID_HOME" "$SHELL_RC"; then
            echo "" >> "$SHELL_RC"
            echo "# Android SDK" >> "$SHELL_RC"
            echo "export ANDROID_HOME=$ANDROID_HOME" >> "$SHELL_RC"
            echo "export PATH=\$PATH:\$ANDROID_HOME/emulator" >> "$SHELL_RC"
            echo "export PATH=\$PATH:\$ANDROID_HOME/tools" >> "$SHELL_RC"
            echo "export PATH=\$PATH:\$ANDROID_HOME/tools/bin" >> "$SHELL_RC"
            echo "export PATH=\$PATH:\$ANDROID_HOME/platform-tools" >> "$SHELL_RC"
            print_success "Android environment variables added to $SHELL_RC"
        else
            print_success "Android environment variables already configured"
        fi
    fi
    
    # Add Java to PATH for macOS
    if [[ "$PLATFORM" == "macos" ]]; then
        if ! grep -q "JAVA_HOME" "$SHELL_RC"; then
            echo "" >> "$SHELL_RC"
            echo "# Java" >> "$SHELL_RC"
            echo "export JAVA_HOME=/opt/homebrew/opt/openjdk@11" >> "$SHELL_RC"
            echo "export PATH=\$JAVA_HOME/bin:\$PATH" >> "$SHELL_RC"
            print_success "Java environment variables added to $SHELL_RC"
        fi
    fi
}

# Function to verify installation
verify_installation() {
    print_status "Verifying installation..."
    
    echo ""
    echo "=============================================================="
    echo "🔍 VERIFICATION RESULTS"
    echo "=============================================================="
    
    # Check Node.js
    if command_exists node; then
        print_success "✅ Node.js: $(node --version)"
    else
        print_error "❌ Node.js: Not installed"
    fi
    
    # Check npm
    if command_exists npm; then
        print_success "✅ npm: $(npm --version)"
    else
        print_error "❌ npm: Not installed"
    fi
    
    # Check Cordova
    if command_exists cordova; then
        print_success "✅ Cordova: $(cordova --version)"
    else
        print_error "❌ Cordova: Not installed"
    fi
    
    # Check Xcode (macOS)
    if [[ "$PLATFORM" == "macos" ]]; then
        if command_exists xcode-select; then
            print_success "✅ Xcode Command Line Tools: Installed"
        else
            print_error "❌ Xcode Command Line Tools: Not installed"
        fi
        
        if [ -d "/Applications/Xcode.app" ]; then
            print_success "✅ Xcode.app: Found"
        else
            print_warning "⚠️  Xcode.app: Not found (download from Mac App Store)"
        fi
    fi
    
    # Check Java
    if command_exists java; then
        print_success "✅ Java: $(java -version 2>&1 | head -n 1)"
    else
        print_error "❌ Java: Not installed"
    fi
    
    # Check Android Studio
    if [ -d "/Applications/Android Studio.app" ] || [ -d "$HOME/Android/Sdk" ]; then
        print_success "✅ Android SDK: Found"
    else
        print_warning "⚠️  Android SDK: Not found (install Android Studio)"
    fi
    
    echo ""
    echo "=============================================================="
    if [[ "$PLATFORM" == "macos" ]]; then
        print_status "Next steps for macOS:"
        print_status "1. Open Xcode and accept license agreements"
        print_status "2. Open Android Studio and complete setup wizard"
        print_status "3. Install Android SDK and tools"
        print_status "4. Restart terminal or run: source ~/.zshrc"
    else
        print_status "Next steps for Linux:"
        print_status "1. Install Android Studio manually"
        print_status "2. Set up Android SDK"
        print_status "3. Restart terminal or run: source ~/.bashrc"
    fi
    echo "=============================================================="
}

# Main installation process
main() {
    print_status "Starting Cordova build requirements installation..."
    
    if [[ "$PLATFORM" == "macos" ]]; then
        install_homebrew
    fi
    
    install_nodejs
    install_cordova
    install_xcode
    install_android
    setup_environment
    verify_installation
    
    print_success "Installation completed!"
    print_status "Please restart your terminal or run: source ~/.zshrc (macOS) or source ~/.bashrc (Linux)"
}

# Run main function
main
