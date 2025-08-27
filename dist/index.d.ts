import { HiddenInAppBrowser, HiddenInAppBrowserOpenOptions } from './definitions';

export declare class HiddenInAppBrowserPlugin implements HiddenInAppBrowser {
    open(options: HiddenInAppBrowserOpenOptions): Promise<void>;
}
export declare const HiddenInAppBrowserInstance: HiddenInAppBrowserPlugin;
export type { HiddenInAppBrowserOpenOptions } from './definitions';
