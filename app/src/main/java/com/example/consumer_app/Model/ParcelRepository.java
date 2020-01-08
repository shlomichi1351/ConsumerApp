package com.example.consumer_app.Model;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;



public class ParcelRepository
{
    private DAO parcelDao;
    private LiveData<List<Parcel>> allParcels;

    public ParcelRepository(Application application)
    {
        ParcelDataBase database = ParcelDataBase.getInstance(application);
        parcelDao = database.ParcelDao();
        allParcels = parcelDao.getAllParcel();
    }

    public void insert(Parcel parcel){
        new InsertParcelAsyncTask(parcelDao).execute(parcel);
    }

    public void update(Parcel parcel){
        new UpdateParcelAsyncTask(parcelDao).execute(parcel);
    }

    public void delete(Parcel parcel){
        new DeleteParcelAsyncTask(parcelDao).execute(parcel);
    }

    public void deleteAllParcels(){
        new DeleteAllParcelAsyncTask(parcelDao).execute();
    }

    public LiveData<List<Parcel>> getAllParcels(){
        return allParcels;
    }

    public static class InsertParcelAsyncTask extends AsyncTask<Parcel,Void,Void>
    {
        private DAO parcelDao;
        public InsertParcelAsyncTask(DAO parcelDao)
        {
            this.parcelDao = parcelDao;
        }
        @Override
        protected Void doInBackground(Parcel... parcels)
        {
            parcelDao.insert(parcels[0]);
            return null;
        }
    }
    public static class UpdateParcelAsyncTask extends AsyncTask<Parcel,Void,Void>
    {
        private DAO ParcelDao;
        public UpdateParcelAsyncTask(DAO ParcelDao)
        {
            this.ParcelDao = ParcelDao;
        }
        @Override
        protected Void doInBackground(Parcel... parcels) {
            ParcelDao.update(parcels[0]);
            return null;
        }
    }
    public static class DeleteParcelAsyncTask extends AsyncTask<Parcel,Void,Void>
    {
        private DAO parcelDao;
        public DeleteParcelAsyncTask(DAO parcelDao)
        {
            this.parcelDao = parcelDao;
        }
        @Override
        protected Void doInBackground(Parcel... parcels) {
            parcelDao.delete(parcels[0]);
            return null;
        }
    }
    public static class DeleteAllParcelAsyncTask extends AsyncTask<Parcel,Void,Void>
    {
        private DAO parcelDao;
        public DeleteAllParcelAsyncTask(DAO parcelDao)
        {
            this.parcelDao = parcelDao;
        }
        @Override
        protected Void doInBackground(Parcel... parcels) {
            parcelDao.deleteAllParcels();
            return null;
        }
    }
}
