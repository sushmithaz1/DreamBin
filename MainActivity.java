package com.example.vegies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.example.vegies.activities.MainUserActivity;

public class MainActivity extends AppCompatActivity {
    Animation rotateAnimation;
   ImageView imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView2=(ImageView) findViewById(R.id.imageView2);

        rotateAnimation();

    }

    private void rotateAnimation() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rotateAnimation= AnimationUtils.loadAnimation(MainActivity.this,R.anim.rotate);
                imageView2.startAnimation(rotateAnimation);
            }
        },50);

    }
}
