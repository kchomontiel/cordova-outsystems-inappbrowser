package com.outsystems.plugins.inappbrowser.osinappbrowser

import com.google.gson.annotations.SerializedName
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.models.OSIABToolbarPosition

data class OSInAppBrowserWebViewHiddenInputArguments(
    @SerializedName("showURL") val showURL: Boolean?,
    @SerializedName("showToolbar") val showToolbar: Boolean?,
    @SerializedName("clearCache") val clearCache: Boolean?,
    @SerializedName("clearSessionCache") val clearSessionCache: Boolean?,
    @SerializedName("mediaPlaybackRequiresUserAction") val mediaPlaybackRequiresUserAction: Boolean?,
    @SerializedName("closeButtonText") val closeButtonText: String?,
    @SerializedName("toolbarPosition") val toolbarPosition: OSIABToolbarPosition?,
    @SerializedName("leftToRight") val leftToRight: Boolean?,
    @SerializedName("showNavigationButtons") val showNavigationButtons: Boolean?,
    @SerializedName("customWebViewUserAgent") val customWebViewUserAgent: String?,
    @SerializedName("android") val android: OSInAppBrowserWebViewAndroidOptions,
    @SerializedName("hidden") val hidden: Boolean?,
    @SerializedName("autoClose") val autoClose: Boolean?,
    @SerializedName("timeout") val timeout: Int?
)
