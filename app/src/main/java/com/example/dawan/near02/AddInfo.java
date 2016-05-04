package com.example.dawan.near02;

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
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;

/**
 * Created by dawan on 2016/4/28.
 */
public class AddInfo extends AppCompatActivity {


    EditText edt_name;
    EditText edt_id;
    EditText edt_payAccount;
    EditText edt_addVer;
    ImageButton btn_ver_3;
    ImageButton btn_summit_3;

    private final AddInfo THISCONTEXT  = AddInfo.this;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addinfo);

        edt_name = (EditText)findViewById(R.id.edt_add_realName);
        edt_id = (EditText)findViewById(R.id.edt_add_realID);
        edt_payAccount = (EditText)findViewById(R.id.edt_add_payAccount);
        edt_addVer = (EditText)findViewById(R.id.edt_add_ver);

        btn_ver_3 = (ImageButton)findViewById(R.id.btn_add_ID_getVer);
        btn_summit_3 = (ImageButton)findViewById(R.id.btn_addID);

        final String tel = (String) BmobUser.getObjectByKey(THISCONTEXT, "mobilePhoneNumber");


        btn_ver_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tel == null) {
                    Toast.makeText(THISCONTEXT, "请先登陆。", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(THISCONTEXT, Start_Activity.class);
                    startActivity(intent);
                } else {
                    BmobSMS.requestSMSCode(THISCONTEXT, tel, "短信模板", new RequestSMSCodeListener() {
                        @Override
                        public void done(Integer integer, BmobException e) {
                            if (e == null){
                                Toast.makeText(THISCONTEXT,"验证码已发送，请查收。",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(THISCONTEXT,"验证码发送失败，请重试。",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        btn_summit_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobSMS.verifySmsCode(THISCONTEXT, tel, edt_addVer.getText().toString(), new VerifySMSCodeListener() {
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

                            User curUser = BmobUser.getCurrentUser(THISCONTEXT,User.class);

                            user.update(THISCONTEXT, curUser.getObjectId(), new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(THISCONTEXT,"用户信息更新成功。",Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    Toast.makeText(THISCONTEXT,"更新失败，请重试。",Toast.LENGTH_SHORT).show();
                                }
                            });


                        }else {
                            //验证不通过
                            Toast.makeText(THISCONTEXT,"验证码错误，请重新输入。",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}
