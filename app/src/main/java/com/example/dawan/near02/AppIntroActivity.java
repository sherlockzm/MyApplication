package com.example.dawan.near02;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.github.paolorotolo.appintro.AppIntro;

public class AppIntroActivity extends AppIntro {

    @Override
    public void init(Bundle savedInstanceState) {

        addSlide(SlideFragment.newInstance(R.layout.intro1));
        addSlide(SlideFragment.newInstance(R.layout.intro2));
        addSlide(SlideFragment.newInstance(R.layout.intro3));
        setBarColor(getResources().getColor(R.color.green));
        setSeparatorColor(getResources().getColor(R.color.colorAccent));
        setVibrateIntensity(30);
        setSkipText(getString(R.string.skip));
        setDoneText(getString(R.string.enter));
    }

    private void startMain(){
        Intent intent = new Intent(AppIntroActivity.this,Start_Activity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onSkipPressed() {
        Log.e("P","onSkipPressed");
        startMain();
    }

    @Override
    public void onDonePressed() {
        Log.e("P","onDonePressed");
        startMain();
    }

    @Override
    public void onSlideChanged() {
        Log.e("P","onSlideChanged");
    }

    @Override
    public void onNextPressed() {
        Log.e("P","onNextPressed");
    }
}
