package com.example.dawan.near02;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.FindStatisticsListener;

/**
 * Created by dawan on 2016/2/21.
 */
public class UserInfo extends AppCompatActivity {

    private ImageButton btn_takeCash;
    private ImageButton btn_changInfo;
    private ImageButton btn_loginOut;
    /////////
    private ImageButton ibtn_area;
    private EditText edt_area;
    private ImageButton ibtn_helpArea;
    private EditText edt_helpArea;
/////////////
    private TextView tv_name;
    private TextView tv_tel;
    private TextView tv_realName;
    private TextView tv_realID;
    private TextView tv_overage;
    private TextView tv_payAccount;
    private TextView tv_curArea;
    private TextView tv_helpArea;
    private TextView tv_requesterScore;
    private TextView tv_helperScore;
    private TextView tv_aboutMe;

    private String overage;

    private User curUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showuserinfo);

        tv_name = (TextView)findViewById(R.id.info_name);
        tv_tel = (TextView)findViewById(R.id.info_tel);
        tv_realName = (TextView)findViewById(R.id.tv_info_realName);
        tv_realID = (TextView)findViewById(R.id.tv_info_ID);
        tv_overage = (TextView)findViewById(R.id.tv_overage);
        tv_payAccount = (TextView)findViewById(R.id.tv_payAccount);
        tv_curArea = (TextView)findViewById(R.id.tv_showArea);
        tv_helpArea = (TextView)findViewById(R.id.tv_showHelpArea);

        tv_requesterScore = (TextView)findViewById(R.id.tv_userInfo_requesterScore);
        tv_helperScore = (TextView)findViewById(R.id.tv_userInfo_helperScore);
