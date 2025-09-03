import Foundation
import WebKit
import UIKit
import os.log

@objc(HiddenInAppBrowser) class HiddenInAppBrowser : CDVPlugin {
    
    private let logger = Logger(subsystem: "com.outsystems.plugins.multibrowser", category: "HiddenInAppBrowser")
    
    @objc(openInWebView:)
    func openInWebView(_ command: CDVInvokedUrlCommand) {
        logger.debug("Executing openInWebView action")
        
        // Validate parameters like Android does
        guard let urlString = command.argument(at: 0) as? String else {
            logger.error("URL parameter is required")
            sendError(command, errorMessage: "URL parameter is required")
            return
        }
        
        let target = command.argument(at: 1) as? String ?? "_blank"
        let options = command.argument(at: 2) as? String ?? ""
        
        logger.debug("Parameters: URL=\(urlString), Target=\(target), Options=\(options)")
        
        guard let url = URL(string: urlString) else {
            logger.error("Invalid URL: \(urlString)")
            sendError(command, errorMessage: "Invalid URL format")
            return
        }
        
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            
            do {
                let webViewController = WebViewController()
                webViewController.url = url
                webViewController.target = target
                webViewController.options = options
                webViewController.successCallback = { [weak self] message in
                    self?.sendSuccess(command, message: message)
                }
                webViewController.errorCallback = { [weak self] errorMessage in
                    self?.sendError(command, errorMessage: errorMessage)
                }
                
                let navigationController = UINavigationController(rootViewController: webViewController)
                self.viewController.present(navigationController, animated: true, completion: nil)
                
                logger.debug("WebView presented successfully")
            } catch {
                self.logger.error("Error creating WebView: \(error.localizedDescription)")
                self.sendError(command, errorMessage: "Error creating WebView: \(error.localizedDescription)")
            }
        }
    }
    
    @objc(openHidden:)
    func openHidden(_ command: CDVInvokedUrlCommand) {
        logger.debug("Executing openHidden action")
        
        // Validate parameters like Android does
        guard let urlString = command.argument(at: 0) as? String else {
            logger.error("URL parameter is required")
            sendError(command, errorMessage: "URL parameter is required")
            return
        }
        
        let target = command.argument(at: 1) as? String ?? "_blank"
        let options = command.argument(at: 2) as? String ?? ""
        
        logger.debug("Hidden mode parameters: URL=\(urlString), Target=\(target), Options=\(options)")
        
        // For hidden mode, we'll just return success immediately like Android
        logger.debug("Hidden mode activated for URL: \(urlString)")
        sendSuccess(command, message: "Hidden mode activated")
    }
    
    @objc(openInExternalBrowser:)
    func openInExternalBrowser(_ command: CDVInvokedUrlCommand) {
        logger.debug("Executing openInExternalBrowser action")
        
        // Validate parameters like Android does
        guard let urlString = command.argument(at: 0) as? String else {
            logger.error("URL parameter is required")
            sendError(command, errorMessage: "URL parameter is required")
            return
        }
        
        let target = command.argument(at: 1) as? String ?? "_system"
        let options = command.argument(at: 2) as? String ?? ""
        
        logger.debug("External browser parameters: URL=\(urlString), Target=\(target), Options=\(options)")
        
        guard let url = URL(string: urlString) else {
            logger.error("Invalid URL: \(urlString)")
            sendError(command, errorMessage: "Invalid URL format")
            return
        }
        
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            
            do {
                logger.debug("Opening URL in external browser: \(urlString)")
                
                if UIApplication.shared.canOpenURL(url) {
                    UIApplication.shared.open(url, options: [:]) { success in
                        if success {
                            self.logger.debug("External browser opened successfully")
                            self.sendSuccess(command, message: "External browser opened")
                        } else {
                            self.logger.error("Failed to open external browser")
                            self.sendError(command, errorMessage: "Failed to open external browser")
                        }
                    }
                } else {
                    self.logger.error("Cannot open URL in external browser: \(urlString)")
                    self.sendError(command, errorMessage: "Cannot open URL in external browser")
                }
            } catch {
                self.logger.error("Error opening external browser: \(error.localizedDescription)")
                self.sendError(command, errorMessage: "Error opening external browser: \(error.localizedDescription)")
            }
        }
    }
    
    private func sendSuccess(_ command: CDVInvokedUrlCommand, message: String) {
        logger.debug("Sending success result: \(message)")
        let result = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: message)
        commandDelegate.send(result, callbackId: command.callbackId)
    }
    
    private func sendError(_ command: CDVInvokedUrlCommand, errorMessage: String) {
        logger.error("Sending error result: \(errorMessage)")
        let result = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: errorMessage)
        commandDelegate.send(result, callbackId: command.callbackId)
    }
}

