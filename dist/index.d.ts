import { HiddenInAppBrowserOpenOptions } from './definitions';

export declare function open(urlOrOptions: string | HiddenInAppBrowserOpenOptions | any, target?: string, optionsString?: string, onSuccess?: () => void, onError?: (error: any) => void): Promise<void>;
export type { HiddenInAppBrowserOpenOptions } from './definitions';
