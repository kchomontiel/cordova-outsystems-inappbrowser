var exec = require('cordova/exec');

var multibrowser = {
    openInWebView: function(url, target, options, successCallback, errorCallback) {
        exec(successCallback, errorCallback, 'multibrowser', 'openInWebView', [url, target, options]);
    },
    
    openHidden: function(url, target, options, successCallback, errorCallback) {
        exec(successCallback, errorCallback, 'multibrowser', 'openHidden', [url, target, options]);
    },
    
    openInExternalBrowser: function(url, target, options, successCallback, errorCallback) {
        exec(successCallback, errorCallback, 'multibrowser', 'openInExternalBrowser', [url, target, options]);
    }
};

module.exports = multibrowser;
