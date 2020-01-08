package com.example.consumer_app.Model;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {Parcel.class}, version = 1)
public abstract class ParcelDataBase extends RoomDatabase
{
    private static ParcelDataBase instance;

    public abstract DAO ParcelDao();

    public static synchronized ParcelDataBase getInstance(Context context)
    {
        if(instance == null)
            instance = Room.databaseBuilder(context.getApplicationContext() , ParcelDataBase.class,
                    "parcel_database").fallbackToDestructiveMigration().build();
        return instance;
    }
}
