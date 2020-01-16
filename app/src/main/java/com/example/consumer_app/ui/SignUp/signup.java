package com.example.consumer_app.ui.SignUp;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.consumer_app.Model.Parcel;
import com.example.consumer_app.Model.User;
import com.example.consumer_app.R;
import com.example.consumer_app.ui.MapsActivity.MapsActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class signup extends AppCompatActivity implements TextWatcher,Serializable {
    private CircleImageView ProfileImage;
    private static final int PICK_IMAGE = 1;
    Uri imageUri;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        final User user=new User("dan","cohen","dandan","1234","0501234567",new ArrayList<User>());
        ProfileImage = (CircleImageView) findViewById(R.id.Profile_Image);
        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              func();
            }
        });


        getSupportActionBar().hide();
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

//        ImageView imageView = findViewById(R.id.gif_background);
//        Glide.with(this).load(R.drawable.gif_background).into(imageView);


        final TextView back = findViewById(R.id.back);
        //back to login
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //bold the text
        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    // When the user clicks the Button
                    case MotionEvent.ACTION_DOWN:
                        back.setTypeface(Typeface.DEFAULT_BOLD);
                        break;

                    // When the user releases the Button
                    case MotionEvent.ACTION_UP:
                        back.setTypeface(Typeface.DEFAULT);
                        break;
                }
                return false;
            }
        });

        final Button nxt = findViewById(R.id.next);
        final EditText id = findViewById(R.id.id_person);
        final EditText fname = findViewById(R.id.fname);
        final EditText phone = findViewById(R.id.phone_number);
        id.addTextChangedListener(this);
        fname.addTextChangedListener(this);
        phone.addTextChangedListener(this);

        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(signup.this, MapsActivity.class);
                it.putExtra("User", (Serializable) user);
                startActivity(it);
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        final ScrollView scrollView = findViewById(R.id.scroll);

        final Button nxt = findViewById(R.id.next);
        final EditText id = findViewById(R.id.id_person);
        final EditText fname = findViewById(R.id.fname);
        final TextView warning = findViewById(R.id.error_messege);
        final EditText phone = findViewById(R.id.phone_number);

        if (!(id.getText().toString().length() == 9)) {
            warning.setVisibility(View.VISIBLE);
            nxt.setEnabled(false);
            nxt.setTextColor(Color.parseColor("#796D6D"));
            warning.setText("  לא קיימת תעודת זהות עם " + id.getText().length() + " ספרות");

        } else if (fname.getText().toString().isEmpty()) {
            warning.setVisibility(View.VISIBLE);
            nxt.setEnabled(false);
            nxt.setTextColor(Color.parseColor("#796D6D"));
            warning.setText("  נא כתוב את שמך המלא!");

        } else if (!(phone.getText().toString().matches("05[0-9]{8}"))) {
            warning.setVisibility(View.VISIBLE);
            warning.setText("  נא הכנס מספר טלפון תקין!");
            nxt.setEnabled(false);
            nxt.setTextColor(Color.parseColor("#796D6D"));
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
        } else {
            warning.setVisibility(View.GONE);
            nxt.setEnabled(true);
            nxt.setTextColor(Color.parseColor("#000000"));

        }
    }

    public void func()
    {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri resultUri = result.getUri();
            user.setImageLocalUri(resultUri);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                ProfileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}