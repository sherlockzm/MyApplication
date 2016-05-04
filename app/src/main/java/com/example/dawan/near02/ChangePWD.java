package com.example.dawan.near02;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.ResetPasswordByCodeListener;

/**
 * Created by dawan on 2016/4/28.
 */
public class ChangePWD extends AppCompatActivity {

    private EditText edt_newpwd;
    private EditText edt_newpwd2;
    private EditText edt_ver;
    private ImageButton btn_changePwdVer;
    private ImageButton btn_change;

    private final ChangePWD THISCONTEXT = ChangePWD.this;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepwd);

        edt_newpwd = (EditText)findViewById(R.id.edt_change_pwd);
        edt_newpwd2 = (EditText)findViewById(R.id.edt_change_pwd_repeat);
        edt_ver = (EditText)findViewById(R.id.edt_change_pwd_ver);
        btn_changePwdVer = (ImageButton)findViewById(R.id.btn_change_pwd_getVer);
        btn_change = (ImageButton)findViewById(R.id.btn_change_pwd);

        final String tel = (String) BmobUser.getObjectByKey(ChangePWD.this, "mobilePhoneNumber");

        btn_changePwdVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tel == null) {
                    Toast.makeText(THISCONTEXT, "请先登陆。", Toast.LENGTH_SHORT).show();
                    toLogin(THISCONTEXT);
                } else {
                    if (new CheckInput().checkPWD(THISCONTEXT, edt_newpwd, edt_newpwd2)) {
                        BmobSMS.requestSMSCode(THISCONTEXT, tel, "短信模板", new RequestSMSCodeListener() {
                            @Override
                            public void done(Integer integer, BmobException e) {
                                if (e == null) {
                                    Toast.makeText(THISCONTEXT, "验证码发送成功，请查收。", Toast.LENGTH_SHORT).show();
                                    edt_newpwd.setEnabled(false);
                                    edt_newpwd2.setEnabled(false);
                                    new CheckInput().getFocus(edt_ver);
                                }
                            }
                        });
                    }
                }
            }
        });

        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = UserRegister.MD5(UserRegister.MD5(edt_newpwd.getText().toString()));
                String ver = edt_ver.getText().toString();
                BmobUser.resetPasswordBySMSCode(THISCONTEXT, ver, pwd, new ResetPasswordByCodeListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null){
                            Toast.makeText(THISCONTEXT,"密码重置成功，请使用新密码登陆。",Toast.LENGTH_SHORT).show();
                            BmobUser.logOut(THISCONTEXT);
                            toLogin(THISCONTEXT);
                            finish();
                        }else {
                            Toast.makeText(THISCONTEXT,"密码重置失败。" + e.getErrorCode(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });


    }

    public void toLogin(Context context){
        Intent intent = new Intent(context, Start_Activity.class);
        startActivity(intent);
    }
}
