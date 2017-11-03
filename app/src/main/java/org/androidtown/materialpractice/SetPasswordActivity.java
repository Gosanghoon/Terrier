package org.androidtown.materialpractice;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;

import static android.os.Build.VERSION.SDK_INT;
import static org.androidtown.materialpractice.SHA.setSHA;


/**
 * Created by ahsxj on 2017-07-22.
 */

public class SetPasswordActivity extends Activity  {

    /**
     * SetPasswordActiivty
     * 1. 목적
     *  - 사용자의 사원번호와 비밀번호를 입력받는다.
     *
     * @param setnumbertext : 사원번호를 입력받는 EditText
     * @param setpasswordtext : 비밀번호를 입력받는 EditText
     * @param employee : 사원번호를 저장하는 SharedPreferences
     * @param ht : 통신을 위한 HttpsConnection.class
     * @param serial : 모바일 기기의 UUID
     * @param employee_num : 사용자의 사원번호
     * @param user_tel : 기기의 전화번호.
     */

    EditText setnumbertext;
    EditText setpasswordtext;
    Button setpasswordbutton;
    SharedPreferences employee;
    SharedPreferences Userinfo;
    HttpsConnection ht;
    String serial;
    String employee_num;
    String user_tel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
            상태창을 검게
         */
        if(SDK_INT >= 21)
        {
            getWindow().setStatusBarColor(Color.parseColor("#000000"));
        }
        setContentView(R.layout.setpasswordactivity);

        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle("로그인 정보 설정.");
        ad.setMessage("로그인 정보를 설정합니다. 비밀번호는 신중히 설정 해 주세요.");
        ad.setNegativeButton("닫기", null);
        ad.show();

        setnumbertext = (EditText) findViewById(R.id.setlogin_text2);
        setpasswordtext = (EditText) findViewById(R.id.setlogin_text);
        setpasswordbutton = (Button) findViewById(R.id.setlogin_btn);
        setpasswordtext.bringToFront();

        ht = new HttpsConnection();
        employee = getSharedPreferences("firstlogin",0);
        Userinfo = getSharedPreferences("User_info",0);
        serial = Userinfo.getString("Id","fail");
        user_tel = GetDeviceTel();
        final SharedPreferences.Editor em = employee.edit();


        setpasswordbutton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                em.putString("employee_num",setnumbertext.getText().toString());
                if(em.commit())
                {
                    sendDeviceInfo();
                    AlertDialog.Builder ad = new AlertDialog.Builder(v.getContext());
                    ad.setTitle("로그인 정보 설정");
                    ad.setMessage("로그인 정보 설정 완료. 로그인 화면으로 넘어갑니다.");
                    ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(getApplicationContext(), PasswordActivity.class));
                        }
                    });
                    ad.show();
                }
                else
                {
                    em.putString("employee_num",setnumbertext.getText().toString());
                    if(em.commit())
                    {
                        sendDeviceInfo();
                        AlertDialog.Builder ad = new AlertDialog.Builder(v.getContext());
                        ad.setTitle("로그인 정보 설정");
                        ad.setMessage("로그인 정보 설정 완료. 로그인 화면으로 넘어갑니다.");
                        ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(getApplicationContext(), PasswordActivity.class));
                            }
                        });
                        ad.show();
                    }
                }
            }
        });
    }

    @Override
    public void onPause()
    {
        super.onPause();
        finish();
    }

    /*
        메모리 해제를 위해서 강제적으로 백그라운드를 널로준다.
     */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        findViewById(R.id.setpassword_View).setBackground(null);
        System.gc();
    }

    /*
        서버에 디바이스 정보를 보내준다.
     */
    public void sendDeviceInfo() {
        /**
         * @param name : 안드로이드 버전 명시적 이름
         * @param version : 안드로이드 버전 숫자
         */
        String name = null;
        String version = null;
        employee_num = employee.getString("employee_num","fail");
        switch(Build.VERSION.SDK_INT)
        {
            case 14:
                name = "Ice Cream Sandwich";
                version = "4.0";
                break;
            case 15:
                name = "Ice Cream Sandwich";
                version = "4.0.3";
                break;
            case 16:
                name = "Jelly Bean";
                version = "4.1";
                break;
            case 17:
                name = "Jelly Bean";
                version = "4.2";
                break;
            case 18:
                name = "Jelly Bean";
                version = "4.3";
                break;
            case 19:
                name = "Kitkat";
                version = "4.4";
                break;
            case 20:
                name = "Kitkat Watch";
                version = "4.4W";
                break;
            case 21:
                name = "Lollipop";
                version = "5.0";
                break;
            case 22:
                name = "Lollipop";
                version = "5.1";
                break;
            case 23:
                name = "MarshMallow";
                version = "6.0";
                break;
            case 24:
                name = "Nougat";
                version = "7.0";
                break;
        }
        /*
            시리얼값 체크
         */
        if(!serial.equals("fail"))
        {
            ht.sendDeviceInfo("https://58.141.234.126:50020/on_fresh",serial,employee_num,FirebaseInstanceId.getInstance().getToken(),
                    setSHA(setpasswordtext.getText().toString()),name,version,Build.MANUFACTURER,user_tel);
        }
        else
        {
            serial = Userinfo.getString("Id","fail");
            ht.sendDeviceInfo("https://58.141.234.126:50020/on_fresh",serial,employee_num,FirebaseInstanceId.getInstance().getToken(),
                    setSHA(setpasswordtext.getText().toString()),name,version,Build.MANUFACTURER,user_tel);
        }

    }
     /*
        기기의 전화번호를 얻는다.
     */
    public String GetDeviceTel() {
        TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String tel = null;
        tel = tm.getLine1Number();
        return tel;
    }

    /*
        키보드 입력중 다른 곳을 터치하면 포커스를 없앤다.
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            Rect outRect = new Rect();
            InputMethodManager imm = null;
            if (v != null) {
                imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (setnumbertext.isFocused()) {

                    setnumbertext.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                        setnumbertext.clearFocus();
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
                if (setpasswordtext.isFocused()) {

                    setpasswordtext.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                        setpasswordtext.clearFocus();
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }


}


