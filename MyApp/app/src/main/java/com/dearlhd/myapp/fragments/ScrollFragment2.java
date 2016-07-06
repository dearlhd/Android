package com.dearlhd.myapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;

import com.dearlhd.myapp.R;

/**
 * Created by dearlhd on 2016/6/26 0028.
 */
public class ScrollFragment2 extends Fragment {

    public ScrollFragment2() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 直接显示page4中的布局和内容
        View view = inflater.inflate(R.layout.page4, container, false);
        return view;
    }

}
