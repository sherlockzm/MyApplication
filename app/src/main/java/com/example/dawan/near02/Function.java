package com.example.dawan.near02;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;

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

    public void showMessage(Context context,String s){

        Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
    }

    public Double trimNull(String d){

        String[] lTime = d.split("\\.");

        if (lTime[0].equals(null)||lTime[0].equals("0")||lTime[0].equals("")){
            lTime[0] = "0";

            String value = lTime[0].concat(".").concat(lTime[1]);
            Double dd = Double.valueOf(value);

            return dd;
        }else {

            return Double.valueOf(d);
        }



    }


    protected boolean checkNetworkInfo(Context context){
        ConnectivityManager conMan = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        Log.e("MyConnect", mobile.toString());
//        if(mobile== NetworkInfo.State. DISCONNECTED){
//
//            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));//进入无线网络配置界面
//        }
        NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        Log.e("MyConnect", wifi.toString());

        if (mobile == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTED) {
            return true;
        } else {
            String str = "请打开网络连接。";
            new Function().showMessage(context, str);
            return false;
        }
    }

    public void verMobile(final Context context,String s){
        if (s == null) {
            Toast.makeText(context, "请先登陆。", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, Login.class);
            context.startActivity(intent);
        } else {
            BmobSMS.requestSMSCode(context, s, "短信模板", new RequestSMSCodeListener() {
                @Override
                public void done(Integer integer, BmobException e) {
                    if (e == null) {
                        Toast.makeText(context, "验证码已发送，请查收。", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(context, "验证码发送失败，请重试。", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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

    public void setButton(ImageButton[] imgbutton,Button button, boolean boo){
        if (boo == false){
            for (ImageButton btn:imgbutton){
                btn.setVisibility(View.GONE);

            }
            button.setVisibility(View.GONE);
        }else {
            for (ImageButton btn:imgbutton){
                btn.setVisibility(View.VISIBLE);
            }
            button.setVisibility(View.VISIBLE);
        }
    }


}
