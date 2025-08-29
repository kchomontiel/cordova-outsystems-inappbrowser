import UIKit

/// The plugin's main class
@objc(HiddenInAppBrowser)
class HiddenInAppBrowser: CDVPlugin {
    private var openedViewController: UIViewController?
    
    override func pluginInitialize() {
        print("🔧 HiddenInAppBrowser - pluginInitialize called")
        print("🔧 HiddenInAppBrowser - Plugin initialization completed")
        NSLog("🔧 HiddenInAppBrowser - NSLog test - Plugin initialized successfully")
    }
    
    @objc(open:)
    func open(command: CDVInvokedUrlCommand) {
        let target = HiddenInAppBrowserTarget.webView
        
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
                return self.send(error: .inputArgumentsIssue(target: target), for: command.callbackId)
            }

            print("✅ open - Model created successfully")
            print("open - URL: \(url.absoluteString)")
            
            print("open - Creating hidden WebView directly")
            
            // Create hidden WebView directly (like Android)
            DispatchQueue.main.async {
                print("open - Running on main queue")
                
                // Create a hidden WebView directly (like Android implementation)
                let webView = UIWebView(frame: CGRect.zero)
                print("open - WebView created")
                
                // Make WebView invisible (like Android: alpha = 0f, visibility = GONE)
                webView.alpha = 0.0
                webView.isHidden = true
                print("open - WebView set to invisible")
                
                // Configure WebView settings
                webView.scalesPageToFit = false
                webView.allowsInlineMediaPlayback = true
                webView.mediaPlaybackRequiresUserAction = false
                print("open - WebView settings configured")
                
                // Create a custom delegate to handle navigation
                let webViewDelegate = HiddenWebViewDelegate { [weak self] success, error in
                    if success {
                        print("open - Page finished loading successfully")
                        self?.sendSuccess(for: command.callbackId)
                    } else {
                        print("open - Error loading URL: \(error ?? "unknown error")")
                        self?.send(error: .failedToOpen(url: url.absoluteString, onTarget: target), for: command.callbackId)
                    }
                }
                
                // Set the delegate
                webView.delegate = webViewDelegate
                
                // Keep a strong reference to the delegate
                objc_setAssociatedObject(webView, &AssociatedKeys.delegateKey, webViewDelegate, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
                
                print("open - WebView delegate configured")
                
                // Load the URL in background
                print("open - Loading URL: \(url.absoluteString)")
                let request = URLRequest(url: url)
                webView.loadRequest(request)
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
                return self.send(error: .inputArgumentsIssue(target: .externalBrowser), for: command.callbackId)
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
                        self.send(error: .failedToOpen(url: url.absoluteString, onTarget: .externalBrowser), for: command.callbackId)
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
                return self.send(error: .inputArgumentsIssue(target: .webView), for: command.callbackId)
            }

            print("✅ openInWebView - Model created successfully")
            print("openInWebView - URL: \(url.absoluteString)")
            
            // Use Apache InAppBrowser plugin
            if let inAppBrowserPlugin = self.commandDelegate.getCommandInstance("InAppBrowser") as? CDVPlugin {
                print("✅ openInWebView - Apache InAppBrowser plugin found")
                
                // Create options for visible WebView
                let options = "location=yes,toolbar=yes,hidenavigationbuttons=no"
                
                // Call Apache InAppBrowser's open method
                let args = CDVInvokedUrlCommand(
                    arguments: [url.absoluteString, "_blank", options],
                    callbackId: command.callbackId,
                    className: "InAppBrowser",
                    methodName: "open"
                )
                
                inAppBrowserPlugin.execute(args)
                
            } else {
                print("❌ openInWebView - Apache InAppBrowser plugin not found")
                self.send(error: .failedToOpen(url: url.absoluteString, onTarget: .webView), for: command.callbackId)
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
                self.send(error: .noBrowserToClose, for: command.callbackId)
            }
        }
    }
}

private extension HiddenInAppBrowser {
    func delegateExternalBrowser(_ url: URL, _ callbackId: String) {
        DispatchQueue.main.async {
            self.plugin?.openExternalBrowser(url, { [weak self] success in
                guard let self else { return }
                
                if success {
                    self.sendSuccess(for: callbackId)
                } else {
                    self.send(error: .failedToOpen(url: url.absoluteString, onTarget: .externalBrowser), for: callbackId)
                }
            })
        }
    }
    
    func handleResult(_ event: OSIABEventType, for callbackId: String, checking viewController: UIViewController?, data: Any?, error: HiddenInAppBrowserError) {
        let sendEvent: (Any?) -> Void = { data in self.sendSuccess(event, for: callbackId, data: data) }
        
        switch event {
        case .success:
            if let viewController {
                self.present(viewController) { [weak self] in
                    self?.openedViewController = viewController
                    sendEvent(data)
                }
            } else {
                self.send(error: error, for: callbackId)
            }
        case .pageClosed:
            self.openedViewController = nil
            fallthrough
        case .pageLoadCompleted:
            sendEvent(data)
        case .pageNavigationCompleted:
            sendEvent(data)
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
    
    func present(_ viewController: UIViewController, _ completionHandler: (() -> Void)?) {
        let showNewViewController: () -> Void = {
            self.viewController.present(viewController, animated: true, completion: completionHandler)
        }
        
        if let presentedViewController = self.viewController.presentedViewController, presentedViewController == self.openedViewController {
            presentedViewController.dismiss(animated: true, completion: showNewViewController)
        } else {
            showNewViewController()
        }
    }
    
    func sendSuccess(for callbackId: String) {
        let pluginResult = CDVPluginResult(status: .ok)
        pluginResult.keepCallback = true
        self.commandDelegate.send(pluginResult, callbackId: callbackId)
    }
    
    func send(error: HiddenInAppBrowserError, for callbackId: String) {
        let pluginResult = CDVPluginResult(status: .error, messageAs: error.toDictionary())
        self.commandDelegate.send(pluginResult, callbackId: callbackId)
    }
}



// MARK: - Hidden WebView Implementation (like Android)

private struct AssociatedKeys {
    static var delegateKey = "delegateKey"
    static var webViewKey = "webViewKey"
}

private class HiddenWebViewDelegate: NSObject, UIWebViewDelegate {
    private let completion: (Bool, String?) -> Void
    
    init(completion: @escaping (Bool, String?) -> Void) {
        self.completion = completion
        super.init()
    }
    
    func webViewDidFinishLoad(_ webView: UIWebView) {
        print("HiddenWebViewDelegate - webViewDidFinishLoad")
        completion(true, nil)
    }
    
    func webView(_ webView: UIWebView, didFailLoadWithError error: Error) {
        print("HiddenWebViewDelegate - didFailLoadWithError: \(error.localizedDescription)")
        completion(false, error.localizedDescription)
    }
    
    func webView(_ webView: UIWebView, shouldStartLoadWith request: URLRequest, navigationType: UIWebView.NavigationType) -> Bool {
        print("HiddenWebViewDelegate - shouldStartLoadWith: \(request.url?.absoluteString ?? "nil")")
        return true
    }
}
