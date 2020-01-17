package com.example.consumer_app.Model;


import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.consumer_app.Model.Parcel;
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

public class Firebase_DBManager_User {
    static DatabaseReference usersRef;
    static DatabaseReference parcelsRef;

    static List<User> usereList;
    static List<User> friendsList;


    static {
        FirebaseDatabase databaseUsers = FirebaseDatabase.getInstance();
        usersRef = databaseUsers.getReference("Users");
        FirebaseDatabase databaseParcels = FirebaseDatabase.getInstance();

        parcelsRef=databaseParcels.getReference("RegisteredPackages");
        usereList = new ArrayList<User>();
        friendsList=new ArrayList<User>();
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
        else action.onFailure(new Exception("select image first ..."));
    }
}
