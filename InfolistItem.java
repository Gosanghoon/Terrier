package org.androidtown.materialpractice;

import android.graphics.drawable.Drawable;

/**
 * Created by ahsxj on 2017-06-29.
 */

public class InfolistItem {

    private Drawable iconDrawable;
    private String titleStr;
    private String descStr;

    public void setIcon(Drawable icon){
        this.iconDrawable = icon;
    }
    public void setTitle(String title){
        this.titleStr = title;
    }
    public void setDesc(String desc){
        this.descStr = desc;
    }
    public Drawable getIcon(){
        return this.iconDrawable;
    }
    public String getTitle(){
        return this.titleStr;
    }
    public String getDesc(){
        return this.descStr;
    }
}
