package com.mac.ekchitthi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

;import com.mac.ekchitthi.login.PhoneNumber_Activity;

public class Splash_Screen_Activity extends AppCompatActivity {


    private static int time = 200;
    private ImageView Splash_image;
    private Animation fade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        try {
            Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.status));
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        Splash_image = findViewById(R.id.splash_image);

        fade = AnimationUtils.loadAnimation(Splash_Screen_Activity.this, R.anim.fade_in);
        Splash_image.setAnimation(fade);
        getSplash();

    }

    private void getSplash() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash_Screen_Activity.this, PhoneNumber_Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in,R.anim.zoom_in);
                finish();

            }
        }, time);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}