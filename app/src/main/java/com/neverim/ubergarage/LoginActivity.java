package com.neverim.ubergarage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class LoginActivity extends AppCompatActivity implements Animation.AnimationListener {
    final int min = 1;
    final int max = 5;
    final int random = new Random().nextInt((max - min) + 1) + min;

    private EditText usernameText, passwordText;
    private Handler handler = new Handler();
    private Timer timer = new Timer();
    private ImageView login_background;
    private Animation fadeIn, fadeOut;
    private Button login_btn;
    private boolean faded = false;
    private int currentImageId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "userDao").allowMainThreadQueries().build();

        fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);

        fadeIn.setAnimationListener(this);
        fadeOut.setAnimationListener(this);

        login_background = findViewById(R.id.login_background);
        login_btn = findViewById(R.id.button_login);
        changeBgImage(random);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.postDelayed(new Runnable() {
                    public void run() {
                        login_background.clearAnimation();
                        faded = fadeBgImage(faded);
                    }
                }, getAnimationDuration());
            }
        }, 0, 2500);

        // Get entered password and username
        usernameText = findViewById(R.id.text_login_username);
        passwordText = findViewById(R.id.text_login_password);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameText.getText().toString();
                String password = passwordText.getText().toString();

                if (username.length() >= 8 || password.length() >= 8) {
                    UserEntity user = db.userDao().getUserByUsername(username);

                    if (user != null && user.username.equals(username)) {
                        if (user.password.equals(password)) {
                            // My version of offline sessions
                            Constants.setUserId(user.userId);
                            Toast.makeText(getBaseContext(), "Successfully logged in!", Toast.LENGTH_SHORT).show();
                            Intent searchIntent = new Intent(LoginActivity.this, SearchActivity.class);
                            startActivity(searchIntent);
                            finish();
                        } else {
                            Toast.makeText(getBaseContext(), "Check your credentials.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "User not found.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getBaseContext(), "User not found.", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
            login_background.startAnimation(fadeIn);
            fadeStatus = false;
        } else {
            login_background.startAnimation(fadeOut);
            fadeStatus = true;
        }
        return fadeStatus;
    }

    public void changeBgImage(int phase) {
        if (phase == 1) {
            login_background.setImageDrawable(getDrawable(R.drawable.door2));
            return;
        }
        if (phase == 2) {
            login_background.setImageDrawable(getDrawable(R.drawable.door3));
            return;
        }
        if (phase == 3) {
            login_background.setImageDrawable(getDrawable(R.drawable.door4));
            return;
        }
        if (phase == 4) {
            login_background.setImageDrawable(getDrawable(R.drawable.door5));
            return;
        } else {
            login_background.setImageDrawable(getDrawable(R.drawable.door1));
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
