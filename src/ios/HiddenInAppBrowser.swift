import UIKit
import ObjectiveC
import WebKit
import Cordova

/// The plugin's main class
@objc(HiddenInAppBrowser)
class HiddenInAppBrowser: CDVPlugin, UIAdaptivePresentationControllerDelegate {
    
    // Agregar variables de clase para las referencias
    private var modalWebView: WKWebView?
    private var modalNavigationController: UINavigationController?
    
    private var openedViewController: UIViewController?
    private var lastCommandId: String?
    
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
            guard let self = self else { 
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
            guard let self = self else { 
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
        print("openInWebView - CallbackId: \(command.callbackId)")
        
        // Guardar el commandId para usarlo cuando se cierre el WebView
        self.lastCommandId = command.callbackId
        
        self.commandDelegate.run { [weak self] in
            guard let self = self else { 
                print("❌ openInWebView - Self is nil")
                return 
            }
            
            print("openInWebView - Processing command arguments")
            print("✅ openInWebView - FASE 1 COMPLETADA: Inicio del método y validación de self")
            
            guard
                let argumentsModel: HiddenInAppBrowserInputArgumentsSimpleModel = self.createModel(for: command.argument(at: 0)),
                let url = URL(string: argumentsModel.url)
            else {
                print("❌ openInWebView - Failed to create model or URL")
                return self.sendError("The input parameters aren't valid.", for: command.callbackId)
            }

            print("✅ openInWebView - Model created successfully")
            print("openInWebView - URL: \(url.absoluteString)")
            print("✅ openInWebView - FASE 2 COMPLETADA: Validación de parámetros y creación del modelo")
            
            // Create a modal WebView (like Android)
            DispatchQueue.main.async {
                print("✅ openInWebView - Creating modal WebView")
                print("✅ openInWebView - FASE 3 COMPLETADA: Inicio de creación en main queue")
                
                // Create WebView with full screen
                let webView = WKWebView()
                webView.configuration.allowsInlineMediaPlayback = true
                webView.configuration.mediaTypesRequiringUserActionForPlayback = []
                print("✅ openInWebView - FASE 4 COMPLETADA: WebView creado y configurado")
                
                // Create view controller
                let webViewController = UIViewController()
                webViewController.view = webView
                webViewController.title = "WebView"
                print("✅ openInWebView - FASE 5 COMPLETADA: ViewController creado")
                
                // Add close button
                let closeButton = UIBarButtonItem(barButtonSystemItem: .close, target: self, action: #selector(self.closeWebViewAndSendSuccess))
                webViewController.navigationItem.leftBarButtonItem = closeButton
                print("✅ openInWebView - FASE 6 COMPLETADA: Botón Close creado")
                
                // Create navigation controller
                let navigationController = UINavigationController(rootViewController: webViewController)
                navigationController.modalPresentationStyle = .fullScreen
                print("✅ openInWebView - FASE 7 COMPLETADA: NavigationController creado")
                
                // Set delegate
                let webViewDelegate = ModalWebViewDelegate { [weak self] success, error in
                    if success {
                        print("✅ openInWebView - Page loaded successfully")
                        print("✅ openInWebView - FASE 16 COMPLETADA: Página cargada completamente")
                        // NO enviar callback aquí - esperar a que el usuario cierre el WebView
                    } else {
                        print("❌ openInWebView - Failed to load page: \(error ?? "unknown error")")
                        self?.sendError("Failed to load URL: \(url.absoluteString)", for: command.callbackId)
                    }
                }
                
                webView.navigationDelegate = webViewDelegate
                print("✅ openInWebView - FASE 8 COMPLETADA: WebViewDelegate configurado")
                
                // Keep references
                objc_setAssociatedObject(webView, &AssociatedKeys.delegateKey, webViewDelegate, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
                objc_setAssociatedObject(self, &AssociatedKeys.webViewKey, webView, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
                self.openedViewController = navigationController
                print("✅ openInWebView - FASE 9 COMPLETADA: Referencias asociadas configuradas")
                
                // Guardar referencias para closeWebView
                self.modalWebView = webView
                self.modalNavigationController = navigationController
                print("✅ openInWebView - FASE 10 COMPLETADA: Referencias de clase guardadas")
                
                // Agregar listener para cuando se cierre el modal
                navigationController.presentationController?.delegate = self
                print("✅ openInWebView - FASE 11 COMPLETADA: PresentationController delegate configurado")
                
                // Present modal
                self.viewController.present(navigationController, animated: true)
                print("✅ openInWebView - FASE 13 COMPLETADA: Modal presentado")
                
                // Enviar callback de éxito DESPUÉS de presentar el modal (como en Android)
                print("✅ openInWebView - FASE 14 COMPLETADA: Enviando callback de éxito")
                self.sendSuccess(for: command.callbackId)
                
                // Load URL DESPUÉS del callback (como en Android)
                let request = URLRequest(url: url)
                webView.load(request)
                print("✅ openInWebView - FASE 15 COMPLETADA: Carga de URL iniciada")
            }
        }
    }
    

    
    @objc(close:)
    func close(command: CDVInvokedUrlCommand) {
        self.commandDelegate.run { [weak self] in
            guard let self = self else { return }
            
            if let openedViewController = openedViewController {
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
    
    @objc func closeWebViewAndSendSuccess() {
        print("HiddenInAppBrowser: closeWebViewAndSendSuccess() called")
        print("🔍 closeWebViewAndSendSuccess - ===== INICIO DEL MÉTODO =====")
        
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { 
                print("❌ closeWebViewAndSendSuccess - Self is nil")
                return 
            }
            
            print("✅ closeWebViewAndSendSuccess - FASE 1 COMPLETADA: Self validado")
            
            do {
                // Cerrar modal WebView
                if let webView = self.modalWebView {
                    print("HiddenInAppBrowser: Closing modal WebView")
                    webView.stopLoading()
                    webView.removeFromSuperview()
                    self.modalWebView = nil
                    print("✅ closeWebViewAndSendSuccess - FASE 2 COMPLETADA: WebView cerrado")
                }
                
                // Cerrar modal
                if let navController = self.modalNavigationController {
                    print("HiddenInAppBrowser: Closing modal navigation")
                    navController.dismiss(animated: true) {
                        self.modalNavigationController = nil
                        print("✅ closeWebViewAndSendSuccess - FASE 3 COMPLETADA: NavigationController cerrado")
                        // Enviar callback de éxito cuando se cierre el modal
                        if let commandId = self.lastCommandId {
                            print("✅ closeWebViewAndSendSuccess - FASE 4 COMPLETADA: Enviando callback de éxito")
                            self.sendSuccess(for: commandId)
                        } else {
                            print("❌ closeWebViewAndSendSuccess - ERROR: No hay commandId disponible")
                        }
                    }
                }
                
                print("HiddenInAppBrowser: Modal WebView closed successfully")
                
            } catch {
                print("HiddenInAppBrowser: Error closing modal WebView: \(error)")
                print("❌ closeWebViewAndSendSuccess - ERROR: \(error.localizedDescription)")
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
                self.commandDelegate.sendPluginResult(CDVPluginResult(status: CDVCommandStatus_OK, messageAs: "Modal WebView closed successfully"), callbackId: command.callbackId)
                
            } catch {
                print("HiddenInAppBrowser: Error closing modal WebView: \(error)")
                self.commandDelegate.sendPluginResult(CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "Error closing modal WebView: \(error.localizedDescription)"), callbackId: command.callbackId)
            }
        }
    }
    

    }
    
    // MARK: - UIAdaptivePresentationControllerDelegate
    
    func presentationControllerDidDismiss(_ presentationController: UIPresentationController) {
        print("HiddenInAppBrowser: Modal dismissed by user gesture")
        print("🔍 presentationControllerDidDismiss - ===== INICIO DEL MÉTODO =====")
        
        // Enviar callback de éxito cuando se cierre el modal por gesto
        if let commandId = lastCommandId {
            print("✅ presentationControllerDidDismiss - FASE 1 COMPLETADA: CommandId encontrado")
            print("✅ presentationControllerDidDismiss - FASE 2 COMPLETADA: Enviando callback de éxito")
            sendSuccess(for: commandId)
        } else {
            print("❌ presentationControllerDidDismiss - ERROR: No hay commandId disponible")
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
        let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK)
        pluginResult.keepCallback = true
        self.commandDelegate.sendPluginResult(pluginResult, callbackId: callbackId)
    }
    
    func sendError(_ message: String, for callbackId: String) {
        let pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: message)
        self.commandDelegate.sendPluginResult(pluginResult, callbackId: callbackId)
    }
}



// MARK: - Hidden WebView Implementation (like Android)

private struct AssociatedKeys {
    static var delegateKey = "delegateKey"
    static var webViewKey = "webViewKey"
    static var callbackIdKey = "callbackIdKey"
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
    
    func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        print("ModalWebViewDelegate - webView didStartProvisionalNavigation")
        print("✅ openInWebView - FASE 17 COMPLETADA: Navegación iniciada")
    }
    
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        print("ModalWebViewDelegate - webView didFinish")
        print("✅ openInWebView - FASE 18 COMPLETADA: Navegación completada")
        completion(true, nil)
    }
    
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        print("ModalWebViewDelegate - didFail: \(error.localizedDescription)")
        print("❌ openInWebView - ERROR: Navegación falló")
        completion(false, error.localizedDescription)
    }
    
    func webView(_ webView: WKWebView, didFailProvisionalNavigation navigation: WKNavigation!, withError error: Error) {
        print("ModalWebViewDelegate - didFailProvisionalNavigation: \(error.localizedDescription)")
        print("❌ openInWebView - ERROR: Navegación provisional falló")
        completion(false, error.localizedDescription)
    }
    
    func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        print("ModalWebViewDelegate - decidePolicyFor: \(navigationAction.request.url?.absoluteString ?? "nil")")
        decisionHandler(.allow)
    }
}
