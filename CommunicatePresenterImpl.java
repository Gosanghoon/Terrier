package org.androidtown.materialpractice;


import android.support.v4.app.Fragment;

/**
 * Created by ahsxj on 2017-07-17.
 */

public class CommunicatePresenterImpl implements CommunicatePresenter {

    private Fragment fragment;
    private CommunicatePresenter.View view;
    private MainModel mainModel;

    public CommunicatePresenterImpl(Fragment fragment)
    {
        this.fragment = fragment;
        if(mainModel != null)
        {
            this.mainModel = new MainModel();
        }
    }

    @Override
    public void setView(CommunicatePresenter.View view)
    {
        this.view = view;
    }
}
