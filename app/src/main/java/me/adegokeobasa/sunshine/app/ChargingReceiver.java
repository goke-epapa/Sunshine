package me.adegokeobasa.sunshine.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ChargingReceiver extends BroadcastReceiver {
    private static final String TAG = ChargingReceiver.class.getSimpleName();

    public ChargingReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Phone was connected to power", Toast.LENGTH_LONG).show();
    }
}
