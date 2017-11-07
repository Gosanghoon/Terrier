package org.androidtown.materialpractice;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;

import org.json.JSONArray;

/**
 * Created by ahsxj on 2017-08-07.
 */

public class MainFragmentPresenterImpl implements MainFragmentPresenter {

    private Fragment fragment;
    private View view;
    private MainFragmentModel model;

    public MainFragmentPresenterImpl(Fragment fragment, String serial)
    {
        this.fragment = fragment;
        this.model = new MainFragmentModel(serial);
    }

    @Override
    public void setView(MainFragmentPresenter.View view)
    {
        this.view = view;
    }

    @Override
    public void sendGpsData(double latitude, double longitude)
    {
        model.sendGpsInfo(latitude, longitude);
    }

    @Override
    public void initialize() {
        //model.initialize();
    }

    @Override
    public void sendCheckOut() { model.sendCheckOut(); }

    @Override
    public void sendAppInfo(String url, String serial, JSONArray jsonArray) {
        model.sendAppinfo(url,serial,jsonArray);
    }

    @Override
    public void sendAppInfo(String url, String serial, JSONArray jsonArray, boolean rootflag, SharedPreferences sh) {
        model.sendAppinfo(url,serial,jsonArray,rootflag,sh);
    }

    @Override
    public boolean checkRooting() {
        return model.check_root();
    }
}

