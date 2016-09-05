package com.lhd.voice.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.lhd.voice.R;

/**
 * Created by Administrator on 2016/8/9.
 */
public class RecordView extends View {
    private Paint mPaint;

    private int mRingColor;
    private int mRingProgressColor;
    private float mRadius;
    private float mRingWidth;

    private int mMaxProgress;
    private int mProgress = 0;

    private int mTipImg;
    private float mTipImgWidth;

    public enum RecordStatus { PREPARED, RECORDING, TERMINATED, PLAYING}

    private RecordStatus mRecordStatus = RecordStatus.PREPARED;


    public RecordView(Context context) {
        this(context, null);
    }

    public RecordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RecordView);
        mRingColor = typedArray.getColor(R.styleable.RecordView_ringColor, Color.GRAY);
        mRingProgressColor = typedArray.getColor(R.styleable.RecordView_ringProgressColor, Color.BLACK);
        mRadius = typedArray.getDimension(R.styleable.RecordView_radius, 10);
        mRingWidth = typedArray.getDimension(R.styleable.RecordView_ringWidth, 5);
        mMaxProgress = typedArray.getInteger(R.styleable.RecordView_maxProgress, 60);
        mTipImg = typedArray.getResourceId(R.styleable.RecordView_tipImg2, R.mipmap.record);
        mTipImgWidth = typedArray.getDimension(R.styleable.RecordView_tipImgWidth2, 180);

        typedArray.recycle();
    }

    @Override
    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);

        int centre = getWidth() / 2;
        mRadius = centre - mRingWidth;

        Log.i("dong", "onDraw: mRadius " + mRadius + " centre " + centre + " mRingWidth " + mRingWidth);

        // 外圈圆环
        mPaint.setColor(mRingColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mRingWidth);
        mPaint.setAntiAlias(true);
        canvas.drawCircle(centre, centre, mRadius, mPaint);

        // 中央图标
        Bitmap tipBM = BitmapFactory.decodeStream(getResources().openRawResource(mTipImg));
        Rect srcRect = new Rect( 0, 0, tipBM.getWidth(), tipBM.getHeight());
        Rect destRect = new Rect((int)(centre - mTipImgWidth / 2), (int)(centre - mTipImgWidth / 2), (int)(centre + mTipImgWidth / 2), (int)(centre + mTipImgWidth / 2));

        canvas.drawBitmap(tipBM, srcRect, destRect, mPaint);

    }


    public int getmRingColor() {
        return mRingColor;
    }

    public void setmRingColor(int mRingColor) {
        this.mRingColor = mRingColor;
    }

    public int getmRingProgressColor() {
        return mRingProgressColor;
    }

    public void setmRingProgressColor(int mRingProgressColor) {
        this.mRingProgressColor = mRingProgressColor;
    }

    public float getmRadius() {
        return mRadius;
    }

    public void setmRadius(float mRadius) {
        this.mRadius = mRadius;
    }

    public float getmRingWidth() {
        return mRingWidth;
    }

    public void setmRingWidth(float mRingWidth) {
        this.mRingWidth = mRingWidth;
    }

    public int getmMaxProgress() {
        return mMaxProgress;
    }

    public void setmMaxProgress(int mMaxProgress) {
        this.mMaxProgress = mMaxProgress;
    }

    public int getmProgress() {
        return mProgress;
    }

    public void setmProgress(int mProgress) {
        this.mProgress = mProgress;
    }

    public int getmTipImg() {
        return mTipImg;
    }

    public void setmTipImg(int mTipImg) {
        this.mTipImg = mTipImg;
    }

    public float getmTipImgWidth() {
        return mTipImgWidth;
    }

    public void setmTipImgWidth(float mTipImgWidth) {
        this.mTipImgWidth = mTipImgWidth;
    }

    public RecordStatus getmRecordStatus() {
        return mRecordStatus;
    }

    public void setmRecordStatus(RecordStatus mRecordStatus) {
        this.mRecordStatus = mRecordStatus;
    }
}
