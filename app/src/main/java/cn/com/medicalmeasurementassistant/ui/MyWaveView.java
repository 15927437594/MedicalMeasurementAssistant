package cn.com.medicalmeasurementassistant.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SizeUtils;

public class MyWaveView extends View {
    private final String NAMESPACE = "http://schemas.android.com/apk/res-auto";
    private int mWidth;
    private int mHeight;
    private final static int offsetX = 100;
    /**
     * 常规绘制模式 不断往后推的方式
     */
    public final static int NORMAL_MODE = 0;

    /**
     * 循环绘制模式
     */
    public final static int LOOP_MODE = 1;

    /**
     * 绘制模式
     */
    private int drawMode = NORMAL_MODE;

    /**
     * 网格画笔
     */
    private Paint mLinePaint;
    // 虚线画笔
    private Paint DashPathEffectPaint;


    private Paint mTextPaint;

    /**
     * 数据线画笔
     */
    private Paint mWavePaint;
    /**
     * 线条的路径
     */
    private Path mPath;

    /**
     * 保存已绘制的数据坐标
     */
    private float[] dataArray;

    /**
     * 数据最大值，默认-2~2之间
     */
    private final float MAX_VALUE = 2F;
    /**
     * 线条粗细
     */
    private final static float WAVE_LINE_STROKE_WIDTH = 3;
    /**
     * 线条的长度，可用于控制横坐标
     */
    private final static int WAVE_LINE_WIDTH = 10;
    /**
     * 网格线条的粗细
     */
    private final static int GRID_LINE_WIDTH = 3;

    private String mWaveDesc;

    /**
     * 数据点的数量
     */
    private int row;

    private int draw_index;

    private final boolean[] mChannelStatus = {true, true, true, true, true, true, true, true};

    /**
     * 网格的横线和竖线的数量
     */
    private int gridHorizontalNum = 16;

    private int gridVerticalNum;


    /**
     * 网格颜色
     */
    private final int gridLineColor = Color.parseColor("#6836B8");
    /**
     * 波形颜色
     */
    private final int waveLineColor = Color.parseColor("#EE4000");
    private int mLineScale;


    public MyWaveView(Context context) {
        this(context, null);
    }

    public MyWaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        /**
         *  网格线画笔
         */
        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(GRID_LINE_WIDTH);
        mLinePaint.setColor(gridLineColor);
        mLinePaint.setAlpha(50);
        mLinePaint.setAntiAlias(true);


        /**
         *  网格线画笔
         */
        DashPathEffectPaint = new Paint();
        DashPathEffectPaint.setStyle(Paint.Style.STROKE);
        DashPathEffectPaint.setStrokeWidth(GRID_LINE_WIDTH);
        DashPathEffectPaint.setColor(gridLineColor);
        DashPathEffectPaint.setAlpha(50);
        DashPathEffectPaint.setAntiAlias(true);
        DashPathEffectPaint.setPathEffect(new DashPathEffect(new float[]{15, 10}, 0));

        /** 抗锯齿效果*/
        /**
         *  波纹线画笔
         */
        mWavePaint = new Paint();
        mWavePaint.setStyle(Paint.Style.STROKE);
        mWavePaint.setColor(waveLineColor);
        mWavePaint.setStrokeWidth(WAVE_LINE_STROKE_WIDTH);
        /** 抗锯齿效果*/
        mWavePaint.setAntiAlias(true);

        /**
         *  刻度及描述 画笔
         */
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(waveLineColor);
        mTextPaint.setTextSize(SizeUtils.dp2px(10));

