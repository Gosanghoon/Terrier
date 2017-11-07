package org.androidtown.materialpractice;

import android.content.SharedPreferences;

import org.json.JSONArray;

/**
 * Created by ahsxj on 2017-08-07.
 */

public interface MainFragmentPresenter{

    void setView(MainFragmentPresenter.View view);
    void sendGpsData(double latitude, double longitude);
    void initialize();
    void sendCheckOut();
    void sendAppInfo(String s, String serial, JSONArray jsonArray);
    void sendAppInfo(String s, String serial, JSONArray jsonArray, boolean b, SharedPreferences sh);
    boolean checkRooting();

    public interface View
    {

    }
}
