package org.androidtown.materialpractice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ahsxj on 2017-10-23.
 */

public class ResultFragment extends Fragment{

    /**
     * ResultFragment : 기기검사 결과창을 보여주는 프래그먼트
     */

    TextView text_Rooting;
    TextView text_Check;
    SharedPreferences userinfo;

    public static ResultFragment newInstance()
    {
        ResultFragment fragment = new ResultFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_result,container,false);
        text_Rooting = (TextView)view.findViewById(R.id.text_rooting);
        text_Check = (TextView)view.findViewById(R.id.text_check);
        userinfo = getActivity().getSharedPreferences("User_info",0);

        Log.v("테스트",userinfo.getString("Check","fail"));
        Log.v("테스트",userinfo.getString("Rooting","fail"));

        if(userinfo.getString("Check","fail").equals("통과"))
        {
            text_Check.setText("통과");
        }

        if(userinfo.getString("Rooting","fail").equals("통과"))
        {
            text_Rooting.setText("통과");
        }


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
        return view;
    }

}
