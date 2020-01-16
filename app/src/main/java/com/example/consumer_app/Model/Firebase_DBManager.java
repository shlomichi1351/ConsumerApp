package com.example.consumer_app.Model;


import android.net.Uri;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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

    public static void addUserToFirebase(final User a, final Action<String> action)
    {
        String userName = a.getUserName();

        //String key = parcel.getParcelId();
        usersRef.child(userName).setValue(a).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess(a.getUserName());
                action.onProgress("upload student data", 100);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
                action.onProgress("error upload student data", 100);

            }
        });
    }


    public static void addUser(final User user, final Action<String> action)
    {
        if (user.getImageLocalUri() != null)
        {
            // upload image
            StorageReference imagesRef = FirebaseStorage.getInstance().getReference();
            imagesRef = imagesRef.child("images").child(System.currentTimeMillis() + ".jpg");
            imagesRef.putFile(user.getImageLocalUri())
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            action.onProgress("upload user data", 90);
                            // Get a URL to the uploaded content
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            user.setImageFirebaseUrl(downloadUrl.toString());
                            // add user
                            addUserToFirebase(user, action);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            action.onFailure(exception);
                            action.onProgress("error upload user image", 100);
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double uploadBytes = taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                    double progress = (90.0 * uploadBytes);
                    action.onProgress("upload image", progress);
                }
            });

        }
        else action.onFailure(new Exception("select image first ..."));
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
                notifyDataChange.onFailure(new Exception("first unNotify user list"));
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