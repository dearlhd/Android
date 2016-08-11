package com.lhd.largelistview.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lhd.largelistview.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/7/21.
 */
public class LargeListView extends ListView implements AbsListView.OnScrollListener, View.OnClickListener {
    private int mTouchSlop;

    private RotateAnimation mDownToUpAnimation;
    private RotateAnimation mUpToDownAnimation;

    // 下拉刷新 头部部分
    private View mHeaderView;
    private ImageView mHeaderArrowView;
    private ProgressBar mHeaderProgressBar;
    private TextView mHeaderTipTxt;
    private TextView mHeaderTimeTxt;

    // 上拉请求数据 尾部
    private View mFooterView;
    private ProgressBar mFooterProgressBar;
    private Button mFooterTipBtn;

    // 头部高度
    private int mHeaderViewHeight;

    // 手指开始滑动时的Y坐标
    private float mStartY;

    // 记录ListView的可见范围
    private int mFirstVisibleItem;
    private int mLastVisibleItem;

    private enum HeaderViewState { Normal, PullToRefresh, ReleaseToRefresh, Refreshing};
    private enum FooterViewState { Normal, Loading, Failure, NoMoreData};

    private HeaderViewState mHeaderViewState = HeaderViewState.Normal;
    private FooterViewState mFooterViewState = FooterViewState.Normal;

    private OnRefreshListener mOnRefreshListener;

    private static final float RATIO = 0.5F;

    public LargeListView(Context context) {
        super(context);
        initView();
    }

    public LargeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LargeListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * Check if this view can be scrolled vertically in a certain direction.
     *
     * @param direction Negative to check scrolling up, positive to check scrolling down.
     * @return true if this view can be scrolled in the specified direction, false otherwise.
     */
    @Override
    public boolean canScrollVertically(int direction) {
        int offset = computeVerticalScrollOffset();
        int range = computeVerticalScrollRange() - computeVerticalScrollExtent();
        if (range == 0) {
            return false;
        }
        if (direction < 0) {
            return offset > 0;
        }
        else {
            return offset < range - 1;
        }
    }

