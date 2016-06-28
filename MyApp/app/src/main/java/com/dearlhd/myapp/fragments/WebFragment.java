package com.dearlhd.myapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;

import com.dearlhd.myapp.R;
import com.dearlhd.myapp.adapter.PersonAdapter;
import com.dearlhd.myapp.entity.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dearlhd on 2016/6/26 0028.
 */
public class WebFragment extends Fragment {

    private WebView webView;

    public WebFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page2, container, false);

        // 为web view 加载页面
        webView = (WebView) view.findViewById(R.id.web_view);

        String url = "http://www.baidu.com";
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient());

        return view;
    }
}
