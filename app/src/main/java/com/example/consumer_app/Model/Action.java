package com.example.consumer_app.Model;

public interface Action<T>
{
    public void onSuccess(T obj);
    public void onFailure(Exception exception);
    public void onProgress(String status, double percent);
}