/////////////范围
        ibtn_area = (ImageButton)findViewById(R.id.btn_defineArea);
        edt_area = (EditText)findViewById(R.id.edt_area);
        ibtn_helpArea = (ImageButton)findViewById(R.id.btn_defineHelpArea);
        edt_helpArea = (EditText)findViewById(R.id.edt_helparea);

        tv_aboutMe = (TextView) findViewById(R.id.aboutUs);




       if (BmobUser.getCurrentUser(UserInfo.this,User.class) != null) {
           curUser = BmobUser.getCurrentUser(UserInfo.this,User.class);

           getHelperScore(curUser);
           getRequesterScore(curUser);

           tv_name.setText(curUser.getUsername());
           tv_tel.setText(curUser.getMobilePhoneNumber());
           tv_realName.setText(curUser.getRealName());
           tv_realID.setText(curUser.getIdNumber());
           tv_payAccount.setText(curUser.getPayAccount());

           getOverage(curUser.getObjectId());


       }
        //新建一个表用于存放余额

        btn_changInfo = (ImageButton)findViewById(R.id.btn_changeInfo);
        btn_takeCash = (ImageButton)findViewById(R.id.btn_takeCash);
        btn_loginOut = (ImageButton)findViewById(R.id.btn_user_loginout);

        //shareperfereace 存放区域

        SharedPreferences getArea = getSharedPreferences("Area", Activity.MODE_PRIVATE);
        Float curArea = getArea.getFloat("myArea", 10);
        SharedPreferences getHelpArea = getSharedPreferences("helpArea", Activity.MODE_PRIVATE);
        Float helpArea = getHelpArea.getFloat("myHelpArea",10);
        tv_curArea.setText("，求助范围" + curArea + "公里");
        tv_helpArea.setText("当前援助范围" + helpArea + "公里");



        tv_aboutMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfo.this,AboutUs.class);
                startActivity(intent);
            }
        });

        ibtn_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean input = new CheckInput().checkArea(UserInfo.this, edt_area);
                if (input) {
                    SharedPreferences areaSave = getSharedPreferences("Area", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = areaSave.edit();
                    editor.putFloat("myArea", Float.valueOf(edt_area.getText().toString()));
                    editor.commit();
                    /////////
                    edt_area.setEnabled(false);
                    /////////
                    tv_curArea.setText("当前求助范围为方圆" + Float.valueOf(edt_area.getText().toString()) + "公里");
                    Toast.makeText(UserInfo.this, "求助范围设置成功。", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ibtn_helpArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean input = new CheckInput().checkArea(UserInfo.this, edt_helpArea);
                if (input) {
                    SharedPreferences areaSave = getSharedPreferences("helpArea", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = areaSave.edit();
                    editor.putFloat("myHelpArea", Float.valueOf(edt_helpArea.getText().toString()));
                    editor.commit();
                    /////////
                    edt_helpArea.setEnabled(false);
                    /////////
                    tv_helpArea.setText("当前援助范围为方圆" + Float.valueOf(edt_helpArea.getText().toString()) + "公里");
                    Toast.makeText(UserInfo.this, "求助范围设置成功。", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btn_loginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobUser.logOut(UserInfo.this);
                Intent outIntent = new Intent(UserInfo.this,Start_Activity.class);
                startActivity(outIntent);
                finish();
            }
        });

        btn_changInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfo.this, ChangeActivity.class);
                startActivity(intent);
            }
        });

        btn_takeCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfo.this,TakeCash.class);
                intent.putExtra("UserInfo",curUser);
                startActivity(intent);
            }
        });
    }


    public void getHelperScore(User user){
        BmobQuery<Score> query = new BmobQuery<Score>();
        query.average(new String[]{"helperScore"});
        query.addWhereEqualTo("helperId", user.getObjectId());
//        query.groupby(new String[]{"helperId"});
        query.findStatistics(UserInfo.this, Score.class, new FindStatisticsListener() {
            @Override
            public void onSuccess(Object o) {
                JSONArray ary = (JSONArray) o;
                if (ary!=null) {
                    try {
                        JSONObject obj = ary.getJSONObject(0);
                        Double helperScore = obj.getDouble("_avgHelperScore");

                        DecimalFormat dcmFmt = new DecimalFormat("0.00");
                        helperScore = Double.valueOf(dcmFmt.format(helperScore));

                        tv_helperScore.setText(helperScore.toString());
                        Log.e("Average", helperScore + "");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("Average","fail");
                }
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    public void getRequesterScore(User user){
        BmobQuery<Score> query = new BmobQuery<Score>();
        query.average(new String[]{"requesterScore"});
        query.addWhereEqualTo("requesterId", user.getObjectId());
//        query.groupby(new String[]{"requesterId"});
        query.findStatistics(UserInfo.this, Score.class, new FindStatisticsListener() {
            @Override
            public void onSuccess(Object o) {
                JSONArray ary = (JSONArray) o;
                if (ary!=null) {
                    try {
                        JSONObject obj = ary.getJSONObject(0);
                        Double requesterScore = obj.getDouble("_avgRequesterScore");

                        DecimalFormat dcmFmt = new DecimalFormat("0.00");
                        requesterScore = Double.valueOf(dcmFmt.format(requesterScore));
                        tv_requesterScore.setText(requesterScore.toString());
                        Log.e("Average", requesterScore + " requester");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("Average","fail");
                }
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    private void getOverage(String userID){

        //test对应你刚刚创建的云端逻辑名称
        String cloudCodeName = "userOverage";
        JSONObject params = new JSONObject();
//name是上传到云端的参数名称，值是bmob，云端逻辑可以通过调用request.body.name获取这个值
        try {
            params.put("userId", userID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//创建云端逻辑对象
        AsyncCustomEndpoints cloudCode = new AsyncCustomEndpoints();
//异步调用云端逻辑
        cloudCode.callEndpoint(UserInfo.this, cloudCodeName, params, new CloudCodeListener() {

            //执行成功时调用，返回result对象
            @Override
            public void onSuccess(Object result) {
                Log.e("Success","get overage success" + result);
                tv_overage.setText(result.toString());
                String mo = result.toString();

            }

            @Override
            public void onFailure(int i, String s) {
                Log.e("Fail","Get overage fail");

            }


        });


    }





}
