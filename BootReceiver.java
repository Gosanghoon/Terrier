package org.androidtown.materialpractice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ahsxj on 2017-09-16.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            Intent i = new Intent(context,CheckService.class);
            context.startService(i);
        }
    }
}
