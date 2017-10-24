package org.androidtown.materialpractice;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;


/**
 * Created by ahsxj on 2017-07-27.
 */

public class Gps extends Service implements LocationListener {

    private final Context mContext;

    /**
     * @param isGPSEnabled : 현재 GPS 사용 여부
     * @param isNetworkEnabled : 현재 네트워크 사용 여부
     * @param isGetLocation : GPS 상태
     */
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean isGetLocation = false;

    Location location;

    /**
     * @param lat = 위도;
     * @param lon = 경도
     */
    double lat;
    double lon;

    /**
     * @param MIN_DISTANCE_CHANGE_FOR_UPDATES : GPS 정보 업데이트 최소 거리.
     */
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;

    /**
     * @param MIN_TIME_BW_UPDATES : GPS 정보 업데이트 최소 시간.
     */
    private static final long MIN_TIME_BW_UPDATES = 2000;

    protected LocationManager locationManager;

    public Gps(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation()
    {
        try
        {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            //GPS 정보 가져오기
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled)
            {
                //gps 와 네트워크 사용이 가능하지 않을 때
            }
            else
            {
                this.isGetLocation = true;
                //네트워크로 부터 위치 정보 값 가져오기
                if (isNetworkEnabled)
                {
                    if(ActivityCompat.checkSelfPermission(mContext,Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED)
                    {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if(locationManager != null)
                        {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                            //위도 경도 저장.
                            if(location != null)
                            {
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                            }
                        }
                    }
                }

                if (isGPSEnabled)
                {
                    if(location == null)
                    {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,this);

                        if(locationManager != null)
                        {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if(location != null)
                            {
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                            }
                        }
                    }
                }
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * GPS 종료
     * */
    public void stopUsingGPS()
    {
        if(locationManager != null)
        {
            locationManager.removeUpdates(Gps.this);
        }
    }

    /**
     * 위도값 가져오기
     */
    public double getLatitude()
    {
        if(location != null)
        {
            lat = location.getLatitude();
        }
        else
            Log.d("위도:","가져오기 실패");
        return lat;
    }

    /**
     * 경도값 가져오기.
     */
    public double getLongitude()
    {
        if(location != null)
        {
            lon = location.getLongitude();
        }
        else
            Log.d("경도:","가져오기 실패");
        return lon;
    }

    public boolean isGetLocation()
    {
        return this.isGetLocation;
    }

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle("GPS 사용유무셋팅");
        alertDialog.setMessage("GPS 셋팅이 되지 않았을수도 있습니다. \n 설정창으로 가시겠습니까?");


                alertDialog.setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                mContext.startActivity(intent);
                            }
                        });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
