package com.example.consumer_app.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.ArrayList;
import java.util.List;


@Entity(tableName = "Parcel_table")
public class Parcel {
    public enum Type{
        Envelope,SmallPackage, LargePackage
    }
    public enum Status{
        Registered, CollectionOffered, OnTheWay, Delivered
    }
    private Status status;
    private Type type;
    private Boolean isFragile;
    private double weight;
    private String distributionCenterAddress;
    private String recipientAddress;
    private String recipientName;
    private String recipientPhoneNumber;
    ArrayList<String> suggesters;
    String phoneDeliver;

    @PrimaryKey
    @NonNull
    private String parcelId;

    //ctor
    public Parcel(Type type, Boolean isFragile, double weight, String distributionCenterAddress, String recipientAddress, String recipientName, String recipientPhoneNumber, String parcelId, ArrayList<String> parcels, String phoneDeliver) {

        this.type = type;
        this.isFragile = isFragile;
        this.weight = weight;
        this.distributionCenterAddress = distributionCenterAddress;
        this.recipientAddress = recipientAddress;
        this.recipientName = recipientName;
        this.recipientPhoneNumber = recipientPhoneNumber;
        this.parcelId = parcelId;
        this.suggesters=parcels;
        this.phoneDeliver=phoneDeliver;
    }


    //default ctor for firebasse utilities.
    public Parcel() {this(null , false , 0 ,"","", "","","", new ArrayList<String>(), "");}


    //getters
    public Status getStatus() {
        return status;
    }

    public Type getType() {
        return type;
    }

    public Boolean getFragile() {
        return isFragile;
    }

    public double getWeight() {
        return weight;
    }

    public String getDistributionCenterAddress() {
        return distributionCenterAddress;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getRecipientPhoneNumber() {
        return recipientPhoneNumber;
    }

    public String getParcelId() {
        return parcelId;
    }


    public List<String> getSuggesters() {
        return suggesters;
    }

    public void setSuggesters(ArrayList<String> suggesters) {
        this.suggesters = suggesters;
    }

    public String getPhoneDeliver() {
        return phoneDeliver;
    }

    public void setPhoneDeliver(String phoneDeliver) {
        this.phoneDeliver = phoneDeliver;
    }

    //setters
    public void setStatus(Status status) {
        this.status = status;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setFragile(Boolean fragile) {
        isFragile = fragile;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setDistributionCenterAddress(String distributionCenterAddress) {
        this.distributionCenterAddress = distributionCenterAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public void setRecipientPhoneNumber(String recipientPhoneNumber) {
        this.recipientPhoneNumber = recipientPhoneNumber;
    }

    public void setParcelId(String parcelId) {
        this.parcelId = parcelId;
    }
}
