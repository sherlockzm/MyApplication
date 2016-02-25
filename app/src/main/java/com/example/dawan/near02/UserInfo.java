package com.example.dawan.near02;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.bmob.v3.BmobUser;

/**
 * Created by dawan on 2016/2/21.
 */
public class UserInfo extends AppCompatActivity {

    private Button btn_takeCash;
    private Button btn_changInfo;
    private Button btn_loginOut;

    private TextView tv_name;
    private TextView tv_tel;
    private TextView tv_realName;
    private TextView tv_realID;
    private TextView tv_overage;
    private TextView tv_payAccount;
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

        User curUser = BmobUser.getCurrentUser(UserInfo.this,User.class);

        tv_name.setText(curUser.getUsername());
        tv_tel.setText(curUser.getMobilePhoneNumber());
        tv_realName.setText(curUser.getRealName());
        tv_realID.setText(curUser.getIdNumber());
        tv_payAccount.setText(curUser.getPayAccount());

        //新建一个表用于存放余额

        btn_changInfo = (Button)findViewById(R.id.btn_changeInfo);
        btn_takeCash = (Button)findViewById(R.id.btn_takeCash);
        btn_loginOut = (Button)findViewById(R.id.btn_user_loginout);

        btn_loginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobUser.logOut(UserInfo.this);
                Intent outIntent = new Intent(UserInfo.this,Login.class);
                startActivity(outIntent);
                finish();
            }
        });

        btn_changInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfo.this, ChangeInfo.class);
                startActivity(intent);
            }
        });
    }
}
