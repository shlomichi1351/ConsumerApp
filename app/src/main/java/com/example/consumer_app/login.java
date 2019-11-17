package com.example.consumer_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Calendar;

public class login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        TextView textView = findViewById(R.id.sup);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(login.this, signup.class);
                startActivity(it);
            }
        });


        ImageView imageView = findViewById(R.id.img);
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        Glide.with(this).load(R.drawable.day).into(imageView);
        if (timeOfDay >= 0 && timeOfDay < 12) {
            Toast.makeText(this, "Good Morning", Toast.LENGTH_SHORT).show();
            // Glide.with(this).load(R.drawable.day).into(imageView);
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            Toast.makeText(this, "Good Afternoon", Toast.LENGTH_SHORT).show();
            // Glide.with(this).load(R.drawable.day).into(imageView);
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            Toast.makeText(this, "Good Evening", Toast.LENGTH_SHORT).show();
            // Glide.with(this).load(R.drawable.night).into(imageView);
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            Toast.makeText(this, "Good Night", Toast.LENGTH_SHORT).show();
            // Glide.with(this).load(R.drawable.night).into(imageView);
        }

        final TextView pss = findViewById(R.id.pswd);
        final TextView sendpss = findViewById(R.id.sendpassword);
        //sendpss.setTextColor(Color.parseColor("#888888"));
        sendpss.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(sendpss.isInTouchMode())
                    sendpss.setTypeface(null, Typeface.BOLD);
                else
                    sendpss.setTypeface(null, Typeface.NORMAL);
                return false;
            }
        });

        sendpss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pss.setVisibility(View.VISIBLE);
                //btn.setText("Sign In!");
                new CountDownTimer(30000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        //remember to check what the user choose!!!!!!!!!!!
                        sendpss.setEnabled(false);
                        sendpss.setText("We've sent you Email/SMS with the password.\nPlease enter the code below.\nYou will be able to request the password again in " + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        sendpss.setEnabled(true);
                        sendpss.setText("send me the password again!");
                    }
                }.start();
            }
        });
    }


}