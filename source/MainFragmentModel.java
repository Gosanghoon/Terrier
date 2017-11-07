package org.androidtown.materialpractice;

import android.content.SharedPreferences;
import android.os.Environment;

import org.json.JSONArray;

import java.io.File;

/**
 * Created by ahsxj on 2017-08-07.
 */

public class MainFragmentModel {

    private String serial;

    MainFragmentModel(String serial)
    {
        this.serial = serial;
    }

    HttpsConnection ht = new HttpsConnection();

    //최초 설치 후 서버에 앱 정보를 보낸다.
    public void sendAppinfo(String url, String serial, JSONArray jsonArray)
    {
        ht.sendAppInfo(url,serial,jsonArray);
    }

    //기기검사
    public void sendAppinfo(String url, String serial, JSONArray jsonArray, boolean rootflag, SharedPreferences sh)
    {
        ht.sendDeviceCheck(url,serial,jsonArray,rootflag,sh);
    }

    //퇴근
    public void sendCheckOut()
    {
        ht.checkout("https://58.141.234.126:55356/process/deviceoff",serial);
    }

    //서버에 gps 위치정보 전송
    public void sendGpsInfo(double latitude, double longitude)
    {
        ht.sendGPS("https://58.141.234.126:55356/process/locationadd",serial,latitude,
            longitude);
    }

    public File[] createFiles(String[] sfiles)
    {
        File[] rootingFiles = new File[sfiles.length];
        for(int i = 0; i < sfiles.length; i++)
        {
            rootingFiles[i] = new File(sfiles[i]);
        }
        return rootingFiles;
    }

    private boolean checkRootingFiles(File... file){
        boolean result = false;
        for(File f : file){
            if(f != null && f.exists() && f.isFile()){
                result = true;
                break;
            }else{
                result = false;
            }
        }
        return result;
    }

    public Boolean check_root()
    {
        boolean isRootingFlag = false;

        final String ROOT_PATH = Environment.
                getExternalStorageDirectory() + "";
        final String ROOTING_PATH_1 = "/system/bin/su";
        final String ROOTING_PATH_2 = "/system/xbin/su";
        final String ROOTING_PATH_3 = "/system/app/SuperUser.apk";
        //final String ROOTING_PATH_4 = "/data/data/com.noshufou.android.su";

        String[] RootFilesPath = new String[]{
                ROOT_PATH + ROOTING_PATH_1 ,
                ROOT_PATH + ROOTING_PATH_2 ,
                ROOT_PATH + ROOTING_PATH_3
                //ROOT_PATH + ROOTING_PATH_4
        };

        try
        {
            Runtime.getRuntime().exec("su -");
            isRootingFlag = true;
        }
        catch(Exception e)
        {
            isRootingFlag = false;
        }
        if(!isRootingFlag)
        {
            isRootingFlag = checkRootingFiles(createFiles(RootFilesPath));
        }

        return isRootingFlag;
    }

}
