package com.example.consumer_app.Model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//import com.ddc.Model.ServiceUpdate;

public class BackgroundReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, ServiceUpdate.class);
        context.startService(startServiceIntent);
    }
}