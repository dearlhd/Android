package com.lhd.qa;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    String[] QUESTIONS = {
            "北方稀土未来走势如何，我该加仓吗？",
            "工商银行去年买的，被套了怎么办呢？",
            "工商银行去年买的，被套了怎么办呢？",
    };

    LinearLayout mQALayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews(R.drawable.icon_bottom1);
        initViews(R.drawable.icon_bottom2);
        initViews(R.drawable.icon_bottom3);
    }

    private void initViews (int dId) {
        mQALayout = (LinearLayout) findViewById(R.id.qa_layout);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View titleView = layoutInflater.inflate(R.layout.qa_title, null);

        RelativeLayout relativeLayout;

        relativeLayout = (RelativeLayout) titleView.findViewById(R.id.qa_title);
        mQALayout.addView(relativeLayout);

        View lineView = new View(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);
        lineView.setLayoutParams(params);
        lineView.setPadding(0, 2, 0, 2);
        lineView.setBackgroundColor(Color.parseColor("#cccccc"));
        mQALayout.addView(lineView);

        for (int i = 0; i < 3; i++) {
            View entryView = layoutInflater.inflate(R.layout.qa_entry, null);
            relativeLayout = (RelativeLayout) entryView.findViewById(R.id.qa_entry);
            TextView cntTxt = (TextView) entryView.findViewById(R.id.cnt);
            Drawable drawable = getResources().getDrawable(dId);
            cntTxt.setBackgroundDrawable(drawable);

            TextView textView = (TextView) entryView.findViewById(R.id.question_content);
            textView.setText(QUESTIONS[i]);
            mQALayout.addView(relativeLayout);

            lineView = new View(this);
            lineView.setLayoutParams(params);
            lineView.setPadding(0, 2, 0, 2);
            lineView.setBackgroundColor(Color.parseColor("#cccccc"));
            mQALayout.addView(lineView);
        }

        TextView loadMoreTxt = new TextView(this);
        ViewGroup.LayoutParams txtParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 120);
        loadMoreTxt.setLayoutParams(txtParams);
        loadMoreTxt.setGravity(Gravity.CENTER);
        loadMoreTxt.setTextColor(Color.parseColor("#7B68EE"));
        loadMoreTxt.setTextSize(17);
        loadMoreTxt.setText("更多高额悬赏问题");
        loadMoreTxt.setPadding(0, 5, 0, 5);
        mQALayout.addView(loadMoreTxt);

        View gapView = new View(this);
        ViewGroup.LayoutParams gapParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50);
        gapView.setLayoutParams(gapParams);
        gapView.setPadding(0, 2, 0, 2);
        gapView.setBackgroundColor(Color.parseColor("#f6f6f6"));
        mQALayout.addView(gapView);
    }

}
