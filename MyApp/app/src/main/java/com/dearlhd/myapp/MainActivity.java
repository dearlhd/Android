package com.dearlhd.myapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.widget.ViewFlipper;

import com.dearlhd.myapp.fragments.ListFragment;
import com.dearlhd.myapp.fragments.ScrollFragment;
import com.dearlhd.myapp.fragments.ScrollFragment2;
import com.dearlhd.myapp.fragments.WebFragment;
import com.dearlhd.myapp.scrollView.HoveringScrollview;

public class MainActivity extends AppCompatActivity
        implements RadioGroup.OnCheckedChangeListener, HoveringScrollview.OnScrollListener {

    private HoveringScrollview hoveringScrollview;
    private LinearLayout hoveringLayout1, hoveringLayout2;
    private int radioGroupTop;

    // ViewFlipper for ad
    private ViewFlipper adFlipper;

    // radio group for tab bar
    private RadioGroup rg_tab_bar;
    private RadioButton rb_channel;

    //Fragment Object
    private ListFragment fg1;
    private WebFragment fg2;
    private ScrollFragment fg3;
    private ScrollFragment2 fg4;

    private FragmentManager fManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        bindViews();
    }


    //UI组件初始化与事件绑定
    private void bindViews() {
        // deal with hovering
        hoveringScrollview = (HoveringScrollview) findViewById(R.id.hovering_scroll_view);
        hoveringLayout1 = (LinearLayout) findViewById(R.id.hoveringLayout1);
        hoveringLayout2 = (LinearLayout) findViewById(R.id.hoveringLayout2);
        hoveringScrollview.setOnScrollListener(this);

        // deal with ad flipping
        adFlipper = (ViewFlipper) findViewById(R.id.adFlipper);
        adFlipper.startFlipping();

        fManager = getFragmentManager();
        rg_tab_bar = (RadioGroup) findViewById(R.id.rg_tab_bar);
        rg_tab_bar.setOnCheckedChangeListener(this);
        //获取第一个单选按钮，并设置其为选中状态
        rb_channel = (RadioButton) findViewById(R.id.rb_channel);
        rb_channel.setChecked(true);
    }


    //隐藏所有Fragment
    private void hideAllFragment(FragmentTransaction fragmentTransaction) {
        if (fg1 != null) fragmentTransaction.hide(fg1);
        if (fg2 != null) fragmentTransaction.hide(fg2);
        if (fg3 != null) fragmentTransaction.hide(fg3);
        if (fg4 != null) fragmentTransaction.hide(fg4);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        FragmentTransaction fTransaction = fManager.beginTransaction();
        hideAllFragment(fTransaction);
        switch (i) {
            case R.id.rb_channel:
                if (fg1 == null) {
                    fg1 = new ListFragment();
                    fTransaction.add(R.id.ly_content, fg1);
                } else {
                    fTransaction.show(fg1);
                }
                break;
            case R.id.rb_message:
                if (fg2 == null) {
                    fg2 = new WebFragment();
                    fTransaction.add(R.id.ly_content, fg2);
                } else {
                    fTransaction.show(fg2);
                }
                break;
            case R.id.rb_better:
                if (fg3 == null) {
                    fg3 = new ScrollFragment();
                    fTransaction.add(R.id.ly_content, fg3);
                } else {
                    fTransaction.show(fg3);
                }
                break;
            case R.id.rb_setting:
                if (fg4 == null) {
                    fg4 = new ScrollFragment2();
                    fTransaction.add(R.id.ly_content, fg4);
                } else {
                    fTransaction.show(fg4);
                }
                break;
        }
        fTransaction.commit();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            radioGroupTop = adFlipper.getBottom();// 获取searchLayout的顶部位置
        }
    }

    @Override
    public void onScroll(int scrollY) {
        if (scrollY >= radioGroupTop) {
            if (rg_tab_bar.getParent() != hoveringLayout2) {
                hoveringLayout1.removeView(rg_tab_bar);
                hoveringLayout2.addView(rg_tab_bar);
            }
        } else {
            if (rg_tab_bar.getParent() != hoveringLayout1) {
                hoveringLayout2.removeView(rg_tab_bar);
                hoveringLayout1.addView(rg_tab_bar);
            }
        }
    }
}