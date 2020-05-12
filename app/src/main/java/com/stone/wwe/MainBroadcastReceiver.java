package com.stone.wwe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class MainBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = MainBroadcastReceiver.class.getName();
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, "receive broadcast:" + action);
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent serviceIntent = new Intent(context, MainService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.i(TAG, "> Android 8.0 ");
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        }
    }
}
