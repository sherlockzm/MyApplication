package com.example.dawan.near02;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;

import java.security.MessageDigest;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by dawan on 2016/2/19.
 */
public class UserRegister extends AppCompatActivity {

    private Button btn_getVerification;
    private Button btn_register;
    private FormEditText edt_mobileNumber;
    private EditText edt_verification;
    private FormEditText edt_name;
    private EditText edt_pwd1;
    private EditText edt_pwd2;

    private User user_reg;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        btn_getVerification = (Button) findViewById(R.id.btn_getVerification);
        btn_register = (Button) findViewById(R.id.btn_reg);
        edt_name = (FormEditText) findViewById(R.id.edt_name);
        edt_pwd1 = (EditText) findViewById(R.id.edt_pwd);
        edt_pwd2 = (EditText) findViewById(R.id.edt_pwd_repeat);
        edt_mobileNumber = (FormEditText) findViewById(R.id.edt_mobileNumber);
        edt_verification = (EditText) findViewById(R.id.edt_verification);

        btn_getVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String telNum = edt_mobileNumber.getText().toString();
                BmobSMS.requestSMSCode(UserRegister.this, telNum, "测试短信", new RequestSMSCodeListener() {
                    @Override
                    public void done(Integer integer, BmobException e) {
                        if (e == null) {
                            Log.e("Smile", "短信id" + integer);
                        }
                    }
                });
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先验证有木有填写验证码,验证两次密码是否一致
                String name = edt_name.getText().toString();
                String pwd1 = MD5(edt_pwd1.getText().toString());
                String pwd2 = MD5(edt_pwd2.getText().toString());
                String tel = edt_mobileNumber.getText().toString();
                String ver = edt_verification.getText().toString();
                FormEditText inputStr[] = new FormEditText[]{edt_name, edt_mobileNumber};
                if (!(new Login().verificationInput(inputStr))) {
                    Log.e("INPUT", "WRONG");
                    Toast.makeText(UserRegister.this,"输入的信息有误。",Toast.LENGTH_SHORT).show();
                } else if (pwd1!=pwd2){
                    Log.e("INPUTPASSWORD", "unequal!");
                    Toast.makeText(UserRegister.this,"密码不一致。",Toast.LENGTH_SHORT).show();
                }else{
                    user_reg = new User();

                    user_reg.setUsername(name);
                    user_reg.setPassword(MD5(pwd1));
                    user_reg.setMobilePhoneNumber(tel);

                    user_reg.signOrLogin(UserRegister.this, ver, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            Log.e("LOGIN", "LOGINED");
                            finish();
                        }

                        @Override
                        public void onFailure(int i, String s) {

                            Log.e("Login","Fail"+s);
                        }
                    });
                }
            }
        });

    }


    //MD5加密
    public static String MD5(String str) {

        MessageDigest md5 = null;

        try {

            md5 = MessageDigest.getInstance("MD5");

        } catch (Exception e) {

            e.printStackTrace();

            return "";

        }

        char[] charArray = str.toCharArray();

        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {

            byteArray[i] = (byte) charArray[i];

        }

        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();

        for (int i = 0; i < md5Bytes.length; i++)

        {

            int val = ((int) md5Bytes[i]) & 0xff;

            if (val < 16)

            {

                hexValue.append("0");

            }

            hexValue.append(Integer.toHexString(val));

        }

        return hexValue.toString();

    }

}
