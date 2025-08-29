import OSInAppBrowserLib
import UIKit

// Define our own event type to avoid conflicts with the original plugin
enum HiddenInAppBrowserEventType: Int {
    case success = 1
    case pageClosed
    case pageLoadCompleted
    case pageNavigationCompleted
}

@objc(HiddenInAppBrowser)
class HiddenInAppBrowser: CDVPlugin {
    
    /// The native library's main class
    private var plugin: OSIABEngine<OSIABApplicationRouterAdapter, OSIABSafariViewControllerRouterAdapter, OSIABWebViewRouterAdapter>?
    private var openedViewController: UIViewController?
    
    override func pluginInitialize() {
        self.plugin = OSIABEngine<OSIABApplicationRouterAdapter, OSIABSafariViewControllerRouterAdapter, OSIABWebViewRouterAdapter>()
    }
    
    @objc(open:)
    func open(command: CDVInvokedUrlCommand) {
        let target = HiddenInAppBrowserTarget.webView
        
        print("🔍 open - ===== INICIO DEL MÉTODO =====")
        print("open - Command received: \(command)")
        
        self.commandDelegate.run { [weak self] in
            if let self = self {
                print("open - Processing command arguments")
                
                if let argumentsModel: HiddenInAppBrowserInputArgumentsSimpleModel = self.createModel(for: command.argument(at: 0)),
                   let url = URL(string: argumentsModel.url) {
                    print("open - URL extracted: \(url.absoluteString)")
                    
                    // Always use hidden mode for the open method
                    var options: [String: String] = [
                        "hidden": "yes",
                        "location": "no",
                        "toolbar": "no",
                        "zoom": "no",
                        "hardwareback": "no",
                        "mediaPlaybackRequiresUserAction": "no",
                        "shouldPauseOnSuspend": "no",
                        "clearsessioncache": "no",
                        "cache": "yes",
                        "disallowoverscroll": "no",
                        "hidenavigationbuttons": "yes",
                        "hideurlbar": "yes",
                        "fullscreen": "no"
                    ]
                    
                    print("open - Default options: \(options)")
                    
                    // Merge with provided options if any
                    if let argumentsDictionary = command.argument(at: 0) as? [String: Any],
                       let providedOptions = argumentsDictionary["options"] as? [String: Any] {
                        print("open - Provided options found: \(providedOptions)")
                        for (key, value) in providedOptions {
                            if key != "hidden" { // Don't override hidden=yes
                                options[key] = "\(value)"
                                print("open - Added option: \(key) = \(value)")
                            }
                        }
                    } else {
                        print("open - No provided options found")
                    }
                    
                    print("open - Final options: \(options)")
                    
                    // Hidden mode: Load URL in background WebView
                    DispatchQueue.main.async {
                        print("open - Running on main queue")
                        
                        do {
                            let activity = self.cordova.activity
                            if let activity = activity {
                                activity.runOnUiThread {
                                    do {
                                        let webView = UIWebView(frame: CGRect.zero)
                                        webView.alpha = 0.0
                                        webView.isHidden = true
                                        
                                        // Configure WebView settings
                                        webView.scalesPageToFit = false
                                        webView.allowsInlineMediaPlayback = true
                                        webView.mediaPlaybackRequiresUserAction = false
                                        
                                        // Load URL
                                        let request = URLRequest(url: url)
                                        webView.loadRequest(request)
                                        
                                        print("✅ open - URL loaded in hidden WebView successfully")
                                        self.sendSuccess(for: command.callbackId)
                                    } catch {
                                        print("❌ open - Error creating WebView: \(error)")
                                        self.sendError(error: .failedToOpen(url: url.absoluteString, onTarget: target), for: command.callbackId)
                                    }
                                }
                            } else {
                                print("❌ open - Activity is nil")
                                self.sendError(error: .failedToOpen(url: url.absoluteString, onTarget: target), for: command.callbackId)
                            }
                        } catch {
                            print("❌ open - Error: \(error)")
                            self.sendError(error: .failedToOpen(url: url.absoluteString, onTarget: target), for: command.callbackId)
                        }
                    }
                } else {
                    print("❌ open - Failed to create model or URL")
                    self.sendError(error: .inputArgumentsIssue(target: target), for: command.callbackId)
                }
            } else {
                print("❌ open - Self is nil")
            }
        }
    }
    
