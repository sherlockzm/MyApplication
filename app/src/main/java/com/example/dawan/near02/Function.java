package com.example.dawan.near02;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by dawan on 2016/2/24.
 */
public class Function {


    public Function() {
    }

    public void pushForHelp(Context context, BmobGeoPoint bmobGeoPoint, Float myArea) {
        String notificationContext = "附近有人请求帮助!如方便，请伸出援助之手。";
        BmobPushManager bmobPushManager = new BmobPushManager(context);
        BmobQuery<BmobInstallation> bmobQuery = new BmobQuery<BmobInstallation>();
        bmobQuery.addWhereWithinRadians("myPoint", bmobGeoPoint, myArea);
        Log.e("Query", bmobGeoPoint + "");
        bmobPushManager.setQuery(bmobQuery);
        bmobPushManager.pushMessage(notificationContext);
    }

    public void pushMessage(Context context, String id,String message) {
        BmobPushManager bmobPush = new BmobPushManager(context);
        BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
        //推送给求助者
        query.addWhereEqualTo("userId", id);

        bmobPush.setQuery(query);
        bmobPush.pushMessage(message);
    }


    public void changeOverage(final Context context, final HelpContext helpContext) {

        helpContext.setIscomplete(5);
        String helpContextId = helpContext.getObjectId();
        helpContext.update(context, helpContextId, new UpdateListener() {
            @Override
            public void onSuccess() {
                Log.e("Update", "已取消求助");
                //TODO  更改账户余额
                if (helpContext.getpStation() == 1) {
                    final String requestId = helpContext.getRequestid();
                    final Double pay = helpContext.getPay();
                    BmobQuery<User> getOverage = new BmobQuery<User>();
                    getOverage.getObject(context, requestId, new GetListener<User>() {
                        @Override
                        public void onSuccess(User user) {
                            Double overage = user.getOverage();
                            overage = overage + pay;
                            User user2 = new User();
                            user2.setOverage(overage);
                            user2.update(context, requestId, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    helpContext.setIscomplete(9);
                                    helpContext.update(context, new UpdateListener() {
                                        @Override
                                        public void onSuccess() {
                                            Log.e("Update", "求助状态已更新");
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {
                                            Log.e("Update", "求助状态更新失败");
                                        }
                                    });
                                    Log.e("Update Overage", "已退还付款");
                                    new Function().showMessage(context, "删除成功，付款已退还。");
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    Log.e("Update Overage", "退款失败");

                                }
                            });
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Log.e("Update", "查询用户失败，请先登录");
                            new Function().showMessage(context, "获取当前用户信息失败，请先登录。");
                        }
                    });
                }
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });

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
    public void setButton(ImageButton btn1,boolean boo){
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

    public void setButton(ImageButton[] imgbutton,ImageButton button, boolean boo){
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

    public void saveBitmap(Bitmap bm) {
        Log.e("TAG", "保存图片");
        File f = new File("/sdcard/nearHelp/", "picName");
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Log.i("TAG", "已经保存");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
