package com.lhd.voice.adapter;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.lhd.voice.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/9.
 */
public class SoundAdapter extends BaseAdapter {
    List<String> mSoundList;
    String mDir;
    LayoutInflater mInflater;

    MediaPlayer mMediaPlayer;

    public SoundAdapter (Context context, String[] soundList, String dir) {
        mInflater = LayoutInflater.from(context);
        mSoundList = new ArrayList<>();
        if (soundList != null) {
            for (int i = 0; i < soundList.length; i++) {
                mSoundList.add(soundList[i]);
            }
        }

        mDir = dir;
    }

    @Override
    public int getCount() {
        return mSoundList.size();
    }

    @Override
    public Object getItem(int i) {
        return mSoundList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mInflater.inflate(R.layout.item_sound, viewGroup, false);
        }

        TextView soundNameTxt = (TextView) view.findViewById(R.id.txt_sound_name);
        Button playBtn = (Button) view.findViewById(R.id.btn_play_sound);

        soundNameTxt.setText(mSoundList.get(i));

        final int index = i;
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                playSound (index);
            }
        });

        return view;
    }

    public void add (String soundPath) {
        if (mSoundList == null) {
            mSoundList = new ArrayList<String>();
        }
        Log.i("dong", soundPath);
        mSoundList.add(soundPath);
        notifyDataSetChanged();
    }

    private void playSound (int i) {
        if (mSoundList.get(i) != null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            try {
                mMediaPlayer.setDataSource(mDir + "/" + mSoundList.get(i));
                Log.i("dong", mDir);
                Log.i("dong", mDir + "/" +mSoundList.get(i));
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
