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
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.consumer_app.Model.Firebase_DBManager_User;
import com.example.consumer_app.Model.User;
import com.example.consumer_app.R;
import com.example.consumer_app.ui.MapsActivity.MapsActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class signup extends AppCompatActivity implements TextWatcher {
    private CircleImageView ProfileImage;
    private static final int PICK_IMAGE = 1;
    Uri imageUri;
    public static User user;
    Button nxt;
    EditText fname;
    EditText lname;
    EditText phone;
    TextView warning;
    ScrollView scrollView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);





        nxt = findViewById(R.id.next);
        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        ProfileImage = (CircleImageView) findViewById(R.id.Profile_Image);
        warning = findViewById(R.id.error_messege);
        phone = findViewById(R.id.phone_number);
        scrollView = findViewById(R.id.scroll);


        user=new User();
        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              func();
            }
        });


        getSupportActionBar().hide();
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);



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

        fname.addTextChangedListener(this);
        phone.addTextChangedListener(this);

        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(signup.this, MapsActivity.class);
                it.putExtra("fname", fname.getText().toString());
                it.putExtra("lname", lname.getText().toString());
                it.putExtra("phone", phone.getText().toString());
                it.putExtra("imageUri", imageUri.toString());

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
    public void afterTextChanged(Editable s)
    {
        if (fname.getText().toString().isEmpty()) {
            warning.setVisibility(View.VISIBLE);
            nxt.setEnabled(false);
            nxt.setTextColor(Color.parseColor("#796D6D"));
            warning.setText("  נא הכנס שם פרטי");

        }
        else if (lname.getText().toString().isEmpty()) {
            warning.setVisibility(View.VISIBLE);
            nxt.setEnabled(false);
            nxt.setTextColor(Color.parseColor("#796D6D"));
            warning.setText("  נא הכנס שם משפחה");

        } else if (!(phone.getText().toString().matches("05[0-9]{8}"))) {
            warning.setVisibility(View.VISIBLE);
            warning.setText("  נא הכנס מספר טלפון תקין!");
            nxt.setEnabled(false);
            nxt.setTextColor(Color.parseColor("#796D6D"));


        } else {
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
            warning.setVisibility(View.GONE);
            nxt.setEnabled(true);
            nxt.setTextColor(Color.parseColor("#000000"));
            user.setFirstName(lname.getText().toString());
            user.setLastName(fname.getText().toString());
            user.setPhoneNumber(phone.getText().toString());

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

    public static User UserDetails()
    {
        return user;
    }

}