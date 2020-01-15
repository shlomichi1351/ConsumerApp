package com.example.consumer_app.Model;

import androidx.room.TypeConverter;

public class convertor
{
    @TypeConverter
    public static String TypeToString(Parcel.Type type)
    {
        if(type== Parcel.Type.LargePackage)
            return "LargePackage";

        return "SmallPackage";
    }

    @TypeConverter
    public static String StatusToString(Parcel.Status status)
    {
        if(status == Parcel.Status.CollectionOffered)
            return "CollectionOffered";

        if(status == Parcel.Status.Delivered)
            return "Delivered";

        if(status == Parcel.Status.OnTheWay)
            return "OnTheWay";

        return "Registered";
    }

    @TypeConverter
    public static Parcel.Status StringToStatus(String string)
    {
        if(string == "CollectionOffered")
            return Parcel.Status.CollectionOffered;

        if(string == "Delivered")
            return Parcel.Status.Delivered;

        if(string == "OnTheWay")
            return Parcel.Status.OnTheWay;

        return Parcel.Status.Registered;
    }

    @TypeConverter
    public static Parcel.Type StringToType(String string)
    {
        if(string == "LargePackage")
            return Parcel.Type.LargePackage;
        return Parcel.Type.SmallPackage;
    }
}