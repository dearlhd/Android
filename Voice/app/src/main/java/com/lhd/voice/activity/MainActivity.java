package com.lhd.voice.activity;

import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.lhd.voice.R;
import com.lhd.voice.adapter.SoundAdapter;
import com.lhd.voice.view.RoundProgressBar;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {
    private MediaRecorder mMediaRecorder;
    private MediaPlayer mMediaPlayer;
    private Button resetBtn;

    private String mDir;
    private String mSoundName;


    private RoundProgressBar mRoundBar;
    private int seconds = 0;
    private Timer mTimer;
    private boolean mTimerStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btn_test);
//        setContentView(R.layout.activity_main);
//        initViews();
        initRoundBar();
    }

    private void initRoundBar() {
        // 录音权限检查
        int audioPerm = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO);
        int storagePerm = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (!(audioPerm == PackageManager.PERMISSION_GRANTED && storagePerm == PackageManager.PERMISSION_GRANTED)) {
            String[] perms = {"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"};
            int permsRequestCode = 200;
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                requestPermissions(perms, permsRequestCode);
            }
        }

        mTimer = new Timer();
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 0;
                handler.sendMessage(msg);
            }
        };
        mTimer.schedule(timerTask, 0, 1000);

        mRoundBar = (RoundProgressBar) findViewById(R.id.roundProgressBar);
        mRoundBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RoundProgressBar.RecordStatus status = mRoundBar.getRecordStatus();

                Log.i("dong", "onclick: bar status is " + status);

                switch (status) {
                    case PREPARED:
                        startRecord();
                        mTimerStatus = true;
                        seconds = 0;
                        break;
                    case RECORDING:
                        stopRecord();
                        mTimerStatus = false;
                        break;
                    case TERMINATED:
                        playSound();
                        mTimerStatus = false;
                        break;
                    case PLAYING:
                        stopPlaying();
                        mTimerStatus = false;
                        break;
                }

                mRoundBar.changeTipImg();
            }
        });


        resetBtn = (Button) findViewById(R.id.reset_btn);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                if (mRoundBar != null) {
                    mRoundBar.reset();
                }
            }
        });
    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (mTimerStatus) {
                        if (seconds <= 60) {
                            mRoundBar.setProgress(seconds++);
                        } else {
                            stopRecord();
                            mRoundBar.setRecordStatus(RoundProgressBar.RecordStatus.TERMINATED);
                        }
                    }
            }
        }
    };


    //    private void initViews() {
//        mRecordBtn = (Button) findViewById(R.id.record_btn);
//        mRecordBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!mIsStart) {
//                    startRecord();
//                    mRecordBtn.setText("停止录制");
//                    mIsStart = true;
//                }
//                else {
//                    stopRecord();
//                    mRecordBtn.setText("开始录制");
//                    mIsStart = false;
//                }
//            }
//        });
//
//        mSoundList = (ListView) findViewById(R.id.sound_list);
//        String dirPath = Environment.getExternalStorageDirectory() + "/sounds";
//        File dir = new File (dirPath);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//
//        mSAdapter = new SoundAdapter(this, dir.list(), dirPath);
//
//        mSoundList.setAdapter(mSAdapter);
//    }
//
    private void startRecord () {
        // 录音权限检查
        int audioPerm = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO);
        int storagePerm = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (!(audioPerm == PackageManager.PERMISSION_GRANTED && storagePerm == PackageManager.PERMISSION_GRANTED)) {
            String[] perms = {"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"};
            int permsRequestCode = 200;
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                requestPermissions(perms, permsRequestCode);
            }
        }

        audioPerm = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO);
        storagePerm = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (!(audioPerm == PackageManager.PERMISSION_GRANTED && storagePerm == PackageManager.PERMISSION_GRANTED)) {
            mRoundBar.setEnabled(false);
        }

        if (mMediaRecorder == null) {
            File dir = new File(Environment.getExternalStorageDirectory(), "sounds");
            mDir = dir.getAbsolutePath();
            if (!dir.exists()) {
                dir.mkdirs();
            }

            mSoundName = System.currentTimeMillis() + ".amr";
            File soundFile = new File(dir, mSoundName);
            if (!soundFile.exists()) {
                try {
                    soundFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
            Log.i("dong", soundFile.getAbsolutePath());

            mMediaRecorder.setOutputFile(soundFile.getAbsolutePath());
            try {
                mMediaRecorder.prepare();
                mMediaRecorder.start();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopRecord () {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
//            mSAdapter.add(mSoundName);
        }
    }

    private void playSound () {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mRoundBar.callOnClick();
            }
        });

        try {
            mMediaPlayer.setDataSource(mDir + "/" + mSoundName);
            Log.i("dong", mDir);
            Log.i("dong", mDir + "/" +mSoundName);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopPlaying () {
        mMediaPlayer.reset();
        mMediaPlayer.release();
    }
}
