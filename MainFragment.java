package org.androidtown.materialpractice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.androidtown.materialpractice.SHA.setSHA;

/**
 * Created by ahsxj on 2017-09-18.
 */

public class MainFragment extends Fragment implements MainFragmentPresenter.View {

    /**
     * MainFragment
     * - 실질적인 메인화면 기능을 수행한다.
     * - MVP(Model View Presenter)구조를 따른다.
     */

    View v;
    PackageManager pkgm;

    TextView text_status;
    TextView text_wifi;
    TextView text_bluetooth;
    TextView text_tether;
    TextView text_camera;
    TextView text_record;
    TextView text_check_day;
    TextView text_version;

    private MainFragmentPresenter presenter;
    private InformationFragment informationFragment;
    private CommunicateFragment communicateFragment;
    private BackupImgFragment backupImgFragment;
    private SettingFragment settingFragment;
    private ResultFragment resultFragment;
    private SharedPreferences Userinfo;
    private SharedPreferences networkFlag;
    private SharedPreferences loginHistory;
    private SharedPreferences.Editor networkEditor;

    private AlarmManager al;

    public static MainFragment newInstance()
    {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.testfragment,null);
        Gps gps = new Gps(getActivity());

        networkFlag = getActivity().getSharedPreferences("NetworkFlag",0);
        networkEditor = networkFlag.edit();

        loginHistory = getActivity().getSharedPreferences("login_history",0);

