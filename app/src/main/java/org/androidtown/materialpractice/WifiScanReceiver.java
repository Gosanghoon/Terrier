package org.androidtown.materialpractice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static org.androidtown.materialpractice.CheckService.wifiManager;

/**
 * Created by ahsxj on 2017-09-16.
 */

public class WifiScanReceiver extends BroadcastReceiver {

    /**
     * WifiSCanReceiver
     * - 출퇴근 기능을 수행 한다.
     * - 주기적으로 와이파이 목록을 스캔하여 해당 MAC주소가 있는지 여부에 따라 출퇴근을 수행한다.
     * - 현재는 테스트 용도로 SSID(무선 랜 이름)을 이용해서 동작한다.
     */

    private Boolean           flag = false;
    private HttpsConnection   ht;
    private List<ScanResult>  scanResult;
    private ArrayList<String> macList;
    private String homeMac = "88:36:6c:7b:0f:e4";
    private String projectMac = "64:E5:99:a7:65:59";
    private int               HomeId;
    private int               ProjectId;
    private WifiConfiguration homeConfig;
    private WifiConfiguration projectConfig;
    private SharedPreferences Userinfo;
    private SharedPreferences loginHistory;
    private Boolean           flagHistory;


    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if(action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        {
            Userinfo = context.getSharedPreferences("User_info",0);
            loginHistory = context.getSharedPreferences("login_history",0);
            scanResult(context,Userinfo.getString("Id","fail"),loginHistory);
            wifiManager.startScan();
        }
    }

    public void scanResult(Context context, String id,SharedPreferences history) {
        Context Context = context;
        String Id = id;
        SharedPreferences History = history;
        SharedPreferences.Editor ed = History.edit();
        scanResult = wifiManager.getScanResults();
        ht = new HttpsConnection();
        macList = new ArrayList<String>();
        flagHistory = History.getBoolean("history",false);

        /**
         * 스캔 결과에서 MAC주소만 담는 리스트에 MAC주소만 복사.
         */
        for(int i = 0; i < scanResult.size(); i++)
        {
            Log.d("결과","복사중");
            ScanResult result = scanResult.get(i);
            //macList.add(result.BSSID);
            macList.add(result.SSID);
        }

        /**
         * MAC 리스트에 해당하는 주소가 있으면 TRUE 없으면 FALSE 플래그
         */

        for(int i = 0; i < macList.size(); i++)
        {
//            if(macList.get(i).equals(homeMac))
//            {
//                flag = true;
//                break;
//            }
//            else
//                flag = false;
            if(macList.get(i).equals("KSH3G"))
            {
                flag = true;
                break;
            }
            else
                flag = false;
        }
        if(flag)
        {
            /**
             * boolean 형 변수 넣어서 equals로 비교하자.
             */
            if(flagHistory.equals(false))
            {
                Intent intent = new Intent(Context,SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Context.startActivity(intent);

                Log.d("출근","출근");
                ht.sendCheckIn("https://58.141.234.126:55356/process/deviceon",Id);
                ed.putBoolean("history",true);
                ed.apply();
            }
        }
        else
        {
            if(flagHistory.equals(true))
            {
                Log.d("퇴근","퇴근");
                ht.checkout("https://58.141.234.126:55356/process/deviceoff",Id);
                ed.putBoolean("history",false);
                ed.apply();
            }
        }
    }
}
