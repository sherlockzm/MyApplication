package com.example.dawan.near02;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by dawan on 2016/2/23.
 */
public class ShowRecord extends AppCompatActivity {

    private TextView tv_title;
    private TextView tv_pay;
    private TextView tv_detail;
    private TextView tv_helperName;
    private TextView tv_helperTel;
    private TextView tv_giveHelpName;
    private TextView tv_giveHelpTel;


//    private Button btn_modify;
    private Button btn_complete;
    private Button btn_delete;

    String helperId = "";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showrecord);

        final HelpContext helpContext = (HelpContext)getIntent().getSerializableExtra("HelpContext");
        String helperName = getIntent().getStringExtra("HelperName");
        String helperTel = getIntent().getStringExtra("HelperTel");
        final int boo = getIntent().getIntExtra("Boo", 1);


        ///////////////////////////按钮事件
        final String helpContextId = helpContext.getObjectId();
        final String helperId = helpContext.getHelperId();

        ///////////////////////////

        tv_title = (TextView)findViewById(R.id.tv_record_title);
        tv_pay = (TextView)findViewById(R.id.tv_record_pay);
        tv_detail = (TextView)findViewById(R.id.tv_record_detail);
        tv_helperName = (TextView)findViewById(R.id.tv_record_helperName);
        tv_helperTel = (TextView)findViewById(R.id.tv_record_helperTel);
        tv_giveHelpName = (TextView)findViewById(R.id.tv_giveHelpName);
        tv_giveHelpTel = (TextView)findViewById(R.id.tv_giveHelpTel);
//        btn_modify = (Button)findViewById(R.id.btn_modify);
        btn_complete = (Button)findViewById(R.id.btn_complete);
        btn_delete = (Button)findViewById(R.id.btn_delete);


        if (boo == 0){//为0则显示自己帮别人的列表
            tv_giveHelpName.setText("求助者昵称：");
            tv_giveHelpTel.setText("求助者电话：");
            btn_complete.setText("确认删除");

        }

        tv_title.setText(helpContext.getSimple_title().toString());
        tv_pay.setText(helpContext.getPay().toString());
        tv_detail.setText(helpContext.getDetail().toString());
        tv_helperName.setText(helperName);
        tv_helperTel.setText(helperTel);

//        btn_modify.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (boo == 1) {
//                    BmobQuery<HelpContext> query = new BmobQuery<HelpContext>();
//                    query.getObject(ShowRecord.this, helpContextId, new GetListener<HelpContext>() {
//                        @Override
//                        public void onSuccess(HelpContext helpContext) {
//                            if (helpContext.getIscomplete()==0){
//                                //允许修改
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(int i, String s) {
//
//                        }
//                    });
//                }
//            }
//        });
        //如果该求助未成交则允许修改或删除

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boo == 1) {
                    BmobQuery<HelpContext> query = new BmobQuery<HelpContext>();
                    query.getObject(ShowRecord.this, helpContextId, new GetListener<HelpContext>() {
                        @Override
                        public void onSuccess(HelpContext helpContext) {
                            if (helpContext.getIscomplete() == 0) {
                                helpContext.setObjectId(helpContextId);
                                helpContext.delete(ShowRecord.this, new DeleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        Log.e("Change", "Delete Success!");
                                        Toast.makeText(ShowRecord.this, "记录已删除.", Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        Log.e("Change", "Delete Fail!");

                                    }
                                });
                            } else if (helpContext.getIscomplete() == 1) {
                                helpContext.setStation(1);
                                helpContext.update(ShowRecord.this, helpContextId, new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        Log.e("Update", "Change Station Success!");
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {

                                    }
                                });

                                String message = "求助者请求取消求助。你是否同意。";
                                Toast.makeText(ShowRecord.this, "提交援助者确认.", Toast.LENGTH_LONG).show();
                                new Function().pushMessage(ShowRecord.this, helperId, message);

                            } else {
                                Toast.makeText(ShowRecord.this, "已完成的记录不允许删除..", Toast.LENGTH_LONG).show();

                            }
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                }else {
                    Toast.makeText(ShowRecord.this,"请联系求助者申请删除该求助。.",Toast.LENGTH_SHORT).show();

                }
            }
        });

        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boo == 0){
                    helpContext.setObjectId(helpContextId);
                    BmobQuery<HelpContext> delQuery = new BmobQuery<HelpContext>();
                    delQuery.getObject(ShowRecord.this, helpContextId, new GetListener<HelpContext>() {
                        @Override
                        public void onSuccess(HelpContext helpContext) {
                            if (helpContext.getStation() == 1) {
                                helpContext.delete(ShowRecord.this, new DeleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        Log.e("Delete", "Delete Success");
                                        Toast.makeText(ShowRecord.this,"该求助已成功删除.",Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        Log.e("Delete","Delete Fail.");
                                        Toast.makeText(ShowRecord.this,"删除失败。",Toast.LENGTH_SHORT).show();


                                    }
                                });
                            }else {

                                Toast.makeText(ShowRecord.this,"如当初一时手快，请点击左侧的申请删除按钮，不能单方面删除喔。",Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });

                }else {
                    //确认完成的情况
                    helpContext.setIscomplete(2);
                    helpContext.update(ShowRecord.this, helpContextId, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Log.e("Complete", "Completed!");
                            Toast.makeText(ShowRecord.this, "该求助已顺利完成，祝天天好心情喔！", Toast.LENGTH_SHORT).show();
                            finish();
                            //跳向评价页面，执行余额变动
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Log.e("Complete", "Complete Error!");
                            Toast.makeText(ShowRecord.this, "出错啦，容本帮主看看便回！", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });
    }
}
