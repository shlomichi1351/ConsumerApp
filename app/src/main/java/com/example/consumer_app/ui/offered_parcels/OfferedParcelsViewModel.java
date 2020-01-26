package com.example.consumer_app.ui.offered_parcels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OfferedParcelsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public OfferedParcelsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is tools fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}