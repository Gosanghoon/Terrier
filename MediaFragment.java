package org.androidtown.materialpractice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ahsxj on 2017-06-07.
 */

public class MediaFragment extends android.support.v4.app.Fragment {

    public static MediaFragment newInstance()
    {
        MediaFragment fragment = new MediaFragment();
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_media,container,false);
    }
}
