package org.androidtown.materialpractice;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.JsonReader;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static org.androidtown.materialpractice.BackupImgFragment.dialog;
import static org.androidtown.materialpractice.HttpsConnection.trustAllHosts;
import static org.androidtown.materialpractice.Login.setValue;



/**
 * Created by ahsxj on 2017-07-10.
 */

public class HttpsConnection {

    public void sendRemove(String url, String serial)
    {
        final String paramURL = url;
        final String Serial = serial;

        Thread thread = new Thread(){
            @Override
            public void run() {
                HttpURLConnection conn = null;
                String urlString = paramURL;

                try{
                    URL url = new URL(urlString);
                    trustAllHosts();

                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });

                    conn = httpsURLConnection;
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type","application/json");
                    conn.connect();

                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                    JSONObject json = new JSONObject();

                    try{
                        json.put("Id",Serial);
                        json.put("flag","관리자 권한 해제");
                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                    bufferedWriter.write(json.toString());
                    bufferedWriter.close();
                    outputStream.close();

                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {
                        Log.d("정보 전송 : ", "성공");
                    }
                    else
                        Log.d("정보 전송 : ", "실패");

                    conn.disconnect();
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public void setName(String url, String em , TextView name , TextView jik)
    {
        final String paramURL = url;
        final String paramEM = em;
        final TextView paramName = name;
        final TextView paramJik  = jik;

        Thread thread = new Thread(){
            @Override
            public void run() {
                HttpURLConnection conn = null;
                String urlString = paramURL;

                try{
                    URL url = new URL(urlString);
                    trustAllHosts();

                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });

                    conn = httpsURLConnection;
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type","application/json");
                    conn.connect();

                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                    JSONObject json = new JSONObject();
                    try{
                        json.put("em",paramEM);
                    }catch(Exception e)

                    {
                        e.printStackTrace();
                    }
                    bufferedWriter.write(json.toString());
                    bufferedWriter.close();
                    outputStream.close();

                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {
//                        InputStream inputStream = conn.getInputStream();
//                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                        Log.d("셋 네임정보 전송 : ", "성공");
//                        Log.d("받은거",bufferedReader.readLine());
//                        bufferedReader.close();
//                        inputStream.close();

                        JsonReader jsonReader = new JsonReader(new InputStreamReader(conn.getInputStream()));
                        jsonReader.beginObject();
                        String Name = null;
                        String Rank1 = null;
                        String Rank2 = null;
                        String resultRank = null;

                        while(jsonReader.hasNext())
                        {
                            String key = jsonReader.nextName();
                            if(key.equals("Name"))
                            {
                                Name = jsonReader.nextString();
                                Log.d("수신테스트",Name);
                            }
                            else if(key.equals("Rank"))
                            {
                                Rank1 = jsonReader.nextString();
                                Log.d("수신테스트",Rank1);
                            }
                            else if(key.equals("Department"))
                            {
                                Rank2 = jsonReader.nextString();
                                Log.d("수신테스트",Rank2);
                                break;
                            }
                        }
                        jsonReader.close();

                        resultRank = Rank2 + " " + Rank1;

                        setNameJiksend(Name,resultRank,paramName,paramJik);
                    }
                    else
                        Log.d("정보 전송 : ", "실패");
                    conn.disconnect();
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public Handler setNameJik(String name , String rank , TextView Name , TextView jik)
    {
        final String paramName = name;
        final String paramRank = rank;
        final TextView viewName = Name;
        final TextView viewJik  = jik;

        if(viewName == null)
        {
            Log.d("핸들러viewName","널이다");
        }
        if(viewJik == null)
        {
            Log.d("핸들러viewJik","널이다");
        }

        final Handler handler = new Handler(Looper.getMainLooper())
        {
            public void handleMessage(Message msg)
            {
                if(viewName != null & viewJik != null)
                {
                    viewName.setText(paramName);
                    viewJik.setText(paramRank);
                }
                else
                    Log.d("handleMessage","널이다");
            }
        };
        return handler;
    }

    public void setNameJiksend(String name , String rank , TextView Name , TextView jik)
    {
        final String paramName = name;
        final String paramRank = rank;
        final TextView viewName = Name;
        final TextView viewJik  = jik;

        if(viewName == null)
        {
            Log.d("viewName","널이다");
        }
        if(viewJik == null)
        {
            Log.d("viewJik","널이다");
        }

        new Thread()
        {
            public void run()
            {
                Message message = setNameJik(paramName,paramRank,viewName,viewJik).obtainMessage();
                setNameJik(paramName,paramRank,viewName,viewJik).sendMessage(message);
            }
        }.start();
    }


    public boolean allimageDownload(String url, String Serial)
    {
        final String paramURL = url;
        final String[] getresult = new String[1];
        final String serial = Serial;

        Thread thread = new Thread(){
            @Override
            public void run() {
                HttpURLConnection conn = null;
                String urlString = paramURL;

                try{
                    URL url = new URL(urlString);
                    trustAllHosts();

                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });

                    conn = httpsURLConnection;
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type","application/json");
                    conn.connect();

                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                    JSONObject json = new JSONObject();

                    try{
                        json.put("id",serial);
                        json.put("ty","getcount_file");
                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                    bufferedWriter.write(json.toString());
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    getresult[0] = bufferedReader.readLine();
                    Log.v("겟카운트", getresult[0]);
                    ArrayList<String> uri = new ArrayList<>();
                    for(int i = 0; (i < Integer.parseInt(getresult[0])); i++)
                    {
                        uri.add("https://58.141.234.126:50030/download");
                    }
                    int i = 0;
                    for(String object:uri)
                    {
                        alldownloadImg(object,i, Integer.parseInt(getresult[0]),serial);
                        i++;
                    }
                    bufferedReader.close();
                    conn.disconnect();
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        return true;
    }

    public void alldownloadImg(String url, int param, int Size, String Serial)
    {
        final String paramURL = url;
        final int size = Size;
        final int Param = param;
        final String serial = Serial;
        Thread thread = new Thread(){
            @Override
            public void run() {
                HttpURLConnection conn = null;
                String urlString = paramURL;

                try{
                    URL url = new URL(urlString);
                    trustAllHosts();

                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });

                    conn = httpsURLConnection;
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type","application/json");
                    conn.connect();

                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

                    String ext = Environment.getExternalStorageDirectory().toString();
                    ext += "/BACKUP";

                    Log.v("이미지다운로드1","여기까지");
                    File folder = new File(ext);
                    folder.mkdirs();

                    JSONObject json = new JSONObject();
                    try{
                        json.put("id",serial);
                        json.put("co",Param);
                        json.put("ty","file");
                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                    bufferedWriter.write(json.toString());
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    Log.v("이미지다운로드2","여기까지");
                    File file = new File(ext,"Backup"+String.valueOf(Param)+".jpg");
                    OutputStream out = new FileOutputStream(file);
                    InputStream is = conn.getInputStream();
                    writeFile(is, out);
                    Log.v(Param+"번째 다운로드","완료");
                    out.close();

                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {
                        Log.d("정보 전송 : ", "성공");
                    }
                    else
                        Log.d("정보 전송 : ", "실패");

                    conn.disconnect();
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try {
            sendProgressUpdate("update",String.valueOf((Param+1))+"/"+String.valueOf(size));
            thread.join();
            if((param+1) == size)
                sendProgressUpdate("quit","0");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Handler quitprogress()
    {
        final Handler handler = new Handler(Looper.getMainLooper())
        {
            public void handleMessage(Message msg)
            {
                dialog.dismiss();
            }
        };
        return handler;
    }

    public Handler updateprogress(String result)
    {
        final String Result = result;
        final Handler handler = new Handler(Looper.getMainLooper())
        {
            public void handleMessage(Message msg)
            {
                Log.d("업데이트프로그레스 result",Result);
                if(!Result.equals("0"))
                    dialog.setMessage(       Result + "  다운로드 중..");
            }
        };
        return handler;
    }

    public Handler Uploadprogress(String result)
    {
        final String Result = result;
        final Handler handler = new Handler(Looper.getMainLooper())
        {
            public void handleMessage(Message msg)
            {
                Log.d("업데이트프로그레스 result",Result);
                if(!Result.equals("0"))
                    dialog.setMessage(       Result);
            }
        };
        return handler;
    }

    public void sendProgressUpdate(String flag, String result)
    {
        final String Flag = flag;
        final String Result = result;
        new Thread()
        {
            public void run()
            {
                if(Flag.equals("quit"))
                {
                    Message message = quitprogress().obtainMessage();
                    quitprogress().sendMessage(message);
                }
                else if(Flag.equals("update"))
                {
                    Log.d("업데이트프로그레스","호출");
                    Message message = updateprogress(Result).obtainMessage();
                    updateprogress(Result).sendMessage(message);
                }
                else if(Flag.equals("upload"))
                {
                    Log.d("업데이트프로그레스","호출");
                    Message message = Uploadprogress(Result).obtainMessage();
                    Uploadprogress(Result).sendMessage(message);
                }
            }
        }.start();
    }



    public void writeFile(InputStream is, OutputStream os) throws IOException
    {
        int c = 0;
        while((c = is.read()) != -1)
            os.write(c);
        os.flush();
    }

    public void crollimg(String url, String Serial, ImageView viewimg)
    {
        final String paramURL = url;
        final String serial = Serial;
        final ImageView viewImg = viewimg;

        Thread thread = new Thread(){
            @Override
            public void run() {
                HttpURLConnection conn = null;
                String urlString = paramURL;
                Bitmap bitmap = null;

                try{
                    URL url = new URL(urlString);
                    trustAllHosts();

                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });

                    conn = httpsURLConnection;
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type","application/json");
                    conn.connect();

                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

                    JSONObject json = new JSONObject();
                    try{
                        json.put("id",serial);
                        json.put("ty","thumbnail");
                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                    bufferedWriter.write(json.toString());
                    Log.d("제이슨 테스트",json.toString());
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    int nSize = conn.getContentLength();
                    Log.d("다운로드테스트크기:", String.valueOf(nSize));

                    BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
                    bitmap = BitmapFactory.decodeStream(in);
                    if(bitmap == null)
                        Log.d("비트맵:","널이다.");
                    setBitmapSend(bitmap,viewImg);
                    in.close();

                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {
                        Log.d("정보 전송 : ", "성공");
                    }
                    else
                        Log.d("정보 전송 : ", "실패");

                    conn.disconnect();

                }catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public Handler setBitmap(Bitmap bitmap,ImageView viewimg)
    {
        final Bitmap Bitmap = bitmap;
        final ImageView viewImg = viewimg;
        if(Bitmap == null)
            {
            Log.d("setBitMap메소드","널이다");
        }

        final Handler handler = new Handler(Looper.getMainLooper())
        {
            public void handleMessage(Message msg)
            {
                if(Bitmap != null)
                    viewImg.setImageBitmap(Bitmap);
                else
                    Log.d("handleMessage","비트맵 널이다");
            }
        };
        return handler;
    }

    public void setBitmapSend(Bitmap bitmap, ImageView viewimg)
    {
        final Bitmap Bitmap = bitmap;
        final ImageView viewImg = viewimg;
        if(Bitmap == null)
        {
            Log.d("setBitMapSend메소드","널이다");
        }
        new Thread()
        {
            public void run()
            {
                Message message = setBitmap(Bitmap,viewImg).obtainMessage();
                setBitmap(Bitmap,viewImg).sendMessage(message);
            }
        }.start();
    }

    public void numberBackup(String url, JSONObject json)
    {
        final String paramURL = url;
        final JSONObject JSON = json;

        Thread thread = new Thread() {
            @Override
            public void run() {

                String urlString = paramURL;
                HttpURLConnection conn = null;

                try{
                    URL url = new URL(urlString);
                    trustAllHosts();

                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });

                    conn = httpsURLConnection;
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json");

                    JSONObject json = new JSONObject();

                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                    bufferedWriter.write(JSON.toString());
                    Log.d("전송한 것:",JSON.toString());
                    bufferedWriter.flush();
                    bufferedWriter.close();

                    outputStream.close();
                    conn.connect();

                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {
                        Log.d("정보 전송 : ", "성공");
                    }
                    else
                        Log.d("정보 전송 : ", "실패");
                    conn.disconnect();

                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if(conn != null)
                    {
                        conn.disconnect();
                    }
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void imgBackup(String url, String serial , byte[] jpg , String name , String size, String number, String flag)
    {
        String Flag = flag;
        String Size = size;
        String Number = number;
        final byte[] File = jpg;
        final String Name = name;
        final String paramURL = url;
        final String Serial = serial;
        Thread thread = new Thread() {
            @Override
            public void run() {

                String urlString = paramURL;
                HttpURLConnection conn = null;

                try{
                    Log.d("이미지백업","백업");
                    URL url = new URL(urlString);
                    String crlf = "\r\n";
                    String twoHyphens = "--";
                    String boundary =  "*****";

                    trustAllHosts();

                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });

                    conn = httpsURLConnection;
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE","multipart/form-data");
                    conn.setRequestProperty("Cache-Control", "no-cache");
                    conn.setRequestProperty(
                            "Content-Type", "multipart/form-data;boundary=" + boundary);

                    DataOutputStream out = new DataOutputStream(conn.getOutputStream());

                    /**
                     * 메타 데이터 작업
                     */

                    StringBuffer postData = new StringBuffer();
                    postData.append(crlf);
                    postData.append(setValue("id",Serial));
                    postData.append(crlf);


                    /**
                     * 메타 데이터 먼저 전송
                     */
                    out.writeBytes(twoHyphens + boundary + crlf);
                    out.writeBytes("Content-Disposition: form-data; name=\""+"ID"+crlf);
                    out.writeBytes(crlf);
                    out.writeBytes(Serial);
                    out.writeBytes(crlf);

                    /**
                     * 이미지 파일
                     */
                    out.writeBytes(twoHyphens + boundary + crlf);
                    out.writeBytes("Content-Disposition: form-data; name=\"" + "file"
                            + "\";filename=\"" + Name + "\"" + crlf);
                    out.writeBytes(crlf);
                    out.write(File);
                    out.writeBytes(crlf);
                    out.writeBytes(twoHyphens + boundary + twoHyphens + crlf);

                    out.flush();
                    out.close();

                    conn.connect();

                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {
                        Log.d("전송받은거","200");
                    }

                    conn.disconnect();

                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if(conn != null)
                    {
                        conn.disconnect();
                    }
                }
            }
        };
        thread.start();
        if(!Flag.equals("profile"))
        {
            if(Flag.equals("LoginFail"))
                return;
            if(size.equals(number))
                sendProgressUpdate("quit","0");
            else
                sendProgressUpdate("upload",number+"/"+size+"   업로드 중..");
        }
    }

    public void checkout(String url, String serial)
    {
        final String paramURL = url;
        final String Serial = serial;

        Thread thread = new Thread() {
            @Override
            public void run() {

                String urlString = paramURL;
                HttpURLConnection conn = null;

                try{
                    URL url = new URL(urlString);

                    trustAllHosts();

                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });

                    conn = httpsURLConnection;
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json");


                    JSONObject json = new JSONObject();

                    try{
                        json.put("Id",Serial);

                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                    bufferedWriter.write(json.toString());
                    bufferedWriter.flush();
                    bufferedWriter.close();

                    outputStream.close();

                    conn.connect();

                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {
                        Log.d("퇴근 정보 전송 : ", "성공");
                    }
                    else
                        Log.d("퇴근 정보 전송 : ", "실패");
                    conn.disconnect();

                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if(conn != null)
                    {
                        conn.disconnect();
                    }
                }
            }
        };
        thread.start();
    }

    public void tokenhttps(String url, String serial ,String token)
    {
        final String paramURL = url;
        final String Serial = serial;
        final String Token = token;

        Thread thread = new Thread() {
            @Override
            public void run() {

                String urlString = paramURL;
                HttpURLConnection conn = null;

                try{
                    URL url = new URL(urlString);

                    trustAllHosts();

                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });

                    conn = httpsURLConnection;
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json");


                    JSONObject json = new JSONObject();

                    try{
                        json.put("Id",Serial);
                        json.put("Fcm_token",Token);
                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                    bufferedWriter.write(json.toString());
                    bufferedWriter.flush();
                    bufferedWriter.close();

                    outputStream.close();

                    conn.connect();

                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {}
                    else
                        Log.e("토큰 정보 전송 : ", "실패");
                    conn.disconnect();

                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if(conn != null)
                    {
                        conn.disconnect();
                    }
                }
            }
        };
        thread.start();

    }


    public void sendDeviceInfo(String url , String Serial , String Number,String Token, String Password,
            String Name , String Version ,String manufacturer , String Tel) {

        final String paramURL = url;
        final String serial = Serial;
        final String number = Number;
        final String token = Token;
        final String password = Password;
        final String name = Name;
        final String version = Version;
        final String manu = manufacturer;
        final String tel = Tel;

        Thread thread = new Thread() {
            @Override
            public void run() {

                String urlString = paramURL;
                HttpURLConnection conn = null;

                try{

                    URL url = new URL(urlString);

                    trustAllHosts();

                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });

                    conn = httpsURLConnection;
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json");


                    JSONObject json = new JSONObject();

                    try{
                        json.put("Id",serial);
                        json.put("User_info_employee_num",number);
                        json.put("Fcm_token",token);
                        json.put("Password",password);
                        json.put("Os",name + "," + version);
                        json.put("Manufacturer",manu);
                        json.put("Telnum",tel);
                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                    bufferedWriter.write(json.toString());
                    bufferedWriter.flush();
                    bufferedWriter.close();

                    outputStream.close();

                    conn.connect();

                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {
                        Log.d("테스트",Thread.currentThread().getName());
                    }
                    conn.disconnect();

                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if(conn != null)
                    {
                        conn.disconnect();
                    }
                }
            }
        };
        thread.start();
    }


    public void sendGPS(String url, String Serial, Double Latitude, Double Longitude) {

        final String serial = Serial;
        final double latitude = Latitude;
        final double longitude = Longitude;
        final String paramURL = url;

        Thread thread = new Thread() {
            @Override
            public void run() {

                String urlString = paramURL;
                HttpURLConnection conn = null;

                try{

                    URL url = new URL(urlString);

                    trustAllHosts();

                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });

                    conn = httpsURLConnection;
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json");

                    JSONObject jsonObject = new JSONObject();

                    try{
                        jsonObject.put("Device_info_Id",serial);
                        jsonObject.put("Latitude",latitude);
                        jsonObject.put("Longitude",longitude);
                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                    bufferedWriter.write(jsonObject.toString());
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    conn.connect();

                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {
                        //Log.d("GPS 정보 전송 선공","OK");
                    }
                    conn.disconnect();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if(conn != null)
                    {
                        conn.disconnect();
                    }
                }
            }
        };
        thread.start();
    }
    public void sendCheckIn(String url, String Serial) {

        final String serial = Serial;
        final String paramURL = url;

        Thread thread = new Thread() {
            @Override
            public void run() {

                String urlString = paramURL;
                HttpURLConnection conn = null;

                try{
                    URL url = new URL(urlString);
                    trustAllHosts();
                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });

                    conn = httpsURLConnection;
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json");

                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

                    JSONObject json = new JSONObject();
                    json.put("Id",serial);
                    bufferedWriter.write(json.toString());
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    conn.connect();

                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {
                        Log.d("앱 정보 전송 선공",json.toString());

                    }
                    conn.disconnect();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if(conn != null)
                    {
                        conn.disconnect();
                    }
                }
            }
        };
        thread.start();
    }
    public void sendAppInfo(String url, String Serial, String Action , String Data)
    {
        final String serial = Serial;
        final String paramURL = url;
        final String action = Action;
        final String data = Data;

        Thread thread = new Thread() {
            @Override
            public void run() {

                String urlString = paramURL;
                HttpURLConnection conn = null;

                try{
                    URL url = new URL(urlString);

                    trustAllHosts();

                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });

                    conn = httpsURLConnection;
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json");

                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

                    JSONObject json = new JSONObject();
                    json.put("Id",serial);
                    json.put("action",action);
                    json.put("data",data);
                    bufferedWriter.write(json.toString());
                    bufferedWriter.flush();
                    conn.connect();

                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {

                    }
                    bufferedWriter.close();
                    outputStream.close();

                    conn.disconnect();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if(conn != null)
                    {
                        conn.disconnect();
                    }
                }
            }
        };
        thread.start();

    }

    public void sendAppInfo(String url, String Serial, String Action , String Data,JSONArray jsonArray)
    {
        final String serial = Serial;
        final JSONArray jsonarr = jsonArray;
        final String paramURL = url;
        final String action = Action;
        final String data = Data;

        Thread thread = new Thread() {
            @Override
            public void run() {

                String urlString = paramURL;
                HttpURLConnection conn = null;

                try{
                    URL url = new URL(urlString);

                    trustAllHosts();

                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });

                    conn = httpsURLConnection;
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json");

                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

                    JSONObject json = new JSONObject();
                    json.put("Id",serial);
                    json.put("action",action);
                    json.put("data",data);
                    json.put("app",jsonarr);
                    bufferedWriter.write(json.toString());
                    bufferedWriter.flush();
                    conn.connect();

                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {

                    }
                    bufferedWriter.close();
                    outputStream.close();

                    conn.disconnect();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if(conn != null)
                    {
                        conn.disconnect();
                    }
                }
            }
        };
        thread.start();
    }


    //최초 설치 후 서버에 앱 정보를 전송한다.
    public void sendAppInfo(String url, String Serial, JSONArray jsonArray)
    {
        final String serial = Serial;
        final JSONArray jsonarr = jsonArray;
        final String paramURL = url;


        Thread thread = new Thread() {
            @Override
            public void run() {

                String urlString = paramURL;
                HttpURLConnection conn = null;

                try{
                    URL url = new URL(urlString);

                    trustAllHosts();

                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });

                    conn = httpsURLConnection;
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json");

                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

                    JSONObject json = new JSONObject();
                    json.put("Id",serial);
                    json.put("app",jsonarr);
                    bufferedWriter.write(json.toString());
                    bufferedWriter.flush();
                    conn.connect();

                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {
                        Log.d("sendAppinfo(최초)","성공");
                    }
                    bufferedWriter.close();
                    outputStream.close();

                    conn.disconnect();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if(conn != null)
                    {
                        conn.disconnect();
                    }
                }
            }
        };
        thread.start();

    }


    public void sendDeviceCheck(String url, String Serial, JSONArray jsonArray, Boolean Rootflag, SharedPreferences sh) {

        SharedPreferences Sh = sh;
        final SharedPreferences.Editor ed = sh.edit();
        final String serial = Serial;
        final JSONArray jsonarr = jsonArray;
        final String paramURL = url;
        final String rootFlag = String.valueOf(Rootflag);

        Thread thread = new Thread() {
            @Override
            public void run() {

                String urlString = paramURL;
                HttpURLConnection conn = null;

                try{
                    URL url = new URL(urlString);

                    trustAllHosts();

                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });

                    conn = httpsURLConnection;
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json");

                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

                    JSONObject json = new JSONObject();
                    json.put("Id",serial);
                    json.put("app",jsonarr);
                    json.put("type","루팅탐지");
                    json.put("history", rootFlag);
                    Log.d("테스트",json.toString());
                    bufferedWriter.write(json.toString());
                    bufferedWriter.flush();
                    conn.connect();

                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {
                        JsonReader jsonReader = new JsonReader(new InputStreamReader(conn.getInputStream()));
                        jsonReader.beginObject();
                        while(jsonReader.hasNext()) {
                            String key = jsonReader.nextName();
                            if (key.equals("message")) {
                                String result = jsonReader.nextString();
                                Log.d("수신테스트", result);
                                Log.v("루팅테스트",rootFlag);

                                if(result.equals("변조탐지 어플 없음"))
                                {
                                    ed.putString("Check","통과");
                                    ed.apply();

                                }
                                else
                                {
                                    ed.putString("Check","변조");
                                    ed.apply();
                                }
                                if(rootFlag.equals("false"))
                                {
                                    ed.putString("Rooting","통과");
                                    ed.apply();
                                }
                                else
                                {
                                    ed.putString("Rooting","루팅");
                                    ed.apply();
                                }
                                break;
                            }
                        }
                        jsonReader.close();
                    }
                    bufferedWriter.close();
                    outputStream.close();

                    conn.disconnect();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if(conn != null)
                    {
                        conn.disconnect();
                    }
                }
            }
        };
        thread.start();
    }


    public void ChgPwdhttps(String url, String serial ,String pwd)
    {
        final String paramURL = url;
        final String Serial = serial;
        final String Pwd = pwd;

        Thread thread = new Thread() {
            @Override
            public void run() {

                String urlString = paramURL;
                HttpURLConnection conn = null;

                try{
                    URL url = new URL(urlString);

                    trustAllHosts();

                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });

                    conn = httpsURLConnection;
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json");


                    JSONObject json = new JSONObject();

                    try{
                        json.put("id",Serial);
                        json.put("pw",Pwd);
                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                    bufferedWriter.write(json.toString());
                    bufferedWriter.flush();
                    bufferedWriter.close();

                    outputStream.close();

                    conn.connect();

                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {}
                    else
                        Log.e("토큰 정보 전송 : ", "실패");
                    conn.disconnect();

                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if(conn != null)
                    {
                        conn.disconnect();
                    }
                }
            }
        };
        thread.start();

    }


    public static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub

            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub

            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Login extends AsyncTask<String,Void,String> {

    String URL;
    String EM;
    String PW;
    String getline = "null";
    String UUID;

    Login(String a,String b, String c, String d)
    {
        URL = a;
        PW = b;
        EM = c;
        UUID = d;
    }

    @Override
    protected String doInBackground(String... params) {
        String urlString = URL;
        HttpURLConnection conn = null;
        try{

            URL url = new URL(urlString);
            trustAllHosts();
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
            httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            conn = httpsURLConnection;
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            JSONObject jsonObject = new JSONObject();

            try{
                jsonObject.put("pw",PW);
                jsonObject.put("em",EM);
                jsonObject.put("id",UUID);
            }catch(Exception e)
            {
                e.printStackTrace();
            }

            OutputStream outputStream = conn.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(jsonObject.toString());
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            conn.connect();

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                getline = bufferedReader.readLine();
                bufferedReader.close();

            }
            conn.disconnect();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally {

            conn.disconnect();

        }
        return getline;
    }

    @Override
    protected void onPostExecute(String s) {

        super.onPostExecute(s);
    }

    public static String setValue(String key, String value) {
        return "Content-Disposition: form-data; name=\"" + key + "\"r\n\r\n"
                + value;
    }
}

