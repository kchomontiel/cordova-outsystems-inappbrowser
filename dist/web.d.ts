import { HiddenInAppBrowser, HiddenInAppBrowserOpenOptions } from './definitions';

export declare class HiddenInAppBrowserWeb implements HiddenInAppBrowser {
    open(options: HiddenInAppBrowserOpenOptions): Promise<void>;
    openInExternalBrowser(options: HiddenInAppBrowserOpenOptions): Promise<void>;
    openInWebView(options: HiddenInAppBrowserOpenOptions): Promise<void>;
    private buildFeaturesString;
}
