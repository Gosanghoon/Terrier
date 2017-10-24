package org.androidtown.materialpractice;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2017-09-27.
 */

public class ApplicationManagerListView {
    private Drawable iconDrawable;
    private String titleStr;
    private String descStr;
    private String Packname;

    public void setIcon(Drawable icon) {
        iconDrawable = icon ;
    }
    public void setTitle(String title) {
        titleStr = title ;
    }
    public void setDesc(String desc) {
        descStr = desc ;
    }
    public void setPack(String pack) { Packname = pack;}

    public Drawable getIcon() {
        return this.iconDrawable ;
    }
    public String getTitle() {
        return this.titleStr ;
    }
    public String getDesc() {return this.descStr ;}
    public String getPackname() {return this.Packname;}

}
