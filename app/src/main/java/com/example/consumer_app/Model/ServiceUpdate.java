package com.example.consumer_app.Model;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

//import com.ddc.MainActivity;
//import com.ddc.Model.Parcel.Parcel;
//import com.ddc.Model.Parcel.ParcelRepository;
//import com.ddc.R;
import com.example.consumer_app.Model.Parcel;
import com.example.consumer_app.Model.ParcelRepository;
import com.example.consumer_app.R;
import com.example.consumer_app.ui.UserMenu;
import com.example.consumer_app.ui.home.HomeViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

//import static com.ddc.Model.Parcel.Parcel_Status.CollectionOffered;
//import static com.ddc.Model.Parcel.Parcel_Status.Registered;

public class ServiceUpdate extends LifecycleService {

    ParcelRepository repository;
    LiveData<List<Parcel>> allParcel;
    List<Parcel> parcels;
    private static int ID = 1;
    private static int lastShownNotificationId = 0;
    ArrayList<Parcel> roomUpdateParcels;
    private FirebaseAuth mAuth;
    String temp_phone_user;
    FirebaseUser userFireBase;
    ParcelRepository homeViewModel;


    @Override
    public void onCreate() {
        super.onCreate();


        Firebase_DBManager_Parcel.notifyToParcelList(new NotifyDataChange<List<Parcel>>()
        {
            @Override
            public void OnDataChanged(List<Parcel> obj)
            {
                roomUpdateParcels=new ArrayList<Parcel>();

                if(obj != null )
                {
                    for (Parcel p : obj) {
                         roomUpdateParcels.add(p);
                        }
                    }



            }

            @Override
            public void onFailure(Exception exception) {

            }
        });

        //mAuth = FirebaseAuth.getInstance();
        //userFireBase = mAuth.getCurrentUser();
        //homeViewModel = new ParcelRepository(getApplication());


        int i =0;
        repository = new ParcelRepository(getApplication());
        allParcel = repository.getAllParcels();
        for (Parcel parcel : roomUpdateParcels) {
           if(parcel != allParcel.getValue())
               roomUpdateParcels.add(parcel);
           else
               Toast.makeText(getBaseContext(),"החבילה כבר נמצאת בroom", Toast.LENGTH_LONG).show();
        }
        /*parcels = allParcel.getValue();

        Firebase_DBManager_Parcel.notifyToParcelList(new NotifyDataChange<List<Parcel>>()
        {
            @Override
            public void OnDataChanged(List<Parcel> obj)
            {


                roomUpdateParcels=new ArrayList<Parcel>();

                if(obj != null )
                {
                    for (Parcel p : obj) {

                        if (p.getRecipientPhoneNumber().equals(temp_phone_user)) {
                            roomUpdateParcels.add(p);
                        }
                    }
                        homeViewModel.deleteAllParcels();
                    for(Parcel p: roomUpdateParcels)
                        homeViewModel.insert(p);
                }

                detectChanges();


            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(getApplicationContext(), "error to get parcel list\n" + exception.toString(), Toast.LENGTH_LONG).show();

            }
        });
        */


        /*
        allParcel.observe(this, new Observer<List<Parcel>>() {
            @Override
            public void onChanged(List<Parcel> parcels) {
                detectChanges();
            }
        });*/
    }



}