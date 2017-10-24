package org.androidtown.materialpractice;

import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.androidtown.materialpractice.MainModel.mbluetoothBackThread;
import static org.androidtown.materialpractice.MainModel.tetheringBackThread;
import static org.androidtown.materialpractice.MainModel.wifithread;

/**
 * Created by ahsxj on 2017-07-16.
 */

public class MainModel{

    static wifiBackThread wifithread;
    static bluetoothBackThread mbluetoothBackThread;
    static TetheringBackThread tetheringBackThread;

    public void setBell(String s , MediaPlayer mediaPlayer, AudioManager audioManager)
    {
        final String param = s;
        if(param.equals("OFF"))
        {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,15,0);
            mediaPlayer.start();
        }
        else if(param.equals("ON"))
        {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,7,0);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    public void set_Mic(String s , AudioManager audioManager , SharedPreferences.Editor networkEditor)
    {
        final String param = s;
        try{
            if(param.equals("OFF")) {
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                audioManager.setMicrophoneMute(false);
                networkEditor.putString("RECORD","false");
                networkEditor.apply();
            }
            else if(param.equals("ON"))
            {
                audioManager.setMode(AudioManager.MODE_NORMAL);
                networkEditor.putString("RECORD","true");
                networkEditor.apply();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void set_Tethering(String s, Context context, WifiManager wifiManager, SharedPreferences.Editor networkEditor)
    {

        if(tetheringBackThread == null)
            tetheringBackThread = new TetheringBackThread(context, wifiManager);

        if(s.equals("OFF"))
        {
            if(tetheringBackThread.isAlive())
            {
                return;
            }
            else
            {
                tetheringBackThread.start();
                networkEditor.putString("TETHER","false");
                networkEditor.apply();
            }

        }
        else
        {
            tetheringBackThread.interrupt();
            networkEditor.putString("TETHER","true");
            networkEditor.apply();
        }
    }

    public void toggleTethering(Context context) {

        Class classBluetoothPan = null;
        Constructor BTPanCtor = null;
        Object BTSrvInstance = null;
        Class noparams[] = {};
        Method mIsBTTetheringOn;

        try {
            classBluetoothPan = Class.forName("android.bluetooth.BluetoothPan");
            mIsBTTetheringOn = classBluetoothPan.getDeclaredMethod("isTetheringOn", noparams);
            BTPanCtor = classBluetoothPan.getDeclaredConstructor(Context.class, BluetoothProfile.ServiceListener.class);
            BTPanCtor.setAccessible(true);
            BTSrvInstance = BTPanCtor.newInstance(context, new BTPanServiceListener(context));

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWifiTetheringDisabled(WifiManager wifiManager)
    {
        Method[] methods = wifiManager.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals("setWifiApEnabled")) {
                try {
                    method.invoke(wifiManager, null, false);
                } catch (Exception ex) {
                }
                break;
            }
        }
    }

    /**
     * 와이파이 제어 메소드
     * @param s on off 값
     */

    public void set_WIFI(String s, WifiManager wifiManager, SharedPreferences.Editor networkEditor)
    {
        if(wifithread == null) {
            wifithread = new wifiBackThread(wifiManager);
        }
        if(s.equals("OFF"))
        {
            if(wifithread.isAlive())
            {
                return;
            }
            else
            {
                wifithread.start();
                networkEditor.putString("WIFI","false");
                networkEditor.apply();
            }
        }
        else if(s.equals("ON"))
        {
            if(wifithread != null && wifithread.isAlive())
            {
                wifithread.interrupt();
                networkEditor.putString("WIFI","true");
                networkEditor.apply();
            }
        }
    }

    /**
     * 블루투스 제어 메소드
     */
    public void set_BlueTooth(String s, BluetoothAdapter mBluetoothAdapter, SharedPreferences.Editor networkEditor)
    {
        if(mbluetoothBackThread == null)
            mbluetoothBackThread = new bluetoothBackThread(mBluetoothAdapter);

        if(s.equals("OFF"))
        {
            if(mbluetoothBackThread.isAlive())
            {
                return;
            }
            else
            {
                mbluetoothBackThread.start();
                networkEditor.putString("BLUE","false");
                networkEditor.apply();
            }
        }
        else if(s.equals("ON"))
        {
            if(mbluetoothBackThread != null && mbluetoothBackThread.isAlive())
            {
                mbluetoothBackThread.interrupt();
                networkEditor.putString("BLUE","true");
                networkEditor.apply();
            }
        }
    }

    public void set_Camera(String s, DevicePolicyManager dmg, ComponentName componentName, SharedPreferences.Editor networkEditor)
    {
        if(s.equals("OFF"))
        {
            if(dmg.isAdminActive(componentName)){
                dmg.setCameraDisabled(componentName, true);
                networkEditor.putString("CAMERA","false");
                networkEditor.apply();
            }
            else
                Log.e("set_camera:","err");
        }
        else if(s.equals("ON"))
        {
            if(dmg.isAdminActive(componentName))
            {
                Log.d("카메라 :","카메라 온");
                dmg.setCameraDisabled(componentName, false);
                networkEditor.putString("CAMERA","true");
                networkEditor.apply();
            }
            else
                Log.e("set_camera:","err");
        }
    }

    public void set_Lock(String s, DevicePolicyManager dmg, ComponentName lock_componentName)
    {
        if(s.equals("OFF"))
        {
            if(dmg.isAdminActive(lock_componentName))
            {
                dmg.lockNow();
            }
        }
    }
}

/**
 * WIFI 쓰레드
 */

class wifiBackThread extends Thread{

    WifiManager wifiManager;

    wifiBackThread(WifiManager wifiManager)
    {
        this.wifiManager = wifiManager;
    }

    public void run()
    {
        while(!wifithread.isInterrupted())
        {
            if(wifiManager.isWifiEnabled())
            {
                wifiManager.disconnect();
            }
            try{

            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}

/**
 * 블루투스 쓰레드
 */
class bluetoothBackThread extends Thread{

    BluetoothAdapter mBluetoothAdapter;

    bluetoothBackThread(BluetoothAdapter mBluetoothAdapter)
    {
        this.mBluetoothAdapter = mBluetoothAdapter;
    }

    public void run()
    {
        while(!mbluetoothBackThread.isInterrupted())
        {
            if(mBluetoothAdapter == null)
                Log.d("블루투스 :" ,"블루투스 지원 안함..");

            if(mBluetoothAdapter.isEnabled())
            {
                mBluetoothAdapter.disable();
            }
            try{

            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}

class TetheringBackThread extends Thread{
    Context context;
    WifiManager wifiManager;

    TetheringBackThread(Context context, WifiManager wifiManager)
    {
        this.context = context;
        this.wifiManager = wifiManager;
    }

    MainModel mainModel = new MainModel();

    @Override
    public void run() {
        super.run();
        while(!tetheringBackThread.isInterrupted())
        {
            try {
                mainModel.setWifiTetheringDisabled(wifiManager);
                mainModel.toggleTethering(context);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

    }

}

