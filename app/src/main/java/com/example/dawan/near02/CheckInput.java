package com.example.dawan.near02;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobUser;

/**
 * Created by dawan on 2016/2/21.
 */
public class CheckInput {

    public CheckInput() {
    }

    public boolean CheckInputHelp(final Context context, final EditText editText, final int maxLength) {

                if (editText.getText().length() > maxLength || editText.getText().length() == 0) {
//                    button.setClickable(false);
                    Toast.makeText(context, "字符超出限制或未输入", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                   return true;
                }
    }

    public boolean checkPwd(Context context,EditText editText1,EditText editText2,Integer minLength,Integer maxLength){
        String edt1 = UserRegister.MD5(editText1.getText().toString());
        String edt2 = UserRegister.MD5(editText2.getText().toString());
        if (!edt1.equals(edt2)){
            Toast.makeText(context,"两次输入的密码不一样.",Toast.LENGTH_SHORT).show();
            editText1.setText("");
            editText2.setText("");
            editText1.setFocusable(true);
            editText1.setFocusableInTouchMode(true);
            editText1.requestFocus();
            return false;
        }else if (editText1.getText().length() < minLength || editText1.getText().length() > maxLength){
            Toast.makeText(context,"密码长度不符合要求。",Toast.LENGTH_SHORT).show();
            editText1.setText("");
            editText2.setText("");
            editText1.setFocusable(true);
            editText1.setFocusableInTouchMode(true);
            editText1.requestFocus();
            return false;
        } else {
            return true;
        }

    }

    public void getFocus(EditText editText) {

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();

    }

    public Boolean checkHelperAndRequester(String requestId, String helperId) {

        if (requestId.equals(helperId)) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean checkLogin(Context context, java.lang.Class<User> clazz) {
        if (BmobUser.getCurrentUser(context, clazz) != null) {
            return true;
        } else {
            Toast.makeText(context, "请先登录并确认已打开GPS", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(context, Login.class);
//            context.startActivity(intent);
            return false;
        }

    }

    public Boolean checkLogin(Context context, java.lang.Class<User> clazz, Boolean boo) {
        if (BmobUser.getCurrentUser(context, clazz) != null) {
            return true;
        } else if (BmobUser.getCurrentUser(context, clazz) == null && !boo) {
            return false;
        } else {
            Toast.makeText(context, "请先登录并确认已打开GPS", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(context, Login.class);
//            context.startActivity(intent);
            return false;
        }

    }

    public void checkPay(final Context context, final EditText editText, final Double pay, final Button button) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (pay < 0) {
                    button.setClickable(false);
                    Toast.makeText(context, "报酬不能少于0", Toast.LENGTH_SHORT).show();
                } else {

                    button.setClickable(true);

                }

            }
        });
    }

    public void checkMobile(final Context context, final EditText editText) {


        String string = editText.getText().toString();

        String regExp = "^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$";

        Pattern p = Pattern.compile(regExp);

        Matcher m = p.matcher(string);

        if (!m.find()) {

            Toast.makeText(context, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
        }
    }


    public void checkMobile(final Context context, final EditText editText, final Button button) {

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                              @Override
                                              public void onFocusChange(View v, boolean hasFocus) {

                                                  String string = editText.getText().toString();

                                                  String regExp = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";

                                                  Pattern p = Pattern.compile(regExp);

                                                  Matcher m = p.matcher(string);

                                                  if (!m.find()) {
                                                      button.setClickable(false);
                                                      Toast.makeText(context, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                                                  } else {
                                                      button.setClickable(true);
                                                  }
                                              }


                                          }


        );
    }

    public void checkMobile(final Context context, final EditText editText, final Button button1, final Button button2) {

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                              @Override
                                              public void onFocusChange(View v, boolean hasFocus) {

                                                  String string = editText.getText().toString();

                                                  String regExp = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";

                                                  Pattern p = Pattern.compile(regExp);

                                                  Matcher m = p.matcher(string);

                                                  if (!m.find()) {
                                                      button1.setClickable(false);
                                                      button2.setClickable(false);
                                                      Toast.makeText(context, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                                                  } else {
                                                      button1.setClickable(true);
                                                      button2.setClickable(true);
                                                  }
                                              }


                                          }


        );
    }
}



