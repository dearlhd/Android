package com.dearlhd.myapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dearlhd.myapp.entity.Person;
import com.dearlhd.myapp.adapter.PersonAdapter;
import com.dearlhd.myapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dearlhd on 2016/6/26.
 */
public class ListFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page1,container,false);
        initListView(view);
        return view;
    }

    public void initListView(View view) {
        List<Person> persons = new ArrayList<Person>();
        ListView listView = (ListView) view.findViewById(R.id.list_view);

        System.out.println("Init list fragment data!");
        for (int i = 0; i < 2; i++) {
            persons.add(new Person("person" + i, "info" + i, R.mipmap.ic_launcher));
        }

        PersonAdapter pAdapter = new PersonAdapter(persons, this.getActivity());
        listView.setAdapter(pAdapter);
    }
}
