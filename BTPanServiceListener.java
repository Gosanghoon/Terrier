package org.androidtown.materialpractice;

import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

/**
 * Created by ahsxj on 2017-09-11.
 */

public class BTPanServiceListener implements BluetoothProfile.ServiceListener{

    private final Context context;

    public BTPanServiceListener(final Context context) {
        this.context = context;

    }

    @Override
    public void onServiceConnected(int profile, final BluetoothProfile proxy) {
        Log.i("MyApp", "BTPan proxy connected");
        try {
            proxy.getClass().getMethod("setBluetoothTethering", new Class[]{Boolean.TYPE}).invoke(proxy, new Object[]{Boolean.valueOf(false)});
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(int profile) {

    }
}
