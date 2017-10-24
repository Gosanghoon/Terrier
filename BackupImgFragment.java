package org.androidtown.materialpractice;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;



/**
 * Created by ahsxj on 2017-08-28.
 */

public class BackupImgFragment extends Fragment {
    private View view;
    private HttpsConnection ht = new HttpsConnection();
    private SharedPreferences Userinfo;


    public static BackupImgFragment newInstance()
    {
        BackupImgFragment fragment = new BackupImgFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_backup,container,false);
        Button choice = (Button)view.findViewById(R.id.choice_btn);
        Button all = (Button)view.findViewById(R.id.all_btn);
        Button allDown = (Button)view.findViewById(R.id.all_down_btn);
        Button all_number = (Button)view.findViewById(R.id.all_number_btn);

        Userinfo = getActivity().getSharedPreferences("User_info",0);

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

        all_number.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
                alert_confirm.setMessage("업로드 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface param, int which) {
                                ProgressDialog("up");
                                dialog.setMessage("연락처 업로드 중");
                                Thread thread = new Thread(new Runnable()
                                {
                                    @Override
                                    public void run() {
                                        getUserContact();
                                    }
                                });
                                thread.start();
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

        choice.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
                alert_confirm.setMessage("업로드 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface param, int which) {
                                getGallary();
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

        all.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
                alert_confirm.setMessage("업로드 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface param, int which) {
                                ProgressDialog("up");
                                Thread thread = new Thread(new Runnable()
                                {
                                    @Override
                                    public void run() {
                                        List<Uri> imglist = fetchAllImages();
                                        copyImage(imglist);
                                    }
                                });
                                thread.start();
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

        allDown.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
                alert_confirm.setMessage("다운로드 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface param, int which) {
                                ProgressDialog("down");
                                ht.allimageDownload("https://58.141.234.126:50030/download",Userinfo.getString("Id","fail"));
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

        return view;
    }

    public void getUserContact()
    {
        String [] arrProjection = {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
        };
        String [] arrPhoneProjection = {
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        String [] arrEmailProjection = {
                ContactsContract.CommonDataKinds.Email.DATA
        };

        if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_DENIED)
        {
            ContentResolver contentResolver= getActivity().getApplicationContext().getContentResolver();
            Cursor infoCursor = contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI, arrProjection,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1",
                    null,null
            );

            while(infoCursor.moveToNext())
            {
                String ContactId = infoCursor.getString(0);
                Log.d("연락처테스트","사용자 ID:" + infoCursor.getString(0));
                Log.d("연락처테스트","사용자 이름:" + infoCursor.getString(1));

                JSONObject json = new JSONObject();
                try {
                    json.put("id",Userinfo.getString("Id","fail"));
                    json.put("na",infoCursor.getString(1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                /**
                 * 번호 뽑기
                 */
                Cursor numberCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        arrPhoneProjection,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId
                        ,null,null
                );

                while(numberCursor.moveToNext())
                {
                    Log.d("연락처테스트","사용자번호:" + numberCursor.getString(0));
                    try {
                        json.put("nu",numberCursor.getString(0));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                numberCursor.close();

                Cursor emailCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        arrEmailProjection,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId,
                        null,null
                );

                while(emailCursor.moveToNext())
                {
                    Log.d("연락처테스트","사용자이메일:" + emailCursor.getString(0));
                    try {
                        json.put("email",emailCursor.getString(0));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                emailCursor.close();
                HttpsConnection ht = new HttpsConnection();
                ht.numberBackup("https://58.141.234.126:50030/number_backup",json);
            }
            dialog.dismiss();
        }
    }

    public void copyImage(List<Uri> list)
    {
        final List<Uri> List = list;
        int i = 0;
        for(Uri object:List) {
            try {
                ++i;
                Log.d("path:", String.valueOf(object));
                String path = String.valueOf(object);
                Uri uri = Uri.parse(String.valueOf(object));
                Log.d("path:", String.valueOf(uri));
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),uri);
                if(bitmap == null)
                {
                    Log.d("비트맵:","null");
                }
                else {
                    ByteArrayOutputStream bytearray = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,50, bytearray);
                    byte[] bytes = bytearray.toByteArray();
                    File f = new File(uri.getPath());
                    String[] buffer = path.split("/");
                    Log.d("파일이름:",buffer[8]);
                    Log.d("파일크기:", String.valueOf(f.length()));
                    Log.d("파일총갯수:", String.valueOf(List.size()));
                    Log.d("파일해당번호:", String.valueOf(i));
                    Log.d("파일 바이트:", String.valueOf(bytes));
                    HttpsConnection ht = new HttpsConnection();
                    ht.imgBackup("https://58.141.234.126:50030/img_backup",Userinfo.getString("Id","fail"),bytes,buffer[8],String.valueOf(List.size()),String.valueOf(i),"no");
                    bitmap.recycle();
                    bitmap = null;
                    bytearray.reset();
                    bytes = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    List<Uri> fetchAllImages()
    {
        ArrayList<Uri> result = null;
        String ext = Environment.getExternalStorageDirectory().toString();
        Uri FileUri = Uri.parse(ext);
        String filePath2 = FileUri.getPath();
        if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext() , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED) {
            String[] projection = {MediaStore.Images.Media.DATA};
            ContentResolver contentResolver = getActivity().getApplicationContext().getContentResolver();
            Cursor imageCursor = contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // 이미지 컨텐트 테이블
                    projection, // DATA를 출력
                    null,
                    null,       // 모든 개체 출력
                    null);      // 정렬 안 함
            result = new ArrayList<>(imageCursor.getCount());
            int dataColumnIndex = imageCursor.getColumnIndex(projection[0]);
            //noinspection ConstantConditions
            if (imageCursor == null) {
                Log.e("이미지커서:", "NULL이다");
            } else if (imageCursor.moveToFirst()) {
                do {
                    String filePath = imageCursor.getString(dataColumnIndex);
                    String path = "file://"+ filePath;
                    Uri uri = Uri.parse(path);
                    Log.d("경로",path);
                    result.add(uri);
                } while (imageCursor.moveToNext());
            } else {
                Log.e("이미지커서:", "비었다");
            }
            imageCursor.close();
        }
        else
        {
            Log.d("리드스토리지","권한 없다");
        }
        return result;
    }

    private void getGallary()
    {
        TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(getActivity().getApplicationContext())
                .setOnMultiImageSelectedListener(new TedBottomPicker.OnMultiImageSelectedListener() {
                    @Override
                    public void onImagesSelected(final ArrayList<Uri> uriList) {
                        // here is selected uri list
                        ProgressDialog("up");
                        Thread thread = new Thread(new Runnable()
                        {
                            @Override
                            public void run() {
                                copyImage(uriList);
                            }
                        });
                        thread.start();
                    }
                })
                .setPeekHeight(2000)
                .setCompleteButtonText("완료")
                .setEmptySelectionText("No Select")
                .create();

        bottomSheetDialogFragment.show(getActivity().getSupportFragmentManager());
    }

    static ProgressDialog dialog;
    public void ProgressDialog(String flag)
    {
        dialog = new ProgressDialog(getActivity());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if(flag.equals("down"))
            dialog.setTitle("              이미지 다운로드");
        else if(flag.equals("up"))
            dialog.setTitle("              이미지 업로드");
        dialog.setCancelable(false);
        dialog.show();
    }
}
