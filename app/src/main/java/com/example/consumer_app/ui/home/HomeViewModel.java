package com.example.consumer_app.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.consumer_app.Model.Firebase_DBManager_User;
import com.example.consumer_app.Model.Parcel;
import com.example.consumer_app.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import  com.example.consumer_app.Model.ParcelRepository;
import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private MutableLiveData<String> mText;
    List<User> friends;

    private ParcelRepository parcelRepository;
    private LiveData<List<Parcel>> allParcels;
    public HomeViewModel(@NonNull Application application) {
        super(application);
        parcelRepository = new ParcelRepository(application);
        allParcels = parcelRepository.getAllParcels();
    }
    public void insert(Parcel parcel) {parcelRepository.insert(parcel);}
    public void update(Parcel parcel) {parcelRepository.update(parcel);}
    public void delete(Parcel parcel) {parcelRepository.delete(parcel);}
    public void deleteAllNotes() { parcelRepository.deleteAllParcels();}
    public LiveData<List<Parcel>> getAllParcel() { return allParcels; }
}
