package com.adsforce.sdk.deeplink;

/**
 * Created by t.wang on 2018/5/31.
 * <p>
 * Copyright © 2018 Adsforce. All rights reserved.
 */

public class AdsforceDeepLink {
    private String mTargetUri;
    private String mLinkArgs;

    public AdsforceDeepLink(String targetUri, String linkArgs) {
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
