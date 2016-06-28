package com.dearlhd.myapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dearlhd.myapp.R;
import com.dearlhd.myapp.adapter.PersonAdapter;
import com.dearlhd.myapp.entity.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dearlhd on 2016/6/26 0028.
 */
public class ListFragment extends Fragment {

    private ListView listView;

    public ListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page1, container, false);

        listView = (ListView) view.findViewById(R.id.list_view);

        // 给 listView 赋值，通过自定义的adapter
        List<Person> persons = new ArrayList<Person>();
        for (int i = 0; i < 10; i++) {
            persons.add(new Person("person" + i, "info" + i, R.mipmap.ic_launcher));
        }

        PersonAdapter pAdapter = new PersonAdapter(persons, this.getActivity());
        listView.setAdapter(pAdapter);
        setListViewHeight();
        return view;
    }

    // 计算listView的高度
    public void setListViewHeight() {
        PersonAdapter personAdapter = (PersonAdapter) listView.getAdapter();

        if (personAdapter == null) {
            return;
        }

        int height = 0;

        for (int i = 0; i < personAdapter.getCount(); i++) {
            View listItem = personAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            height += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = height + (listView.getDividerHeight() * (personAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
