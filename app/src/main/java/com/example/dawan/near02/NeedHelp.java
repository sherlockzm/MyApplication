package com.example.dawan.near02;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by dawan on 2016/2/18.
 */
public class NeedHelp extends AppCompatActivity {

    private FormEditText edt_simple_title;
    private FormEditText edt_pay;
    private EditText edt_time;
    private FormEditText edt_detail;
    private ImageButton btn_submit;
    private ImageButton ibtn_clear;
    private Button btn_uploadImage;
    private Double latitude;
    private Double longitude;
    BmobGeoPoint bmobGeoPoint;
    private String installID;
    private String notificationContext;

    private ImageView imgView_upload;

    private BmobFile bmobFile;

    String detail;
    String time;
    Double pay;
    String simpleTitle;

    HelpContext helpContext = new HelpContext();

    private Uri imageUri;

    private static final int INTENT_REQUEST_GET_IMAGES = 13;

    private static final int PAY_CODE = 929;


    private TextView show_notice;

    private int count;

    private BmobDate timeBefore = null;

    private static final String notice = "注意：当前定位未成功，将使用上次的定位信息。";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.needhelp);

        installID = (String) BmobUser.getObjectByKey(NeedHelp.this, "objectId");

        edt_simple_title = (FormEditText) findViewById(R.id.edt_simple_title);
        edt_pay = (FormEditText) findViewById(R.id.edt_pay);
        edt_time = (EditText) findViewById(R.id.edt_time);
        edt_detail = (FormEditText) findViewById(R.id.edt_detail);
        btn_submit = (ImageButton) findViewById(R.id.btn_submit);
        ibtn_clear = (ImageButton) findViewById(R.id.btn_clear);
        show_notice = (TextView) findViewById(R.id.tv_show_notice);
        btn_uploadImage = (Button) findViewById(R.id.btn_takePhoto);

        imgView_upload = (ImageView) findViewById(R.id.imgV_upload);


        new CheckInput().getFocus(edt_simple_title);
        ////////////////////////
        if (savedInstanceState != null) {
            String tt = savedInstanceState.getString("TITLE");
            String dt = savedInstanceState.getString("DETAIL");
            String pp = savedInstanceState.getString("PAY");
            String tTime = savedInstanceState.getString("Time");

            edt_simple_title.setText(tt);
            edt_pay.setText(pp);
            edt_time.setText(tTime);
            edt_detail.setText(dt);

        }

        ////////////////
        setSaveText();
        countRequest(NeedHelp.this, installID);
        ///////////////////////////

        Intent intent = getIntent();
        longitude = intent.getDoubleExtra("lon", 0);

        latitude = intent.getDoubleExtra("lat", 0);


        setGeo(longitude, latitude);
