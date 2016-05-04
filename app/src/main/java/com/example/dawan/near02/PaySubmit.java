package com.example.dawan.near02;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by dawan on 2016/4/29.
 */
public class PaySubmit extends AppCompatActivity {

    private TextView tv_repayment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay);

        tv_repayment = (TextView)findViewById(R.id.repayment);

        String money = getIntent().getStringExtra("MONEY");

        if (money != null){

            tv_repayment.setText(money);
        }

    }
}
