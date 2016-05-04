package com.example.dawan.near02;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.RequestSMSCodeListener;

/**
 * Created by dawan on 2016/2/19.
 */
public class Login extends AppCompatActivity {

    //    private EditText edt_tel;
    private FormEditText edt_tel;
    private EditText edt_pwd;
    private EditText edt_ver;
    private ImageButton btn_getVer;
    private ImageButton btn_log;
    private ImageButton btn_reg;

    private Boolean style;

    //点击了获取验证码则密码框不可用

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        edt_tel = (FormEditText) findViewById(R.id.edt_login_tel);
//        edt_tel = (EditText)findViewById(R.id.edt_login_tel);
        edt_pwd = (EditText) findViewById(R.id.edt_login_pwd);
        edt_ver = (EditText) findViewById(R.id.edt_login_ver);
        btn_getVer = (ImageButton) findViewById(R.id.btn_getVer_login);
        btn_log = (ImageButton) findViewById(R.id.btn_login);
        btn_reg = (ImageButton) findViewById(R.id.btn_reg_in_login);
        style = false;

//        new  CheckInput().checkMobile(Login.this,edt_tel,btn_getVer,btn_log);

        btn_getVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String telNum = edt_tel.getText().toString();
                new  CheckInput().checkMobile(Login.this,edt_tel);

                if (verificationInput(new FormEditText[]{edt_tel})) {

                    BmobSMS.requestSMSCode(Login.this, telNum, "测试短信", new RequestSMSCodeListener() {
                        @Override
                        public void done(Integer integer, BmobException e) {
                            if (e == null) {
                                Log.e("Smile", "短信id" + integer);
                                style = true;
                                edt_pwd.setFocusable(false);
                                new CheckInput().getFocus(edt_ver);
                                edt_pwd.setText("请在下方输入收到的验证码.");
                                Toast.makeText(Login.this,"验证码已发送，请查收。",Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                } else {
                    Log.e("TEL", "NOT OK!");
                    Toast.makeText(Login.this, "请输入正确的电话号码", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//验证数据有效性
                if (verificationInput(new FormEditText[]{edt_tel})) {
                    String telNum = edt_tel.getText().toString();
                    String pwd = UserRegister.MD5(UserRegister.MD5(edt_pwd.getText().toString()));
                    String ver = edt_ver.getText().toString();
                    if (style) {
                        BmobUser.loginBySMSCode(Login.this, telNum, ver, new LogInListener<User>() {
                            @Override
                            public void done(User user, BmobException e) {
                                if (user != null) {
                                    Log.e("Login", "Succese");
                                    Intent intent = new Intent(Login.this,NeedHelp.class);
                                    startActivity(intent);
//                                    finish();
                                } else {
                                    Log.e("Login", "Fail");
                                    Toast.makeText(Login.this, "账号或密码有错", Toast.LENGTH_SHORT).show();
                                    edt_tel.setText("");
                                    edt_pwd.setText("");
                                    edt_ver.setText("");
                                }
                            }
                        });
                    } else {

                        BmobUser.loginByAccount(Login.this, telNum, pwd, new LogInListener<User>() {
                            @Override
                            public void done(User user, BmobException e) {
                                if (user != null) {
                                    Log.e("Login", "Done!");
                                    Toast.makeText(Login.this,"你已成功登陆。",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Login.this,Start_Activity.class);
                                    startActivity(intent);
//                                    finish();
                                }else {
                                    Toast.makeText(Login.this, "账号或密码有误，请重新输入。", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }else {
                    Log.e("TEL", "NOT OK!");
                    Toast.makeText(Login.this, "请输入正确的电话号码", Toast.LENGTH_SHORT).show();
                }
            }


        });

        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, UserRegister.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public Boolean verificationInput(FormEditText arg[]) {
//        FormEditText[] allFields    = arg;


        boolean allValid = true;
        for (FormEditText field : arg) {
            allValid = field.testValidity() && allValid;
        }

        if (allValid) {
            return true;//验证通过
        } else {
            return false;
            // EditText are going to appear with an exclamation mark and an explicative message.
        }


    }
}
