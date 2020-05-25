package com.neverim.ubergarage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements Animation.AnimationListener {

    final int min = 1;
    final int max = 5;
    final int random = new Random().nextInt((max - min) + 1) + min;

    private Handler handler = new Handler();
    private Timer timer = new Timer();
    private ImageView main_background;
    private Animation fadeIn, fadeOut;
    private Button login_btn, register_btn;
    private boolean faded = false, exitPressedOnce = false;
    private int currentImageId = 0;
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);

        fadeIn.setAnimationListener(this);
        fadeOut.setAnimationListener(this);

        main_background = findViewById(R.id.main_background);
        login_btn = findViewById(R.id.button_login);
        register_btn = findViewById(R.id.button_register);
        changeBgImage(random);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.postDelayed(new Runnable() {
                    public void run() {
                        main_background.clearAnimation();
                        faded = fadeBgImage(faded);
                    }
                }, getAnimationDuration());
            }
        }, 0, 2500);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(loginIntent);
            }
        });

        // Export database
        //Utils.exportDB(this);
    }

    @Override
    public void onBackPressed() {
        if (mBackPressed + Constants.BACK_TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        }
        else { Toast.makeText(getBaseContext(), "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        }
        mBackPressed = System.currentTimeMillis();
    }


    public long getAnimationDuration() {
        long duration = 2500;
        if (fadeIn.hasStarted()) {
            duration = fadeIn.getDuration();
        }
        if (fadeOut.hasStarted()) {
            duration = fadeOut.getDuration();
        }
        return duration;
    }

    public boolean fadeBgImage(boolean fadeStatus) {
        if (fadeStatus) {
            main_background.startAnimation(fadeIn);
            fadeStatus = false;
        } else {
            main_background.startAnimation(fadeOut);
            fadeStatus = true;
        }
        return fadeStatus;
    }

    public void changeBgImage(int phase) {
        if (phase == 1) {
            main_background.setImageDrawable(getDrawable(R.drawable.door2));
            return;
        }
        if (phase == 2) {
            main_background.setImageDrawable(getDrawable(R.drawable.door3));
            return;
        }
        if (phase == 3) {
            main_background.setImageDrawable(getDrawable(R.drawable.door4));
            return;
        }
        if (phase == 4) {
            main_background.setImageDrawable(getDrawable(R.drawable.door5));
            return;
        }
        if (phase == 5) {
            main_background.setImageDrawable(getDrawable(R.drawable.door1));
            currentImageId = 0;
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == fadeOut) {
            currentImageId++;
            changeBgImage(currentImageId);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
