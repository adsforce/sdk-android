package com.xhance.sdk.deeplink;

public class XhanceDeepLink {
    private String mTargetUri;
    private String mLinkArgs;

    public XhanceDeepLink(String targetUri, String linkArgs) {
        mTargetUri = targetUri;
        mLinkArgs = linkArgs;
    }

    public String getTargetUri() {
        return mTargetUri;
    }

    public String getLinkArgs() {
        return mLinkArgs;
    }

}
