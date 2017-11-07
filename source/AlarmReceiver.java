package org.androidtown.materialpractice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by ahsxj on 2017-09-11.
 */

public class AlarmReceiver extends BroadcastReceiver {
    /**
     * 주기적으로 서버에 GPS 정보를 보내기 위한 AlarmReceiver.class
     * 알람을 리시브 하면 서버에 현재 위치정보를 전송한다.
     */
    Gps gps;
    HttpsConnection ht;
    SharedPreferences userinfo;
    double latitude;
    double longitude;
    String serial;

    @Override
    public void onReceive(Context context, Intent intent){
        Log.d("알람","리시브");
        ht = new HttpsConnection();
        gps = new Gps(context);
        userinfo = context.getSharedPreferences("User_info",0);
        serial = userinfo.getString("Id","fail");
        latitude = setGpsInfo("latitude");
        longitude = setGpsInfo("longitude");
        ht.sendGPS("https://58.141.234.126:55356/process/locationadd",serial,latitude,
                    longitude);
    }

    public double setGpsInfo(String s)
    {
        double latitude = 0.0;
        double longitude = 0.0;

        /**
         * gps 사용 체크
         */
        if(gps.isGetLocation())
        {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }

        if(s.equals("latitude"))
            return latitude;
        else
            return longitude;
    }
}