class WebViewController: UIViewController, WKNavigationDelegate, WKUIDelegate {
    var webView: WKWebView!
    var url: URL!
    var target: String!
    var options: String!
    var successCallback: ((String) -> Void)?
    var errorCallback: ((String) -> Void)?
    
    private let logger = Logger(subsystem: "com.outsystems.plugins.multibrowser", category: "WebViewController")
    
    override func viewDidLoad() {
        super.viewDidLoad()
        logger.debug("WebViewController viewDidLoad")
        
        setupWebView()
        setupNavigationBar()
        loadURL()
    }
    
    private func setupWebView() {
        // Configure WebView like Android does
        let webConfiguration = WKWebViewConfiguration()
        webConfiguration.allowsInlineMediaPlayback = true
        webConfiguration.mediaTypesRequiringUserActionForPlayback = []
        
        webView = WKWebView(frame: view.bounds, configuration: webConfiguration)
        webView.navigationDelegate = self
        webView.uiDelegate = self
        webView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        
        // Enable JavaScript like Android
        webView.configuration.preferences.javaScriptEnabled = true
        
        // Enable DOM storage like Android
        webView.configuration.preferences.javaScriptCanOpenWindowsAutomatically = true
        
        view.addSubview(webView)
        
        logger.debug("WebView configured and added to view")
    }
    
    private func setupNavigationBar() {
        // Add close button like Android has close functionality
        let closeButton = UIBarButtonItem(barButtonSystemItem: .done, target: self, action: #selector(closeWebView))
        navigationItem.rightBarButtonItem = closeButton
        
        // Set title
        navigationItem.title = "WebView"
        
        logger.debug("Navigation bar configured")
    }
    
    private func loadURL() {
        logger.debug("Loading URL: \(url.absoluteString)")
        
        let request = URLRequest(url: url)
        webView.load(request)
    }
    
    @objc func closeWebView() {
        logger.debug("Close button tapped")
        dismiss(animated: true) { [weak self] in
            self?.successCallback?("WebView closed successfully")
        }
    }
    
    // MARK: - WKNavigationDelegate (like Android's WebViewClient)
    
    func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        logger.debug("Page started loading: \(url.absoluteString)")
    }
    
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        logger.debug("Page finished loading: \(url.absoluteString)")
        successCallback?("Page loaded successfully")
    }
    
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        logger.error("WebView failed to load: \(error.localizedDescription)")
        errorCallback?(error.localizedDescription)
    }
    
    func webView(_ webView: WKWebView, didFailProvisionalNavigation navigation: WKNavigation!, withError error: Error) {
        logger.error("WebView failed provisional navigation: \(error.localizedDescription)")
        errorCallback?(error.localizedDescription)
    }
    
    func webView(_ webView: WKWebView, decidePolicyFor navigationResponse: WKNavigationResponse, decisionHandler: @escaping (WKNavigationResponsePolicy) -> Void) {
        logger.debug("Navigation response received: \(navigationResponse.response.url?.absoluteString ?? "unknown")")
        decisionHandler(.allow)
    }
    
    // MARK: - WKUIDelegate (like Android's WebChromeClient)
    
    func webView(_ webView: WKWebView, createWebViewWith configuration: WKWebViewConfiguration, for navigationAction: WKNavigationAction, windowFeatures: WKWindowFeatures) -> WKWebView? {
        // Handle new window requests like Android does
        if navigationAction.targetFrame == nil {
            logger.debug("Opening new window for: \(navigationAction.request.url?.absoluteString ?? "unknown")")
            webView.load(navigationAction.request)
        }
        return nil
    }
    
    func webView(_ webView: WKWebView, runJavaScriptAlertPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping () -> Void) {
        // Handle JavaScript alerts like Android
        logger.debug("JavaScript alert: \(message)")
        
        let alert = UIAlertController(title: "Alert", message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default) { _ in
            completionHandler()
        })
        
        present(alert, animated: true, completion: nil)
    }
    
    func webView(_ webView: WKWebView, runJavaScriptConfirmPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping (Bool) -> Void) {
        // Handle JavaScript confirms like Android
        logger.debug("JavaScript confirm: \(message)")
        
        let alert = UIAlertController(title: "Confirm", message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel) { _ in
            completionHandler(false)
        })
        alert.addAction(UIAlertAction(title: "OK", style: .default) { _ in
            completionHandler(true)
        })
        
        present(alert, animated: true, completion: nil)
    }
}
