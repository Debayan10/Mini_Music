package com.example.minimusic;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;

public class likedsongs extends AppCompatActivity {
    private LottieAnimationView anilike;
    private boolean isChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likedsongs);
        anilike = findViewById(R.id.like);


        anilike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isChecked) {
                    anilike.setSpeed(1);
                    anilike.playAnimation();
                    isChecked = true;
                } else {
                    anilike.setSpeed(1);
                    anilike.playAnimation();
                    isChecked = true;

                }

            }
        });
    }
}