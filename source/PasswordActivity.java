package org.androidtown.materialpractice;

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
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static org.androidtown.materialpractice.SHA.setSHA;

/**
 * Created by ahsxj on 2017-07-01.
 */

public class PasswordActivity extends AppCompatActivity {

    /**
     * PasswordActivity
     * 1. 목적
     *  - 로그인과 비밀번호 재발급 기능을 수행한다.
     *
     *  @param login : 로그인 기능을 수행하는 Asynctask
     *  @param pp : 패스워드 재설정을 수행하는 Asynctask
     *  @param loginbtn : 로그인 확인 버튼
     *  @param findbtn :  패스워드 재설정 버튼
     *  @param logintext : 패스워드를 입력하는 EditText
     *  @param inputpassword : 입력받은 패스워드를 해시화해서 저장하는 변수
     *  @param Returndata : login을 실행하고 받은 반환값이 저장되는 변수
     *  @param EM : 사원번호
     *  @param uuid : 기기의 UUID
     */

    private Login login;
    private Button loginbtn;
    private EditText logintext;
    private String inputpassword = "null";
    private String Returndata = "null";
    private SharedPreferences Loginhistory;
    private String EM;
    private String uuid;
    private PasswordRecovery pp;
    private int logincheck;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= 21)
        {
            getWindow().setStatusBarColor(Color.parseColor("#000000"));
        }
        setContentView(R.layout.login_main);
        SharedPreferences userinfo = getSharedPreferences("User_info", 0);
        Button findbtn = (Button) findViewById(R.id.find_btn);
        uuid = userinfo.getString("Id","fail");
        loginbtn = (Button) findViewById(R.id.login_btn);
        logintext = (EditText) findViewById(R.id.login_text);
        logintext.bringToFront();
        Loginhistory = getSharedPreferences("login_history",0);
        SharedPreferences em = getSharedPreferences("firstlogin",0);
        EM = em.getString("employee_num","fail");

        /*
            비밀번호 재설정
            본인 인증은 구글 OTP 서비스를 이용한다.
         */
        findbtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(v.getContext());
                ad.setTitle("비밀번호 재설정");
                ad.setMessage("OTP입력");
                final EditText et = new EditText(PasswordActivity.this);
                et.setBackgroundResource(R.drawable.editbox_shape);
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                ad.setView(et);

                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String value = et.getText().toString();
                            pp = new PasswordRecovery("https://58.141.234.126:55306/otpverify", uuid, value);
                            pp.execute();
                            String result = pp.get();

                            /*
                                인증 성공하면 비밀번호 재설정을 위해 SetPasswordActivity로 넘어간다.
                             */
                            if (result.equals("success")) {
                                Toast.makeText(PasswordActivity.this, "OTP인증 성공", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(PasswordActivity.this,SetPasswordActivity.class));
                            }
                            else
                            {
                                Toast.makeText(PasswordActivity.this, "OTP 코드 에러", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(PasswordActivity.this, "OTP 취소", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                ad.show();
            }
        });

        /*
            자동 로그인
            출근중이고 로그인을 했다면 다음부터는 비밀번호를 입력하지 않아도 메인화면으로 넘어간다.
         */
        if(Loginhistory.getBoolean("history",false))
        {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            onPause();
        }
        else
        {
            loginbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!logintext.getText().equals("")) {
                        inputpassword = setSHA(String.valueOf(logintext.getText()));
                        login = new Login("https://58.141.234.126:50020/certification", inputpassword, EM, uuid);
                        try {
                            /*
                                login기능을 수행하고 리턴값으로 onSuccess를 호출한다.
                             */
                            Returndata = login.execute().get();
                            onSuccess(Returndata);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        AlertDialog.Builder ad = new AlertDialog.Builder(v.getContext());
                        ad.setTitle("로그인");
                        ad.setMessage("비밀번호를 입력해주세요");
                        ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logintext.requestFocus();
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.showSoftInput(logintext, InputMethodManager.SHOW_IMPLICIT);
                            }
                        });
                        ad.show();
                    }
                }
            });
        }

    }

    public void onSuccess(final String data) {
        /*
            리턴값이 1이라면 로그인 성공
         */
        switch (data) {
            case "1":
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                onPause();
                break;
            case "nno":
                Toast.makeText(this, "30초뒤에 다시눌러주세요", Toast.LENGTH_SHORT).show();
                break;
            default:
                logincheck++;
                Toast.makeText(getApplicationContext(), "비밀번호가 틀렸습니다", Toast.LENGTH_SHORT).show();
                if(logincheck == 2)
                {
                    Intent intent = new Intent(PasswordActivity.this,CaptureActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }


    @Override
    public void onPause()
    {
        super.onPause();
        if(loginbtn != null)
            loginbtn.setOnClickListener(null);
        loginbtn = null;
        logintext = null;
        Loginhistory = null;
        login = null;
        findViewById(R.id.password_View).setBackground(null);
        finish();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        System.gc();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            Rect outRect = new Rect();
            InputMethodManager imm = null;
            if (v != null) {
                imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            }
            if (logintext.isFocused()) {

                logintext.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    logintext.clearFocus();
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}

