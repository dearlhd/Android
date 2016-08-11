package com.lhd.voice.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.lhd.voice.R;

/**
 * Created by Administrator on 2016/8/10.
 */

public class RoundProgressBar extends View {
    /**
     * 画笔对象的引用
     */
    private Paint paint;

    /**
     * 圆环的颜色
     */
    private int roundColor;

    /**
     * 圆环进度的颜色
     */
    private int roundProgressColor;

    /**
     * 中间进度百分比的字符串的颜色
     */
    private int textColor;

    /**
     * 中间进度百分比的字符串的字体
     */
    private float textSize;

    /**
     * 圆环的宽度
     */
    private float roundWidth;

    /**
     * 最大进度
     */
    private int max = 60;

    /**
     * 当前进度
     */
    private int progress;
    /**
     * 是否显示中间的进度
     */
    private boolean textIsDisplayable;

    /**
     * 进度的风格，实心或者空心
     */
    private int style;

    // lhd 文本内容
    private String text;

    private int tipImg;

    private float tipImgWidth = 180;

    public enum RecordStatus { PREPARED, RECORDING, TERMINATED, PLAYING}

    private RecordStatus recordStatus = RecordStatus.PREPARED;

    public static final int STROKE = 0;
    public static final int FILL = 1;

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        paint = new Paint();

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RoundProgressBar);

        //获取自定义属性和默认值
        roundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.RED);
        roundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, Color.GREEN);
        textColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColor, Color.GREEN);
        textSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_textSize, 15);
        roundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 5);
        max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 60);
        textIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, true);
        style = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);

        // lhd
        text = mTypedArray.getString(R.styleable.RoundProgressBar_text);
        tipImg = mTypedArray.getResourceId(R.styleable.RoundProgressBar_tipImg, R.mipmap.record);
        tipImgWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_tipImgWidth, 180);

        mTypedArray.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         * 画最外层的大圆环
         */
        int centre = getWidth()/2; //获取圆心的x坐标
        int radius = (int) (centre - roundWidth/2); //圆环的半径
        paint.setColor(roundColor); //设置圆环的颜色
        paint.setStyle(Paint.Style.STROKE); //设置空心
        paint.setStrokeWidth(roundWidth); //设置圆环的宽度
        paint.setAntiAlias(true);  //消除锯齿
        canvas.drawCircle(centre, centre, radius, paint); //画出圆环

        Log.e("log", centre + "");

        /**
         * 画中间的图标
         */
        paint.setStrokeWidth(0);

        Bitmap tipBitmap = BitmapFactory.decodeStream(getResources().openRawResource(tipImg));
//        canvas.drawBitmap(tipBitmap, centre - tipBitmap.getHeight() / 2, centre - tipBitmap.getHeight() / 2, paint);

        Rect srcRect = new Rect( 0, 0, tipBitmap.getWidth(), tipBitmap.getHeight());
        Rect destRect = new Rect((int)(centre - tipImgWidth / 2), (int)(centre - tipImgWidth / 2), (int)(centre + tipImgWidth / 2), (int)(centre + tipImgWidth / 2));

        canvas.drawBitmap(tipBitmap, srcRect, destRect, paint);

        /**
         * 画圆弧 ，画圆环的进度
         */

        //设置进度是实心还是空心
        paint.setStrokeWidth(roundWidth); //设置圆环的宽度
        paint.setColor(roundProgressColor);  //设置进度的颜色
        RectF oval = new RectF(centre - radius, centre - radius, centre
                + radius, centre + radius);  //用于定义的圆弧的形状和大小的界限

        switch (style) {
            case STROKE:{
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawArc(oval, 0, 360 * progress / max, false, paint);  //根据进度画圆弧
                break;
            }
            case FILL:{
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                if(progress !=0)
                    canvas.drawArc(oval, 0, 360 * progress / max, true, paint);  //根据进度画圆弧
                break;
            }
        }

    }


    public synchronized int getMax() {
        return max;
    }

    /**
     * 设置进度的最大值
     * @param max
     */
    public synchronized void setMax(int max) {
        if(max < 0){
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }

    /**
     * 获取进度.需要同步
     * @return
     */
    public synchronized int getProgress() {
        return progress;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     * @param progress
     */
    public synchronized void setProgress(int progress) {
        if(progress < 0){
            throw new IllegalArgumentException("progress not less than 0");
        }
        if(progress > max){
            progress = max;
        }
        if(progress <= max){
            this.progress = progress;
            postInvalidate();
        }

    }

    public void changeTipImg () {
        Log.i("dong", "bar status is " + recordStatus);

        switch (recordStatus) {
            case PREPARED:
                recordStatus = RecordStatus.RECORDING;
                tipImg = R.mipmap.terminate;
                progress = 0;
                break;
            case RECORDING:
                recordStatus = RecordStatus.TERMINATED;
                tipImg = R.mipmap.play;
                progress = max;
                break;
            case PLAYING:
                recordStatus = RecordStatus.TERMINATED;
                tipImg = R.mipmap.play;
                progress = max;
                break;
            case TERMINATED:
                recordStatus = RecordStatus.PLAYING;
                tipImg = R.mipmap.terminate;
                progress = max;
                break;
        }

        postInvalidate();
    }

    public void reset () {
        recordStatus = RecordStatus.PREPARED;
        tipImg = R.mipmap.record;
        progress = 0;
        postInvalidate();
    }

    public int getCricleColor() {
        return roundColor;
    }

    public void setCricleColor(int cricleColor) {
        this.roundColor = cricleColor;
    }

    public int getCricleProgressColor() {
        return roundProgressColor;
    }

    public void setCricleProgressColor(int cricleProgressColor) {
        this.roundProgressColor = cricleProgressColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public float getRoundWidth() {
        return roundWidth;
    }

    public void setRoundWidth(float roundWidth) {
        this.roundWidth = roundWidth;
    }

    public int getTipImg() {
        return tipImg;
    }

    public void setTipImg(int tipImg) {
        this.tipImg = tipImg;
    }

    public float getTipImgWidth () {
        return tipImgWidth;
    }

    public void setTipImgWidth (float tipImgWidth) {
        this.tipImgWidth = tipImgWidth;
    }

    public RecordStatus getRecordStatus () {
        return recordStatus;
    }

    public void setRecordStatus (RecordStatus status) {
        recordStatus = status;
    }
}