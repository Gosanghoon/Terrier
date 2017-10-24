package org.androidtown.materialpractice;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017-09-27.
 */

public class ApplicationManagerListViewAdapter extends BaseAdapter {

    private ArrayList<ApplicationManagerListView> listViewItemList = new ArrayList<ApplicationManagerListView>();
    int PostoFrag;

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final int pos = position;

        final Context context = parent.getContext();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.application_list_adapter, parent, false);
        }
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.app_icon) ;
        TextView titleTextView = (TextView) convertView.findViewById(R.id.app_name) ;
        TextView descTextView = (TextView) convertView.findViewById(R.id.app_package) ;
        Button deleteButton = (Button) convertView.findViewById(R.id.app_delete_button);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final ApplicationManagerListView listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        iconImageView.setImageDrawable(listViewItem.getIcon());
        titleTextView.setText(listViewItem.getTitle());
        descTextView.setText(listViewItem.getDesc());
        deleteButton.setText("삭제");


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                AlertDialog.Builder ad = new AlertDialog.Builder(v.getContext());
                ad.setTitle("Uninstall apps");
                ad.setMessage("정말로 앱을 지우시겠습니까?");

                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Uri packageURI = Uri.parse("package:"+listViewItem.getPackname());
                        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
                        ((Activity) context).startActivityForResult(uninstallIntent,pos);
                        PostoFrag = pos;
                        listViewItemList.remove(pos);
                    }

                });


                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        dialog.dismiss();
                        return;
                    }
                });
                ad.show();

            }

        });


        return convertView;

    }
    public int retPos()
    {
        return this.PostoFrag;
    }

    public void addItem(Drawable icon, String title, String desc,String pkn)
    {
        ApplicationManagerListView item = new ApplicationManagerListView();

        item.setIcon(icon);
        item.setTitle(title);
        item.setDesc(desc);
        item.setPack(pkn);

        listViewItemList.add(item);
    }

}
