package org.androidtown.materialpractice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import static android.os.Build.VERSION.SDK_INT;
import static org.androidtown.materialpractice.R.menu.main;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    /**
     * MainActivity
     * - 네비게이션 드로어를 기능한다.
     *
     */

    private ApplicationManagerFragment applicationManagerFragment;
    private CommunicateFragment communicateFragment;
    private InformationFragment informationFragment;
    private BackupImgFragment backupImgFragment;
    private MainFragment mainFragment;
    static ImageView mainImg;
    public TextView mainName;
    public TextView mainJik;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * SDK 21버전부터 상태바 검은색 세팅
         */
        if (SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#000000"));
        }

        /**
         * 툴바
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        /**
         * 네비게이션 뷰 세팅
         */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View nav_header_view = navigationView.getHeaderView(0);
        mainImg = (ImageView)nav_header_view.findViewById(R.id.main_img);
        mainName = (TextView)nav_header_view.findViewById(R.id.main_name);
        mainJik = (TextView)nav_header_view.findViewById(R.id.main_jik);



        /**
         * 사용할 프래그먼트 인스턴스 가져오기.
         */
        communicateFragment = CommunicateFragment.newInstance();
        informationFragment = InformationFragment.newInstance();
        mainFragment = MainFragment.newInstance();
        applicationManagerFragment = ApplicationManagerFragment.newInstance();
        backupImgFragment = BackupImgFragment.newInstance();

        /**
         * SharedPreferences 및 HttpsConnection.class 세팅
         */

        SharedPreferences em = getSharedPreferences("firstlogin", 0);
        SharedPreferences userinfo = getSharedPreferences("User_info", 0);
        SharedPreferences profileinfo = getSharedPreferences("userinfo", 0);
        String serial = userinfo.getString("Id", "fail");
        String employee_num = em.getString("employee_num", "fail");
        HttpsConnection ht = new HttpsConnection();


        /**
         * 자신이 프로필 사진 올렸을때만 프로필 사진 세팅
         */
        if(profileinfo.getBoolean("profile",false))
        {
            ht.crollimg("https://58.141.234.126:50030/download", serial,mainImg);
        }

        /**
         * 네비게이션 뷰 이름 직급 세팅
         */

        ht.setName("https://58.141.234.126:50020/get_user_info", employee_num,mainName,mainJik);


        /**
         * 홈바 숨기기
         */
        //setHomebar();


        Intent intent = getIntent();
        if(intent != null)
        {
            if(intent.getExtras() != null)
            {
                if(intent.getExtras().getString("Backup")!=null)
                {
                    if(intent.getExtras().getString("Backup").equals("Img"))
                    {
                        Log.v("이미지","넘어옴");
                        Bundle bundle = new Bundle(1); //전달할 데이터 개수 1개
                        bundle.putString("Backup","Img");
                        backupImgFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fl_activity_main, backupImgFragment).commit();
                    }
                    else
                    {
                        Log.v("연락처","넘어옴");
                        Bundle bundle = new Bundle(1); //전달할 데이터 개수 1개
                        bundle.putString("Backup","Number");
                        backupImgFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fl_activity_main, backupImgFragment).commit();
                    }
                }
                if(intent.getExtras().getString("Gps")!=null)
                {
                    if(intent.getExtras().getString("Gps").equals("Go"))
                    {
                        Log.v("트루","넘어옴");
                        Bundle bundle = new Bundle(1); //전달할 데이터 개수 1개
                        bundle.putString("Gps","Go");
                        mainFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fl_activity_main, mainFragment).commit();
                    }
                    else
                    {
                        Log.v("false","넘어옴");
                        Bundle bundle = new Bundle(1); //전달할 데이터 개수 1개
                        bundle.putString("Gps","Stop");
                        mainFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fl_activity_main, mainFragment).commit();
                    }
                }
            }
        }


        /**
         * 메인 프래그먼트 활성화.
         */
        setMainFragment();

    }

    public void setHomebar()
    {
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled)
        {
            Log.d("태그","Turning immersive mode mode off.");
        }
        else
        {
            Log.d("태그","Turning immersive mode mode on.");
        }
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }

    public void setMainFragment()
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_activity_main, mainFragment).commit();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_information) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_activity_main, informationFragment).commit();
        } else if (id == R.id.nav_media) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_activity_main, communicateFragment).commit();
        } else if (id == R.id.nav_test) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_activity_main,applicationManagerFragment).commit();
        } else if (id == R.id.nav_communication) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_activity_main, communicateFragment).commit();
        } else if (id == R.id.nav_device) {

        } else if (id == R.id.nav_Img){

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

