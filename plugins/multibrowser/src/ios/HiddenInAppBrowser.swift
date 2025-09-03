import UIKit
import ObjectiveC
@preconcurrency import WebKit

/// The plugin's main class - Compatible with multibrowser plugin
@objc(HiddenInAppBrowser)
class HiddenInAppBrowser: CDVPlugin {
    
    // Agregar variables de clase para las referencias (como en Android)
    private var modalWebView: WKWebView?
    private var modalNavigationController: UINavigationController?
    
    private var openedViewController: UIViewController?
    
    override func pluginInitialize() {
        print("🔧 HiddenInAppBrowser - pluginInitialize called")
        print("🔧 HiddenInAppBrowser - Plugin initialization completed")
        NSLog("🔧 HiddenInAppBrowser - NSLog test - Plugin initialized successfully")
    }
    
    // MARK: - Multibrowser Plugin Methods (Compatible with Android)
    
    @objc(openInWebView:)
    func openInWebView(command: CDVInvokedUrlCommand) {
        print("🔍 openInWebView - ===== INICIO DEL MÉTODO =====")
        print("openInWebView - Command received: \(command)")
        
        // Extract parameters like Android implementation
        guard let url = command.argument(at: 0) as? String,
              let target = command.argument(at: 1) as? String,
              let options = command.argument(at: 2) as? String else {
            print("❌ openInWebView - Invalid parameters")
            return self.sendError("Invalid parameters", for: command.callbackId)
        }
        
        // Extract showCloseButton parameter (default to true if not provided)
        let showCloseButton = command.argument(at: 3) as? Bool ?? true
        
        print("✅ openInWebView - Parameters extracted: URL=\(url), Target=\(target), Options=\(options), ShowCloseButton=\(showCloseButton)")
        
        self.commandDelegate.run { [weak self] in
            guard let self else { 
                print("❌ openInWebView - Self is nil")
                return 
            }
            
            // Create a modal WebView (like Android implementation)
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
                
                // Add close button conditionally
                if showCloseButton {
                    let closeButton = UIBarButtonItem(barButtonSystemItem: .close, target: self, action: #selector(self.closeWebView))
                    webViewController.navigationItem.leftBarButtonItem = closeButton
                }
                
                // Create navigation controller
                let navigationController = UINavigationController(rootViewController: webViewController)
                navigationController.modalPresentationStyle = .fullScreen
                
                // Set delegate
                let webViewDelegate = ModalWebViewDelegate { [weak self] success, error in
                    if success {
                        print("✅ openInWebView - Page loaded successfully")
                        // NO enviar callback aquí - como en Android, callback se envía después de mostrar el modal
                    } else {
                        print("❌ openInWebView - Failed to load page: \(error ?? "unknown error")")
                        self?.sendError("Failed to load URL: \(url)", for: command.callbackId)
                    }
                }
                
                webView.navigationDelegate = webViewDelegate
                
                // Keep references
                objc_setAssociatedObject(webView, &AssociatedKeys.delegateKey, webViewDelegate, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
                objc_setAssociatedObject(self, &AssociatedKeys.webViewKey, webView, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
                self.openedViewController = navigationController
                
                // Guardar referencias para closeWebView (como en Android)
                self.modalWebView = webView
                self.modalNavigationController = navigationController
                
                // Present modal
                self.viewController.present(navigationController, animated: true)
                print("✅ openInWebView - Modal presentado")
                
                // ENVIAR CALLBACK DESPUÉS DE MOSTRAR EL MODAL (como en Android)
                // Agregar delay de 2 segundos como en Android para GTM y contenido
                DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
                    print("✅ openInWebView - Enviando callback de éxito después de 2 segundos (como en Android)")
                    self.sendSuccess("Page loaded successfully", for: command.callbackId)
                }
                
                // Load URL DESPUÉS del callback (como en Android)
                let request = URLRequest(url: URL(string: url)!)
                webView.load(request)
                print("✅ openInWebView - Carga de URL iniciada")
            }
        }
    }
    
    @objc(openHidden:)
    func openHidden(command: CDVInvokedUrlCommand) {
        print("🔍 openHidden - ===== INICIO DEL MÉTODO =====")
        print("openHidden - Command received: \(command)")
        
        // Extract parameters like Android implementation
        guard let url = command.argument(at: 0) as? String,
              let target = command.argument(at: 1) as? String,
              let options = command.argument(at: 2) as? String else {
            print("❌ openHidden - Invalid parameters")
            return self.sendError("Invalid parameters", for: command.callbackId)
        }
        
        print("✅ openHidden - Parameters extracted: URL=\(url), Target=\(target), Options=\(options)")
        
        // For hidden mode, we'll just return success immediately (like Android)
        print("✅ openHidden - Hidden mode activated")
        self.sendSuccess("Hidden mode activated", for: command.callbackId)
    }
    
    @objc(closeWebView:)
    func closeWebView(command: CDVInvokedUrlCommand) {
        print("🔍 closeWebView - ===== INICIO DEL MÉTODO =====")
        print("closeWebView - Command received: \(command)")
        
        DispatchQueue.main.async { [weak self] in
            guard let self else { 
                print("❌ closeWebView - Self is nil")
                return 
            }
            
            if let openedViewController = self.openedViewController {
                print("✅ closeWebView - Cerrando WebView")
                openedViewController.dismiss(animated: true) {
                    print("✅ closeWebView - WebView cerrado exitosamente")
                    self.sendSuccess("WebView closed successfully", for: command.callbackId)
                }
            } else {
                print("❌ closeWebView - No hay WebView abierto")
                self.sendError("No WebView is currently open", for: command.callbackId)
            }
        }
    }
    
    @objc(openInExternalBrowser:)
    func openInExternalBrowser(command: CDVInvokedUrlCommand) {
        print("🔍 openInExternalBrowser - ===== INICIO DEL MÉTODO =====")
        print("openInExternalBrowser - Command received: \(command)")
        
        // Extract parameters like Android implementation
        guard let url = command.argument(at: 0) as? String,
              let target = command.argument(at: 1) as? String,
              let options = command.argument(at: 2) as? String else {
            print("❌ openInExternalBrowser - Invalid parameters")
            return self.sendError("Invalid parameters", for: command.callbackId)
        }
        
        print("✅ openInExternalBrowser - Parameters extracted: URL=\(url), Target=\(target), Options=\(options)")
        
        // Open in external browser
        DispatchQueue.main.async {
            if let urlObj = URL(string: url) {
                UIApplication.shared.open(urlObj) { success in
                    if success {
                        print("✅ openInExternalBrowser - URL opened successfully")
                        self.sendSuccess("External browser opened", for: command.callbackId)
                    } else {
                        print("❌ openInExternalBrowser - Failed to open URL")
                        self.sendError("Failed to open URL: \(url)", for: command.callbackId)
                    }
                }
            } else {
                self.sendError("Invalid URL: \(url)", for: command.callbackId)
            }
        }
    }
    
    // MARK: - Helper Methods
    
    @objc func closeWebView() {
        if let openedViewController = openedViewController {
            openedViewController.dismiss(animated: true)
        }
    }
    
    private func sendSuccess(_ message: String, for callbackId: String) {
        let pluginResult = CDVPluginResult(status: .ok, messageAs: message)!
        pluginResult.keepCallback = true
        self.commandDelegate.send(pluginResult, callbackId: callbackId)
    }
    
    private func sendError(_ message: String, for callbackId: String) {
        let pluginResult = CDVPluginResult(status: .error, messageAs: message)!
        self.commandDelegate.send(pluginResult, callbackId: callbackId)
    }
}

// MARK: - WebView Delegates

private struct AssociatedKeys {
    static var delegateKey = "delegateKey"
    static var webViewKey = "webViewKey"
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
