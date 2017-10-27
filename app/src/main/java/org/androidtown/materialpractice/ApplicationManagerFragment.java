package org.androidtown.materialpractice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ahsxj on 2017-09-26.
 */

public class ApplicationManagerFragment extends ListFragment {

    /**
     * ApplicationManagerFragment
     * - 설치된 앱 목록들을 보여주고 삭제 할 수 있는 기능이 있다.
     */

    private View view;
    PackageManager pkgm;
    ActivityInfo ai = null;
    Drawable image;
    String title;
    String details;
    ListView listview;
    String pkn;

    ApplicationManagerListViewAdapter adapter;


    public static ApplicationManagerFragment newInstance()
    {
        ApplicationManagerFragment fragment = new ApplicationManagerFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_applicationmanager,container,false);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.i(CommunicateFragment.class.getSimpleName(), "onKey Back listener is working!!!");
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("finishstatus", true);
                    startActivity(intent);
                    getActivity().finish();
                    return true;
                } else {
                    return false;
                }
            }
        });
        return view;
    }


    public void getAppinfo()
    {
        /**
         * @param installTime : 앱이 설치된 시간
         */

        Date installTime;
        pkgm = getActivity().getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> AppInfos = pkgm.queryIntentActivities(intent, 0);
        ApplicationManagerListView listView = new ApplicationManagerListView();

        for (ResolveInfo info : AppInfos) {
            try {

                if((info.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    //시스템 앱인지 아닌지 확인 시스템 앱이 아니면 0
                }
                else
                {
                    ai = info.activityInfo;
                    image = ai.loadIcon(pkgm);
                    title = ai.loadLabel(pkgm).toString();
                    pkn = ai.packageName;
                    PackageInfo packageInfo = pkgm.getPackageInfo(pkn, PackageManager.GET_PERMISSIONS);
                    installTime = new Date( packageInfo.firstInstallTime );
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String installTimes = dateFormat.format( new Date( packageInfo.firstInstallTime ) );
                    details = "날짜:"+installTimes;
                    listView.setIcon(image);
                    listView.setTitle(title);
                    listView.setDesc(details);
                    listView.setPack(pkn);
                    adapter.addItem(image,title,details,pkn);
                }
            }
            catch (Exception e)
            {

            }

        }


    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ApplicationFragmenttask asytask = new ApplicationFragmenttask();
        asytask.execute();
        adapter = new ApplicationManagerListViewAdapter();

        listview = (ListView) getActivity().findViewById(android.R.id.list);


    }
    public class ApplicationFragmenttask extends AsyncTask<Void,Void,Void>
    {
        private ProgressDialog progressDialog;
        @Override
        protected Void doInBackground(Void... params) {
            getAppinfo();
            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getActivity(),null,"로딩중....");
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            listview.setAdapter(adapter);

            progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }

    @Override
    public void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<ApplicationManagerListView> listViewItemList = new ArrayList<ApplicationManagerListView>();
        int i = adapter.retPos();
        if(requestCode == i)
        {
            listViewItemList.remove(requestCode);
        }
    }
}
/**
 * @param PackageManager pkgm : 패키지 매니저 -> 앱 목록을 뽑기 위한 컴포넌트
 * @param List<ResolveInfo> Appinfos : 앱 목록이 저장되어 있는 리스트
 * @param ActivityInfo ai : 앱 정보를 뽑기 위한 컴포넌트
 * @param PackageInfo pi : 앱 정보중 버전 시그니처 등등을 뽑기 위한 컴포넌트
 *
 * 사용해야 할 것
 * ai.loadlabel(pkgm) : 앱 라벨(이름)을 뽑는 메소드
 * ai.loadicon(pkgm)  : 앱 아이콘을 뽑는 메소드
 *                      return value = drawable
 *
 */





