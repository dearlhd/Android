package com.dearlhd.myapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dearlhd.myapp.entity.Person;
import com.dearlhd.myapp.R;

import java.util.List;

/**
 * Created by dearlhd on 2016/6/26.
 */
public class PersonAdapter extends BaseAdapter {
    private List<Person> persons;
    private Context mContext;

    public PersonAdapter(List<Person> ps,Context context) {
        persons = ps;
        mContext = context;
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
        convertView = LayoutInflater.from(mContext).inflate(R.layout.page1, parent, false);
        //ImageView icon = (ImageView) convertView.findViewById(R.id.img_icon);
        TextView nameText = (TextView) convertView.findViewById(R.id.person_name);
        TextView infoText = (TextView) convertView.findViewById(R.id.person_info);

        //icon.setBackgroundResource(persons.get(position).getaIcon());
        nameText.setText(persons.get(position).getName());
        infoText.setText(persons.get(position).getInfo());

        return convertView;
    }
}