        Userinfo = getActivity().getSharedPreferences("User_info",0);
        al = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);

        ImageView img_information = (ImageView) v.findViewById(R.id.img_information);
        ImageView img_control = (ImageView) v.findViewById(R.id.img_control);
        ImageView img_check = (ImageView) v.findViewById(R.id.img_check);
        ImageView img_backup = (ImageView) v.findViewById(R.id.img_backup);
        ImageView img_setting = (ImageView) v.findViewById(R.id.img_setting);
        ImageView img_exit = (ImageView) v.findViewById(R.id.img_exit);
        String serial = Userinfo.getString("Id","fail");

        text_status = (TextView)v.findViewById(R.id.text_status_flag);
        text_wifi = (TextView)v.findViewById(R.id.text_wifi);
        text_bluetooth = (TextView)v.findViewById(R.id.text_bluetooth);
        text_tether = (TextView)v.findViewById(R.id.text_tether);
        text_camera = (TextView)v.findViewById(R.id.text_camera);
        text_record = (TextView)v.findViewById(R.id.text_record);
        text_check_day = (TextView)v.findViewById(R.id.text_check_day);
        text_version = (TextView)v.findViewById(R.id.text_version);

        informationFragment = InformationFragment.newInstance();
        communicateFragment = CommunicateFragment.newInstance();
        backupImgFragment = BackupImgFragment.newInstance();
        settingFragment = SettingFragment.newInstance();
        resultFragment = ResultFragment.newInstance();

        presenter = new MainFragmentPresenterImpl(MainFragment.this,serial);
        presenter.setView(this);

        img_information.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fl_activity_main, informationFragment).commit();
            }
        });

        img_control.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fl_activity_main, communicateFragment).commit();
            }
        });

        img_check.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
                alert_confirm.setMessage("기기검사 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface param, int which) {

                                ApplicationCheckTask applicationCheckTask = new ApplicationCheckTask();
                                applicationCheckTask.execute();
                                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fl_activity_main, resultFragment).commit();
                                text_check_day.setText(setTime());
                                networkEditor.putString("last_check",setTime());
                                networkEditor.apply();

                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'No'
                                return;
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
            }
        });

        img_backup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fl_activity_main, backupImgFragment).commit();
            }
        });

        img_setting.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fl_activity_main, settingFragment).commit();
            }
        });

        img_exit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
                alert_confirm.setMessage("종료 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface param, int which) {
                                getActivity().finish();
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'No'
                                return;
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
            }
        });

        setInit();

        if(loginHistory.getBoolean("history",false))
        {
            if(gps.isGetLocation())
            {
                onAlarm();
            }
            else
            {
                gps.showSettingsAlert();
                onAlarm();
            }
        }
        else
        {
            stopAlarm();
        }

        return v;
    }

    public void setVersion()
    {
        PackageInfo pa = null;
        try {
            pa = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        text_version.setText(pa.versionName);
    }

    public String setTime()
    {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");
        String getTime = sim.format(date);
        return getTime;
    }

    public void setInit()
    {
        String wifi = networkFlag.getString("WIFI","null");
        String blue = networkFlag.getString("BLUE", "null");
        String tether = networkFlag.getString("TETHER", "null");
        String camera = networkFlag.getString("CAMERA","null");
        String record = networkFlag.getString("RECORD","null");

        if(wifi.equals("true"))
            text_wifi.setText("허용");
        else
            text_wifi.setText("차단");

        if(blue.equals("true"))
            text_bluetooth.setText("허용");
        else
            text_bluetooth.setText("차단");

        if(tether.equals("true"))
            text_tether.setText("허용");
        else
            text_tether.setText("차단");

        if(camera.equals("true"))
            text_camera.setText("허용");
        else
            text_camera.setText("차단");

        if(record.equals("true"))
            text_record.setText("허용");
        else
            text_record.setText("차단");
        text_check_day.setText(networkFlag.getString("last_check","fail"));

        loginHistory = getActivity().getSharedPreferences("login_history",0);

        if(loginHistory.getBoolean("history",false))
        {
            text_version.setText("출근");
        }
        else
            text_version.setText("퇴근");
    }

    /*
        onAlarm - 서버에 주기적으로 Gps 위치정보를 보내기 위한 알람을 울린다.
     */
    public void onAlarm()
    {
        Intent intent = new Intent(getActivity().getApplicationContext(),AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(getActivity().getApplicationContext(),1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, cal.get(Calendar.SECOND)+10);
        long interval = 3;
        al.setRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),interval,pi);
    }

    /*
        stopAlarm - 알람을 해제한다.
     */
    public void stopAlarm()
    {
        Intent intent = new Intent(getActivity().getApplicationContext(),AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(getActivity().getApplicationContext(),1,intent,0);

        al.cancel(pi);
    }

    /*
        ApplicationCheckTask - 기기 검사(루팅 체크, 설치된 앱 변조체크)를 수행하는 AsyncTask
     */

    public class ApplicationCheckTask extends AsyncTask<Void,Void,Void>
    {
        private ProgressDialog progressDialog;
        private String serial;

        @Override
        protected Void doInBackground(Void... params) {

            /**
             * @param serial : UUID
             * @param jsonArray : 기기내 앱 정보들을 담을 JSONArray
             * @param pkgm : 기기내 앱 정보들을 뽑기 위한 PackageManager
             * @param Appinfos : 앱 정보들이 저장된 리스트
             *
             */
            serial = Userinfo.getString("Id","fail");
            JSONArray jsonArray = new JSONArray();
            pkgm = getActivity().getApplicationContext().getPackageManager();
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> AppInfos = pkgm.queryIntentActivities(intent, 0);
            for (ResolveInfo info : AppInfos)
            {
                ActivityInfo ai = info.activityInfo;
                /*
                    jsonObject
                    앱 하나당 정보가 저장되는 JSONObject를 만든다.
                 */
                JSONObject json = new JSONObject();
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
                    pi = getActivity().getPackageManager().getPackageInfo(ai.packageName, 0);
                    Signature[] signature = getActivity().getPackageManager().getPackageInfo(ai.packageName, PackageManager.GET_SIGNATURES).signatures;
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
            }
            if(!serial.equals("fail"))
            {
                presenter.sendAppInfo("https://58.141.234.126:55356/process/appmanage",serial,jsonArray,presenter.checkRooting(),Userinfo);
            }
            else
            {
                serial = Userinfo.getString("Id","fail");
                presenter.sendAppInfo("https://58.141.234.126:55356/process/appmanage",serial,jsonArray,presenter.checkRooting(),Userinfo);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getActivity(),null,"로딩중....");
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fl_activity_main, resultFragment).commit();
            super.onPostExecute(aVoid);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
