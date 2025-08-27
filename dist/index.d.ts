import { HiddenInAppBrowser, HiddenInAppBrowserOpenOptions } from './definitions';

declare class HiddenInAppBrowserPlugin implements HiddenInAppBrowser {
    open(options: HiddenInAppBrowserOpenOptions): Promise<void>;
}
declare const HiddenInAppBrowserInstance: HiddenInAppBrowserPlugin;
export { HiddenInAppBrowserInstance, HiddenInAppBrowserPlugin };
export type { HiddenInAppBrowserOpenOptions } from './definitions';
