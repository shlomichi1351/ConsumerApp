package com.example.consumer_app.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.consumer_app.Model.Parcel;
import com.example.consumer_app.Model.ParcelRepository;
import com.example.consumer_app.R;
import com.example.consumer_app.ui.SignUp.signup;

import java.util.List;

public class login extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        viewModelLogin viewModelLogin = ViewModelProviders.of(this).get(com.example.consumer_app.ui.login.viewModelLogin.class);

        getSupportActionBar().hide();
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        final TextView textView = findViewById(R.id.sup);
        textView.setTypeface(Typeface.DEFAULT);


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(login.this, signup.class);
                startActivity(it);
            }
        });

        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    // When the user clicks the Button
                    case MotionEvent.ACTION_DOWN:
                        textView.setTypeface(Typeface.DEFAULT_BOLD);
                        break;

                    // When the user releases the Button
                    case MotionEvent.ACTION_UP:
                        textView.setTypeface(Typeface.DEFAULT);
                        break;
                }
                return false;
            }
        });


        Parcel parcel=new Parcel(Parcel.Type.LargePackage,false,3.5,"haifa","jerus","moshe","0584647888","57750");
        viewModelLogin.insert(parcel);
        final TextView parc=findViewById(R.id.parcel);
        viewModelLogin.getAllparcels().observe(this, new Observer<List<Parcel>>() {
            @Override
            public void onChanged(List<Parcel> parcels) {
                parc.setText(parcels.size() + "");
            }
        });


        // ImageView imageView = findViewById(R.id.img);
        //Glide.with(this).load(R.drawable.day).into(imageView);

//        Calendar c = Calendar.getInstance();
//        TextView title=findViewById(R.id.title_login);
//        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
//        if (timeOfDay >= 0 && timeOfDay < 12) {
//            title.setText("בוקר טוב!");
//            Toast.makeText(this, "Good Morning", Toast.LENGTH_SHORT).show();
//            // Glide.with(this).load(R.drawable.day).into(imageView);
//        } else if (timeOfDay >= 12 && timeOfDay < 16) {
//            title.setText("צהריים טובים!");
//
//            Toast.makeText(this, "Good Afternoon", Toast.LENGTH_SHORT).show();
//            // Glide.with(this).load(R.drawable.day).into(imageView);
//        } else if (timeOfDay >= 16 && timeOfDay < 21) {
//            title.setText("ערב טוב!");
//
//            Toast.makeText(this, "Good Evening", Toast.LENGTH_SHORT).show();
//            // Glide.with(this).load(R.drawable.night).into(imageView);
//        } else if (timeOfDay >= 21 && timeOfDay < 24) {
//            title.setText("לילה טוב!");
//            Toast.makeText(this, "Good Night", Toast.LENGTH_SHORT).show();
//            // Glide.with(this).load(R.drawable.night).into(imageView);
//        }

        final TextView pss = findViewById(R.id.pswd);
        final TextView sendpss = findViewById(R.id.sendpassword);
        sendpss.setTypeface(Typeface.DEFAULT);

        sendpss.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    // When the user clicks the Button
                    case MotionEvent.ACTION_DOWN:
                        sendpss.setTypeface(Typeface.DEFAULT_BOLD);
                        break;

                    // When the user releases the Button
                    case MotionEvent.ACTION_UP:
                        sendpss.setTypeface(Typeface.DEFAULT);
                        break;
                }
                return false;
            }
        });

        sendpss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pss.setVisibility(View.VISIBLE);
                //btn.setText("Sign In!");
                new CountDownTimer(30000, 1000)
                {
                    public void onTick(long millisUntilFinished)
                    {
                        //remember to check what the user choose!!!!!!!!!!!
                        sendpss.setEnabled(false);
                        sendpss.setTypeface(null, Typeface.NORMAL);
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