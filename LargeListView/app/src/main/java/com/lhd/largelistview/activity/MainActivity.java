package com.lhd.largelistview.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lhd.largelistview.R;
import com.lhd.largelistview.fragment.LargeListViewFragment;
import com.lhd.largelistview.ui.LargeListView;

public class MainActivity extends AppCompatActivity {

    private FragmentManager mFragmentManager;
    private LargeListViewFragment mListFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView () {
        mFragmentManager = getSupportFragmentManager();

        mListFragment = new LargeListViewFragment();

        FragmentTransaction fTransaction = mFragmentManager.beginTransaction();
        fTransaction.add(R.id.fragment, mListFragment);
        fTransaction.commit();
    }
}
