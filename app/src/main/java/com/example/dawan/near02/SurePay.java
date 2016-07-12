package com.example.dawan.near02;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import cn.beecloud.BCPay;
import cn.beecloud.async.BCCallback;
import cn.beecloud.async.BCResult;
import cn.beecloud.entity.BCPayResult;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.UpdateListener;


/**
 * Created by dawan on 2016/6/12.
 */
public class SurePay extends AppCompatActivity {

    String TAG = "Error";
    String title;
    String outTradNo;
    String desc;
    Double pay;
    Float myArea;
    BmobGeoPoint bmobGeoPoint;
    int money;

    ImageButton button_pay;
    TextView order_id;
    TextView order_desc;
    TextView order_topay;
    private ProgressDialog loadingDialog;


    //定义回调
    BCCallback bcCallback = new BCCallback() {
        @Override
        public void done(final BCResult bcResult) {
            //此处根据业务需要处理支付结果
            final BCPayResult bcPayResult = (BCPayResult) bcResult;

            switch (bcPayResult.getResult()) {
                case BCPayResult.RESULT_SUCCESS:
                    //用户支付成功  更新状态，显示已支付
                    Log.e("PAY", "支付成功");
                    new Function().pushForHelp(SurePay.this, bmobGeoPoint, myArea);
                    new Function().showMessage(SurePay.this, "支付成功，热心人马上就到。");
                    setPStation(SurePay.this, outTradNo);
                    Intent intent = new Intent(SurePay.this, Start_Activity.class);
                    startActivity(intent);

                    break;
                case BCPayResult.RESULT_CANCEL:
                    Log.e("PAY", "支付取消");
                    //用户取消支付"
                    new Function().showMessage(SurePay.this, "支付未成功，请重新发起支付。");
//                    new Function().pushForHelp(SurePay.this,bmobGeoPoint,myArea);
//                    setPStation(outTradNo);
//记录页面添加未支付的求助 添加支付按钮、  过期未获得求助的，半小时或一小时跑批一次，返回款项到本公司账户
                    break;
                case BCPayResult.RESULT_FAIL:
                    Log.e("PAY", "支付失败");
                    new Function().showMessage(SurePay.this, "支付失败，请在求助记录页面重新发起支付。");
                    //支付失败
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surepay);

        String initInfo = BCPay.initWechatPay(SurePay.this, "wxd1579d8c233b79c1");

        if (initInfo != null) {
            Toast.makeText(this, "微信初始化失败：" + initInfo, Toast.LENGTH_LONG).show();
        }

        HelpContext order = (HelpContext) getIntent().getSerializableExtra("ORDER");

        title = order.getSimple_title();
        outTradNo = order.getObjectId();
        desc = order.getSimple_title();
        pay = order.getPay();
        bmobGeoPoint = order.getBmobGeoPoint();
        myArea = getArea();

        button_pay = (ImageButton) findViewById(R.id.button_pay);
        order_id = (TextView) findViewById(R.id.order_id);
        order_desc = (TextView) findViewById(R.id.order_desc);
        order_topay = (TextView) findViewById(R.id.order_toPay);

        order_id.setText(outTradNo);
        order_desc.setText(desc);
        order_topay.setText(pay.toString());

        money = pay.intValue() * 100;


        loadingDialog = new ProgressDialog(SurePay.this);
        loadingDialog.setMessage("处理中，请稍候...");
        loadingDialog.setIndeterminate(true);
        loadingDialog.setCancelable(true);

        button_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用支付接口
                Log.e(TAG, "点击按钮");
                loadingDialog.show();

//                new Function().showMessage(SurePay.this,"正在跳转，请稍候。");
                if (BCPay.isWXAppInstalledAndSupported() &&
                        BCPay.isWXPaySupported()) {
                    Log.e(TAG, "进入if");
                    Map<String, String> mapOptional = new HashMap<>();
                    mapOptional.put("testkey1", "测试value值1");

//发起支付
                    BCPay.getInstance(SurePay.this).reqWXPaymentAsync(
                            title,               //订单标题
                            money,                           //订单金额(分)
                            outTradNo,  //订单流水号
                            null,            //扩展参数(可以null)或填入mapOptional
                            bcCallback);            //支付完成后回调入口
                } else {
                    Log.e(TAG, "进入else");
                    Toast.makeText(SurePay.this,
                            "您尚未安装微信或者安装的微信版本不支持", Toast.LENGTH_LONG).show();
                    loadingDialog.dismiss();
                }
//                loadingDialog.dismiss();
            }
        });

    }

    public Float getArea() {
        SharedPreferences getArea = getSharedPreferences("Area", Activity.MODE_PRIVATE);
        Float area = getArea.getFloat("myArea", 10);
        Log.e("Area", area + "");
        return area;
    }

    public void setPStation(final Context context, String id) {
        HelpContext helpContext = new HelpContext();
        helpContext.setpStation(1);
        helpContext.update(SurePay.this, id, new UpdateListener() {
            @Override
            public void onSuccess() {
                //推送
//                Log.e("GEO",bmobGeoPoint.getLatitude() + "");
            }

            @Override
            public void onFailure(int i, String s) {

                new Function().showMessage(context, "标记失败，请联系客服。");
            }
        });
    }
}
