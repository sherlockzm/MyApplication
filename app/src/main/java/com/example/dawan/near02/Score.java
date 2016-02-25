package com.example.dawan.near02;

import cn.bmob.v3.BmobObject;

/**
 * Created by dawan on 2016/2/24.
 */
public class Score extends BmobObject {
    String userId;
    String helpContextId;
    Integer role;
    Double score;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHelpContextId() {
        return helpContextId;
    }

    public void setHelpContextId(String helpContextId) {
        this.helpContextId = helpContextId;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Score( String userId, String helpContextId, Integer role, Double score) {

        this.userId = userId;
        this.helpContextId = helpContextId;
        this.role = role;
        this.score = score;
    }
}
