package com.neverim.ubergarage;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
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

public class RegisterActivity extends AppCompatActivity implements Animation.AnimationListener {
    final int min = 1;
    final int max = 5;
    final int random = new Random().nextInt((max - min) + 1) + min;

    private EditText usernameText, passwordText, passwordTextConfirm, emailText;
    private Handler handler = new Handler();
    private Timer timer = new Timer();
    private ImageView register_background;
    private Animation fadeIn, fadeOut;
    private Button register_btn;
    private boolean faded = false;
    private int currentImageId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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

        register_background = findViewById(R.id.register_background);
        register_btn = findViewById(R.id.button_reg_register);
        changeBgImage(random);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.postDelayed(new Runnable() {
                    public void run() {
                        register_background.clearAnimation();
                        faded = fadeBgImage(faded);
                    }
                }, getAnimationDuration());
            }
        }, 0, 2500);

        usernameText = findViewById(R.id.text_reg_username);
        passwordText = findViewById(R.id.text_reg_pass);
        passwordTextConfirm = findViewById(R.id.text_reg_pass_confirm);
        emailText = findViewById(R.id.text_reg_email);

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameText.getText().toString();
                String password = passwordText.getText().toString();
                String passwordConfirm = passwordTextConfirm.getText().toString();
                String email = emailText.getText().toString();
                if (username.length() < 8) {
                    Toast.makeText(getBaseContext(), "Username must be at least 8 characters long.", Toast.LENGTH_SHORT).show();
                }
                else if (password.length() < 8) {
                    Toast.makeText(getBaseContext(), "Password must be at least 8 characters long.", Toast.LENGTH_SHORT).show();
                }
                else if (!password.equals(passwordConfirm)) {
                    Toast.makeText(getBaseContext(), "Passwords do not match.", Toast.LENGTH_SHORT).show();
                }
                else if (!isValidEmail(email)) {
                    Toast.makeText(getBaseContext(), "You have entered an invalid email.", Toast.LENGTH_SHORT).show();
                }
                else {
                    final UserEntity user = new UserEntity();
                    List<UserEntity> users = db.userDao().getAllUsers();

                    boolean exists = false;
                    user.email = email;
                    user.username = username;
                    user.password = password;

                    for (UserEntity u:users) {
                        if (u.username.equals(username)) {
                            exists = true;
                        }
                    }
                    if (exists) {
                        Toast.makeText(getBaseContext(), "Account with this username already exists.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        db.userDao().insertUser(user);
                        // My version of offline sessions
                        Constants.setUserId(db.userDao().getUserByUsername(username).userId);
                        Toast.makeText(getBaseContext(), "Account has been registered.", Toast.LENGTH_SHORT).show();
                        Intent intentSearch = new Intent(RegisterActivity.this, SearchActivity.class);
                        startActivity(intentSearch);
                        finish();
                    }
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
            register_background.startAnimation(fadeIn);
            fadeStatus = false;
        } else {
            register_background.startAnimation(fadeOut);
            fadeStatus = true;
        }
        return fadeStatus;
    }

    public void changeBgImage(int phase) {
        if (phase == 1) {
            register_background.setImageDrawable(getDrawable(R.drawable.door2));
            return;
        }
        if (phase == 2) {
            register_background.setImageDrawable(getDrawable(R.drawable.door3));
            return;
        }
        if (phase == 3) {
            register_background.setImageDrawable(getDrawable(R.drawable.door4));
            return;
        }
        if (phase == 4) {
            register_background.setImageDrawable(getDrawable(R.drawable.door5));
            return;
        } else {
            register_background.setImageDrawable(getDrawable(R.drawable.door1));
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

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
