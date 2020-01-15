package com.example.consumer_app.Model;

public interface NotifyDataChange<T> {
    public void OnDataChanged(T obj);

    public void onFailure(Exception exception);
}