package com.example.consumer_app.Model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable
{
    String firstName;
    String lastName;
    String userName;
    String password;
    String PhoneNumber;
    ArrayList<String> friendsList;
    Uri imageLocalUri;
    String imageFirebaseUrl;
    String address;





    public User(String firstName,String lastName,
            String userName,
            String password,
            String PhoneNumber,
            ArrayList<String> friendsList)
    {
        this.firstName=firstName;
        this.lastName=lastName;
        this.userName=userName;
        this.password=password;
        this.PhoneNumber=PhoneNumber;
        this.friendsList=new ArrayList<>(friendsList);
    }

    public User(){
        friendsList=new ArrayList<String>();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Exclude
    public Uri getImageLocalUri()
    {
        return imageLocalUri;
    }
    public void setImageLocalUri(Uri imageLocalUri)
    {
        this.imageLocalUri = imageLocalUri;
    }

    public String getImageFirebaseUrl()
    {
        return imageFirebaseUrl;
    }

    public void setImageFirebaseUrl(String imageFirebaseUrl)
    {
        this.imageFirebaseUrl = imageFirebaseUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public ArrayList<String> getFriendsList() {
        return friendsList;
    }

    public String getAddress() {    return address; }

    public void setAddress(String address) {    this.address = address; }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public void setFriendsList(ArrayList<String> friendsList) {
        this.friendsList = friendsList;
    }

}
