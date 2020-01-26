package com.example.consumer_app.ui.takenParcels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TakenParcelsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TakenParcelsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is share fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}