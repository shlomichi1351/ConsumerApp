package com.example.consumer_app.ui.home;

import androidx.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    List<User> friends;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
        friends= new ArrayList<User>();
    }

    public LiveData<String> getText() {
        return mText;
    }
//    public List<User> getFriendsList(final String userName)
//    {
//        Query query =  Firebase_DBManager_User.usersRef.orderByChild("userName");
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                final User value = dataSnapshot.getValue(User.class);
//                if(value.getUserName()!=userName)
//                    friends.add(value);
//            }
//            return friends;
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
}
