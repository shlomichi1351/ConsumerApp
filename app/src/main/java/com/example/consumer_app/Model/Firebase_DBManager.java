package com.example.consumer_app.Model;


import androidx.annotation.NonNull;

import com.example.consumer_app.Model.Parcel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Firebase_DBManager {
    static DatabaseReference usersRef;
    static List<User> usereList;
    static List<User> friendsList;

    static {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Users");
        usereList = new ArrayList<User>();
        friendsList=new ArrayList<User>();
    }




    public static void addParcel(final User a, final Action<String> action) {
        String userName = a.getUserName();

        //String key = parcel.getParcelId();
        usersRef.child(userName).setValue(a).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess(a.getUserName());
                action.onProgress("upload parcel data", 100);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
                action.onProgress("error upload parcel data", 100);

            }
        });
    }







    public static void removeParcel(String parcelid, final Action<String> action) {
        final String key = parcelid;

        usersRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User value = dataSnapshot.getValue(User.class);
                if (value == null)
                    action.onFailure(new Exception("parcel not found"));
                else {
                    usersRef.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            action.onSuccess(value.getUserName());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            action.onFailure(e);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                action.onFailure(databaseError.toException());
            }
        });
    }

    public static void updateParcel(final Parcel toUpdate, final Action<String> action) {
        final String key = toUpdate.getParcelId();
        final String phone = toUpdate.getRecipientPhoneNumber();
        usersRef.child(phone + '/' + key).setValue(toUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess(key);
                action.onProgress("updated parcel data", 100);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
            }
        });
    }

    //setting the listener to the changes.
    private static ChildEventListener parcelRefChildEventListener;
    public static void notifyToParcelList(final NotifyDataChange<List<User>> notifyDataChange) {
        if (notifyDataChange != null) {
            if (parcelRefChildEventListener != null) {
                notifyDataChange.onFailure(new Exception("first unNotify student list"));
                return;
            }
            usereList.clear();

            parcelRefChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                        User user = uniqueKeySnapshot.getValue(User.class);
                        boolean flag = true;
                        /*
                        for (int i = 0; i < parcelList.size(); i++) {
                            if (parcelList.get(i).getParcelId().equals(parcel.getParcelId())) {
                                parcelList.set(i, parcel);
                                flag = false;
                                break;
                            }
                        }
                        */

                        if (flag)
                            usereList.add(user);
                    }
                    notifyDataChange.OnDataChanged(usereList);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                        User user = uniqueKeySnapshot.getValue(User.class);
                        boolean flag = true;
                        for (int i = 0; i < usereList.size(); i++) {
                            if (usereList.get(i).getUserName().equals(user.getUserName())) {
                                usereList.set(i, user);
                                flag = false;
                                break;
                            }
                        }
                        if (flag)
                            usereList.add(user);
                    }

                    notifyDataChange.OnDataChanged(usereList);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    //Parcel parcel = dataSnapshot.child(id).getValue(Parcel.class);
                    String parcelid = dataSnapshot.child(dataSnapshot.getKey()).getKey();

                    for (int i = 0; i < usereList.size(); i++) {
                        if (usereList.get(i).getUserName().equals( parcelid)) {
                            usereList.remove(i);
                            break;
                        }
                    }

                    notifyDataChange.OnDataChanged(usereList);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    notifyDataChange.onFailure(databaseError.toException());
                }
            };
            usersRef.addChildEventListener(parcelRefChildEventListener);
        }
    }
    //stop the listener to the changes in the parcels list.
    public static void stopNotifyToParcelList() {
        if (parcelRefChildEventListener != null) {
            usersRef.removeEventListener(parcelRefChildEventListener);
            parcelRefChildEventListener = null;
        }
    }
}