class PasswordRecovery extends AsyncTask<String,Void,String>{
    String uuid;
    String Url;
    String getline;
    String otpcode;
    String result;
    PasswordRecovery() {}
    PasswordRecovery(String url,String uuid,String otpcode)
    {
        this.otpcode = otpcode;
        this.Url = url;
        this.uuid = uuid;
    }
    @Override
    protected String doInBackground(String... params) {
        String urlString = Url;
        HttpURLConnection conn = null;
        try{

            URL url = new URL(urlString);
            trustAllHosts();
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
            httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            conn = httpsURLConnection;
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            JSONObject jsonObject = new JSONObject();

            try{
                jsonObject.put("Code",otpcode);
                jsonObject.put("Id",uuid);
            }catch(Exception e)
            {
                e.printStackTrace();
            }

            OutputStream outputStream = conn.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(jsonObject.toString());
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            conn.connect();
            StringBuilder stringBuilder = new StringBuilder();
            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
            {

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                while((getline = bufferedReader.readLine()) != null) {
                    stringBuilder.append(getline);


                }
                result = stringBuilder.toString();

                Log.d("다이얼","0 "+result+"\n"+stringBuilder.length());
                bufferedReader.close();

                if(stringBuilder.length() == 23)
                    result = "success";

                else
                    result = "failed";


            }
            conn.disconnect();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally {

            conn.disconnect();

        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
