package com.example.dawan.near02;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by dawan on 2016/2/18.
 */
public class ShowHelp extends AppCompatActivity {

    private TextView tv_title;
    private TextView tv_pay;
    private TextView tv_detail;
    private TextView tv_limitTime;
    private ImageView detailImage;

    String sUrl = "";

    private ImageButton btn_iHelp;
    private ImageButton btn_notHelp;
    TransactionRecord record;

    private int isComplete;

    private HelpContext showhelpContext;

//    图片

    PhotoViewAttacher mAttacher;
    boolean click = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showhelp);

        record = new TransactionRecord();

        tv_title = (TextView) findViewById(R.id.tv_show_title);
        tv_pay = (TextView) findViewById(R.id.tv_show_pay);
        tv_detail = (TextView) findViewById(R.id.tv_show_detail);
        btn_iHelp = (ImageButton) findViewById(R.id.btn_iHelp);
        btn_notHelp = (ImageButton) findViewById(R.id.btn_notHelp);
        tv_limitTime = (TextView) findViewById(R.id.tv_limitTime);
        detailImage = (ImageView) findViewById(R.id.detailImage);

        HelpContext helpContext = (HelpContext) getIntent().getSerializableExtra("ext_helpContext");

        String title = helpContext.getSimple_title();
        Double pay = helpContext.getPay();
        final String detail = helpContext.getDetail();
        String lTime = helpContext.getTime().getDate().toString();
        final String objectId = helpContext.getObjectId();
        final String requestID = helpContext.getRequestid();


        BmobFile bmobFile = helpContext.getUploadImg();
        if (bmobFile != null) {
            sUrl = bmobFile.getFileUrl(ShowHelp.this);
            Log.e("File", sUrl + "图片网址");

            AsynImageLoader asynImageLoader = new AsynImageLoader();
            asynImageLoader.showImageAsyn(detailImage, sUrl, R.drawable.logo);


        }
        tv_title.setText(title);
        tv_pay.setText(pay.toString());
        tv_limitTime.setText(lTime);
        tv_detail.setText(detail);
        /////////////////////////////////////////////
        BmobQuery<HelpContext> query = new BmobQuery<HelpContext>();
        //判断能否提供帮助
        query.getObject(ShowHelp.this, objectId, new GetListener<HelpContext>() {
                    @Override
                    public void onSuccess(HelpContext helpContext) {
                        showhelpContext = helpContext;
                        isComplete = helpContext.getIscomplete();
                        if (isComplete != 0) {
                            btn_iHelp.setVisibility(View.GONE);
//                            btn_notHelp.setGravity(Gravity.CENTER);
//                            btn_notHelp.setText("返回");
                        }
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Log.e("Record", "No this record.");

                    }
                }
        );


        ////////////////////////////////////////////


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

                    Log.e("UserId,helper", helperId);
                    Log.e("UserId,requester", requestID);

                    if (helperId.equals(requestID)) {

                        Toast.makeText(ShowHelp.this, "你不能自己解决自己的求助，谢谢。", Toast.LENGTH_SHORT).show();
                    } else {
                        if (isComplete != 0) {
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
                        } else if (isComplete == 0) {
                            BmobQuery<HelpContext> queryProgress = new BmobQuery<HelpContext>();
                            queryProgress.getObject(ShowHelp.this, objectId, new GetListener<HelpContext>() {
                                @Override
                                public void onSuccess(HelpContext helpContext) {
                                    if (helpContext.getIscomplete() == 0) {
                                        showhelpContext.setIscomplete(1);
                                        showhelpContext.setHelperId(helperId);
                                        showhelpContext.update(ShowHelp.this, objectId, new UpdateListener() {
                                            @Override
                                            public void onSuccess() {
                                                Log.e("Change", "OK!");

                                                //////////////保存该记录
                                                record.setHelperID(helperId);
                                                record.setRequestID(requestID);
                                                record.setBuniessId(objectId);
                                                record.save(ShowHelp.this, new SaveListener() {
                                                    @Override
                                                    public void onSuccess() {

                                                        String message = "你的请求已有人响应。";
                                                        new Function().pushMessage(ShowHelp.this, requestID, message);

                                                        Log.e("SaveRecord", "OK!");
                                                        Toast.makeText(ShowHelp.this, "好样的，记事长老给你记一功！", Toast.LENGTH_SHORT).show();
                                                    }

                                                    @Override
                                                    public void onFailure(int i, String s) {
                                                        Log.e("SaveRecord", "Fail");
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onFailure(int i, String s) {
                                                Log.e("Change", "Fail!" + s);
                                            }
                                        });
                                        finish();
                                    }
                                }

                                @Override
                                public void onFailure(int i, String s) {

                                    Toast.makeText(ShowHelp.this, "该求助已有其他帮众响应或已删除。", Toast.LENGTH_SHORT).show();

                                }
                            });


                        } else {
                            Toast.makeText(ShowHelp.this, "你果然很热心，就是手慢了一点，该请求已有帮众先一步响应！", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            }
        });


        detailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                detailImage.setDrawingCacheEnabled(true);
                Bitmap cacheImage = Bitmap.createBitmap(detailImage.getDrawingCache());
                detailImage.setDrawingCacheEnabled(false);

                detailImage.setImageBitmap(cacheImage);


                LayoutParams para;
                para = detailImage.getLayoutParams();

                DisplayMetrics dm2 = getResources().getDisplayMetrics();

                para.height = dm2.heightPixels / 2;

                para.width = dm2.widthPixels;
                detailImage.setLayoutParams(para);

                mAttacher = new PhotoViewAttacher(detailImage);
                mAttacher.update();
//                mAttacher.cleanup();


            }
        });


    }


}