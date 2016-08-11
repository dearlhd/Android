package com.lhd.largelistview.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lhd.largelistview.R;
import com.lhd.largelistview.bean.MyItem;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2016/7/22.
 */
public class MyAdapter extends BaseAdapter{
    final String[] PATHS = {
            "http://a.hiphotos.baidu.com/image/h%3D200/sign=aaaa5a3da00f4bfb93d09954334e788f/a8ec8a13632762d0dfab911da4ec08fa503dc6fe.jpg",
            "http://www.5068.com/u/faceimg/20140804114111.jpg",
            "http://t-1.tuzhan.com/4a6cdc6c876b/c-2/l/2013/05/23/22/ee4dd25e87fb4b27b63710ea6945491b.jpg",
            "http://img3.imgtn.bdimg.com/it/u=1720993534,2031607673&fm=206&gp=0.jpg",
            "http://img2.imgtn.bdimg.com/it/u=4198754941,397536641&fm=206&gp=0.jpg",
            "http://img2.imgtn.bdimg.com/it/u=1744644003,881536991&fm=21&gp=0.jpg",
            "http://d.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=d8c645729513b07ebde8580c39e7bd15/a8014c086e061d95294ff56b7df40ad162d9ca5c.jpg",
            "http://img3.imgtn.bdimg.com/it/u=1900870038,3293077285&fm=21&gp=0.jpg",
            "http://c.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=9742b125133853438c9a8f25a6239c48/29381f30e924b8990b693b716d061d950a7bf626.jpg",
            "http://img1.imgtn.bdimg.com/it/u=3554137992,3877295111&fm=21&gp=0.jpg"
    };

    private List<MyItem> mDataList;
    private LayoutInflater mInflater;

    public MyAdapter (Context context, List<MyItem> dataList) {
        mInflater = LayoutInflater.from(context);
        mDataList = dataList;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int i) {
        return mDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        MyItem item = mDataList.get(i);
        ViewHolder viewHolder;

        if (view == null) {
            view = mInflater.inflate(R.layout.item_list, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.img = (ImageView) view.findViewById(R.id.img);
            viewHolder.title = (TextView) view.findViewById(R.id.txt_title);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }

        //final ViewHolder holder = viewHolder;

        final ImageView img = (ImageView) view.findViewById(R.id.img);
        TextView title = (TextView) view.findViewById(R.id.txt_title);

        img.setImageResource(item.getmImg());

        final int index = i % 10;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final String path = PATHS[index];
                try {
                    InputStream is = new URL(path).openStream();
                    final Bitmap bm = BitmapFactory.decodeStream(is);

                    img.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("dong", "received pic");
                            if (img.getTag().equals(path)) {
                                Log.i("dong", "pic matched");
                                img.setImageBitmap(bm);
                            }
                        }
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        img.setTag(PATHS[index]);
        thread.start();

        title.setText(item.getmTitle());

        return view;
    }

    public void addAll (List<MyItem> items) {
        if (mDataList == null) {
            mDataList = new ArrayList<MyItem>();
        }

        mDataList.addAll(items);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        ImageView img;
        TextView title;
    }
}