    @objc(openInExternalBrowser:)
    func openInExternalBrowser(command: CDVInvokedUrlCommand) {
        let target = HiddenInAppBrowserTarget.externalBrowser
        
        print("🔍 openInExternalBrowser - ===== INICIO DEL MÉTODO =====")
        print("openInExternalBrowser - Command received: \(command)")
        
        self.commandDelegate.run { [weak self] in
            if let self = self {
                print("openInExternalBrowser - Processing command arguments")
                
                if let argumentsModel: HiddenInAppBrowserInputArgumentsSimpleModel = self.createModel(for: command.argument(at: 0)),
                   let url = URL(string: argumentsModel.url) {
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
                                        self.sendError(error: .failedToOpen(url: url.absoluteString, onTarget: target), for: command.callbackId)
                                    }
                                }
                            } else {
                                print("❌ openInExternalBrowser - Cannot open URL: \(url.absoluteString)")
                                self.sendError(error: .failedToOpen(url: url.absoluteString, onTarget: target), for: command.callbackId)
                            }
                        }
                    }
                } else {
                    print("❌ openInExternalBrowser - Failed to create model or URL")
                    self.sendError(error: .inputArgumentsIssue(target: target), for: command.callbackId)
                }
            } else {
                print("❌ openInExternalBrowser - Self is nil")
            }
        }
    }
    
    @objc(openInWebView:)
    func openInWebView(command: CDVInvokedUrlCommand) {
        let target = HiddenInAppBrowserTarget.webView
        
        print("🔍 openInWebView - ===== INICIO DEL MÉTODO =====")
        print("openInWebView - Command received: \(command)")
        
        self.commandDelegate.run { [weak self] in
            if let self = self {
                print("openInWebView - Processing command arguments")
                
                if let argumentsModel: HiddenInAppBrowserInputArgumentsComplexModel = self.createModel(for: command.argument(at: 0)),
                   let url = URL(string: argumentsModel.url) {
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
                            self.delegateWebView(url: url, options: argumentsModel.toWebViewOptions(), customHeaders: argumentsModel.customHeaders)
                        }
                    }
                } else {
                    print("❌ openInWebView - Failed to create model or URL")
                    self.sendError(error: .inputArgumentsIssue(target: target), for: command.callbackId)
                }
            } else {
                print("❌ openInWebView - Self is nil")
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
    
    @objc(close:)
    func close(command: CDVInvokedUrlCommand) {
        self.commandDelegate.run { [weak self] in
            if let self = self {
                if let openedViewController = self.openedViewController {
                    DispatchQueue.main.async {
                        openedViewController.dismiss(animated: true) { [weak self] in
                            if let self = self {
                                self.sendSuccess(for: command.callbackId)
                            }
                        }
                    }
                } else {
                    self.sendError(error: .noBrowserToClose, for: command.callbackId)
                }
            }
        }
    }
}

private extension HiddenInAppBrowser {
    func createModel<T: Decodable>(for inputArgument: Any?) -> T? {
        if let argumentsDictionary = inputArgument as? [String: Any],
           let argumentsData = try? JSONSerialization.data(withJSONObject: argumentsDictionary),
           let argumentsModel = try? JSONDecoder().decode(T.self, from: argumentsData) {
            return argumentsModel
        }
        return nil
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
    
    func sendError(error: HiddenInAppBrowserError, for callbackId: String) {
        let pluginResult = CDVPluginResult(status: .error, messageAs: error.toDictionary())
        self.commandDelegate.send(pluginResult, callbackId: callbackId)
    }
    
    func delegateExternalBrowser(_ url: URL, _ callbackId: String) {
        DispatchQueue.main.async {
            self.plugin?.openExternalBrowser(url, { [weak self] success in
                if let self = self {
                    if success {
                        self.sendSuccess(for: callbackId)
                    } else {
                        self.sendError(error: .failedToOpen(url: url.absoluteString, onTarget: .externalBrowser), for: callbackId)
                    }
                }
            })
        }
    }
    
    func handleResult(_ event: HiddenInAppBrowserEventType, for callbackId: String, checking viewController: UIViewController?, data: Any?, error: HiddenInAppBrowserError) {
        let sendEvent: (Any?) -> Void = { data in self.sendSuccess(for: callbackId) }
        
        switch event {
        case .success:
            if let viewController = viewController {
                self.present(viewController) { [weak self] in
                    if let self = self {
                        self.openedViewController = viewController
                        sendEvent(data)
                    }
                }
            } else {
                self.sendError(error: error, for: callbackId)
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
