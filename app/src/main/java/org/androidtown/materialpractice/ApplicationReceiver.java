package org.androidtown.materialpractice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import static org.androidtown.materialpractice.SHA.setSHA;

/**
 * Created by ahsxj on 2017-09-25.
 */

public class ApplicationReceiver extends BroadcastReceiver {

    /**
     * ApplicationReceiver
     * 기기내 설치된 앱들의 액션(새로운 앱 설치 , 기존 앱 업데이트 , 기존 앱 삭제)이 감지되면
     * 해당 내용을 서버에 전송한다.
     */

    private static final String TAG = "Observer";
    private HttpsConnection ht;
    private SharedPreferences userinfo;


    @Override
    public void onReceive(Context context, Intent intent) {

        userinfo = context.getSharedPreferences("User_info",0);
        ht = new HttpsConnection();
        Log.v(TAG, "intent : " + intent);
        Log.v(TAG, "action : " + intent.getAction());
        Log.v(TAG, "data : " + intent.getDataString());


        String[] resultAction = intent.getAction().split("\\.");
        String[] resultData = intent.getDataString().split(":");

        for(String result:resultAction)
        {
            Log.v("result:",result);
        }

        for(String result:resultData)
        {
            Log.v("result:",result);
        }

        Log.v(TAG, "action : " + resultAction[3]);
        Log.v(TAG, "data : " + resultData[1]);

        if(resultAction[3].equals("PACKAGE_REMOVED"))
        {
            /**
             * 액션이 삭제일때
             * 액션과 데이터만 보내주자.
             */
            ht.sendAppInfo("https://58.141.234.126:55356/process/appupdate",userinfo.getString("Id","fail"),resultAction[3],resultData[1]);
        }
        else
        {
            /**
             * 삭제가 아니라면
             * 액션과 데이터 , 그 다음 앱 정보들 보내주자.
             */
            sendAppInfo(context,resultAction[3],resultData[1]);
        }
    }

    /**
     * 앱이 새로 설치하거나 업데이트 되었을때 어떤 앱이 어떤 액션을 취했는지와 그 앱에 대한 정보를 서버로 전송한다.
     */
    public void sendAppInfo(Context context , String action , String data) {
        Context Context = context;
        String Action = action;
        String Data = data;
        userinfo = Context.getSharedPreferences("User_info",0);

        JSONArray jsonArray = new JSONArray();
        PackageManager pkgm = Context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> AppInfos = pkgm.queryIntentActivities(intent, 0);
        for (ResolveInfo info : AppInfos)
        {
            ActivityInfo ai = info.activityInfo;
            JSONObject json = new JSONObject();
            if(data.equals(ai.packageName))
            {
                try
                {
                    json.put("name", ai.loadLabel(pkgm).toString());
                    json.put("packagename", ai.packageName);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                ApplicationInfo tmpInfo = null;
                try
                {
                    tmpInfo = pkgm.getApplicationInfo(ai.packageName, PackageManager.GET_META_DATA);
                    long size = new File(tmpInfo.sourceDir).length();
                    json.put("size", size);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                PackageInfo pi = null;
                try
                {
                    pi = Context.getPackageManager().getPackageInfo(ai.packageName,0);
                    Signature[] signature = Context.getPackageManager().getPackageInfo(ai.packageName, PackageManager.GET_SIGNATURES).signatures;
                    json.put("version", pi.versionName);
                    for (Signature sig : signature)
                    {
                        json.put("signature", setSHA(sig.toString()));
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                jsonArray.put(json);
                break;
            }
        }
        ht.sendAppInfo("https://58.141.234.126:55356/process/appupdate",userinfo.getString("Id","fail"),Action,Data,jsonArray);
    }
}
