package com.example.cjlhappiness.clock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("test----", "onReceive: ");
        Intent intent1 = new Intent(context, AlarmServer.class);
        intent1.putExtra("OBJECT", AlarmServer.OBJECT[1]);
        context.startService(intent1);
    }
}
