package com.example.dawan.near02;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.ResetPasswordByCodeListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;

/**
 * Created by dawan on 2016/2/21.
 */
public class ChangeInfo extends AppCompatActivity{

    ImageButton btn_ver_1;
    ImageButton btn_summit_1;

    ImageButton btn_ver_2;
    ImageButton btn_summit_2;

    ImageButton btn_ver_3;
    ImageButton btn_summit_3;

    ImageButton btn_ver_4;
    ImageButton btn_summit_4;

    EditText pwd1;
    EditText pwd2;
    EditText ver1;

    EditText edt_newTelNum;

    EditText edt_oldVer;
    EditText edt_newVer;

    EditText edt_name;
    EditText edt_id;
    EditText edt_payAccount;
    EditText edt_addVer;
    String newTel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changeinfo);

        init();

        final String tel = (String) BmobUser.getObjectByKey(ChangeInfo.this, "mobilePhoneNumber");

        btn_ver_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tel == null) {
                    Toast.makeText(ChangeInfo.this, "请先登陆。", Toast.LENGTH_SHORT).show();
                    toLogin(ChangeInfo.this);
                } else {
                    if (new CheckInput().checkPWD(ChangeInfo.this, pwd1, pwd2)) {
                        BmobSMS.requestSMSCode(ChangeInfo.this, tel, "短信模板", new RequestSMSCodeListener() {
                            @Override
                            public void done(Integer integer, BmobException e) {
                                if (e == null) {
                                    Toast.makeText(ChangeInfo.this, "验证码发送成功，请查收。", Toast.LENGTH_SHORT).show();
                                    pwd1.setEnabled(false);
                                    pwd2.setEnabled(false);
                                    new CheckInput().getFocus(ver1);
                                }
                            }
                        });
                    }
                }
            }
        });

        btn_summit_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = UserRegister.MD5(UserRegister.MD5(pwd1.getText().toString()));
                String ver = ver1.getText().toString();
                BmobUser.resetPasswordBySMSCode(ChangeInfo.this, ver, pwd, new ResetPasswordByCodeListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null){
                            Toast.makeText(ChangeInfo.this,"密码重置成功，请使用新密码登陆。",Toast.LENGTH_SHORT).show();
                            BmobUser.logOut(ChangeInfo.this);
                            toLogin(ChangeInfo.this);
                            finish();
                        }else {
                            Toast.makeText(ChangeInfo.this,"密码重置失败。" + e.getErrorCode(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        btn_ver_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tel == null) {
                    Toast.makeText(ChangeInfo.this, "请先登陆。", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ChangeInfo.this, Login.class);
                    startActivity(intent);
                } else {
                    BmobSMS.requestSMSCode(ChangeInfo.this, tel, "短信模板", new RequestSMSCodeListener() {
                        @Override
                        public void done(Integer integer, BmobException e) {
                            if (e == null){
                                Toast.makeText(ChangeInfo.this,"验证码已发送，请查收。",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(ChangeInfo.this,"验证码发送失败，请重试。",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                }
        });

        btn_summit_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobSMS.verifySmsCode(ChangeInfo.this, tel, edt_addVer.getText().toString(), new VerifySMSCodeListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null){
                            //验证通过
                            String realName = edt_name.getText().toString();
                            String realID = edt_id.getText().toString();
                            String payAccount = edt_payAccount.getText().toString();
                            User user = new User();
                            user.setRealName(realName);
                            user.setIdNumber(realID);
                            user.setPayAccount(payAccount);

                            User curUser = BmobUser.getCurrentUser(ChangeInfo.this,User.class);

                            user.update(ChangeInfo.this, curUser.getObjectId(), new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(ChangeInfo.this,"用户信息更新成功。",Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    Toast.makeText(ChangeInfo.this,"更新失败，请重试。",Toast.LENGTH_SHORT).show();
                                }
                            });


                        }else {
                            //验证不通过
                            Toast.makeText(ChangeInfo.this,"验证码错误，请重新输入。",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btn_ver_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Function().verMobile(ChangeInfo.this, tel);

            }
        });

        btn_ver_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CheckInput().checkMobile(ChangeInfo.this,edt_newTelNum);
               newTel = edt_newTelNum.getText().toString();
                new Function().verMobile(ChangeInfo.this,newTel);
            }
        });

        btn_summit_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobSMS.verifySmsCode(ChangeInfo.this, tel, edt_oldVer.getText().toString(), new VerifySMSCodeListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null){
                            Log.e("Ver","原手机验证成功。");
                            BmobSMS.verifySmsCode(ChangeInfo.this, newTel, edt_newVer.getText().toString(), new VerifySMSCodeListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        Log.e("Ver", "新手机验证成功。");
                                        User cUser = new User();
                                        cUser.setMobilePhoneNumber(newTel);
                                        User myUser = BmobUser.getCurrentUser(ChangeInfo.this,User.class);
                                        cUser.update(ChangeInfo.this, myUser.getObjectId(), new UpdateListener() {
                                            @Override
                                            public void onSuccess() {
                                                Toast.makeText(ChangeInfo.this,"手机号码更新成功。",Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onFailure(int i, String s) {
                                                Toast.makeText(ChangeInfo.this,"手机号码更新失败，请重试。",Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }else {
                                        Log.e("Ver","新手机验证失败。");
                                    }
                                }
                            });
                        }else {
                            Log.e("Ver","原手机验证失败。");
                        }
                    }
                });
            }
        });

    }
    public void init(){

        btn_ver_1 = (ImageButton)findViewById(R.id.btn_change_pwd_getVer);
        btn_summit_1 = (ImageButton)findViewById(R.id.btn_change_pwd);

        btn_ver_2 = (ImageButton)findViewById(R.id.btn_change_tel_getVerOld);
        btn_summit_2 = (ImageButton)findViewById(R.id.btn_change_tel);

        btn_ver_4 = (ImageButton)findViewById(R.id.btn_change_tel_getVerNew);
        edt_newTelNum = (EditText)findViewById(R.id.edt_change_newNumber);
        edt_oldVer = (EditText)findViewById(R.id.edt_oldTel_ver);
        edt_newVer = (EditText)findViewById(R.id.edt_newtelVer);


        btn_ver_3 = (ImageButton)findViewById(R.id.btn_add_ID_getVer);
        btn_summit_3 = (ImageButton)findViewById(R.id.btn_addID);

        pwd1 = (EditText)findViewById(R.id.edt_change_pwd);
        pwd2 = (EditText)findViewById(R.id.edt_change_pwd_repeat);
        ver1 = (EditText)findViewById(R.id.edt_change_pwd_ver);

        edt_name = (EditText)findViewById(R.id.edt_add_realName);
        edt_id = (EditText)findViewById(R.id.edt_add_realID);
        edt_payAccount = (EditText)findViewById(R.id.edt_add_payAccount);
        edt_addVer = (EditText)findViewById(R.id.edt_add_ver);


    }

    public void toLogin(Context context){
        Intent intent = new Intent(context, Start_Activity.class);
        startActivity(intent);
    }

}
