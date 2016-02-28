package com.example.dawan.near02;

import cn.bmob.v3.BmobObject;

/**
 * Created by dawan on 2016/2/24.
 */
public class Score extends BmobObject {
    String requesterId;
    float requesterScore;
    String helperId;
    float helperScore;
    String helpContextId;

    public Score(String requesterId, float requesterScore, String helperId, float helperScore, String helpContextId) {
        this.requesterId = requesterId;
        this.requesterScore = requesterScore;
        this.helperId = helperId;
        this.helperScore = helperScore;
        this.helpContextId = helpContextId;
    }

    public Score() {
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public float getRequesterScore() {
        return requesterScore;
    }

    public void setRequesterScore(float requesterScore) {
        this.requesterScore = requesterScore;
    }

    public String getHelperId() {
        return helperId;
    }

    public void setHelperId(String helperId) {
        this.helperId = helperId;
    }

    public float getHelperScore() {
        return helperScore;
    }

    public void setHelperScore(float helperScore) {
        this.helperScore = helperScore;
    }

    public String getHelpContextId() {
        return helpContextId;
    }

    public void setHelpContextId(String helpContextId) {
        this.helpContextId = helpContextId;
    }
}
