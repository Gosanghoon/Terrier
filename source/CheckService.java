package org.androidtown.materialpractice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by ahsxj on 2017-09-16.
 */

public class CheckService extends Service {

    /**
     * CheckService
     * 출퇴근 기능을 수행하는 서비스
     */

    private WifiScanReceiver   mWifiScanReceiver;
    static WifiManager         wifiManager;
    ConnectivityManager manager;

    CheckService getService()
    {
        return CheckService.this;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressWarnings("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        /**
         * 동작방식
         *  인텐트에 액션을 추가해서 WifiScanReceiver라는 브로드캐스트 리시버에 전송한다.
         *
         *  @param wifiManager : 와이파이 스캔을 하기 위한 와이파이 매니저
         *  @param mWifiScanReceiver : WifiScanReceiver
         *
         */

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        manager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifiScanReceiver = new WifiScanReceiver();
        final IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        this.registerReceiver(mWifiScanReceiver, filter);
        wifiManager.startScan();
        return super.onStartCommand(intent, START_REDELIVER_INTENT, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
