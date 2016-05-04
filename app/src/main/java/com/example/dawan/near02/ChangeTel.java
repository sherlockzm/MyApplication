package com.example.dawan.near02;

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
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;

/**
 * Created by dawan on 2016/4/28.
 */
public class ChangeTel extends AppCompatActivity {

    ImageButton btn_ver_2;
    ImageButton btn_ver_4;
    ImageButton btn_summit_2;

    EditText edt_newTelNum;

    EditText edt_oldVer;
    EditText edt_newVer;
    String newTel;

    private final ChangeTel THISCONTEXT = ChangeTel.this;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changetel);


        edt_oldVer = (EditText)findViewById(R.id.edt_oldTel_ver);
        edt_newVer = (EditText)findViewById(R.id.edt_newtelVer);

        edt_newTelNum = (EditText)findViewById(R.id.edt_change_newNumber);

        btn_ver_2 = (ImageButton)findViewById(R.id.btn_change_tel_getVerOld);
        btn_summit_2 = (ImageButton)findViewById(R.id.btn_change_tel);

        btn_ver_4 = (ImageButton)findViewById(R.id.btn_change_tel_getVerNew);

        final String tel = (String) BmobUser.getObjectByKey(THISCONTEXT, "mobilePhoneNumber");


        btn_ver_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Function().verMobile(THISCONTEXT, tel);

            }
        });

        btn_ver_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CheckInput().checkMobile(THISCONTEXT,edt_newTelNum);
                newTel = edt_newTelNum.getText().toString();
                new Function().verMobile(THISCONTEXT,newTel);
            }
        });

        btn_summit_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobSMS.verifySmsCode(THISCONTEXT, tel, edt_oldVer.getText().toString(), new VerifySMSCodeListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null){
                            Log.e("Ver","原手机验证成功。");
                            BmobSMS.verifySmsCode(THISCONTEXT, newTel, edt_newVer.getText().toString(), new VerifySMSCodeListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        Log.e("Ver", "新手机验证成功。");
                                        User cUser = new User();
                                        cUser.setMobilePhoneNumber(newTel);
                                        User myUser = BmobUser.getCurrentUser(THISCONTEXT,User.class);
                                        cUser.update(THISCONTEXT, myUser.getObjectId(), new UpdateListener() {
                                            @Override
                                            public void onSuccess() {
                                                Toast.makeText(THISCONTEXT,"手机号码更新成功。",Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onFailure(int i, String s) {
                                                Toast.makeText(THISCONTEXT,"手机号码更新失败，请重试。",Toast.LENGTH_SHORT).show();
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
}
