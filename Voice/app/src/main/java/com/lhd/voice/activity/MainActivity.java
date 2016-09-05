package com.lhd.voice.activity;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lhd.library.MP3Recorder;
import com.lhd.voice.R;

import java.io.File;
import java.io.IOException;

// http://172.16.86.39/fenda/build/#/record?_k=0t45hl
public class MainActivity extends Activity {
    private MediaRecorder mMediaRecorder;
    private MediaPlayer mMediaPlayer;

    private WebView mWebView;

    private String mDir;
    private String mSoundName;

    private MP3Recorder mMp3Recorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        initRecordComponent();
        initWebView();
    }

    @Override
    protected void onPause () {
        super.onPause();
        if (mMp3Recorder != null) {
            stopRecordingMp3();
        }
        if (mMediaPlayer != null) {
            stopPlaying();
        }
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        if (mMp3Recorder != null) {
            stopRecordingMp3();
        }
        if (mMediaPlayer != null) {
            stopPlaying();
        }
    }

    private void initRecordComponent () {
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
    }

    private void initWebView () {
        mWebView = (WebView) findViewById(R.id.record_web_view);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new WebViewClient());
        mWebView.addJavascriptInterface(new MyJsInterface(), "record");

        Log.i("dong", "loading url...");
        String url = "http://172.16.86.39/fenda/build/#/record?_k=0t45hl";
//        url = "file:///android_asset/record.html";
        mWebView.loadUrl(url);
    }

    private void startRecordingMp3 () {
        Log.i("dong", "startRecordingMp3");

        // 权限检查
        int audioPerm = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO);
        int storagePerm = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (!(audioPerm == PackageManager.PERMISSION_GRANTED && storagePerm == PackageManager.PERMISSION_GRANTED)) {
            String[] perms = {"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"};
            int permsRequestCode = 200;
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                requestPermissions(perms, permsRequestCode);
            }
        }

        // 创建文件
        File dir = new File(Environment.getExternalStorageDirectory(), "sounds");
        mDir = dir.getAbsolutePath();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        mSoundName = System.currentTimeMillis() + ".mp3";
        Log.i("dong", mSoundName);
        File soundFile = new File(dir, mSoundName);
        if (!soundFile.exists()) {
            try {
                soundFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mMp3Recorder = new MP3Recorder(soundFile);
        try {
            mMp3Recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecordingMp3 () {
        Log.i("dong", "stopRecordingMp3");
        mMp3Recorder.stop();
    }

    private void playSound () {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
//                mRoundBar.callOnClick();
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

    private void deleteMp3File () {
        if (mDir != null && mSoundName != null) {
            File soundFile = new File(mDir, mSoundName);
            if (soundFile.exists()) {
                soundFile.delete();
            }
        }
    }

    final class MyJsInterface {
        @JavascriptInterface
        public void startRecord() {
            Log.i("dong", "start recording...");
            startRecordingMp3();
        }

        @JavascriptInterface
        public void stopRecord() {
            Log.i("dong", "record stopped");
            stopRecordingMp3();
        }

        @JavascriptInterface
        public void play() {
            Log.i("dong", "start playing...");
            playSound();
        }

        @JavascriptInterface
        public void stopPlay(){
            Log.i("dong", "play stopped.");
            stopPlaying();
        }

        @JavascriptInterface
        public void reRecord() {
            deleteMp3File();
            startRecordingMp3();
        }
    }
}
