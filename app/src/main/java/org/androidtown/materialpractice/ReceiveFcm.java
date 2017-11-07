package org.androidtown.materialpractice;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by ahsxj on 2017-07-17.
 */

public class ReceiveFcm extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "FirebaseMsgService";
    private MainModel               mainModel;
    private DevicePolicyManager     dmg;
    private ComponentName           componentName;
    private ComponentName           lock_componentName;
    private SharedPreferences       networkFlag;
    private SharedPreferences.Editor networkEditor;
    private SharedPreferences.Editor loginHistoryEditor;
    private SharedPreferences       loginHistory;
    private AudioManager            audioManager;
    private Context                 context;
    private WifiManager             wifiManager;
    private BluetoothAdapter        mBluetoothAdapter;
    private MediaPlayer             media;
    private AudioManager            audio;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        mainModel = new MainModel();
        dmg = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(getApplicationContext(), CameraDisableReceiver.class);
        lock_componentName = new ComponentName(getApplicationContext(), LockScreenReceiver.class);
        loginHistory = getSharedPreferences("login_history",0);
        loginHistoryEditor = loginHistory.edit();
        networkFlag = getSharedPreferences("NetworkFlag",0);
        networkEditor = networkFlag.edit();
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        context = getApplicationContext();
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        media = new MediaPlayer();
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        try{
            media.setDataSource(this,uri);
            media.setLooping(true);
            media.prepare();
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        //추가한것
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d("리시브테스트:", String.valueOf(remoteMessage.getData().size()));

        if (remoteMessage.getData().size() > 0) {
            Log.d("리시브테스트:", "여기까지");
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            Log.d("테스트타이틀:", title);
            Log.d("테스트바디:", body);
            sendNotification(title, body);

            switch (title) {
                case "CW": {
                    mainModel.set_WIFI(body,wifiManager,networkEditor);
                    break;
                }
                case "CB": {
                    mainModel.set_BlueTooth(body,mBluetoothAdapter,networkEditor);
                    break;
                }
                case "CT": {
                    mainModel.set_Tethering(body,context,wifiManager,networkEditor);
                    break;
                }
                case "MC": {
                    mainModel.set_Camera(body,dmg,componentName,networkEditor);
                    break;
                }
                case "MR": {
                    mainModel.set_Mic(body,audioManager,networkEditor);
                    break;
                }

                case "출근":{
                    mainModel.set_Camera(body,dmg,componentName,networkEditor);
                    loginHistoryEditor.putBoolean("Check",true);
                    loginHistoryEditor.apply();
                    Intent intent  = new Intent(this,SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                }

                case "퇴근":{
                    mainModel.set_Camera(body,dmg,componentName,networkEditor);
                    loginHistoryEditor.putBoolean("Check",false);
                    loginHistoryEditor.putBoolean("history",false);
                    loginHistoryEditor.apply();
                    break;
                }

                case "Backup": {
                    Intent intent = new Intent(context,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if(body.equals("Img"))
                        intent.putExtra("Backup","Img");
                    else
                        intent.putExtra("Backup","Number");
                    context.startActivity(intent);
                    break;
                }

                case "Alarm":{
                    Log.v("알람","fcm 수신");
                    mainModel.setBell(body,media,audio);
                    break;
                }

                case "Gps":{
                    Intent intent = new Intent(context,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if(body.equals("ON"))
                        intent.putExtra("Gps","Go");
                    else
                        intent.putExtra("Gps","Stop");
                    context.startActivity(intent);
                    break;
                }

                case "MCA": {
                    break;
                }
            }
        }
    }
    private void sendNotification(String title, String body) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1000, 1000})
                .setLights(Color.BLUE,1,1)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }
}
