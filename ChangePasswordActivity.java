package org.androidtown.materialpractice;

/**
 * Created by ahsxj on 2017-09-11.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.UUID;

import static org.androidtown.materialpractice.SHA.setSHA;

/**
 * Created by Administrator on 2017-08-23.
 */

public class ChangePasswordActivity extends Activity {

    Button ChgBtn;
    EditText chgPwdEditText;
    String UUID;
    String ChgPwd;
    SharedPreferences Loginhistory;
    SharedPreferences.Editor history;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);

        ChgBtn = (Button) findViewById(R.id.chgpwd_btn);
        chgPwdEditText = (EditText) findViewById(R.id.chgpwd_text);
        chgPwdEditText.bringToFront();
        Loginhistory = getSharedPreferences("login_history",0);
        history = Loginhistory.edit();
        history.putBoolean("login_history",true).apply();

        ChgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!chgPwdEditText.getText().toString().isEmpty())
                {
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(v.getContext());
                    alert_confirm.setMessage("변경 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface param, int which) {
                                    ChgPwd = setSHA(String.valueOf(chgPwdEditText.getText()));
                                    UUID = GetDeviceSerial();
                                    HttpsConnection ht = new HttpsConnection();
                                    ht.ChgPwdhttps("https://58.141.234.126:50020/change_password",UUID,ChgPwd);
                                    startActivity(new Intent(getApplicationContext(),PasswordActivity.class));
                                    finish();
                                }
                            }).setNegativeButton("취소",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    return;
                                }
                            });
                    AlertDialog alert = alert_confirm.create();
                    alert.show();
                }
                else{
                    AlertDialog.Builder ad = new AlertDialog.Builder(v.getContext());
                    ad.setTitle("비밀번호 변경");
                    ad.setMessage("비밀번호를 입력해주세요");
                    ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            chgPwdEditText.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(chgPwdEditText, InputMethodManager.SHOW_IMPLICIT);
                        }
                    });
                    ad.show();
                }
            }
        });

    }

    @SuppressLint("HardwareIds")
    public String GetDeviceSerial() {
        TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String tmDevice = "" + tm.getDeviceId();
        String tmSerial = "" + tm.getSimSerialNumber();
        String androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();

        return deviceId;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            Rect outRect = new Rect();
            InputMethodManager imm = null;
            if (v != null) {
                imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (chgPwdEditText.isFocused()) {

                    chgPwdEditText.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                        chgPwdEditText.clearFocus();
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(ChgBtn != null)
            ChgBtn.setOnClickListener(null);

        findViewById(R.id.chgpassword_View).setBackground(null);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}
