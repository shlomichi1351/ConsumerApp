package com.example.consumer_app.Model;


import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class Firebase_DBManager_Parcel {
    static DatabaseReference parcelsRef;
    static List<Parcel> parcelList;

    static {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        parcelsRef = database.getReference("RegisteredPackages");
        parcelList = new ArrayList<>();
    }




    public static void addParcel(final Parcel parcel, final Action<String> action) {
        String phone = parcel.getRecipientPhoneNumber();
        String key = parcel.getParcelId();
        parcelsRef.child(phone + "/" + key).setValue(parcel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess(parcel.getParcelId());
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

        parcelsRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Parcel value = dataSnapshot.getValue(Parcel.class);
                if (value == null)
                    action.onFailure(new Exception("parcel not found"));
                else {
                    parcelsRef.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            action.onSuccess(value.getParcelId());
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
        parcelsRef.child(phone + '/' + key).setValue(toUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
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
    public static void notifyToParcelList(final NotifyDataChange<List<Parcel>> notifyDataChange) {
        if (notifyDataChange != null)
        {
            if (parcelRefChildEventListener != null)
            {
                notifyDataChange.onFailure(new Exception("first unNotify parcel list"));
                return;
            }
            parcelList.clear();

            parcelRefChildEventListener = new ChildEventListener()
            {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s)
                {
                    for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                        Parcel parcel = uniqueKeySnapshot.getValue(Parcel.class);
                        parcelList.add(parcel);
                    }

                    notifyDataChange.OnDataChanged(parcelList);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s)
                {

                    for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren())
                    {
                        Parcel parcel = uniqueKeySnapshot.getValue(Parcel.class);
                        boolean flag = true;
                        for (int i = 0; i < parcelList.size(); i++) {
                            if (parcelList.get(i).getParcelId().equals(parcel.getParcelId())) {
                                parcelList.set(i, parcel);
                                flag = false;
                                break;
                            }
                        }
                        if (flag)
                            parcelList.add(parcel);
                    }

                    notifyDataChange.OnDataChanged(parcelList);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    //Parcel parcel = dataSnapshot.child(id).getValue(Parcel.class);
                    String parcelid = dataSnapshot.child(dataSnapshot.getKey()).getKey();

                    for (int i = 0; i < parcelList.size(); i++) {
                        if (parcelList.get(i).getRecipientPhoneNumber().equals( parcelid)) {
                            parcelList.remove(i);
                            break;
                        }
                    }

                    notifyDataChange.OnDataChanged(parcelList);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    notifyDataChange.onFailure(databaseError.toException());
                }
            };
            parcelsRef.addChildEventListener(parcelRefChildEventListener);
        }
    }
    //stop the listener to the changes in the parcels list.
    public static void stopNotifyToParcelList() {
        if (parcelRefChildEventListener != null) {
            parcelsRef.removeEventListener(parcelRefChildEventListener);
            parcelRefChildEventListener = null;
        }
    }
}
