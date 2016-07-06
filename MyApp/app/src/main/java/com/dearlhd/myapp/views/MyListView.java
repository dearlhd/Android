package com.dearlhd.myapp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by Administrator on 2016/7/5.
 */
public class MyListView extends ListView {
    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean f = super.dispatchTouchEvent(ev);

        Log.i("dong", "ListView dispatchTouchEvent " + f);

        return f;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean f = super.onTouchEvent(ev);

        Log.i("dong", "ListView onTouchEvent " + f);

        return true;
    }

}
