package com.example.consumer_app.Model;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class convertor
{
    @TypeConverter
    public static String TypeToString(Parcel.Type type)
    {
        if(type.equals(Parcel.Type.LargePackage))
            return "LargePackage";
        if(type.equals((Parcel.Type.Envelope)))
            return "Envelope";

        return "SmallPackage";
    }

    @TypeConverter
    public static String StatusToString(Parcel.Status status)
    {
        if(status.equals( Parcel.Status.CollectionOffered))
            return "CollectionOffered";

        if(status.equals(Parcel.Status.Delivered))
            return "Delivered";

        if(status.equals(Parcel.Status.OnTheWay))
            return "OnTheWay";

        return "Registered";
    }

    @TypeConverter
    public static Parcel.Status StringToStatus(String string)
    {
        if(string.equals("CollectionOffered"))
            return Parcel.Status.CollectionOffered;

        if(string.equals( "Delivered"))
            return Parcel.Status.Delivered;

        if(string.equals( "OnTheWay"))
            return Parcel.Status.OnTheWay;

        return Parcel.Status.Registered;
    }

    @TypeConverter
    public static Parcel.Type StringToType(String string)
    {
        if(string.equals( "LargePackage"))
            return Parcel.Type.LargePackage;
        if(string.equals("Envelope"))
            return  Parcel.Type.Envelope;
        return Parcel.Type.SmallPackage;
    }

    @TypeConverter
    public static String SuggestersToString(ArrayList<String> s)
    {
        StringBuilder sb = new StringBuilder();
        for (String user : s)
        {
            sb.append(user);
            sb.append("|");
        }
        return sb.toString();
    }


    @TypeConverter
    public static ArrayList<String> StringToSuggesters(String s)
    {
        return new ArrayList<String>(Arrays.asList(s.split("|")));
    }

}
