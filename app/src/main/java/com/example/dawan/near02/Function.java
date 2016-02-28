package com.example.dawan.near02;

import android.content.Context;
import android.view.View;
import android.widget.Button;

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


    public void checkCacaheContext(Context context,BmobQuery<HelpContext> query){

        boolean isCache = query.hasCachedResult(context,HelpContext.class);
        if(isCache){
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        }else{
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        }
    }
    public void checkCacaheUser(Context context,BmobQuery<User> query){

        boolean isCache = query.hasCachedResult(context,User.class);
        if(isCache){
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        }else{
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        }
    }
    public void checkCacaheTransaction(Context context,BmobQuery<TransactionRecord> query){

        boolean isCache = query.hasCachedResult(context,TransactionRecord.class);
        if(isCache){
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        }else{
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        }
    }
    public void checkCacaheScore(Context context,BmobQuery<Score> query){

        boolean isCache = query.hasCachedResult(context,Score.class);
        if(isCache){
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        }else{
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        }
    }

    /////////////////////////设置按钮是否可用//////////////////////////////
    public void setButton(Button btn1,boolean boo){
        if (boo == false){
            btn1.setVisibility(View.GONE);
        }else
            btn1.setVisibility(View.VISIBLE);
    }
    public void setButton(Button btn1,Button btn2,boolean boo){
        if (boo == false){
            btn1.setVisibility(View.GONE);
            btn2.setVisibility(View.GONE);
        }else {
            btn1.setVisibility(View.VISIBLE);
            btn2.setVisibility(View.VISIBLE);
        }
    }

    public void setButton(Button[] button,boolean boo){
        if (boo == false){
            for (Button btn:button){
                btn.setVisibility(View.GONE);
            }
        }else {
            for (Button btn:button){
                btn.setVisibility(View.VISIBLE);
            }
        }
    }


}
