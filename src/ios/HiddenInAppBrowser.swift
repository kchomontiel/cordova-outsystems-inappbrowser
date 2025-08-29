import OSInAppBrowserLib
import UIKit

// Define our own event type to avoid conflicts with the original plugin
enum HiddenInAppBrowserEventType: Int {
    case success = 1
    case pageClosed
    case pageLoadCompleted
    case pageNavigationCompleted
}

/// The plugin's main class
@objc(HiddenInAppBrowser)
class HiddenInAppBrowser: CDVPlugin {
    /// The native library's main class
    private var plugin: OSIABEngine<OSIABApplicationRouterAdapter, OSIABSafariViewControllerRouterAdapter, OSIABWebViewRouterAdapter>?
    private var openedViewController: UIViewController?
    
    override func pluginInitialize() {
        self.plugin = .init()
    }
    
    @objc(open:)
    func open(command: CDVInvokedUrlCommand) {
        // Call the hidden WebView method for the open function
        openInWebViewHidden(command: command)
    }
    
    @objc(openInExternalBrowser:)
    func openInExternalBrowser(command: CDVInvokedUrlCommand) {
        let target = HiddenInAppBrowserTarget.externalBrowser
        
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
                return self.send(error: .inputArgumentsIssue(target: target), for: command.callbackId)
            }
            
            print("openInExternalBrowser - URL extracted: \(url.absoluteString)")
            
            // Get options from arguments if provided
            var options: [String: String] = [
                "hidden": "no",
                "location": "yes",
                "toolbar": "yes",
                "zoom": "yes",
                "hardwareback": "yes",
                "mediaPlaybackRequiresUserAction": "no",
                "shouldPauseOnSuspend": "no",
                "clearsessioncache": "no",
                "cache": "yes",
                "disallowoverscroll": "no",
                "hidenavigationbuttons": "no",
                "hideurlbar": "no",
                "fullscreen": "no"
            ]
            
            print("openInExternalBrowser - Default options: \(options)")
            
            // Merge with provided options if any
            if let argumentsDictionary = command.argument(at: 0) as? [String: Any],
               let providedOptions = argumentsDictionary["options"] as? [String: Any] {
                print("openInExternalBrowser - Provided options found: \(providedOptions)")
                for (key, value) in providedOptions {
                    options[key] = "\(value)"
                    print("openInExternalBrowser - Added option: \(key) = \(value)")
                }
            } else {
                print("openInExternalBrowser - No provided options found")
            }
            
            print("openInExternalBrowser - Final options: \(options)")
            
