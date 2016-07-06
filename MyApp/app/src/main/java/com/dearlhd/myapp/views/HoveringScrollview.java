package com.dearlhd.myapp.views;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 * Created by dearlhd on 2016/6/26.
 */
public class HoveringScrollview extends ScrollView {

    //当前底部可滚动容器 (ListView, WebView, ScrollView, etc.)
    private ViewGroup currentBottomScrollableView;

    //手指是否在向下滑
    private boolean isFingerMoveDown;

    //记住上次事件的Y坐标
    private float lastY;
    //是否要控制事件分发
    private boolean needControlEventDispatch = false;
    //最后一次事件发往子view
    private boolean lastEventGoToChildView = false;

    private ViewGroup fragment_container;

    /**
     * 主要是用在用户手指离开本view，本view还在继续滑动，保存Y的距离，然后做比较
     */
//    private int lastScrollY;

    public HoveringScrollview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //用于用户手指离开HoveringScrollview的时候获取HoveringScrollview滚动的Y距离，然后回调给onScroll方法中
//    private Handler handler = new Handler() {
//
//        public void handleMessage(android.os.Message msg) {
//            int scrollY = HoveringScrollview.this.getScrollY();
//
//            // 此时的距离和记录下的距离不相等，在隔6毫秒给handler发送消息
//            if (lastScrollY != scrollY) {
//                lastScrollY = scrollY;
//                handler.sendMessageDelayed(handler.obtainMessage(), 6);
//            }
//
//        }
//    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        fragment_container = (ViewGroup) ((ViewGroup) this.getChildAt(0)).getChildAt(3);

        //寻找可滚动容器
        if (currentBottomScrollableView == null || !currentBottomScrollableView.isShown()) {
            currentBottomScrollableView = findShownScrollableView((ViewGroup) this.getChildAt(0));
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

        final int action = ev.getActionMasked();

        // 判断手势是否在fragment中可滑动的控件上
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
                break;
            case MotionEvent.ACTION_MOVE:
                //滚动到底分发逻辑

                // 滑动动作在fragment的滑动控件上，且控件没顶到屏幕顶端
                if(needControlEventDispatch && isOnBottom()) {
                    //处理手指下滑后，导航栏触到顶部，将事件交回父容器处理的情况
                    if(isFingerMoveDown && isContentReachTop) {
                        //当上次事件是由子控件处理时
                        if (lastEventGoToChildView) {
                            //取消子view状态
                            MotionEvent down = MotionEvent.obtain(ev);
                            //模拟down
                            down.setAction(MotionEvent.ACTION_DOWN);
                            lastEventGoToChildView=false;
                            super.dispatchTouchEvent(down);
                        }
                        return super.dispatchTouchEvent(ev);
                    }
                    else{
                        //手指上滑或下滑时没滑到顶，交由子控件处理
                        if (!lastEventGoToChildView) {
                            //取消父view状态
                            MotionEvent fakeEvent = MotionEvent.obtain(ev);
                            //模拟down
                            fakeEvent.setAction(MotionEvent.ACTION_DOWN);
                            lastEventGoToChildView=true;
                            fragment_container.dispatchTouchEvent(offsetEventForView(fakeEvent, fragment_container));
                        }
                        return fragment_container.dispatchTouchEvent(offsetEventForView(ev, fragment_container));
                    }
                }
                else if (needControlEventDispatch && isContentReachTop && !isOnBottom()){
                    if (lastEventGoToChildView){
                        //取消子view状态
                        MotionEvent down = MotionEvent.obtain(ev);
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
                }
                else {
                    return fragment_container.dispatchTouchEvent(offsetEventForView(ev, fragment_container));
                }
            default:
                return false;
        }
        //
        if((isOnBottom() || !isContentReachTop) && isOnChild) {
            lastEventGoToChildView=true;
            return fragment_container.dispatchTouchEvent(offsetEventForView(ev, fragment_container));
        }
        else {
            lastEventGoToChildView=false;
            return super.dispatchTouchEvent(ev);
        }
    }

    /**
     * 重写onTouchEvent， 当用户的手在HoveringScrollview上面的时候，
     * 直接将HoveringScrollview滑动的Y方向距离回调给onScroll方法中，当用户抬起手的时候，
     * HoveringScrollview可能还在滑动，所以当用户抬起手我们隔20毫秒给handler发送消息，在handler处理
     * HoveringScrollview滑动的距离
     */
//    public boolean onTouchEvent(MotionEvent ev) {
//        if (onScrollListener != null) {
//            onScrollListener.onScroll(lastScrollY = this.getScrollY());
//        }
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_UP:
//                handler.sendMessageDelayed(handler.obtainMessage(), 20);
//                break;
//        }
//
//        boolean f = super.onTouchEvent(ev);
//
//        return f;
//    }

    /**
     * 从底部Fragment容器中寻找当前可见的一个可滚动容器:ScrollView, ListView, WebView
     */
    public ViewGroup findShownScrollableView(ViewGroup container) {

        if (container == null || !container.isShown()) {
            return null;
        }

        if (container instanceof ScrollView || container instanceof ListView || container instanceof WebView) {
            return container;
        }

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

    //判断滚动容器是否滚动到顶部
    private boolean doesContentReachTop(ViewGroup scrollableContainer) {
        if (scrollableContainer == null) {
            return true;
        }

        if (scrollableContainer instanceof MyListView) {
            MyListView listview = (MyListView) scrollableContainer;
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

    //判断上部的导航栏是否碰到了顶部,碰到了则返回true
    public boolean isOnBottom(){
        boolean isOnBottom = getChildAt(0).getMeasuredHeight() <= getScrollY() + getHeight();
        return isOnBottom;
    }

    //为子View偏移事件
    private MotionEvent offsetEventForView(MotionEvent ev, View view) {
        MotionEvent newEvent = MotionEvent.obtain(ev);
        newEvent.offsetLocation(getScrollX()-view.getLeft(), getScrollY()-view.getTop());
        return newEvent;
    }
}
