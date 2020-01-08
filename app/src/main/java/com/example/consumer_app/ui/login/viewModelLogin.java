package com.example.consumer_app.ui.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.consumer_app.Model.Parcel;
import com.example.consumer_app.Model.ParcelRepository;

import java.util.List;

public class viewModelLogin extends AndroidViewModel
{
    ParcelRepository parcelRepository;

    public viewModelLogin(@NonNull Application application)
    {
        super(application);

        parcelRepository= new ParcelRepository(application);

    }

    public void insert(Parcel parcel)
    {
        parcelRepository.insert(parcel);
    }

    public LiveData<List<Parcel>> getAllparcels() { return parcelRepository.getAllParcels(); }

}
