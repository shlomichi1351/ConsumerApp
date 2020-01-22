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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Firebase_DBManager_User {
    static public DatabaseReference usersRef;

    static List<User> userList;
    static List<User> friendsList;


    static {
        FirebaseDatabase databaseUsers = FirebaseDatabase.getInstance();
        usersRef = databaseUsers.getReference("Users");
        FirebaseDatabase databaseParcels = FirebaseDatabase.getInstance();

        //parcelsRef=databaseParcels.getReference("RegisteredPackages");
        userList = new ArrayList<User>();
        friendsList=new ArrayList<User>();
    }





    public static void addUserToFirebase(final User a, final Action<String> action)
    {
        String phoneNumber = a.getPhoneNumber();

        //String key = parcel.getParcelId();
        usersRef.child(phoneNumber).setValue(a).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess(a.getPhoneNumber());
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

                            Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    user.setImageFirebaseUrl(uri.toString());
                                }
                            });

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
        else addUserToFirebase(user, action);
    }


    public static void updateUser(final User toUpdate, final Action<String> action) {

        usersRef.child(toUpdate.getPhoneNumber()).setValue(toUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess(toUpdate.getPhoneNumber());
                action.onProgress("updated user data", 100);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
            }
        });

    }



    //setting the listener to the changes.
    private static ChildEventListener userRefChildEventListener;
    public static void notifyToUserList(final NotifyDataChange<List<User>> notifyDataChange)
    {
        if (notifyDataChange != null)
        {
            stopNotifyToUserList();
            if (userRefChildEventListener != null)
            {
                notifyDataChange.onFailure(new Exception("first unNotify user list"));
                return;
            }
            userList.clear();

            userRefChildEventListener = new ChildEventListener()
            {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s)
                {
                    User user;
                    user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                    notifyDataChange.OnDataChanged(userList);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s)
                {

                    for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren())
                    {
                        User user = uniqueKeySnapshot.getValue(User.class);
                        boolean flag = true;
                        for (int i = 0; i < userList.size(); i++) {
                            if (userList.get(i).getPhoneNumber().equals(user.getPhoneNumber())) {
                                userList.set(i, user);
                                flag = false;
                                break;
                            }
                        }
                        if (flag)
                            userList.add(user);
                    }

                    notifyDataChange.OnDataChanged(userList);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    //Parcel parcel = dataSnapshot.child(id).getValue(Parcel.class);
                    String userName = dataSnapshot.child(dataSnapshot.getKey()).getKey();

                    for (int i = 0; i < userList.size(); i++) {
                       if (userList.get(i).getPhoneNumber().equals( userName)) {
                            userList.remove(i);
                            break;
                        }
                    }

                    notifyDataChange.OnDataChanged(userList);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    notifyDataChange.onFailure(databaseError.toException());
                }
            };
            usersRef.addChildEventListener(userRefChildEventListener);
        }
    }
    public static void stopNotifyToUserList() {
        if (userRefChildEventListener != null) {
            usersRef.removeEventListener(userRefChildEventListener);
            userRefChildEventListener = null;
        }
    }

    public static void r(User user) {
        userList.remove(user);
    }
}
