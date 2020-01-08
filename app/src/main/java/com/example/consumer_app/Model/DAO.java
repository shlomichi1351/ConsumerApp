package com.example.consumer_app.Model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DAO
{
    @Insert
    void insert(Parcel parcel);

    @Update
    void update(Parcel parcel);

    @Delete
    void delete(Parcel parcel);

    @Query("DELETE FROM parcel_table")
    void deleteAllParcels();

    @Query("SELECT * FROM parcel_table ORDER BY parcelId Desc")
    LiveData<List<Parcel>> getAllParcel();
}
