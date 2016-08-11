package com.lhd.largelistview.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lhd.largelistview.R;
import com.lhd.largelistview.adapter.MyAdapter;
import com.lhd.largelistview.bean.MyItem;
import com.lhd.largelistview.ui.LargeListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/21.
 */
public class LargeListViewFragment extends Fragment {
    protected View mRootView;

    private LargeListView mListView;

    protected MyAdapter mAdapter;

    private List<MyItem> mDataList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_large_list, container, false);
            initView();
        }

        return mRootView;
    }

    private void initView () {
        initListView();
    }

    private void initListView () {
        mListView = (LargeListView) mRootView.findViewById(R.id.large_list_view);
        initAdapter();

        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
        }

        setListViewListener();
        mListView.initList();
    }

    protected void initAdapter() {
        mDataList = new ArrayList<MyItem>();

        for (int i = 0; i < 20; i++) {
            MyItem item = new MyItem(R.mipmap.ic_launcher, "Title"+i, "content");
            mDataList.add(item);
        }

        if (mAdapter == null) {
            mAdapter = new MyAdapter(this.getActivity(), mDataList);
            mListView.setAdapter(mAdapter);
        }
    }

    protected void setListViewListener() {
        mListView.setOnRefreshListener(new LargeListView.OnRefreshListener() {
            @Override
            public void onRefresh () {
                refresh();
            }

            @Override
            public void onGetMore () {
                getNextPage();
            }
        });
        //mListView.setOnScrollListener();
    }

    final int FRESHINGSTATUS = 1;
    final int LOADINGSTATUS = 2;

    final Handler handler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            switch (msg.what) {
                case FRESHINGSTATUS:
                    mListView.onRefreshComplete();
                    break;
                case LOADINGSTATUS:
                    List<MyItem> newPageData = new ArrayList<MyItem>();
                    Log.i("dong", "adapter count is " + mAdapter.getCount());
                    for (int i = mAdapter.getCount(); i < mAdapter.getCount() + 20; i++) {
                        MyItem item = new MyItem(R.mipmap.ic_launcher, "Title"+i, "content");
                        newPageData.add(item);
                    }
                    mAdapter.addAll(newPageData);
                    mListView.onLoadingComplete();
                    break;
            }
        }
    };

    private void getNextPage() {
        //TODO do something
        Log.i("dong", "getNextPage");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100000000; i++) {

                }

                Message message = new Message();
                message.what = LOADINGSTATUS;
                handler.sendMessage(message);
            }
        });
        thread.start();
    }

    private void refresh () {
        //TODO do something
        Log.i("dong", "refreshing...");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100000000; i++) {

                }

                Message message = new Message();
                message.what = FRESHINGSTATUS;
                handler.sendMessage(message);
            }
        });
        thread.start();
    }
}
