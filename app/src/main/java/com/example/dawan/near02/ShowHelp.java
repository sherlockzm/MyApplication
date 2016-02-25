package com.example.dawan.near02;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by dawan on 2016/2/18.
 */
public class ShowHelp extends AppCompatActivity {

    private TextView tv_title;
    private TextView tv_pay;
    private TextView tv_detail;


    private Button btn_iHelp;
    private Button btn_notHelp;
    TransactionRecord record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showhelp);

        record = new TransactionRecord();

        tv_title = (TextView) findViewById(R.id.tv_show_title);
        tv_pay = (TextView) findViewById(R.id.tv_show_pay);
        tv_detail = (TextView) findViewById(R.id.tv_show_detail);
        btn_iHelp = (Button) findViewById(R.id.btn_iHelp);
        btn_notHelp = (Button) findViewById(R.id.btn_notHelp);
        //获取数据
        final Intent intent = getIntent();
        String title = intent.getStringExtra("ext_title");
        Double pay = intent.getDoubleExtra("ext_pay", 0);
        String detail = intent.getStringExtra("ext_detail");
        final String objectId = intent.getStringExtra("ext_objectId");//该请求的id
        final String requestID = intent.getStringExtra("ext_requestId");//发出请求的用户ID

        tv_title.setText(title);
        tv_pay.setText(pay.toString());
        tv_detail.setText(detail);

        btn_notHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_iHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //用于更新交易记录
                //get current User ID

                if (new CheckInput().checkLogin(ShowHelp.this, User.class)) {

                    final String helperId = (String) BmobUser.getObjectByKey(ShowHelp.this, "objectId");

                    Log.e("UserId,helper",helperId);
                    Log.e("UserId,requester",requestID);
//                String helperId = BmobInstallation.getInstallationId(ShowHelp.this);//获取帮助者ID
                    //【判断帮助者和求助者是否同一人

//                    if (!new CheckInput().checkHelperAndRequester(helperId, requestID)) {
                    if (helperId.equals(requestID)) {

                        Toast.makeText(ShowHelp.this, "你不能自己解决自己的求助，谢谢。", Toast.LENGTH_SHORT).show();
                    } else {
                        record.setHelperID(helperId);
                        record.setRequestID(requestID);
                        record.setBuniessId(objectId);
                        record.save(ShowHelp.this, new SaveListener() {
                            @Override
                            public void onSuccess() {
                                Log.e("Save", "OK!");
                                HelpContext helpContext = new HelpContext();

                                BmobQuery<HelpContext> query = new BmobQuery<HelpContext>();
                                //判断能否提供帮助
                                query.getObject(ShowHelp.this, objectId, new GetListener<HelpContext>() {
                                    @Override
                                    public void onSuccess(HelpContext helpContext) {
                                        if (helpContext.getIscomplete() != 0) {
                                            AlertDialog.Builder dialog = new AlertDialog.Builder(ShowHelp.this);
                                            dialog.setTitle("哎呀！");
                                            dialog.setMessage("被人抢先一步了！");
                                            dialog.setCancelable(false);
                                            dialog.setPositiveButton("好吧", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                }
                                            });
                                            dialog.setNegativeButton("帮别人", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });
                                            dialog.show();
                                        } else if (helpContext.getIscomplete() ==1){
                                            Toast.makeText(ShowHelp.this, "感谢你的热心帮助，该请求已帮众提前响应！", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }else if (helpContext.getIscomplete() == 0){
                                            Toast.makeText(ShowHelp.this, "好样的，本帮主看好你喔！加油完成使命！", Toast.LENGTH_SHORT).show();
                                            Intent intent1 = new Intent(ShowHelp.this,ShowRecord.class);
                                            startActivity(intent1);

                                        }else {
                                            Toast.makeText(ShowHelp.this, "你果然很热心，就是手慢了一点，该请求已完成！", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {

                                    }
                                });

                                helpContext.setIscomplete(1);
                                helpContext.setHelperId(helperId);
                                helpContext.update(ShowHelp.this, objectId, new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        Log.e("Change", "OK!");

                                        String message = "你的请求已有人响应。";
                                        new Function().pushMessage(ShowHelp.this,requestID,message);
//                                        BmobPushManager bmobPush = new BmobPushManager(ShowHelp.this);
//                                        BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
//                                        //推送给求助者
//                                        query.addWhereEqualTo("requesterId", requestID);
//                                        bmobPush.setQuery(query);
//                                        bmobPush.pushMessage("你的请求已有人响应！");

                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        Log.e("Change", "Fail!" + s);
                                    }
                                });
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                Log.e("Save", "Fail!");

                            }
                        });
                    }
                }
            }
        });


    }
}