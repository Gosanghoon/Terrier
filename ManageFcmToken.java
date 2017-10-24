package org.androidtown.materialpractice;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;




/**
 * Created by ahsxj on 2017-07-10.
 */

public class ManageFcmToken extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private String token;
    private SharedPreferences Userinfo;

    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + token);

        // 생성등록된 토큰을 개인 앱서버에 보내 저장해 두었다가 추가 뭔가를 하고 싶으면 할 수 있도록 한다.
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        Userinfo = getSharedPreferences("User_info",0);
        String Token = token;
        HttpsConnection ht = new HttpsConnection();
        ht.tokenhttps("https://58.141.234.126:50020/change_fcm",Userinfo.getString("Id","fail"),Token);
        ht = null;
    }
}
