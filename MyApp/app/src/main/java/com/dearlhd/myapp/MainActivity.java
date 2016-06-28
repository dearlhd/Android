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

    // 最外层的scrollview,使标签栏和下方的fragment可以一起移动
    private HoveringScrollview hoveringScrollview;
    private LinearLayout hoveringLayout1, hoveringLayout2;
    private int radioGroupTop;

    // ViewFlipper，实现广告循播
    private ViewFlipper adFlipper;

    // radio group，用以实现标签栏
    private RadioGroup rg_tab_bar;
    private RadioButton rb_channel;

    // 四个fragment
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
        initViews();
    }


    //UI组件初始化与事件绑定
    private void initViews() {
        // deal with hovering
        hoveringScrollview = (HoveringScrollview) findViewById(R.id.hovering_scroll_view);
        hoveringLayout1 = (LinearLayout) findViewById(R.id.hoveringLayout1);
        hoveringLayout2 = (LinearLayout) findViewById(R.id.hoveringLayout2);
        hoveringScrollview.setOnScrollListener(this);

        // 开始广告的循环播放
        adFlipper = (ViewFlipper) findViewById(R.id.adFlipper);
        adFlipper.startFlipping();

        // fragment manager, 用以控制fragment之间的切换
        fManager = getFragmentManager();
        rg_tab_bar = (RadioGroup) findViewById(R.id.rg_tab_bar);
        rg_tab_bar.setOnCheckedChangeListener(this);
        // 获取第一个单选按钮，并设置其为选中状态
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
    // 当标签栏被点击时，切换fragment
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
    // 当屏幕滑动时，判断是否要悬浮标签栏的位置，悬浮是通过将标签栏add至另一个layout上实现的
    public void onScroll(int scrollY) {
        if (scrollY >= radioGroupTop) {
            if (rg_tab_bar.getParent() != hoveringLayout2) {
                hoveringLayout1.removeView(rg_tab_bar);
                hoveringLayout2.addView(rg_tab_bar);
            }
        }
        else {
            if (rg_tab_bar.getParent() != hoveringLayout1) {
                hoveringLayout2.removeView(rg_tab_bar);
                hoveringLayout1.addView(rg_tab_bar);
            }
        }
    }
}