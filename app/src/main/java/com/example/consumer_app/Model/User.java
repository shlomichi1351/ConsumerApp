package com.example.consumer_app.Model;

import java.util.ArrayList;

public class User
{
    String firstName;
    String lastName;
    String userName;
    String password;
    String PhoneNumber;
    ArrayList<User> friendsList;

    public User(String firstName,String lastName,
            String userName,
            String password,
            String PhoneNumber,
            ArrayList<User> friendsList)
    {
        this.firstName=firstName;
        this.lastName=lastName;
        this.userName=userName;
        this.password=password;
        this.PhoneNumber=PhoneNumber;
        this.friendsList=new ArrayList<>(friendsList);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
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

    public ArrayList<User> getFriendsList() {
        return friendsList;
    }

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

    public void setFriendsList(ArrayList<User> friendsList) {
        this.friendsList = friendsList;
    }
    //user name, nickname, pass, picture, phone number,
}
