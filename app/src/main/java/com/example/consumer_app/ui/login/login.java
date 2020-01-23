package com.example.consumer_app.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.consumer_app.Model.Firebase_DBManager_User;
import com.example.consumer_app.Model.User;
import com.example.consumer_app.ui.UserMenu;
import com.example.consumer_app.R;
import com.example.consumer_app.ui.SignUp.signup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;


public class login extends AppCompatActivity implements TextWatcher {

    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 0;
    private FirebaseAuth mAuth;

    EditText phone;
    EditText passwordUser;
    private static login inst;
    Button singIn;
    TextView wrongPhone;
    TextView sendpss;
    String mVerificationId;
    boolean user_exist=false;

    public static login instance()
    {
        return inst;
    }

    @Override
    protected void onStart() {
        super.onStart();
        inst = this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        viewModelLogin viewModelLogin = ViewModelProviders.of(this).get(com.example.consumer_app.ui.login.viewModelLogin.class);

        passwordUser = findViewById(R.id.password);
        sendpss = findViewById(R.id.sendpassword);
        phone = findViewById(R.id.phone_login);
        wrongPhone = findViewById(R.id.wrong_phone);
        phone.addTextChangedListener(this);
        mAuth = FirebaseAuth.getInstance();
        singIn=findViewById(R.id.singIn);

        singIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    verifyVerificationCode(passwordUser.getText().toString());

                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), e.toString(),Toast.LENGTH_SHORT).show();
                    //EditText code = findViewById(R.id.code);
                }
            }
        });

        if (mAuth.getCurrentUser() == null)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {

                } else {

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
                }

            }
        }
        getSupportActionBar().hide();
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

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


        /*Parcel parcel=new Parcel(Parcel.Type.LargePackage,false,3.5,"haifa","jerus","moshe","0584647888","07508");
        viewModelLogin.insert(parcel);
        final TextView parc=findViewById(R.id.parcel);

        viewModelLogin.getAllparcels().observe(this, new Observer<List<Parcel>>()
        {
            @Override
            public void onChanged(List<Parcel> parcels)
            {
                parc.setText(parcels.get(0).getStatus() + "");
            }
        });*/


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


        sendpss.setTypeface(Typeface.DEFAULT);

        sendpss.setOnTouchListener(new View.OnTouchListener()
        {
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
                try {
                    Query query =  Firebase_DBManager_User.usersRef
                            .orderByChild("phoneNumber");
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()){
                                if(child.getValue(User.class).getPhoneNumber().equals(phone.getText().toString())) {
                                    user_exist = true;
                                    break;
                                }
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    if(!user_exist)
                        throw new Exception("המשתמש לא קיים");
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                passwordUser.setVisibility(View.VISIBLE);
                //btn.setText("Sign In!");
                new CountDownTimer(30000, 1000) {
                    public void onTick(long millisUntilFinished) {
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


                if (mAuth.getCurrentUser() == null) {
                    try {
                        String t = phone.getText().toString();
                        sendVerificationCode(t);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(getApplicationContext(), mAuth.getCurrentUser().getPhoneNumber(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void sendVerificationCode(String mobile)
    {
        if(mobile.length() == 10 && mobile.toCharArray()[0] == '0')
        {
            mobile = mobile.substring(1);
        }
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+972" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
    {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();

        }



        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
        }
    };

    private void verifyVerificationCode(String code) {
        try{
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            signInWithPhoneAuthCredential(credential);

        }catch (Exception e){

            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    // Sign in success, update UI with the signed-in user's information
                    //FirebaseUser user = task.getResult().getUser();
                    // String phone_number=user.getPhoneNumber();
                    Intent it= new Intent(login.this, UserMenu.class);
                    startActivity(it);

                }
                else {
                    // Sign in failed, display a message and update the UI
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(getApplicationContext(),"סיסמא שגויה",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    public void setEditTextCode(String sms) {

        passwordUser.setText(sms);
        try {
            verifyVerificationCode(sms);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!(phone.getText().toString().matches("05[0-9]{8}"))) {
            wrongPhone.setVisibility(View.VISIBLE);
            sendpss.setVisibility(View.GONE);
            passwordUser.setVisibility(View.GONE);
        }
        else
        {
            wrongPhone.setVisibility(View.GONE);
            sendpss.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECEIVE_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {


                    Toast.makeText(this, "לא ניתן לקבל את הסיסמא. נא לאפשר קבלת הודעות בהגדרות המכשיר", Toast.LENGTH_LONG).show();

                }

        }
    }
}