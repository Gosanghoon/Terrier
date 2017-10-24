package org.androidtown.materialpractice;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ahsxj on 2017-07-06.
 */

public class CameraDisableReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent)
    {
        super.onEnabled(context,intent);
        //Toast.makeText(context,"Enabled",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisabled(Context context, Intent intent)
    {
        super.onDisabled(context,intent);
        //Toast.makeText(context,"Disabled",Toast.LENGTH_SHORT).show();
    }


}
