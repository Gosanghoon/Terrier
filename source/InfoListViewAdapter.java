package org.androidtown.materialpractice;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ahsxj on 2017-06-29.
 */

public class InfoListViewAdapter extends BaseAdapter {

    private ArrayList<InfolistItem> listViewItemList = new ArrayList<InfolistItem>();

    public InfoListViewAdapter(){
    }

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
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context= parent.getContext();

        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.info_item,parent,false);
        }

        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.info_imageview1);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.info_textview1);
        TextView descTextView = (TextView) convertView.findViewById(R.id.info_textview2);

        InfolistItem listViewItem = listViewItemList.get(position);

        iconImageView.setImageDrawable(listViewItem.getIcon());
        titleTextView.setText(listViewItem.getTitle());
        descTextView.setText(listViewItem.getDesc());

        return convertView;
    }

    public void addItem(Drawable icon, String title, String desc)
    {
        InfolistItem item = new InfolistItem();

        item.setIcon(icon);
        item.setTitle(title);
        item.setDesc(desc);

        listViewItemList.add(item);
    }
}
