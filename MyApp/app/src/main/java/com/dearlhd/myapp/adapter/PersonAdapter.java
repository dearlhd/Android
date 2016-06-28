package com.dearlhd.myapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dearlhd.myapp.entity.Person;
import com.dearlhd.myapp.R;

import java.util.List;

/**
 * Created by dearlhd on 2016/6/26.
 *
 *  自定义的adapter，用以显示list view中的内容，主要包括图片，和两段文字
 */
public class PersonAdapter extends BaseAdapter {
    private List<Person> persons;
    private Context mContext;
    private static int cnt = 0;

    public PersonAdapter(List<Person> ps,Context context) {
        this.persons = ps;
        this.mContext = context;
    }


    @Override
    public int getCount() {
        return persons.size();
    }

    @Override
    public Object getItem(int position) {
        return persons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        }
        ImageView img_icon = (ImageView) convertView.findViewById(R.id.img_icon);
        TextView aName = (TextView) convertView.findViewById(R.id.person_name);
        TextView aSaying = (TextView) convertView.findViewById(R.id.person_info);

        Person person = persons.get(position);
        img_icon.setBackgroundResource(person.getaIcon());
        aName.setText(person.getName());
        aSaying.setText(person.getInfo());

        return convertView;
    }
}
