package com.example.dawan.near02;

import cn.bmob.v3.BmobObject;

/**
 * Created by dawan on 2016/5/11.
 */
public class Act extends BmobObject {

    private String contextId;
    private String requestId;
    private String acterId;

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getActerId() {
        return acterId;
    }

    public void setActerId(String acterId) {
        this.acterId = acterId;
    }
}
