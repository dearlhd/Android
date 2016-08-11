package com.lhd.largelistview.bean;

/**
 * Created by Administrator on 2016/7/22.
 */
public class MyItem {
    private int mImg;
    private String mTitle;
    private String mContent;

    public MyItem () {

    }

    public MyItem (int img, String title, String content) {
        mImg = img;
        mTitle = title;
        mContent = content;
    }

    public int getmImg() {
        return mImg;
    }

    public void setmImg(int mImg) {
        this.mImg = mImg;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }
}