    @Override
    public boolean onTouchEvent (MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                final float moveY = ev.getRawY() - mStartY;
                switch (mHeaderViewState) {
                    case Normal:
                        if (mFirstVisibleItem == 0 && moveY > mTouchSlop) {
                            mStartY = ev.getRawY();
                            mHeaderViewState = HeaderViewState.PullToRefresh;
                        }
                        break;
                    case PullToRefresh:
                        setSelection(0);
                        if (moveY * RATIO <= 0) {
                            mHeaderView.setPadding(0, -mHeaderViewHeight, 0 , 0);
                        }
                        else {
                            mHeaderView.setPadding(0, (int)(-mHeaderViewHeight + moveY * RATIO), 0, 0);
                        }
                        if (mHeaderView.getPaddingTop() >= 0) {
                            mHeaderViewState = HeaderViewState.ReleaseToRefresh;
                            changeHeaderViewByState();
                        }
                        break;
                    case ReleaseToRefresh:
                        setSelection(0);
                        if (mHeaderView.getPaddingTop() < 0) {
                            mHeaderViewState = HeaderViewState.PullToRefresh;
                            changeHeaderViewByState();
                        }
                        mHeaderView.setPadding(0, (int)(-mHeaderViewHeight + moveY * RATIO), 0, 0);
                        break;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                mStartY = ev.getRawY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                switch (mHeaderViewState) {
                    case PullToRefresh:
                        mHeaderViewState = HeaderViewState.Normal;
                        changeHeaderViewByState();
                        break;
                    case ReleaseToRefresh:
                        mHeaderViewState = HeaderViewState.Refreshing;
                        changeHeaderViewByState();
                        onRefresh();
                        break;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void onClick(View view) {
        onGetMore();
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mFirstVisibleItem = firstVisibleItem;
        if (mLastVisibleItem == this.getLastVisiblePosition()) {
            return;
        }
        mLastVisibleItem = this.getLastVisiblePosition();
        if (mLastVisibleItem == this.getCount()-1) {
            Log.i("dong", "OnGetMore... " + this.getCount());
            onGetMore();
        }
    }

    public void initList () {
        this.post(new Runnable() {
            @Override
            public void run() {
                setSelection(0);
                mHeaderViewState = HeaderViewState.Refreshing;
                changeHeaderViewByState();
                onRefresh();
            }
        });
    }

    public void initView () {
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        initHeaderView();
        initFooterView();
        initAnimation();
        this.setOnScrollListener(this);
    }

    public void setOnRefreshListener (OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    // 下拉刷新完成后调用
    public void onRefreshComplete() {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String time = df.format(date);

        mHeaderTimeTxt.setText(time);
        mHeaderViewState = HeaderViewState.Normal;
        changeHeaderViewByState();
    }

    public void onLoadingComplete () {
        setFooterViewState(FooterViewState.Normal);
    }

    public void showNoMoreData () {
        addFooter();
        setFooterViewState(FooterViewState.NoMoreData);
    }

    private void initHeaderView () {
        mHeaderView = LayoutInflater.from(getContext()).inflate(R.layout.item_refresh_header, this, false);
        mHeaderView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mHeaderArrowView = (ImageView) mHeaderView.findViewById(R.id.header_arrow_img);
        mHeaderProgressBar = (ProgressBar) mHeaderView.findViewById(R.id.header_progress_bar);
        mHeaderTipTxt = (TextView) mHeaderView.findViewById(R.id.header_tip_txt);
        mHeaderTimeTxt = (TextView) mHeaderView.findViewById(R.id.header_time_txt);

        measureView(mHeaderView);

        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String time = df.format(date);
        mHeaderTimeTxt.setText("最近更新：" + time);
        mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0); // 放在屏幕之外
        this.addHeaderView(mHeaderView);
    }

    private void measureView (View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, params.width);
        int childHeightSpec;
        if (params.height > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY);
        }
        else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(childWidthSpec, childHeightSpec);
    }

    private void initFooterView () {
        mFooterView = LayoutInflater.from(getContext()).inflate(R.layout.item_refresh_footer, this, false);
        mFooterProgressBar = (ProgressBar) mFooterView.findViewById(R.id.footer_progress_bar);
        mFooterTipBtn = (Button) mFooterView.findViewById(R.id.footer_tip_btn);
        mFooterTipBtn.setOnClickListener(this);
        setFooterViewState(FooterViewState.Normal);
        addFooter();
    }

    private void initAnimation () {
        mDownToUpAnimation = new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mDownToUpAnimation.setInterpolator(new LinearInterpolator());
        mDownToUpAnimation.setDuration(200);
        mDownToUpAnimation.setFillAfter(true);

        mUpToDownAnimation = new RotateAnimation(180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mDownToUpAnimation.setInterpolator(new LinearInterpolator());
        mDownToUpAnimation.setDuration(200);
        mDownToUpAnimation.setFillAfter(true);
    }

    private void changeHeaderViewByState () {
        switch (mHeaderViewState) {
            case ReleaseToRefresh:
                mHeaderProgressBar.setVisibility(View.INVISIBLE);
                mHeaderTipTxt.setVisibility(View.VISIBLE);
                mHeaderTimeTxt.setVisibility(View.VISIBLE);
                mHeaderArrowView.setVisibility(View.VISIBLE);
                mHeaderArrowView.clearAnimation();
                mHeaderArrowView.setAnimation(mDownToUpAnimation);
                mHeaderTipTxt.setText("松开刷新");
                break;
            case PullToRefresh:
                mHeaderProgressBar.setVisibility(View.INVISIBLE);
                mHeaderTipTxt.setVisibility(View.VISIBLE);
                mHeaderTimeTxt.setVisibility(View.VISIBLE);
                mHeaderArrowView.setVisibility(View.VISIBLE);
                mHeaderArrowView.clearAnimation();
                mHeaderArrowView.setAnimation(mUpToDownAnimation);
                mHeaderTipTxt.setText("下拉刷新");
                break;
            case Refreshing:
                mHeaderView.setPadding(0, 0, 0, 0);
                mHeaderProgressBar.setVisibility(View.VISIBLE);
                mHeaderArrowView.clearAnimation();
                mHeaderArrowView.setVisibility(View.INVISIBLE);
                mHeaderTipTxt.setText("正在刷新...");
                mHeaderTimeTxt.setVisibility(View.VISIBLE);
                break;
            case Normal:
                mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);
                mHeaderArrowView.setVisibility(View.VISIBLE);
                mHeaderArrowView.clearAnimation();
                mHeaderArrowView.setImageResource(R.drawable.arrow_down);
                mHeaderTipTxt.setText("下拉刷新");
                mHeaderProgressBar.setVisibility(View.INVISIBLE);
                mHeaderTimeTxt.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void addFooter () {
        if (this.getFooterViewsCount() == 0) {
            this.addFooterView(mFooterView);
        }
    }

    private void setFooterViewState(FooterViewState state) {
        mFooterViewState = state;
        switch (state) {
            case Loading:
                mFooterProgressBar.setVisibility(View.VISIBLE);
                mFooterTipBtn.setVisibility(View.GONE);
                break;
            case Failure:
                mFooterProgressBar.setVisibility(View.GONE);
                mFooterTipBtn.setVisibility(View.VISIBLE);
                mFooterTipBtn.setText("加载失败，点击重试");
                break;
            case NoMoreData:
                mFooterProgressBar.setVisibility(View.GONE);
                mFooterTipBtn.setVisibility(View.VISIBLE);
                mFooterTipBtn.setText("已显示全部内容");
                break;
            case Normal:
            default:
                mFooterProgressBar.setVisibility(View.GONE);
                mFooterTipBtn.setVisibility(View.VISIBLE);
                mFooterTipBtn.setText("点击加载更多");
                break;
        }
    }

    private void onRefresh () {
        if (mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh();
        }
    }

    private synchronized void onGetMore () {
        Log.i("dong", "onGetMore state is " + mFooterViewState);
        if (mFooterViewState == FooterViewState.NoMoreData || mFooterViewState == FooterViewState.Loading) {
            return;
        }

        if (mOnRefreshListener != null) {
            setFooterViewState(mFooterViewState.Loading);
            mOnRefreshListener.onGetMore();
        }
    }

    public interface OnRefreshListener {
        void onRefresh();

        void onGetMore();
    }
}
