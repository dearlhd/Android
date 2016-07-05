package com.eastmoney.android.ui;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lkh on 2015/6/24.
 */
public class LKHScrollView extends ScrollView{
    //当前底部可滚动容器
    private ViewGroup currentBottomScrollableView;
    //手指是否在向下滑
    private boolean isFingerMoveDown;
    //记录按下时的y坐标
    private float startY;
    //记住上次事件的Y坐标
    private float lastY;
    //是否要控制事件分发
    private boolean needControlEventDispatch = false;
    //最后一次事件发往子view
    private boolean lastEventGoToChildView = false;

    private ViewGroup bottom_tab_content_container;

    private int mTouchSlop;

    private float downY;

    public LKHScrollView(Context context) {
        super(context);
        init();
    }

    public LKHScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
            setOverScrollMode(OVER_SCROLL_NEVER);
        }
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    //本方法如果down时返回false则后续事件都不会再进来了
    //否则，后续事件都会进来
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return handTouch(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean handTouch(MotionEvent ev) {
        //寻找可滚动容器
        if (currentBottomScrollableView == null || !currentBottomScrollableView.isShown()) {
            currentBottomScrollableView = findShownScrollableView(bottom_tab_content_container);
            if(currentBottomScrollableView==null || !currentBottomScrollableView.isShown()){
                //还是没找到
                lastEventGoToChildView=false;
                return super.dispatchTouchEvent(ev);
            }
        }

        //手势方向
        float distance = ev.getRawY() - lastY;
        isFingerMoveDown = (distance >= 0);
        lastY = ev.getRawY();
        //
        final int action = ev.getActionMasked();

        boolean isOnChild = false;
        if (currentBottomScrollableView != null) {
            Rect rect = new Rect();
            if (currentBottomScrollableView.getGlobalVisibleRect(rect) && rect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                isOnChild = true;
                //ACTION_DOWN事件发生在滚动容器上
                if (action == MotionEvent.ACTION_DOWN) {
                    needControlEventDispatch = true;
                }
            }
        }

        boolean isContentReachTop = doesContentReachTop(currentBottomScrollableView);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                //---
                //滚动到底分发逻辑
                if(needControlEventDispatch && (isOnBottom() || !isContentReachTop)){
                    //手向下滑到顶了，事件转交父容器处理
                    if(isFingerMoveDown && isContentReachTop
                            || (downY-ev.getRawY()>mTouchSlop)&&!isOnBottom()){//父容器没到底时，向上移动
                        //由子容器处理
                        if (lastEventGoToChildView) {
                            //取消子view状态
                            MotionEvent down = MotionEvent.obtain(ev);
                            down.setAction(MotionEvent.ACTION_CANCEL);
                            bottom_tab_content_container.dispatchTouchEvent(offsetEventForView(down, bottom_tab_content_container));
                            //模拟down
                            down.setAction(MotionEvent.ACTION_DOWN);
                            lastEventGoToChildView=false;
                            super.dispatchTouchEvent(down);
                        }
                        return super.dispatchTouchEvent(ev);

                    }else{
                        //由子容器处理
                        if (!lastEventGoToChildView) {
                            //取消父view状态
                            MotionEvent fakeEvent = MotionEvent.obtain(ev);
                            fakeEvent.setAction(MotionEvent.ACTION_CANCEL);
                            super.dispatchTouchEvent(fakeEvent);
                            //模拟down
                            fakeEvent.setAction(MotionEvent.ACTION_DOWN);
                            lastEventGoToChildView=true;
                            bottom_tab_content_container.dispatchTouchEvent(offsetEventForView(fakeEvent, bottom_tab_content_container));
                        }
                        return bottom_tab_content_container.dispatchTouchEvent(offsetEventForView(ev, bottom_tab_content_container));
                    }
                }else if (needControlEventDispatch && isContentReachTop && !isOnBottom()){
                    if (lastEventGoToChildView){
                        //取消子view状态
                        MotionEvent down = MotionEvent.obtain(ev);
                        down.setAction(MotionEvent.ACTION_CANCEL);
                        bottom_tab_content_container.dispatchTouchEvent(offsetEventForView(down, bottom_tab_content_container));
                        //模拟down
                        down.setAction(MotionEvent.ACTION_DOWN);
                        lastEventGoToChildView=false;
                        super.dispatchTouchEvent(down);
                    }
                    return super.dispatchTouchEvent(ev);
                }

                if (!needControlEventDispatch){
                    lastEventGoToChildView=false;
                    return super.dispatchTouchEvent(ev);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                needControlEventDispatch = false;
                if (!lastEventGoToChildView){
                    return super.dispatchTouchEvent(ev);
                }else {
                    return bottom_tab_content_container.dispatchTouchEvent(offsetEventForView(ev, bottom_tab_content_container));
                }
            default:
                return false;
        }
        //
        if((isOnBottom() || !isContentReachTop) && isOnChild){
            lastEventGoToChildView=true;
            return bottom_tab_content_container.dispatchTouchEvent(offsetEventForView(ev, bottom_tab_content_container));
        }else{
            lastEventGoToChildView=false;
            return super.dispatchTouchEvent(ev);
        }
    }


    //为子View偏移事件
    private MotionEvent offsetEventForView(MotionEvent ev, View view) {
        MotionEvent newEvent = MotionEvent.obtain(ev);
        newEvent.offsetLocation(getScrollX()-view.getLeft(), getScrollY()-view.getTop());
        return newEvent;
    }

    //判断滚动容器是否滚动到顶部
    private boolean doesContentReachTop(ViewGroup scrollableContainer) {
        if (scrollableContainer == null) {
            return true;
        }
        //
        if (scrollableContainer instanceof ListView) {
            ListView listview = (ListView) scrollableContainer;
            //如果是listview
            View mostTopChildView = listview.getChildAt(0);
            if(mostTopChildView==null){
                //没有孩子
                return true;
            }
            boolean isMostTopChildViewAtTop = mostTopChildView.getTop() - listview.getPaddingTop() >= 0;
            //listview滚动到最上边
            return listview.getFirstVisiblePosition() <= 0 && isMostTopChildViewAtTop;

        } else if (scrollableContainer instanceof ScrollView) {
            //如果是ScrollView
            ScrollView scrollView = (ScrollView) scrollableContainer;
            boolean isChildViewAtTop =(scrollView.getScrollY()<=0);
            return isChildViewAtTop;

        } else if (scrollableContainer instanceof WebView) {
            //如果是WebView
            WebView webView = (WebView) scrollableContainer;
            boolean isAtTop =(webView.getScrollY()<=0);
            return isAtTop;

        }
        return false;
    }

    /**
     * 从底部Fragment容器中寻找当前可见的一个可滚动容器:ScrollView, ListView, WebView
     */
    public ViewGroup findShownScrollableView(ViewGroup container) {
        //
        if (container == null || !container.isShown()) {
            return null;
        }
        //
        if (container instanceof ScrollView || container instanceof ListView || container instanceof WebView) {
            return container;
        }
        //
        ViewGroup vg = container;
        int count = vg.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = vg.getChildAt(i);
            if (child instanceof ViewGroup) {
                ViewGroup target = findShownScrollableView((ViewGroup) child);
                if (target != null) {
                    return target;
                }
            }
        }
        return null;
    }

    //底部的滚动容器滚动到顶
    private void bottomContentSrollToTop(){
        try {
            List<ViewGroup> scrollableViews = findAllScrollableView(bottom_tab_content_container);
            for (ViewGroup vg : scrollableViews) {
                if (vg instanceof WebView) {
                    ((WebView)vg).scrollTo(0,0);
                } else if (vg instanceof ScrollView) {
                    ((ScrollView)vg).scrollTo(0,0);
                } else if (vg instanceof ListView) {
                    int count=((ListView)vg).getCount();
                    if(count>0){
                        ((ListView)vg).setSelection(0);
                    }

                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    /**
     * 从底部container容器中寻找所有可滚动容器:ScrollView, ListView, WebView
     */
    public List<ViewGroup> findAllScrollableView(ViewGroup container) {
        List<ViewGroup> resultList=new ArrayList<>();
        findScrollableView(resultList, container);
        return resultList;
    }
    public void findScrollableView(List<ViewGroup> resultList, ViewGroup container) {
        //
        if (container == null) {
            return;
        }
        //
        if (container instanceof ScrollView || container instanceof ListView || container instanceof WebView) {
            resultList.add(container);
            return ;
        }
        //
        ViewGroup vg = (ViewGroup) container;
        int count = vg.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = vg.getChildAt(i);
            if (child instanceof ViewGroup) {
                findScrollableView(resultList, (ViewGroup) child);
            }
        }
    }

    //判断当前StockItem是否滚动到了底部
    public boolean isOnBottom(){
        boolean isOnBottom = getChildAt(0).getMeasuredHeight() <= getScrollY() + getHeight();
        return isOnBottom;
    }

    public void initBottomContent(ViewGroup bottom_tab_content_container, int height){
        this.bottom_tab_content_container = bottom_tab_content_container;
        //由于布局还未完成就需要高度，这里需要写死
        ViewGroup.LayoutParams lp = bottom_tab_content_container.getLayoutParams();
        lp.height = height;
        bottom_tab_content_container.setLayoutParams(lp);
    }
}
