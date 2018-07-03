package com.example.jona.latacungadigital.Activities.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.jona.latacungadigital.Activities.AtractivoActivity;

/**
 * Created by fabia on 01/07/2018.
 */

public class BootBroadcast extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, AtractivoActivity.class));
    }
}
