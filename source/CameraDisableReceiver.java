package org.androidtown.materialpractice;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Created by ahsxj on 2017-07-06.
 */

public class CameraDisableReceiver extends DeviceAdminReceiver  {

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //"완료!! 권한 체크 해주세요"
            Intent in = new Intent(context.getApplicationContext(), SplashActivity.class);

            context.startActivity(in);
        }
        else{
            //"마시멜로우 아래 버젼은 패스워드 등록으로 넘어갑니다"
            Intent in = new Intent(context.getApplicationContext(), SetPasswordActivity.class);

            context.startActivity(in);
        }
    }

    @Override
    public void onDisabled(Context context, Intent intent)
    {
        super.onDisabled(context,intent);
        //Toast.makeText(context,"Disabled",Toast.LENGTH_SHORT).show();
    }


}
