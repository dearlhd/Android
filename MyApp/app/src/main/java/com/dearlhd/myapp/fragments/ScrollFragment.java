package com.dearlhd.myapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dearlhd.myapp.R;
import com.dearlhd.myapp.adapter.PersonAdapter;
import com.dearlhd.myapp.entity.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jay on 2015/8/28 0028.
 */
public class ScrollFragment extends Fragment {

    private ListView listView;

    public ScrollFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page3, container, false);

        return view;
    }

}