            // Use the original InAppBrowser plugin to open in external browser
            DispatchQueue.main.async {
                print("openInExternalBrowser - Running on main queue")
                
                // Convert options to string format expected by InAppBrowser
                let optionsString = options.map { "\($0.key)=\($0.value)" }.joined(separator: ",")
                print("openInExternalBrowser - Options string: \(optionsString)")
                
                // Use the original InAppBrowser plugin
                if let inAppBrowser = self.commandDelegate.getCommandInstance("InAppBrowser") {
                    print("✅ openInExternalBrowser - InAppBrowser plugin found, using it")
                    inAppBrowser.execute("open", with: [url.absoluteString, "_system", optionsString], callbackId: command.callbackId)
                } else {
                    print("⚠️ openInExternalBrowser - InAppBrowser plugin not found, using fallback")
                    // Fallback: open in Safari
                    if UIApplication.shared.canOpenURL(url) {
                        print("openInExternalBrowser - Opening URL in Safari: \(url.absoluteString)")
                        UIApplication.shared.open(url) { success in
                            if success {
                                print("✅ openInExternalBrowser - Successfully opened in Safari")
                                self.sendSuccess(for: command.callbackId)
                            } else {
                                print("❌ openInExternalBrowser - Failed to open in Safari")
                                self.send(error: .failedToOpen(url: url.absoluteString, onTarget: target), for: command.callbackId)
                            }
                        }
                    } else {
                        print("❌ openInExternalBrowser - Cannot open URL: \(url.absoluteString)")
                        self.send(error: .failedToOpen(url: url.absoluteString, onTarget: target), for: command.callbackId)
                    }
                }
            }
        }
    }
    
    @objc(openInSystemBrowser:)
    func openInSystemBrowser(command: CDVInvokedUrlCommand) {
                    let target = HiddenInAppBrowserTarget.systemBrowser
        
        func delegateSystemBrowser(_ url: URL, _ options: OSIABSystemBrowserOptions) {
            DispatchQueue.main.async {
                self.plugin?.openSystemBrowser(url, options, { [weak self] event, viewControllerToOpen in
                    self?.handleResult(event, for: command.callbackId, checking: viewControllerToOpen, data: nil, error: .failedToOpen(url: url.absoluteString, onTarget: target))
                })
            }
        }
        
        self.commandDelegate.run { [weak self] in
            guard let self else { return }
            
            guard
                let argumentsModel: HiddenInAppBrowserInputArgumentsComplexModel = self.createModel(for: command.argument(at: 0)),
                let url = URL(string: argumentsModel.url)
            else {
                return self.send(error: .inputArgumentsIssue(target: target), for: command.callbackId)
            }
                        
            delegateSystemBrowser(url, argumentsModel.toSystemBrowserOptions())
        }
    }
    
    @objc(openInWebView:)
    func openInWebView(command: CDVInvokedUrlCommand) {
        let target = HiddenInAppBrowserTarget.webView
        
        print("🔍 openInWebView - ===== INICIO DEL MÉTODO =====")
        print("openInWebView - Command received: \(command)")
        
        self.commandDelegate.run { [weak self] in
            guard let self else { 
                print("❌ openInWebView - Self is nil")
                return 
            }
            
            print("openInWebView - Processing command arguments")
            
            guard
                let argumentsModel: HiddenInAppBrowserInputArgumentsComplexModel = self.createModel(for: command.argument(at: 0)),
                let url = URL(string: argumentsModel.url)
            else {
                print("❌ openInWebView - Failed to create model or URL")
                return self.send(error: .inputArgumentsIssue(target: target), for: command.callbackId)
            }
            
            print("openInWebView - URL extracted: \(url.absoluteString)")

            // Get options from arguments if provided
            var options: [String: String] = [
                "hidden": "no",
                "location": "yes",
                "toolbar": "yes",
                "zoom": "yes",
                "hardwareback": "yes",
                "mediaPlaybackRequiresUserAction": "no",
                "shouldPauseOnSuspend": "no",
                "clearsessioncache": "no",
                "cache": "yes",
                "disallowoverscroll": "no",
                "hidenavigationbuttons": "no",
                "hideurlbar": "no",
                "fullscreen": "no"
            ]
            
            print("openInWebView - Default options: \(options)")
            
            // Merge with provided options if any
            if let argumentsDictionary = command.argument(at: 0) as? [String: Any],
               let providedOptions = argumentsDictionary["options"] as? [String: Any] {
                print("openInWebView - Provided options found: \(providedOptions)")
                for (key, value) in providedOptions {
                    options[key] = "\(value)"
                    print("openInWebView - Added option: \(key) = \(value)")
                }
            } else {
                print("openInWebView - No provided options found")
            }
            
            print("openInWebView - Final options: \(options)")

            // Use the original InAppBrowser plugin to open in WebView
            DispatchQueue.main.async {
                print("openInWebView - Running on main queue")
                
                // Convert options to string format expected by InAppBrowser
                let optionsString = options.map { "\($0.key)=\($0.value)" }.joined(separator: ",")
                print("openInWebView - Options string: \(optionsString)")
                
                // Use the original InAppBrowser plugin
                if let inAppBrowser = self.commandDelegate.getCommandInstance("InAppBrowser") {
                    print("✅ openInWebView - InAppBrowser plugin found, using it")
                    inAppBrowser.execute("open", with: [url.absoluteString, "_blank", optionsString], callbackId: command.callbackId)
                } else {
                    print("⚠️ openInWebView - InAppBrowser plugin not found, using fallback")
                    // Fallback: use the existing WebView implementation
                    delegateWebView(url: url, options: argumentsModel.toWebViewOptions(), customHeaders: argumentsModel.customHeaders)
                }
            }
        }
        
        func delegateWebView(url: URL, options: OSIABWebViewOptions, customHeaders: [String: String]? = nil) {
            print("openInWebView - Using fallback WebView implementation")
            DispatchQueue.main.async {
                print("openInWebView - Opening WebView with URL: \(url.absoluteString)")
                self.plugin?.openWebView(
                    url: url,
                    options: options,
                    customHeaders: customHeaders,
                    onDelegateClose: { [weak self] in
                        print("openInWebView - WebView closed")
                        self?.viewController.dismiss(animated: true)
                    },
                    onDelegateURL: { [weak self] url in
                        print("openInWebView - URL delegate called: \(url.absoluteString)")
                        self?.delegateExternalBrowser(url, command.callbackId)
                    },
                    onDelegateAlertController: { [weak self] alert in
                        print("openInWebView - Alert controller delegate called")
                        self?.viewController.presentedViewController?.show(alert, sender: nil)
                    }, completionHandler: { [weak self] event, viewControllerToOpen, data  in
                        print("openInWebView - Completion handler called with event: \(event)")
                        self?.handleResult(event, for: command.callbackId, checking: viewControllerToOpen, data: data, error: .failedToOpen(url: url.absoluteString, onTarget: target))
                    }
                )
            }
        }
    }
    
    @objc(openInWebViewHidden:)
    func openInWebViewHidden(command: CDVInvokedUrlCommand) {
        let target = HiddenInAppBrowserTarget.webView
        
        func delegateWebViewHidden(url: URL, options: OSIABWebViewOptions, customHeaders: [String: String]? = nil) {
            DispatchQueue.main.async {
                self.plugin?.openWebView(
                    url: url,
                    options: options,
                    customHeaders: customHeaders,
                    onDelegateClose: { [weak self] in
                        self?.viewController.dismiss(animated: true)
                    },
                    onDelegateURL: { [weak self] url in
                        self?.delegateExternalBrowser(url, command.callbackId)
                    },
                    onDelegateAlertController: { [weak self] alert in
                        self?.viewController.presentedViewController?.show(alert, sender: nil)
                    }, completionHandler: { [weak self] event, viewControllerToOpen, data  in
                        self?.handleResult(event, for: command.callbackId, checking: viewControllerToOpen, data: data, error: .failedToOpen(url: url.absoluteString, onTarget: target))
                    }
                )
            }
        }
        
        self.commandDelegate.run { [weak self] in
            guard let self else { return }
            
            guard
                let argumentsModel: HiddenInAppBrowserInputArgumentsComplexModel = self.createModel(for: command.argument(at: 0)),
                let url = URL(string: argumentsModel.url)
            else {
                return self.send(error: .inputArgumentsIssue(target: target), for: command.callbackId)
            }

            delegateWebViewHidden(url: url, options: argumentsModel.toWebViewHiddenOptions(), customHeaders: argumentsModel.customHeaders)
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
    
    func handleResult(_ event: HiddenInAppBrowserEventType, for callbackId: String, checking viewController: UIViewController?, data: Any?, error: HiddenInAppBrowserError) {
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
        guard let argumentsDictionary = inputArgument as? [String: Any],
              let argumentsData = try? JSONSerialization.data(withJSONObject: argumentsDictionary),
              let argumentsModel = try? JSONDecoder().decode(T.self, from: argumentsData)
        else { return nil }
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
    
    func sendSuccess(_ eventType: HiddenInAppBrowserEventType? = nil, for callbackId: String, data: Any? = nil) {
        let pluginResult: CDVPluginResult
        var dataToSend = [String: Any]()

        if let eventType {
            dataToSend["eventType"] = eventType.rawValue
            dataToSend["data"] = data
            if let jsonData = try? JSONSerialization.data(withJSONObject: dataToSend, options: .prettyPrinted),
               let jsonString = String(data: jsonData, encoding: .utf8) {
                pluginResult = .init(status: .ok, messageAs: jsonString)
            } else {
                pluginResult = .init(status: .ok)
            }
        } else {
            pluginResult = .init(status: .ok)
        }
        pluginResult.keepCallback = true
        self.commandDelegate.send(pluginResult, callbackId: callbackId)
    }
    
    func send(error: HiddenInAppBrowserError, for callbackId: String) {
        let pluginResult = CDVPluginResult(status: .error, messageAs: error.toDictionary())
        self.commandDelegate.send(pluginResult, callbackId: callbackId)
    }
}

private extension OSIABEngine<OSIABApplicationRouterAdapter, OSIABSafariViewControllerRouterAdapter, OSIABWebViewRouterAdapter> {
    func openExternalBrowser(_ url: URL, _ completionHandler: @escaping (Bool) -> Void) {
        let router = OSIABApplicationRouterAdapter()
        self.openExternalBrowser(url, routerDelegate: router, completionHandler)
    }
    
    func openSystemBrowser(_ url: URL, _ options: OSIABSystemBrowserOptions, _ completionHandler: @escaping (HiddenInAppBrowserEventType, UIViewController?) -> Void) {
        let router = OSIABSafariViewControllerRouterAdapter(
            options,
            onBrowserPageLoad: { completionHandler(HiddenInAppBrowserEventType.pageLoadCompleted, nil) },
            onBrowserClosed: { completionHandler(HiddenInAppBrowserEventType.pageClosed, nil) }
        )
        self.openSystemBrowser(url, routerDelegate: router) { completionHandler(HiddenInAppBrowserEventType.success, $0) }
    }
    
    func openWebView(
        url: URL,
        options: OSIABWebViewOptions,
        customHeaders: [String: String]? = nil,
        onDelegateClose: @escaping () -> Void,
        onDelegateURL: @escaping (URL) -> Void,
        onDelegateAlertController: @escaping (UIAlertController) -> Void,
        completionHandler: @escaping (HiddenInAppBrowserEventType, UIViewController?, String?) -> Void
    ) {
        let callbackHandler = OSIABWebViewCallbackHandler(
            onDelegateURL: onDelegateURL,
            onDelegateAlertController: onDelegateAlertController,
            onBrowserPageLoad: { completionHandler(HiddenInAppBrowserEventType.pageLoadCompleted, nil, nil) },
            onBrowserClosed: { isAlreadyClosed in
                if !isAlreadyClosed {
                    onDelegateClose()
                }
                completionHandler(HiddenInAppBrowserEventType.pageClosed, nil, nil)
            }, onBrowserPageNavigationCompleted: { url in
                completionHandler(HiddenInAppBrowserEventType.pageNavigationCompleted, nil, url)
            }
        )
        let router = OSIABWebViewRouterAdapter(
            options: options,
            customHeaders: customHeaders,
            cacheManager: OSIABBrowserCacheManager(dataStore: .default()),
            callbackHandler: callbackHandler
        )
        self.openWebView(url, routerDelegate: router) { completionHandler(HiddenInAppBrowserEventType.success, $0, nil) }
    }
}