//        bmobGeoPoint = new BmobGeoPoint(longitude, latitude);

        btn_uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getImages();
            }
        });


        ibtn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_simple_title.setText("");
                edt_pay.setText("");
                edt_time.setText("");
                edt_detail.setText("");
                imgView_upload.setImageDrawable(null);
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检验输入的内容

                if (count == 0) {
                    new Function().checkNetworkInfo(NeedHelp.this);
                    boolean cInput = new Login().verificationInput(new FormEditText[]{edt_simple_title, edt_pay, edt_detail}) &&
                            new CheckInput().CheckInputHelp(NeedHelp.this, edt_simple_title, 18) && new CheckInput().CheckInputHelp(NeedHelp.this, edt_detail, 100);
                    if (cInput && bmobGeoPoint != null) {

                        //添加try ，保证先保存在服务器再进行推送。
                        simpleTitle = edt_simple_title.getText().toString();
                        pay = new Function().trimNull(edt_pay.getText().toString());
                        time = edt_time.getText().toString();
                        BmobDate lmTime = limitTime(time);
                        detail = edt_detail.getText().toString();


                        helpContext.setBmobGeoPoint(bmobGeoPoint);
                        helpContext.setSimple_title(simpleTitle);
                        helpContext.setPay(pay);
                        helpContext.setTime(lmTime);
                        helpContext.setDetail(detail);
                        helpContext.setIscomplete(0);
                        helpContext.setStation(0);
                        helpContext.setRequestid(installID);//当前手机ID

                        if (bmobFile != null) {
                            bmobFile.uploadblock(NeedHelp.this, new UploadFileListener() {
                                @Override
                                public void onSuccess() {

                                    helpContext.setUploadImg(bmobFile);

                                    final Float myArea = getArea();

                                    helpContext.save(NeedHelp.this, new SaveListener() {
                                        @Override
                                        public void onSuccess() {
                                            Log.e("Save", "SUCCESS!" + bmobGeoPoint.getLatitude());

                                            new Function().pushForHelp(NeedHelp.this, bmobGeoPoint, myArea);

                                            cleanInput();

                                            gotoPay(pay.toString());

//                                            finish();
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {
                                            Log.e("Save", "FAIL");

                                        }
                                    });

                                }

                                @Override
                                public void onFailure(int i, String s) {

                                }
                            });
                        } else {
                            helpContext.setUploadImg();
                            final Float myArea = getArea();
                            helpContext.save(NeedHelp.this, new SaveListener() {
                                @Override
                                public void onSuccess() {
                                    new Function().pushForHelp(NeedHelp.this, bmobGeoPoint, myArea);
                                    cleanInput();


                                    gotoPay(pay.toString());
                                    finish();

                                }

                                @Override
                                public void onFailure(int i, String s) {

                                }
                            });

                        }
                        //TODO 时间转为后再存入

                    } else {
                        Toast.makeText(NeedHelp.this, "定位未成功或输入完整信息不完整", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    new Function().showMessage(NeedHelp.this, "每天只能使用10次求助。你已经用完。");
                }
            }
        });


    }


    private void getImages() {

        Config config = new Config();
//        config.setCameraHeight(R.dimen.app_camera_height);
//        config.setToolbarTitleRes(R.string.custom_title);
        config.setSelectionMin(1);
        config.setSelectionLimit(1);
//        config.setSelectedBottomHeight(R.dimen.bottom_height);

        ImagePickerActivity.setConfig(config);

        Intent intent = new Intent(this, ImagePickerActivity.class);
        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INTENT_REQUEST_GET_IMAGES && resultCode == Activity.RESULT_OK) {

            ArrayList<Uri> image_uris = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

            imageUri = image_uris.get(0);

            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(imageUri.getPath(), options);

            options.inSampleSize = computeInitialSampleSize(options, -1, 256 * 256);

            options.inJustDecodeBounds = false;

            try {

                Bitmap bmp = BitmapFactory.decodeFile(imageUri.getPath(), options);

                imgView_upload.setImageBitmap(bmp);

                saveBitmap2file(bmp, "upload.jpg");

                File upload = new File("/sdcard/upload.jpg");

                bmobFile = new BmobFile(upload);

            } catch (OutOfMemoryError err) {

            }

        }else if (requestCode == PAY_CODE && resultCode == RESULT_OK){


        }

    }


    public Float getArea() {
        SharedPreferences getArea = getSharedPreferences("Area", Activity.MODE_PRIVATE);
        Float area = getArea.getFloat("myArea", 10);
        Log.e("Area", area + "");
        return area;
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {

        if (edt_simple_title.getText().toString().trim() != "") {
            savedInstanceState.putString("TITLE", edt_simple_title.getText().toString());
        }
        if (edt_detail.getText().toString().trim() != "") {
            savedInstanceState.putString("DETAIL", edt_detail.getText().toString());
        }
        if (edt_pay.getText().toString().trim() != "") {
            savedInstanceState.putString("PAY", edt_pay.getText().toString());
        }
        if (edt_time.getText().toString().trim() != "") {
            savedInstanceState.putString("Time", edt_time.getText().toString());
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String title = savedInstanceState.getString("TITLE");

        String detail = savedInstanceState.getString("DETAIL");

        String pay = savedInstanceState.getString("PAY");

        String time = savedInstanceState.getString("Time");


    }


    public void onBackPressed() {

        SharedPreferences sharedPreferences = getSharedPreferences("SAVEDETAIL", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Title", edt_simple_title.getText().toString());
        editor.putString("Pay", edt_pay.getText().toString());
        editor.putString("Time", edt_time.getText().toString());
        editor.putString("Detail", edt_detail.getText().toString());
        editor.commit();
        super.onBackPressed();

    }

    private void gotoPay(String money){

        Intent intent = new Intent(NeedHelp.this,PaySubmit.class);
        intent.putExtra("MONEY",money);
        startActivityForResult(intent,PAY_CODE);
//        finish();
    }

    private void setSaveText() {
        SharedPreferences preferences = getSharedPreferences("SAVEDETAIL", MODE_PRIVATE);
        String title = preferences.getString("Title", "");
        String pay = preferences.getString("Pay", "");
        String time = preferences.getString("Time", "");
        String detail = preferences.getString("Detail", "");
        edt_simple_title.setText(title);
        edt_pay.setText(pay);
        edt_time.setText(time);
        edt_detail.setText(detail);
    }

    private void countRequest(final Context context, String id) {

        Log.e("Date", id + "用户ID");


        Calendar now = Calendar.getInstance();

        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DATE);

        now.set(year, month, day, 00, 00, 00);

        Date dBefore = now.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //设置时间格式
        String defaultStartDate = sdf.format(dBefore);
        Log.e("Date", defaultStartDate + "");

        Date date1 = null;
        try {
            date1 = sdf.parse(defaultStartDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        now.set(year, month, day, 23, 59, 59);
        Date dBefore1 = now.getTime();

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //设置时间格式
        String defaultStartDate3 = sdf2.format(dBefore1);
        Log.e("Date", defaultStartDate3 + "");

        Date date2 = null;
        try {
            date2 = sdf.parse(defaultStartDate3);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        BmobQuery<HelpContext> query = new BmobQuery<>();
        BmobQuery<HelpContext> query1 = new BmobQuery<>();
        BmobQuery<HelpContext> query2 = new BmobQuery<>();
        BmobQuery<HelpContext> query3 = new BmobQuery<>();

        query1.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(date1));
        query2.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date2));
        query3.addWhereEqualTo("requestid", id);
        List<BmobQuery<HelpContext>> andQuerys = new ArrayList<BmobQuery<HelpContext>>();
        andQuerys.add(query1);
        andQuerys.add(query2);
        andQuerys.add(query3);
        query.and(andQuerys);
        query.count(context, HelpContext.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                Log.e("Date", i + "条数据");
                if (i >= 10) {
                    count = 1;
                    Log.e("Date", count + " count 的值");
                    new Function().showMessage(context, "每天只能使用10次求助。你已经用完。");
                } else {
                    count = 0;
                }
            }

            @Override
            public void onFailure(int i, String s) {

            }

        });


    }

    private void setGeo(Double lon, Double lat) {

        if (lon == 0.0 || lat == 0.0) {
            BmobQuery<MyInstallation> query = new BmobQuery<MyInstallation>();

            query.addWhereEqualTo("userId", installID);
            query.findObjects(NeedHelp.this, new FindListener<MyInstallation>() {
                @Override
                public void onSuccess(List<MyInstallation> list) {
                    for (MyInstallation installation : list) {
                        bmobGeoPoint = installation.getMyPoint();
                        show_notice.setText(notice);
                        Log.e("geo", bmobGeoPoint + "######" + bmobGeoPoint.getLatitude() + "????" + bmobGeoPoint.getLongitude());

                    }
                }

                @Override
                public void onError(int i, String s) {
                    Log.e("geo", "~~~~fail");
                }
            });
        } else {
            Log.e("geo", bmobGeoPoint + "！！！");
            bmobGeoPoint = new BmobGeoPoint(lon, lat);
        }
    }


    private BmobDate limitTime(String time) {


        int hour = 0;
        int minute = 0;
        if (time.trim().equals("")) {
            hour = 24;
            minute = 0;
        } else if (Double.valueOf(time) > 24 || Double.valueOf(time) <= 0) {
            hour = 24;
            minute = 0;
        } else {

            String[] lTime = time.split("\\.");

            Log.e("NOTHING", lTime[0]);
//            Log.e("NOTHING", lTime[1]);
            Log.e("NOTHING", lTime.length + "长度");

            if (lTime[0].equals(null) || lTime[0].equals("0") || lTime[0].equals("")) {
                lTime[0] = "0";
                hour = 0;
            } else if (lTime.length > 1 && Double.valueOf(time) < 1) {
                hour = 0;
                minute = Integer.valueOf(lTime[1].substring(0, 1)) * 6;
            }else if (lTime.length > 1 && Double.valueOf(lTime[0]) > 0 && Double.valueOf(lTime[1]) > 0){
                hour =  Integer.valueOf(lTime[0]);
                minute =  Integer.valueOf(lTime[1]);
            }else {
                hour =  Integer.valueOf(lTime[0]);
                minute = 0;
            }
        }
        Date dNow = new Date();   //当前时间
        Date dBefore = null;

        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(dNow);

        calendar.add(Calendar.HOUR_OF_DAY, hour);  //设置实际有效时间
        calendar.add(Calendar.MINUTE, minute);
        dBefore = calendar.getTime();   //得到有效时间

//        BmobDate bmobDate = new  BmobDate(dBefore);

        return timeBefore = new BmobDate(dBefore);

//        Log.e("Time", timeBefore + "!BEFORE!");
//        Log.e("Time", dBefore + "!BEFORE!");


    }


    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {

        double w = options.outWidth;

        double h = options.outHeight;


        int lowerBound = (maxNumOfPixels == -1) ? 1 :

                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));

        int upperBound = (minSideLength == -1) ? 128 :

                (int) Math.min(Math.floor(w / minSideLength),

                        Math.floor(h / minSideLength));


        if (upperBound < lowerBound) {

            // return the larger one when there is no overlapping zone.

            return lowerBound;

        }


        if ((maxNumOfPixels == -1) &&

                (minSideLength == -1)) {

            return 1;

        } else if (minSideLength == -1) {

            return lowerBound;

        } else {

            return upperBound;

        }

    }


    static boolean saveBitmap2file(Bitmap bmp, String filename) {
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 20;
        OutputStream stream = null;
        try {
            stream = new FileOutputStream("/sdcard/" + filename);
        } catch (FileNotFoundException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }

        return bmp.compress(format, quality, stream);
    }


    private void cleanInput() {
        SharedPreferences sharedPreferences = getSharedPreferences("SAVEDETAIL", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Title", "");
        editor.putString("Pay", "");
        editor.putString("Time", "");
        editor.putString("Detail", "");
        editor.commit();
    }


}
