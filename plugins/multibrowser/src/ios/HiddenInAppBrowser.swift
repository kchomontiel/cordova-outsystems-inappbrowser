import WebKit
import UIKit

@objc(HiddenInAppBrowser) class HiddenInAppBrowser : CDVPlugin {
    
    @objc(openInWebView:)
    func openInWebView(_ command: CDVInvokedUrlCommand) {
        guard let urlString = command.argument(at: 0) as? String,
              let url = URL(string: urlString) else {
            sendError(command, errorMessage: "Invalid URL")
            return
        }
        
        let target = command.argument(at: 1) as? String ?? "_blank"
        let options = command.argument(at: 2) as? String ?? ""
        
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            
            let webViewController = WebViewController(url: url, target: target, options: options)
            webViewController.successCallback = { [weak self] message in
                self?.sendSuccess(command, message: message)
            }
            webViewController.errorCallback = { [weak self] errorMessage in
                self?.sendError(command, errorMessage: errorMessage)
            }
            
            let navigationController = UINavigationController(rootViewController: webViewController)
            self.viewController.present(navigationController, animated: true)
        }
    }
    
    @objc(openHidden:)
    func openHidden(_ command: CDVInvokedUrlCommand) {
        guard let urlString = command.argument(at: 0) as? String else {
            sendError(command, errorMessage: "URL required")
            return
        }
        
        sendSuccess(command, message: "Hidden mode activated")
    }
    
    @objc(openInExternalBrowser:)
    func openInExternalBrowser(_ command: CDVInvokedUrlCommand) {
        guard let urlString = command.argument(at: 0) as? String,
              let url = URL(string: urlString) else {
            sendError(command, errorMessage: "Invalid URL")
            return
        }
        
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            
            if UIApplication.shared.canOpenURL(url) {
                UIApplication.shared.open(url) { success in
                    if success {
                        self.sendSuccess(command, message: "External browser opened")
                    } else {
                        self.sendError(command, errorMessage: "Failed to open external browser")
                    }
                }
            } else {
                self.sendError(command, errorMessage: "Cannot open URL in external browser")
            }
        }
    }
    
    private func sendSuccess(_ command: CDVInvokedUrlCommand, message: String) {
        let result = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: message)
        commandDelegate.send(result, callbackId: command.callbackId)
    }
    
    private func sendError(_ command: CDVInvokedUrlCommand, errorMessage: String) {
        let result = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: errorMessage)
        commandDelegate.send(result, callbackId: command.callbackId)
    }
}

class WebViewController: UIViewController {
    private let webView: WKWebView
    private let url: URL
    private let target: String
    private let options: String
    var successCallback: ((String) -> Void)?
    var errorCallback: ((String) -> Void)?
    
    init(url: URL, target: String, options: String) {
        self.url = url
        self.target = target
        self.options = options
        
        let config = WKWebViewConfiguration()
        config.allowsInlineMediaPlayback = true
        config.mediaTypesRequiringUserActionForPlayback = []
        
        self.webView = WKWebView(frame: .zero, configuration: config)
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupWebView()
        setupNavigationBar()
        loadURL()
    }
    
    private func setupWebView() {
        webView.navigationDelegate = self
        webView.uiDelegate = self
        webView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        webView.frame = view.bounds
        view.addSubview(webView)
    }
    
    private func setupNavigationBar() {
        navigationItem.rightBarButtonItem = UIBarButtonItem(
            barButtonSystemItem: .done,
            target: self,
            action: #selector(closeWebView)
        )
        navigationItem.title = "WebView"
    }
    
    private func loadURL() {
        webView.load(URLRequest(url: url))
    }
    
    @objc private func closeWebView() {
        dismiss(animated: true) { [weak self] in
            self?.successCallback?("WebView closed successfully")
        }
    }
}

// MARK: - WKNavigationDelegate
extension WebViewController: WKNavigationDelegate {
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        successCallback?("Page loaded successfully")
    }
    
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        errorCallback?(error.localizedDescription)
    }
    
    func webView(_ webView: WKWebView, didFailProvisionalNavigation navigation: WKNavigation!, withError error: Error) {
        errorCallback?(error.localizedDescription)
    }
}

// MARK: - WKUIDelegate
extension WebViewController: WKUIDelegate {
    func webView(_ webView: WKWebView, createWebViewWith configuration: WKWebViewConfiguration, for navigationAction: WKNavigationAction, windowFeatures: WKWindowFeatures) -> WKWebView? {
        if navigationAction.targetFrame == nil {
            webView.load(navigationAction.request)
        }
        return nil
    }
    
    func webView(_ webView: WKWebView, runJavaScriptAlertPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping () -> Void) {
        let alert = UIAlertController(title: "Alert", message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default) { _ in
            completionHandler()
        })
        present(alert, animated: true)
    }
    
    func webView(_ webView: WKWebView, runJavaScriptConfirmPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping (Bool) -> Void) {
        let alert = UIAlertController(title: "Confirm", message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel) { _ in
            completionHandler(false)
        })
        alert.addAction(UIAlertAction(title: "OK", style: .default) { _ in
            completionHandler(true)
        })
        present(alert, animated: true)
    }
}
