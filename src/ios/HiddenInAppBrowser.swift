import OSInAppBrowserLib
import UIKit

typealias HiddenInAppBrowserEngine = OSIABEngine<OSIABApplicationRouterAdapter, OSIABSafariViewControllerRouterAdapter, OSIABWebViewRouterAdapter>

/// The plugin's main class
@objc(HiddenInAppBrowser)
class HiddenInAppBrowser: CDVPlugin {
    /// The native library's main class
    private var plugin: HiddenInAppBrowserEngine?
    private var openedViewController: UIViewController?
    
    override func pluginInitialize() {
        print("🔧 HiddenInAppBrowser - pluginInitialize called")
        print("🔧 HiddenInAppBrowser - Starting plugin initialization...")
        self.plugin = .init()
        print("🔧 HiddenInAppBrowser - Plugin initialized: \(self.plugin != nil)")
        print("🔧 HiddenInAppBrowser - Plugin initialization completed")
        
        // Add a test log that should always appear
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
            
            // Create hidden WebView options
            let hiddenOptions = OSIABWebViewOptions(
                showURL: false,
                showToolbar: false,
                clearCache: true,
                clearSessionCache: true,
                mediaPlaybackRequiresUserAction: false,
                closeButtonText: "Close",
                toolbarPosition: .defaultValue,
                showNavigationButtons: false,
                leftToRight: false,
                allowOverScroll: true,
                enableViewportScale: false,
                allowInLineMediaPlayback: false,
                surpressIncrementalRendering: false,
                viewStyle: .defaultValue,
                animationEffect: .defaultValue,
                customUserAgent: nil,
                allowsBackForwardNavigationGestures: true
            )
            
            print("open - Hidden options created")
            
            // Create hidden WebView
            DispatchQueue.main.async {
                print("open - Running on main queue")
                
                if let plugin = self.plugin {
                    print("✅ open - Plugin found, calling openWebView with hidden options")
                    plugin.openWebView(
                        url: url,
                        options: hiddenOptions,
                        customHeaders: nil,
                        onDelegateClose: { [weak self] in
                            print("open - onDelegateClose called")
                            self?.viewController.dismiss(animated: true)
                        },
                        onDelegateURL: { [weak self] url in
                            print("open - onDelegateURL called with: \(url.absoluteString)")
                            self?.delegateExternalBrowser(url, command.callbackId)
                        },
                        onDelegateAlertController: { [weak self] alert in
                            print("open - onDelegateAlertController called")
                            self?.viewController.presentedViewController?.show(alert, sender: nil)
                        }, completionHandler: { [weak self] event, viewControllerToOpen, data  in
                            print("open - completionHandler called with event: \(event)")
                            self?.handleResult(event, for: command.callbackId, checking: viewControllerToOpen, data: data, error: .failedToOpen(url: url.absoluteString, onTarget: target))
                        }
                    )
                } else {
                    print("❌ open - Plugin is nil, cannot open WebView")
                    self.send(error: .failedToOpen(url: url.absoluteString, onTarget: target), for: command.callbackId)
                }
            }
        }
    }
    
    @objc(openInExternalBrowser:)
    func openInExternalBrowser(command: CDVInvokedUrlCommand) {
        let target = HiddenInAppBrowserTarget.externalBrowser
        
        self.commandDelegate.run { [weak self] in
            guard let self else { return }
            
            guard
                let argumentsModel: HiddenInAppBrowserInputArgumentsSimpleModel = self.createModel(for: command.argument(at: 0)),
                let url = URL(string: argumentsModel.url)
            else {
                return self.send(error: .inputArgumentsIssue(target: target), for: command.callbackId)
            }
            
            delegateExternalBrowser(url, command.callbackId)
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
        
        NSLog("🔍 openInWebView - ===== INICIO DEL MÉTODO =====")
        print("🔍 openInWebView - ===== INICIO DEL MÉTODO =====")
        print("openInWebView - Command received: \(command)")
        print("openInWebView - Plugin is nil: \(self.plugin == nil)")
        
        func delegateWebView(url: URL, options: OSIABWebViewOptions, customHeaders: [String: String]? = nil) {
            print("openInWebView - delegateWebView called with URL: \(url.absoluteString)")
            print("openInWebView - Plugin is nil in delegateWebView: \(self.plugin == nil)")
            
            DispatchQueue.main.async {
                print("openInWebView - Running on main queue")
                print("openInWebView - Plugin is nil on main queue: \(self.plugin == nil)")
                
                if let plugin = self.plugin {
                    print("✅ openInWebView - Plugin found, calling openWebView")
                    plugin.openWebView(
                        url: url,
                        options: options,
                        customHeaders: customHeaders,
                        onDelegateClose: { [weak self] in
                            print("openInWebView - onDelegateClose called")
                            self?.viewController.dismiss(animated: true)
                        },
                        onDelegateURL: { [weak self] url in
                            print("openInWebView - onDelegateURL called with: \(url.absoluteString)")
                            self?.delegateExternalBrowser(url, command.callbackId)
                        },
                        onDelegateAlertController: { [weak self] alert in
                            print("openInWebView - onDelegateAlertController called")
                            self?.viewController.presentedViewController?.show(alert, sender: nil)
                        }, completionHandler: { [weak self] event, viewControllerToOpen, data  in
                            print("openInWebView - completionHandler called with event: \(event)")
                            self?.handleResult(event, for: command.callbackId, checking: viewControllerToOpen, data: data, error: .failedToOpen(url: url.absoluteString, onTarget: target))
                        }
                    )
                } else {
                    print("❌ openInWebView - Plugin is nil, cannot open WebView")
                    self.send(error: .failedToOpen(url: url.absoluteString, onTarget: target), for: command.callbackId)
                }
            }
        }
        
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
                return self.send(error: .inputArgumentsIssue(target: target), for: command.callbackId)
            }

            print("✅ openInWebView - Model created successfully")
            print("openInWebView - URL: \(url.absoluteString)")
            
            // Create visible WebView options
            let visibleOptions = OSIABWebViewOptions(
                showURL: true,
                showToolbar: true,
                clearCache: true,
                clearSessionCache: false,
                mediaPlaybackRequiresUserAction: false,
                closeButtonText: "Close",
                toolbarPosition: .defaultValue,
                showNavigationButtons: true,
                leftToRight: false,
                allowOverScroll: false,
                enableViewportScale: false,
                allowInLineMediaPlayback: false,
                surpressIncrementalRendering: false,
                viewStyle: .defaultValue,
                animationEffect: .defaultValue,
                customUserAgent: nil,
                allowsBackForwardNavigationGestures: true
            )
            
            print("openInWebView - Visible options created")
            
            delegateWebView(url: url, options: visibleOptions, customHeaders: nil)
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
    
    func sendSuccess(_ eventType: OSIABEventType? = nil, for callbackId: String, data: Any? = nil) {
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

private extension HiddenInAppBrowserEngine {
    func openExternalBrowser(_ url: URL, _ completionHandler: @escaping (Bool) -> Void) {
        let router = OSIABApplicationRouterAdapter()
        self.openExternalBrowser(url, routerDelegate: router, completionHandler)
    }
    
    func openSystemBrowser(_ url: URL, _ options: OSIABSystemBrowserOptions, _ completionHandler: @escaping (OSIABEventType, UIViewController?) -> Void) {
        let router = OSIABSafariViewControllerRouterAdapter(
            options,
            onBrowserPageLoad: { completionHandler(.pageLoadCompleted, nil) },
            onBrowserClosed: { completionHandler(.pageClosed, nil) }
        )
        self.openSystemBrowser(url, routerDelegate: router) { completionHandler(.success, $0) }
    }
    
    func openWebView(
        url: URL,
        options: OSIABWebViewOptions,
        customHeaders: [String: String]? = nil,
        onDelegateClose: @escaping () -> Void,
        onDelegateURL: @escaping (URL) -> Void,
        onDelegateAlertController: @escaping (UIAlertController) -> Void,
        completionHandler: @escaping (OSIABEventType, UIViewController?, String?) -> Void
    ) {
        let callbackHandler = OSIABWebViewCallbackHandler(
            onDelegateURL: onDelegateURL,
            onDelegateAlertController: onDelegateAlertController,
            onBrowserPageLoad: { completionHandler(.pageLoadCompleted, nil, nil) },
            onBrowserClosed: { isAlreadyClosed in
                if !isAlreadyClosed {
                    onDelegateClose()
                }
                completionHandler(.pageClosed, nil, nil)
            }, onBrowserPageNavigationCompleted: { url in
                completionHandler(.pageNavigationCompleted, nil, url)
            }
        )
        let router = OSIABWebViewRouterAdapter(
            options: options,
            customHeaders: customHeaders,
            cacheManager: OSIABBrowserCacheManager(dataStore: .default()),
            callbackHandler: callbackHandler
        )
        self.openWebView(url, routerDelegate: router) { completionHandler(.success, $0, nil) }
    }
}
