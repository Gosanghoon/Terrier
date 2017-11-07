package org.androidtown.materialpractice;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by ahsxj on 2017-06-07.
 */

public class CommunicateFragment extends Fragment implements CommunicatePresenter.View {

    Button btnWeb;
    View v;

    private CommunicatePresenter communicatePresenter;

    public static CommunicateFragment newInstance()
    {
        CommunicateFragment fragment = new CommunicateFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        if(v == null){
            v = inflater.inflate(R.layout.fragment_communication,container,false);
        }

        btnWeb = (Button)v.findViewById(R.id.btn_web);
        communicatePresenter = new CommunicatePresenterImpl(CommunicateFragment.this);
        communicatePresenter.setView(this);

        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
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

        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = new Fragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frag_communication,fragment);
        fragmentTransaction.addToBackStack("FragC");
        fragmentTransaction.commit();


        btnWeb.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.terrier.co19.kr/control"));
                startActivity(intent);
            }
        });


        return v;
    }
}





