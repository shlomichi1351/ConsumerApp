package com.example.consumer_app.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.consumer_app.Model.Firebase_DBManager_User;
import com.example.consumer_app.Model.User;
import com.example.consumer_app.R;
import com.example.consumer_app.ui.home.HomeFragment;
import com.example.consumer_app.ui.home.HomeViewModel;
import com.example.consumer_app.ui.login.login;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserMenu extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    CircleImageView circleImageView;
    private FirebaseAuth mAuth;
    public static User user;
    FirebaseUser userFireBase;
    String temp_phone_user;
    TextView userName;
    TextView subtitle;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        mAuth = FirebaseAuth.getInstance();
        userFireBase = mAuth.getCurrentUser();

        if (mAuth.getCurrentUser() == null)
        {
            Intent it = new Intent(UserMenu.this, login.class);
            startActivity(it);
            finish();
        }
        else {
            temp_phone_user=userFireBase.getPhoneNumber();
            temp_phone_user=temp_phone_user.substring(4);
            temp_phone_user="0"+temp_phone_user;
            Query query =  Firebase_DBManager_User.usersRef
                    .orderByChild("phoneNumber");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren())
                    {
                        if(child.getValue(User.class).getPhoneNumber().equals(temp_phone_user)) {
                            user = child.getValue(User.class);
                            navigationStart();
                            break;
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }



    }

    public void navigationStart()
    {
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        circleImageView=headerView.findViewById(R.id.image_user_navigation);
        userName=headerView.findViewById(R.id.name_user);
        subtitle=headerView.findViewById(R.id.subtitle_navigation);
        subtitle.setText(user.getAddress());
        userName.setText(user.getFirstName() + " " + user.getLastName());


        if(user.getImageFirebaseUrl() == null)
            Glide.with(getBaseContext())
                    .load(R.drawable.user)
                    .apply(RequestOptions.circleCropTransform())
                    .into(circleImageView);
        else
            Glide.with(getBaseContext())
                    .load(user.getImageFirebaseUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(circleImageView);



        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


    }

    @Override
    public boolean onSupportNavigateUp()
    {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logout_menu:
                if(userFireBase != null) {
                    mAuth.signOut();
                    Intent it = new Intent(UserMenu.this, login.class);
                    startActivity(it);
                    finish();
                }
        }
        return super.onOptionsItemSelected(item);
    }
    public User getUser()
    {

        return user;
    }

    public static Intent newPhoneCallIntent(String phoneNumber){
        Intent callintent = new Intent(Intent.ACTION_DIAL);
        callintent.setData(Uri.parse("tel:"+phoneNumber));
        return callintent;
    }
}
