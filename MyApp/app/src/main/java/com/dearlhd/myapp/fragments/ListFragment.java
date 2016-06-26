package com.dearlhd.myapp.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.dearlhd.myapp.MainActivity;
import com.dearlhd.myapp.Person;
import com.dearlhd.myapp.PersonAdapter;
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
        //initListView();
        return view;
    }

    public void initListView(Context context, View view) {
        List<Person> persons = new ArrayList<Person>();
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        for (int i = 0; i < 10; i++) {
            persons.add(new Person("person" + i, "info" + i, R.mipmap.ic_launcher));
        }

        PersonAdapter pAdapter = new PersonAdapter(persons, context);
        listView.setAdapter(pAdapter);
    }
}
