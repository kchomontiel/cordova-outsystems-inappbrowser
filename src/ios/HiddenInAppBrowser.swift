import UIKit
import ObjectiveC
@preconcurrency import WebKit
import Cordova

/// The plugin's main class
@objc(HiddenInAppBrowser)
class HiddenInAppBrowser: CDVPlugin {
    
    // Agregar variables de clase para las referencias
    private var modalWebView: WKWebView?
    private var modalNavigationController: UINavigationController?
    
    private var openedViewController: UIViewController?
    
    override func pluginInitialize() {
        print("🔧 HiddenInAppBrowser - pluginInitialize called")
        print("🔧 HiddenInAppBrowser - Plugin initialization completed")
        NSLog("🔧 HiddenInAppBrowser - NSLog test - Plugin initialized successfully")
    }
    
    override func execute(_ command: CDVInvokedUrlCommand) -> Bool {
        switch command.methodName {
        case "open":
            open(command: command)
        case "openInExternalBrowser":
            openInExternalBrowser(command: command)
        case "openInWebView":
            openInWebView(command: command)
        case "closeWebView":
            closeWebView(command: command)
        default:
            return false
        }
        return true
    }
    
    @objc(open:)
    func open(command: CDVInvokedUrlCommand) {
        print("🔍 open - ===== INICIO DEL MÉTODO =====")
        print("open - Command received: \(command)")
        
        self.commandDelegate.run { [weak self] in
            guard let self else { 
                print("❌ open - Self is nil")
                return 
            }
            
            print("open - Processing command arguments")
            
            guard
                let argumentsModel: HiddenInAppBrowserInputArgumentsSimpleModel = self.createModel(for: command.argument(at: 0)),
                let url = URL(string: argumentsModel.url)
            else {
                print("❌ open - Failed to create model or URL")
                return self.sendError("The input parameters aren't valid.", for: command.callbackId)
            }

            print("✅ open - Model created successfully")
            print("open - URL: \(url.absoluteString)")
            
            print("open - Creating hidden WebView directly")
            
            // Create hidden WebView directly (like Android)
            DispatchQueue.main.async {
                print("open - Running on main queue")
                
                // Create a hidden WebView directly (like Android implementation)
                let webView = WKWebView(frame: CGRect.zero)
                print("open - WebView created")
                
                // Make WebView invisible (like Android: alpha = 0f, visibility = GONE)
                webView.alpha = 0.0
                webView.isHidden = true
                print("open - WebView set to invisible")
                
                // Configure WebView settings
                webView.configuration.allowsInlineMediaPlayback = true
                webView.configuration.mediaTypesRequiringUserActionForPlayback = []
                print("open - WebView settings configured")
                
                // Create a custom delegate to handle navigation
                let webViewDelegate = HiddenWebViewDelegate { [weak self] success, error in
                    if success {
                        print("open - Page finished loading successfully")
                        self?.sendSuccess(for: command.callbackId)
                    } else {
                        print("open - Error loading URL: \(error ?? "unknown error")")
                        self?.sendError("Failed to open URL: \(url.absoluteString)", for: command.callbackId)
                    }
                }
                
                // Set the delegate
                webView.navigationDelegate = webViewDelegate
                
                // Keep a strong reference to the delegate
                objc_setAssociatedObject(webView, &AssociatedKeys.delegateKey, webViewDelegate, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
                
                print("open - WebView delegate configured")
                
                // Load the URL in background
                print("open - Loading URL: \(url.absoluteString)")
                let request = URLRequest(url: url)
                webView.load(request)
                print("open - URL load initiated")
                
                // Keep a reference to the WebView to prevent it from being deallocated
                objc_setAssociatedObject(self, &AssociatedKeys.webViewKey, webView, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
            }
        }
    }
    
    @objc(openInExternalBrowser:)
    func openInExternalBrowser(command: CDVInvokedUrlCommand) {
        print("🔍 openInExternalBrowser - ===== INICIO DEL MÉTODO =====")
        print("openInExternalBrowser - Command received: \(command)")
        
        self.commandDelegate.run { [weak self] in
            guard let self else { 
                print("❌ openInExternalBrowser - Self is nil")
                return 
            }
            
            print("openInExternalBrowser - Processing command arguments")
            
            guard
                let argumentsModel: HiddenInAppBrowserInputArgumentsSimpleModel = self.createModel(for: command.argument(at: 0)),
                let url = URL(string: argumentsModel.url)
            else {
                print("❌ openInExternalBrowser - Failed to create model or URL")
                return self.sendError("The input parameters aren't valid.", for: command.callbackId)
            }

            print("✅ openInExternalBrowser - Model created successfully")
            print("openInExternalBrowser - URL: \(url.absoluteString)")
            
            // Open in external browser
            DispatchQueue.main.async {
                UIApplication.shared.open(url) { success in
                    if success {
                        print("✅ openInExternalBrowser - URL opened successfully")
                        self.sendSuccess(for: command.callbackId)
                    } else {
                        print("❌ openInExternalBrowser - Failed to open URL")
                        self.sendError("Failed to open URL: \(url.absoluteString)", for: command.callbackId)
                    }
                }
            }
        }
    }
    

    
    @objc(openInWebView:)
    func openInWebView(command: CDVInvokedUrlCommand) {
        print("🔍 openInWebView - ===== INICIO DEL MÉTODO =====")
        print("openInWebView - Command received: \(command)")
        
        self.commandDelegate.run { [weak self] in
            guard let self else { 
                print("❌ openInWebView - Self is nil")
                return 
            }
            
            print("openInWebView - Processing command arguments")
            
            guard
                let argumentsModel: HiddenInAppBrowserInputArgumentsSimpleModel = self.createModel(for: command.argument(at: 0)),
                let url = URL(string: argumentsModel.url)
            else {
                print("❌ openInWebView - Failed to create model or URL")
                return self.sendError("The input parameters aren't valid.", for: command.callbackId)
            }

            print("✅ openInWebView - Model created successfully")
            print("openInWebView - URL: \(url.absoluteString)")
            
            // Create a modal WebView (like Android)
            DispatchQueue.main.async {
                print("✅ openInWebView - Creating modal WebView")
                
                // Create WebView with full screen
                let webView = WKWebView()
                webView.configuration.allowsInlineMediaPlayback = true
                webView.configuration.mediaTypesRequiringUserActionForPlayback = []
                
                // Create view controller
                let webViewController = UIViewController()
                webViewController.view = webView
                webViewController.title = "WebView"
                
                // Add close button
                let closeButton = UIBarButtonItem(barButtonSystemItem: .close, target: self, action: #selector(self.closeWebView))
                webViewController.navigationItem.leftBarButtonItem = closeButton
                
                // Create navigation controller
                let navigationController = UINavigationController(rootViewController: webViewController)
                navigationController.modalPresentationStyle = .fullScreen
                
                // Set delegate
                let webViewDelegate = ModalWebViewDelegate { [weak self] success, error in
                    if success {
                        print("✅ openInWebView - Page loaded successfully")
                        self?.sendSuccess(for: command.callbackId)
                    } else {
                        print("❌ openInWebView - Failed to load page: \(error ?? "unknown error")")
                        self?.sendError("Failed to load URL: \(url.absoluteString)", for: command.callbackId)
                    }
                }
                
                webView.navigationDelegate = webViewDelegate
                
                // Keep references
                objc_setAssociatedObject(webView, &AssociatedKeys.delegateKey, webViewDelegate, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
                objc_setAssociatedObject(self, &AssociatedKeys.webViewKey, webView, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
                self.openedViewController = navigationController
                
                // Guardar referencias para closeWebView
                self.modalWebView = webView
                self.modalNavigationController = navigationController
                
                // Load URL
                let request = URLRequest(url: url)
                webView.load(request)
                
                // Present modal
                self.viewController.present(navigationController, animated: true)
            }
        }
    }
    

    
    @objc(close:)
    func close(command: CDVInvokedUrlCommand) {
        self.commandDelegate.run { [weak self] in
            guard let self else { return }
            
            if let openedViewController {
                DispatchQueue.main.async {
                    openedViewController.dismiss(animated: true) { [weak self] in
                        self?.sendSuccess(for: command.callbackId)
                    }
                }
            } else {
                self.sendError("There's no browser view to close.", for: command.callbackId)
            }
        }
    }
    
    @objc func closeWebView(_ command: CDVInvokedUrlCommand) {
        print("HiddenInAppBrowser: closeWebView() called")
        
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            
            do {
                // Cerrar modal WebView
                if let webView = self.modalWebView {
                    print("HiddenInAppBrowser: Closing modal WebView")
                    webView.stopLoading()
                    webView.removeFromSuperview()
                    self.modalWebView = nil
                }
                
                // Cerrar modal
                if let navController = self.modalNavigationController {
                    print("HiddenInAppBrowser: Closing modal navigation")
                    navController.dismiss(animated: true) {
                        self.modalNavigationController = nil
                    }
                }
                
                print("HiddenInAppBrowser: Modal WebView closed successfully")
                let result = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: "Modal WebView closed successfully")!
                self.commandDelegate.send(result, callbackId: command.callbackId)
                
            } catch {
                print("HiddenInAppBrowser: Error closing modal WebView: \(error)")
                let result = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "Error closing modal WebView: \(error.localizedDescription)")!
                self.commandDelegate.send(result, callbackId: command.callbackId)
            }
        }
    }
}



private extension HiddenInAppBrowser {
    func createModel<T: Decodable>(for inputArgument: Any?) -> T? {
        print("createModel - Input argument: \(inputArgument ?? "nil")")
        print("createModel - Input argument type: \(type(of: inputArgument))")
        
        guard let argumentsDictionary = inputArgument as? [String: Any] else {
            print("❌ createModel - Failed to cast to [String: Any]")
            return nil
        }
        
        print("createModel - Arguments dictionary: \(argumentsDictionary)")
        
        guard let argumentsData = try? JSONSerialization.data(withJSONObject: argumentsDictionary) else {
            print("❌ createModel - Failed to serialize to JSON data")
            return nil
        }
        
        print("createModel - JSON data created successfully")
        
        guard let argumentsModel = try? JSONDecoder().decode(T.self, from: argumentsData) else {
            print("❌ createModel - Failed to decode JSON to model")
            return nil
        }
        
        print("✅ createModel - Model created successfully")
        return argumentsModel
    }
    

    
    func sendSuccess(for callbackId: String) {
        let pluginResult = CDVPluginResult(status: .ok)!
        pluginResult.keepCallback = true
        self.commandDelegate.send(pluginResult, callbackId: callbackId)
    }
    
    func sendError(_ message: String, for callbackId: String) {
        let pluginResult = CDVPluginResult(status: .error, messageAs: message)!
        self.commandDelegate.send(pluginResult, callbackId: callbackId)
    }
}



// MARK: - Hidden WebView Implementation (like Android)

private struct AssociatedKeys {
    static var delegateKey = "delegateKey"
    static var webViewKey = "webViewKey"
}

private class HiddenWebViewDelegate: NSObject, WKNavigationDelegate {
    private let completion: (Bool, String?) -> Void
    
    init(completion: @escaping (Bool, String?) -> Void) {
        self.completion = completion
        super.init()
    }
    
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        print("HiddenWebViewDelegate - webView didFinish")
        completion(true, nil)
    }
    
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        print("HiddenWebViewDelegate - didFail: \(error.localizedDescription)")
        completion(false, error.localizedDescription)
    }
    
