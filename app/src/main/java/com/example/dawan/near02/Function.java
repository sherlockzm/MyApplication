package com.example.dawan.near02;

import android.content.Context;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;

/**
 * Created by dawan on 2016/2/24.
 */
public class Function {

    public Function() {
    }

    public void pushMessage(Context context, String id,String message) {
        BmobPushManager bmobPush = new BmobPushManager(context);
        BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
        //推送给求助者
        query.addWhereEqualTo("userId", id);

        bmobPush.setQuery(query);
        bmobPush.pushMessage(message);
    }
}
