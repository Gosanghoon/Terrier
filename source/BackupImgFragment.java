package org.androidtown.materialpractice;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
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


    static ProgressDialog dialog;

    /**
     * BackupImgFragment
     * 기기내 이미지(다운로드 가능),연락처를 백업하는 기능을 한다.
     */

    private View view;
    private HttpsConnection ht = new HttpsConnection();
    private SharedPreferences Userinfo;
    private Context mContext;
    private WarningFragment warningFragment;


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
        mContext = getActivity().getApplicationContext();
        warningFragment = WarningFragment.newInstance();

        /**
         * 프래그먼트 뒤로가기
         */
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



        /**
         * 사용자가 연락처를 업로드 하겠다고 하는 경우
         */
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

        /**
         * 사용자가 이미지를 선택해서 업로드 하겠다고 하는 경우
         */
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

        /**
         * 사용자가 모든 이미지를 업로드 하겠다고 하는 경우
         */
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
                                        List<Uri> imglist = fetchAllImages(mContext);
                                        copyImage(imglist,mContext);
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
        /**
         * 사용자가 서버에 업로드 한 모든 이미지를 다운로드 하겠다고 하는경우
         */
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


        if(getArguments()!=null)
        {
            String backupFlag = getArguments().getString("Backup");
            if(backupFlag.equals("Img"))
            {
                ImgReceive();
            }
            else
            {
                NumberReceive();
            }
        }
        return view;
    }

    public void NumberReceive()
    {
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
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fl_activity_main, warningFragment).commit();
        //getActivity().finish();

    }

    public void ImgReceive()
    {
        ProgressDialog("up");
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run() {
                List<Uri> imglist = fetchAllImages(mContext);
                copyImage(imglist,mContext);
            }
        });
        thread.start();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fl_activity_main, warningFragment).commit();
        //getActivity().finish();
    }


    /**
     * void getUserContact()
     * ContentProvider를 이용해 기기내 저장된 연락처(이름,번호,이메일)를 서버에 백업한다.
     */

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

    /**
     * copyImage()
     * fetchAllImage()의 결과값으로 이미지의 Uri가 들어있는 리스트를 이용하여
     * 리스트에 들어있는 순서대로 서버에 하나씩 전송한다.
     */

    public void copyImage(List<Uri> list, Context context)
    {
        final List<Uri> List = list;
        final Context mContext = context;
        int i = 0;
        for(Uri object:List) {
            try {
                ++i;
                Log.d("path:", String.valueOf(object));
                String path = String.valueOf(object);
                Uri uri = Uri.parse(String.valueOf(object));
                Log.d("path:", String.valueOf(uri));
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(),uri);
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

    /**
     * fetchAllImages()
     * ContentProvider를 이용하여 기기내 저장된 이미지들의 Uri를 리스트에 저장 후 리스트를 반환한다.
     *
     */

    List<Uri> fetchAllImages(Context context)
    {
        ArrayList<Uri> result = null;
        String ext = Environment.getExternalStorageDirectory().toString();
        Uri FileUri = Uri.parse(ext);
        String filePath2 = FileUri.getPath();
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED) {
            String[] projection = {MediaStore.Images.Media.DATA};
            ContentResolver contentResolver = context.getContentResolver();
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

    /**
     * getGallary()
     * 사용자가 이미지를 선택해서 올릴 경우 갤러리를 보여준다.
     * 오픈소스 사용
     * https://github.com/ParkSangGwon/TedBottomPicker
     */
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
                                copyImage(uriList,mContext);
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