    func webView(_ webView: WKWebView, didFailProvisionalNavigation navigation: WKNavigation!, withError error: Error) {
        print("HiddenWebViewDelegate - didFailProvisionalNavigation: \(error.localizedDescription)")
        completion(false, error.localizedDescription)
    }
    
    func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        print("HiddenWebViewDelegate - decidePolicyFor: \(navigationAction.request.url?.absoluteString ?? "nil")")
        decisionHandler(.allow)
    }
}

private class ModalWebViewDelegate: NSObject, WKNavigationDelegate {
    private let completion: (Bool, String?) -> Void
    
    init(completion: @escaping (Bool, String?) -> Void) {
        self.completion = completion
        super.init()
    }
    
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        print("ModalWebViewDelegate - webView didFinish")
        completion(true, nil)
    }
    
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        print("ModalWebViewDelegate - didFail: \(error.localizedDescription)")
        completion(false, error.localizedDescription)
    }
    
    func webView(_ webView: WKWebView, didFailProvisionalNavigation navigation: WKNavigation!, withError error: Error) {
        print("ModalWebViewDelegate - didFailProvisionalNavigation: \(error.localizedDescription)")
        completion(false, error.localizedDescription)
    }
    
    func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        print("ModalWebViewDelegate - decidePolicyFor: \(navigationAction.request.url?.absoluteString ?? "nil")")
        decisionHandler(.allow)
    }
}
