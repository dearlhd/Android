package com.dearlhd.helloworld;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v4.graphics.BitmapCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.dearlhd.helloworld.adapter.MyPagerAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private ViewFlipper adFlipper;

    private ViewPager viewPager;
    private ImageView imgCursor;
    private TextView menuTab1;
    private TextView menuTab2;
    private TextView menuTab3;
    private TextView menuTab4;

    private ArrayList<View> listViews;
    private int offset = 0;
    private int currentIndex = 0;
    private int bmpWidth;
    private int dis = 0;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    public void initViews () {
        adFlipper = (ViewFlipper) findViewById(R.id.adFlipper);
        adFlipper.startFlipping();

        viewPager = (ViewPager) findViewById(R.id.vpager);
        imgCursor = (ImageView) findViewById(R.id.img_cursor);
        menuTab1 = (TextView) findViewById(R.id.menu_tab1);
        menuTab2 = (TextView) findViewById(R.id.menu_tab2);
        menuTab3 = (TextView) findViewById(R.id.menu_tab3);
        menuTab4 = (TextView) findViewById(R.id.menu_tab4);

        bmpWidth = BitmapFactory.decodeResource(getResources(), R.mipmap.line).getWidth();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int screenWid = dm.widthPixels;
        offset = (screenWid / 4 - bmpWidth)  / 2;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        imgCursor.setImageMatrix(matrix); // origin position of the animation

        dis = offset * 2 + bmpWidth;

        listViews = new ArrayList<View>();
        LayoutInflater mInflater = getLayoutInflater();
        listViews.add(mInflater.inflate(R.layout.view1, null, false));
        listViews.add(mInflater.inflate(R.layout.view2, null, false));
        listViews.add(mInflater.inflate(R.layout.view3, null, false));
        listViews.add(mInflater.inflate(R.layout.view4, null, false));

        viewPager.setAdapter(new MyPagerAdapter(listViews));
        viewPager.setCurrentItem(0);

        menuTab1.setOnClickListener(this);
        menuTab2.setOnClickListener(this);
        menuTab3.setOnClickListener(this);
        menuTab4.setOnClickListener(this);

        viewPager.addOnPageChangeListener(this);

        webView = (WebView) findViewById(R.id.web_view);

        if (webView == null) {
            System.out.println("web view is null!");
            return;
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://www.baidu.com");
        //setContentView(webView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_tab1:
                viewPager.setCurrentItem(0);
                break;
            case R.id.menu_tab2:
                viewPager.setCurrentItem(1);
                break;
            case R.id.menu_tab3:
                viewPager.setCurrentItem(2);
                break;
            case R.id.menu_tab4:
                viewPager.setCurrentItem(3);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        System.out.println("lhd\n\n\n\n\n\n\n\n\n\n\n");
        Animation animation = null;
        System.out.println(position + " Tab is pressed!");
        switch (position) {
            case 0:
                if (currentIndex == 1) {
                    animation = new TranslateAnimation(dis, 0, 0, 0);
                }
                else if (currentIndex == 2){
                    animation = new TranslateAnimation(2 * dis, 0, 0, 0);
                }
                else if (currentIndex == 3){
                    animation = new TranslateAnimation(3 * dis, 0, 0, 0);
                }
                break;
            case 1:
                if (currentIndex == 0) {
                    animation = new TranslateAnimation(offset, dis, 0, 0);
                }
                else if (currentIndex == 2){
                    animation = new TranslateAnimation(2 * dis, dis, 0, 0);
                }
                else if (currentIndex == 3){
                    animation = new TranslateAnimation(3 * dis, dis, 0, 0);
                }
                break;
            case 2:
                if (currentIndex == 0) {
                    animation = new TranslateAnimation(offset, 2 * dis, 0, 0);
                }
                else if (currentIndex == 1){
                    animation = new TranslateAnimation(dis, 2 * dis, 0, 0);
                }
                else if (currentIndex == 3){
                    animation = new TranslateAnimation(3 * dis, 2 * dis, 0, 0);
                }
                break;
            case 3:
                if (currentIndex == 0) {
                    animation = new TranslateAnimation(offset, 3 * dis, 0, 0);
                }
                else if (currentIndex == 1){
                    animation = new TranslateAnimation(dis, 3 * dis, 0, 0);
                }
                else if (currentIndex == 2){
                    animation = new TranslateAnimation(2 * dis, 3 * dis, 0, 0);
                }
                break;
        }
        currentIndex = position;
        animation.setFillAfter(true);
        animation.setDuration(300);
        imgCursor.startAnimation(animation);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
