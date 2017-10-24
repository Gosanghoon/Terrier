package org.androidtown.materialpractice;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by ahsxj on 2017-06-07.
 */

public class InformationFragment extends Fragment {


    public static InformationFragment newInstance()
    {
        InformationFragment fragment = new InformationFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_information,null);
        //ArrayAdapter Adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,LIST_MENU);

        Fragment newFragment2 = new Fragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.frag_info, newFragment2);
        transaction.addToBackStack("FragI");
        transaction.commit();
        //ArrayAdapter Adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,LIST_MENU);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.i(InformationFragment.class.getSimpleName(), "onKey Back listener is working!!!");
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

        InfoListViewAdapter adapter;

        adapter = new InfoListViewAdapter();

        ListView listView = (ListView) view.findViewById(R.id.information_list);
        listView.setAdapter(adapter);

        adapter.addItem(ContextCompat.getDrawable(getActivity(),R.drawable.ic_phonelink_lock_black_36dp),
                "    암호 재설정                                        ","      암호를 설정하세요                ");
        adapter.addItem(ContextCompat.getDrawable(getActivity(),R.drawable.ic_gps_not_fixed_black_24dp),
                "    분실 관리                                        ","      분실시 관리                     ");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //아이템 얻기
                InfolistItem item = (InfolistItem) parent.getItemAtPosition(position);
                String titleStr = item.getTitle();
                String descStr = item.getDesc();
                Drawable iconDrawable = item.getIcon();

                if(position == 0)
                {
                    Bundle extras = new Bundle();
                    extras.putString("reset","reset");

                    Intent intent = new Intent(getContext(),ChangePasswordActivity.class);
                    intent.putExtras(extras);
                    startActivity(intent);

                }


            }
        });
        return view;


    }


}
