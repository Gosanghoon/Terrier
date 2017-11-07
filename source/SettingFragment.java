package org.androidtown.materialpractice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;

import static org.androidtown.materialpractice.MainActivity.mainImg;


/**
 * Created by ahsxj on 2017-09-07.
 */

public class SettingFragment extends Fragment {

    HttpsConnection ht = new HttpsConnection();
    SharedPreferences userinfo;
    SharedPreferences loginHistory;
    MainActivity mainActivity;
    Button btn_checkin;
    Button btn_checkout;
    String serial;


    public static SettingFragment newInstance()
    {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting,container,false);
        mainActivity = new MainActivity();
        Button choice = (Button)view.findViewById(R.id.choice_btn);
        btn_checkin = (Button)view.findViewById(R.id.button2);
        btn_checkout = (Button)view.findViewById(R.id.button3);
        userinfo = getActivity().getSharedPreferences("User_info",0);
        serial = userinfo.getString("Id","fail");
        loginHistory = getActivity().getSharedPreferences("login_history",0);
        final SharedPreferences.Editor ed = loginHistory.edit();

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

        choice.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                getGallary();
                userinfo = getActivity().getSharedPreferences("userinfo", 0);
                SharedPreferences.Editor useredit = userinfo.edit();
                useredit.putBoolean("profile",true);
                useredit.apply();
                useredit = null;
            }
        });

        btn_checkin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                ht.sendCheckIn("https://58.141.234.126:55356/process/deviceonoff",serial);
                ed.putBoolean("Check",true);
                ed.commit();
                Log.v("출퇴근값", String.valueOf(loginHistory.getBoolean("history",false)));
            }
        });

        btn_checkout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                ht.checkout("https://58.141.234.126:55356/process/deviceonoff",serial);
                ed.putBoolean("Check",false);
                ed.putBoolean("history",false);
                ed.commit();
                Log.v("출퇴근값", String.valueOf(loginHistory.getBoolean("history",false)));
            }
        });

        return view;
    }

    public void copyImage(List<Uri> list)
    {
        final List<Uri> List = list;
        int i = 0;
        for(Uri object:List) {
            try {
                ++i;
                String path = String.valueOf(object);
                Uri uri = Uri.parse(String.valueOf(object));
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),uri);
                if(bitmap == null)
                {
                    Log.d("비트맵:","null");
                }
                else {
                    ByteArrayOutputStream bytearray = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,50, bytearray);
                    byte[] bytes = bytearray.toByteArray();
                    String[] buffer = path.split("/");
                    HttpsConnection ht = new HttpsConnection();
                    ht.imgBackup("https://58.141.234.126:50030/thumbnail",serial,bytes,buffer[8],"ff","ff","profile");
                    bitmap = null;
                    bytearray.reset();
                    bytes = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getGallary()
    {
        TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(getActivity().getApplicationContext())
                .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(Uri uri) {
                        Bitmap bitmap = null;
                        Bitmap result = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),uri);
                            if(bitmap != null)
                            {
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inSampleSize = 4;
                                result = Bitmap.createScaledBitmap(bitmap,(bitmap.getWidth()/2),(bitmap.getHeight()/2),true);
                                mainImg.setImageBitmap(result);
                                ArrayList<Uri> list= new ArrayList<>();
                                list.add(uri);
                                copyImage(list);
                                list = null;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }finally{
                            bitmap = null;
                            result = null;
                        }


                    }
                })
                .setPeekHeight(1600)
                .setCompleteButtonText("완료")
                .setEmptySelectionText("No Select")
                .create();

        bottomSheetDialogFragment.show(getActivity().getSupportFragmentManager());
    }
}
