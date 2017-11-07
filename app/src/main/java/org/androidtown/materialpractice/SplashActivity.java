package org.androidtown.materialpractice;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.UUID;

/**
 * Created by ahsxj on 2017-06-29.
 */

public class SplashActivity extends Activity {

    /**
     * @param componentName : 카메라 잠금을 하기위한 ComponentName
     * @param lock_componentName : 화면 잠금을 하기위한 lock_componentName
     * @param Userinfo : 시리얼 값을 저장하기 위한 SharedPreferences
     * @param Firstlogin : 첫 실행인지 아닌지를 저장하기위한 SharedPreferences
     * @param TOKEN : FCM 토큰값
     */

    private static final long SPLASH_DISPLAY_LENGTH = 150;
    public static final int MY_PERMISSIONS_REQUEST=7777;
    private DevicePolicyManager dmg;
    private ComponentName componentName;
    private ComponentName lock_componentName;
    private SharedPreferences Userinfo;
    private SharedPreferences FirstLogin;
    private String TOKEN;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);
        dmg = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(getApplicationContext(), CameraDisableReceiver.class);
        //lock_componentName = new ComponentName(getApplicationContext(), LockScreenReceiver.class);
        FirstLogin = getSharedPreferences("firstlogin", 0);
        Userinfo = getSharedPreferences("User_info",0);

        startService(new Intent(getApplicationContext(),CheckService.class));

        boolean hasVisited = FirstLogin.getBoolean("firstflag", false);

        /*
            첫 실행 여부 체크
         */

        if(!hasVisited)
        {
            CheckAdminPermission();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    /*
                첫 실행이라면 ->
                관리자 권한과 앱에 필요한 권한을 요청한다.
                    */
                    Toast.makeText(getApplicationContext(), "권한이 완료될때까지 기다려주세요", Toast.LENGTH_SHORT).show();


                    if((ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_WIFI_STATE)) == PackageManager.PERMISSION_GRANTED &&
                            (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_SMS)) == PackageManager.PERMISSION_GRANTED &&
                            (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WAKE_LOCK)) == PackageManager.PERMISSION_GRANTED &&
                            (ContextCompat.checkSelfPermission(getApplicationContext(),android.Manifest.permission.CAMERA)) == PackageManager.PERMISSION_GRANTED &&
                            (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CHANGE_WIFI_STATE)) == PackageManager.PERMISSION_GRANTED &&
                            (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_WIFI_STATE)) == PackageManager.PERMISSION_GRANTED &&
                            (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) == PackageManager.PERMISSION_GRANTED &&
                            (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS)) == PackageManager.PERMISSION_GRANTED &&
                            (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)) == PackageManager.PERMISSION_GRANTED &&
                            (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE)) == PackageManager.PERMISSION_GRANTED)
                    {
                /*
                    토큰값이 제대로 들어오는지 테스트.
                 */
                    }
                    else
                    {
                /*
                    위에 명시한 앱 권한 이 거부된 상태라면->
                    권한 허용을 요청한다.
                 */
                        //Toast.makeText(getApplicationContext(), "솔루션에 필요한 권한을 허용 해주세요.", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        {
                            requestPermissions(new String[]{
                                            android.Manifest.permission.READ_SMS, android.Manifest.permission.READ_PHONE_STATE,
                                            android.Manifest.permission.CAMERA,
                                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS,
                                            android.Manifest.permission.WAKE_LOCK, android.Manifest.permission.CHANGE_WIFI_STATE,
                                            android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_WIFI_STATE,
                                            Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAPTURE_AUDIO_OUTPUT
                                            ,Manifest.permission.CHANGE_CONFIGURATION},
                                    MY_PERMISSIONS_REQUEST);
                        }
                        else{
                            //버전이 마시멜로우 아래이면 기본적으로 권한 설정이 되어있기때문에 권한 설정 할필요없음
                            //마시멜로우 아래 버젼인 경우 CameraDisableReceiver에서 바로 SetPasswordActivity로 넘어감
                            SharedPreferences.Editor info = Userinfo.edit();
                            info.putString("Id",GetDeviceSerial());
                            info.apply();
                            Log.v("테스트",Userinfo.getString("Id","fail"));
                            SharedPreferences.Editor e = FirstLogin.edit();
                            e.putBoolean("firstflag", true);
                            e.apply();
                            //아래버전 테스트 안해봄
                        }
                    }
                }
            },4000);



        }
        else
        {
            /*
                앱 권한이 다 허용되어 있다면 로그인 화면으로 넘어간다.
             */
            startActivity(new Intent(this, PasswordActivity.class));
            Log.v("테스트", Userinfo.getString("Id", "fail"));

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST:
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    TOKEN = FirebaseInstanceId.getInstance().getToken();
                    if(TOKEN != null) {
                        Log.d("TOKEN값", "" + TOKEN);

                    /*
                        Userinfo에 모바일 디바이스 고유값인 UUID를 저장한다.
                     */

                        SharedPreferences.Editor info = Userinfo.edit();
                        info.putString("Id",GetDeviceSerial());
                        info.apply();
                        Log.v("테스트",Userinfo.getString("Id","fail"));
                        SharedPreferences.Editor e = FirstLogin.edit();
                        e.putBoolean("firstflag", true);
                        e.apply();
                        Intent i = new Intent(SplashActivity.this, SetPasswordActivity.class);
                        startActivity(i);
                        TOKEN = null;
                        FirstLogin = null;
                        finish();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "토큰 생성안됌", Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    finish();
                }
                return;
        }
    }

    /*
        카메라 잠금, 화면 잠금 관리자 권한 체크
     */
    public void CheckAdminPermission()
    {
        if(!dmg.isAdminActive(componentName))
        {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"관리자 권한이 필요합니다.");
            startActivityForResult(intent,0);
        }
//        if(!dmg.isAdminActive(lock_componentName))
//        {
//            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
//            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,lock_componentName);
//            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"관리자 권한이 필요합니다.");
//            startActivityForResult(intent,0);
//        }
    }

    @SuppressLint("HardwareIds")
    public String GetDeviceSerial() {
        TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String tmDevice = "" + tm.getDeviceId();
        String tmSerial = "" + tm.getSimSerialNumber();
        String androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();

        return deviceId;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        findViewById(R.id.splash_View).setBackground(null);
        System.gc();
    }
}

