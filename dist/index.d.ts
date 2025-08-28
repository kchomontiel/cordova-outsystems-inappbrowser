import { HiddenInAppBrowserOpenOptions } from './definitions';

export declare function open(urlOrOptions: string | HiddenInAppBrowserOpenOptions | any, target?: string, optionsString?: string, onSuccess?: () => void, onError?: (error: any) => void): Promise<void>;
export declare function openInExternalBrowser(urlOrOptions: string | HiddenInAppBrowserOpenOptions | any, target?: string, optionsString?: string, onSuccess?: () => void, onError?: (error: any) => void): Promise<void>;
export declare function openInWebView(urlOrOptions: string | HiddenInAppBrowserOpenOptions | any, target?: string, optionsString?: string, onSuccess?: () => void, onError?: (error: any) => void): Promise<void>;
export type { HiddenInAppBrowserOpenOptions } from './definitions';
