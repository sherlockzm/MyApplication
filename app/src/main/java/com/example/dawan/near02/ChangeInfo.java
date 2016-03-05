package com.example.dawan.near02;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.ResetPasswordByCodeListener;

/**
 * Created by dawan on 2016/2/21.
 */
public class ChangeInfo extends AppCompatActivity {

    private EditText edt_pwd_change;
    private EditText edt_pwd_change_repeat;
    private EditText edt_c_pwd_ver;
    private Button btn_getVer;
    private Button btn_change_pwd;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changeinfo);

        edt_pwd_change = (EditText)findViewById(R.id.edt_change_pwd);
        edt_pwd_change_repeat = (EditText)findViewById(R.id.edt_change_pwd_repeat);
        edt_c_pwd_ver = (EditText)findViewById(R.id.edt_change_pwd_ver);
        btn_getVer = (Button)findViewById(R.id.btn_change_pwd_getVer);
        btn_change_pwd = (Button)findViewById(R.id.btn_change_pwd);

        final String telNum = (String) BmobUser.getObjectByKey(ChangeInfo.this,"mobilePhoneNumber");

        btn_getVer.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              if (new CheckInput().checkPwd(ChangeInfo.this, edt_pwd_change, edt_pwd_change_repeat, 6, 30)) {
                                                  if (telNum == null) {
                                                      Toast.makeText(ChangeInfo.this, "登陆状态异常，请先登陆。", Toast.LENGTH_SHORT).show();
                                                  } else {
                                                      Log.e("TEL",telNum);
                                                      BmobSMS.requestSMSCode(ChangeInfo.this, telNum, "默认格式", new RequestSMSCodeListener() {
                                                          @Override
                                                          public void done(Integer integer, BmobException e) {
                                                              if (e == null) {
                                                                  Log.e("SMS_ID", integer + "");
                                                              } else {
                                                                  Toast.makeText(ChangeInfo.this, "获取验证码失败，请重试。", Toast.LENGTH_SHORT).show();
                                                              }
                                                          }
                                                      });
                                                  }
                                              }else {
                                                  Log.e("PWD","密码格式不对。");
                                              }
                                          }
                                      }

            );

        btn_change_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobUser.resetPasswordBySMSCode(ChangeInfo.this, edt_c_pwd_ver.getText().toString(), UserRegister.MD5(UserRegister.MD5(edt_pwd_change.getText().toString())), new ResetPasswordByCodeListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null){
                            Toast.makeText(ChangeInfo.this,"密码重置成功，请使用新密码登陆。",Toast.LENGTH_SHORT).show();
                            BmobUser.logOut(ChangeInfo.this);
                            Intent intentL = new Intent(ChangeInfo.this,Login.class);
                            startActivity(intentL);
                        }else {
                            Toast.makeText(ChangeInfo.this,"密码修改失败，请重试。",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

    }
}
