package com.example.consumer_app.Model;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


@Database(entities = {Parcel.class}, version = 1, exportSchema = false)
@TypeConverters({convertor.class})
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