        mPath = new Path();
        gridHorizontalNum = getChannelShowCount();
    }

    private int getChannelShowCount() {
        int i = 0;
        for (Boolean isShow : mChannelStatus) {
            if (isShow) {
                i++;
            }
        }

        return Math.max(2, i * 2);
    }

    public void changeChannelStatus(int position, boolean status) {
        mChannelStatus[position] = status;
        initLineNum();
        invalidate();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        /** 获取控件的宽高*/
        mWidth = getMeasuredWidth() - offsetX;
        mHeight = getMeasuredHeight() - 2 * offsetX;
        initLineNum();
    }

    private void initLineNum() {
        /** 根据网格的单位长宽，获取能绘制网格横线和竖线的数量*/
        // 获取每个
        gridHorizontalNum = getChannelShowCount();

        mLineScale = mHeight / gridHorizontalNum;

        gridVerticalNum = mWidth * gridHorizontalNum / mHeight;

        /** 根据线条长度，最多能绘制多少个数据点*/
        row = (gridVerticalNum * mLineScale / WAVE_LINE_WIDTH);
        dataArray = new float[row];
    }


    private final Rect mTextRect = new Rect();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        /** 绘制网格*/
        drawGrid(canvas);
        /**
         *  绘制刻度
         */
        drawScale(canvas);

        /** 绘制折线*/
//        switch (drawMode) {
//            case 0:
//                drawWaveLineNormal(canvas);
//                break;
//            case 1:
//                drawWaveLineLoop(canvas);
//                break;
//        }
//        draw_index += 1;
//        if (draw_index >= row) {
//            draw_index = 0;
//            dataArray = new float[row];
//        }
    }

    /**
     * 绘制网格
     *
     * @param canvas
     */
    private void drawGrid(Canvas canvas) {
        /** 设置颜色*/
////        mLinePaint.setColor(gridLineColor);
        int bottom = gridHorizontalNum * mLineScale + offsetX;

        int right = gridVerticalNum * mLineScale;
//        int bottom = gridVerticalNum*mLineScaleHeight;
        /** 绘制横线*/
        for (int i = 0; i < gridHorizontalNum + 1; i++) {
            int startY;
            if (i == 0) {
                startY = 2;
            } else {
                startY = i * mLineScale;
            }
            canvas.drawLine(offsetX, startY + offsetX,
                    right + offsetX, startY + offsetX, i % 2 == 0 ? mLinePaint : DashPathEffectPaint);
        }


        /** 绘制竖线*/
        for (int i = 0; i < gridVerticalNum + 1; i++) {
            int startX;
            if (i == 0) {
                startX = 2;
            } else {
                startX = i * mLineScale;
            }
            canvas.drawLine(startX + offsetX, offsetX,
                    startX + offsetX, bottom, i % 2 == 0 ? mLinePaint : DashPathEffectPaint);
        }


    }

    /**
     * 绘制刻度和图名称
     *
     * @param canvas
     */
    private void drawScale(Canvas canvas) {

        int index = 0;
        for (int i = 0, l = mChannelStatus.length; i < l; i++) {
            boolean status = this.mChannelStatus[i];
            if (status) {
                String desc = "通道"+(i+1);
                mTextPaint.getTextBounds(desc, 0, desc.length(), mTextRect);
                int height = mTextRect.height() / 2 + offsetX + (2 * index + 1) * mLineScale;
                canvas.drawText(desc, 0, height, mTextPaint);
//                canvas.save();
                index++;


            }
        }

//        if (mWaveDesc != null && mWaveDesc.length() != 0) {
//            mTextPaint.getTextBounds(mWaveDesc, 0, mWaveDesc.length(), mTextRect);
//            int height = getHeight() / 2 - mTextRect.height() / 2;
//            canvas.drawText(mWaveDesc, 0, height, mTextPaint);
//            canvas.save();
//        }


//        if (scaleVisible) {
////            mTextPaint.getTextBounds(mWaveDesc, 0, mWaveDesc.length(), mTextRect);
//            int height = mTextRect.height();
//            canvas.drawText(MAX_VALUE + "", 50, 50, mTextPaint);
//            canvas.drawText(-MAX_VALUE + "", 0, 100, mTextPaint);
//
//        }
    }
}
