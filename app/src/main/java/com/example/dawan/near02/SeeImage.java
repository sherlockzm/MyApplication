package com.example.dawan.near02;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by dawan on 2016/5/4.
 */
public class SeeImage extends AppCompatActivity {

    ImageView seeImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seeimage);

        seeImage = (ImageView) findViewById(R.id.seeImage);

        String path = getIntent().getStringExtra("Path");

        Log.e("Path",path);
    }
}
