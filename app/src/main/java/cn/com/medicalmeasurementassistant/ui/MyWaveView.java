package cn.com.medicalmeasurementassistant.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SizeUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.com.medicalmeasurementassistant.R;
import cn.com.medicalmeasurementassistant.utils.LogUtils;
import cn.com.medicalmeasurementassistant.utils.StringUtils;

public class MyWaveView extends View {
    private final String NAMESPACE = "http://schemas.android.com/apk/res-auto";
    // 示波器宽度
    private int mOscillographWidth;
    // 示波器高度
    private int mOscillographHeight;
    private final static int mOffsetX = 50;
    private final static int mOffsetY = 50;
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
    private int drawMode = LOOP_MODE;

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
    private Paint mWavePaintOne, mWavePaintTwo;
    /**
     * 线条的路径
     */
    private Path mPath;

    /**
     * 保存已绘制的数据坐标
     */
//    private float[] dataArray;
//    private final LinkedList<Float> dataArray = new LinkedList<>();

    private final List<LinkedList<Double>> totalDataArray = new ArrayList<>();

    /**
     * 数据最大值，默认-1~1之间
     */
    private int MAX_VALUE = 2;
    private int MIN_VALUE= 2;

    /**
     * 线条的长度，可用于控制横坐标
     */
    private final static int WAVE_LINE_WIDTH = 1;
    /**
     * 点的数量
     */
    private int row = 20;
    private final static int ROW_S = 100;
    /**
     * 网格线条的粗细
     */
    private final static int GRID_LINE_WIDTH = 3;
    /**
     * 线条粗细
     */
    private final static float WAVE_LINE_STROKE_WIDTH = GRID_LINE_WIDTH;

    private final boolean[] mChannelStatus = {true, true, true, true, true, true, true, true};

    /**
     * 网格的横线和竖线的数量
     */
    private int gridHorizontalNum;

    private int gridVerticalNum;
    private int mHorizontalLineScale, mVerticalLineScale;

    /**
     * 网格颜色
     */
    private final int gridLineColor = Color.parseColor("#6836B8");
    /**
     * 波形颜色
     */
    private final int waveLineColor = Color.parseColor("#EE4000");

    /**
     *
     */
    private String xAxisDesc;
    private String yAxisDesc;

    public String getxAxisDesc() {
        return xAxisDesc;
    }

    public void setxAxisDesc(String xAxisDesc) {
        this.xAxisDesc = xAxisDesc;
    }

    public String getyAxisDesc() {
        return yAxisDesc;
    }

    public void setyAxisDesc(String yAxisDesc) {
        this.yAxisDesc = yAxisDesc;
    }

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
         *  网格线虚线画笔
         */
        DashPathEffectPaint = new Paint();
        DashPathEffectPaint.setStyle(Paint.Style.STROKE);
        DashPathEffectPaint.setStrokeWidth(GRID_LINE_WIDTH);
        DashPathEffectPaint.setColor(gridLineColor);
        DashPathEffectPaint.setAlpha(50);
        DashPathEffectPaint.setAntiAlias(true);
        DashPathEffectPaint.setPathEffect(new DashPathEffect(new float[]{15, 10}, 0));

        /**
         *  波纹线画笔
         */
        mWavePaintOne = new Paint();
        mWavePaintOne.setStyle(Paint.Style.STROKE);
        mWavePaintOne.setColor(getResources().getColor(R.color.theme_color));
        mWavePaintOne.setStrokeWidth(WAVE_LINE_STROKE_WIDTH);
        /** 抗锯齿效果*/
        mWavePaintOne.setAntiAlias(true);
        /**
         *  波纹线画笔
         */
        mWavePaintTwo = new Paint();
        mWavePaintTwo.setStyle(Paint.Style.STROKE);
        mWavePaintTwo.setColor(getResources().getColor(R.color.electrode_text_color_on));
        mWavePaintTwo.setStrokeWidth(WAVE_LINE_STROKE_WIDTH);
        /** 抗锯齿效果*/
        mWavePaintTwo.setAntiAlias(true);


        /**
         *  刻度及描述 画笔
         */
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(getResources().getColor(R.color.theme_color));
        mTextPaint.setTextSize(SizeUtils.dp2px(12));

        mPath = new Path();
        gridHorizontalNum = getChannelShowCount();

        for (int i = 0; i < mChannelStatus.length; i++) {
            totalDataArray.add(new LinkedList<>());
        }
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
        updateWaveLine();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        mOscillographWidth = w;
//        mOscillographHeight = h;
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i("MyWaveView---", "  onSizeChanged  w = " + w + "  h = " + h);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.i("MyWaveView---", "  onLayout  ");
        /** 获取控件的宽高*/
        int witdh = getMeasuredWidth() - mOffsetX;
        gridVerticalNum = witdh / (ROW_S * WAVE_LINE_WIDTH) + 1;
        mOscillographWidth = gridVerticalNum * ROW_S * WAVE_LINE_WIDTH;
        mOscillographHeight = getMeasuredHeight() - 2 * mOffsetY;
        initLineNum();
    }

    private void initLineNum() {
        /** 根据网格的单位长宽，获取能绘制网格横线和竖线的数量*/
        // 获取每个
        gridHorizontalNum = getChannelShowCount();

        mHorizontalLineScale = mOscillographHeight / gridHorizontalNum;

        mVerticalLineScale = ROW_S * WAVE_LINE_WIDTH;

//        gridVerticalNum = mOscillographWidth / mVerticalLineScale + 2;


        /** 根据线条长度，最多能绘制多少个数据点*/
        /**
         * 数据点的数量
         */

        row = gridVerticalNum * ROW_S;
    }


    private final Rect mTextRect = new Rect();
    private final Rect mScaleTextRect = new Rect();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i("MyWaveView---", "  onDraw  w = " + getWidth() + " hei = " + getHeight());

        /** 绘制网格*/
        drawGrid(canvas);
        /**
         *  绘制刻度
         */
        drawScale(canvas);

//        try {
        drawWaveLineNormal(canvas);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        /** 绘制折线*/
//        switch (drawMode) {
//            case NORMAL_MODE:
//                drawWaveLineNormal(canvas);
//                break;
//            case LOOP_MODE:
//                drawWaveLineNormal(canvas);
//                break;
//        }
    }

    /**
     * 绘制网格
     *
     * @param canvas
     */
    private void drawGrid(Canvas canvas) {
        int bottom = gridHorizontalNum * mHorizontalLineScale + mOffsetY;

        /** 绘制横线*/
        for (int i = 0; i < gridHorizontalNum + 1; i++) {
            int startY = i * mHorizontalLineScale;
            if (i == 0 || i == gridHorizontalNum) {
                Log.i("MyWaveView---", "  drawGrid_for  i " + i);
                mLinePaint.setColor(gridLineColor);
                mLinePaint.setAlpha(50);
                canvas.drawLine(mOffsetX, startY + mOffsetY,
                        getRight(), startY + mOffsetY, mLinePaint);
                continue;
            }
            if (i % 2 != 0) {
                Log.i("MyWaveView---", "  drawGrid__if  i " + i);
                mLinePaint.setColor(getResources().getColor(R.color.electrode_text_color_off));
                mLinePaint.setAlpha(120);
                canvas.drawLine(mOffsetX, startY + mOffsetY,
                        getRight(), startY + mOffsetY, mLinePaint);
                continue;
            }
            mLinePaint.setColor(gridLineColor);
            mLinePaint.setAlpha(50);
            canvas.drawLine(mOffsetX, startY + mOffsetY,
                    getRight(), startY + mOffsetY, DashPathEffectPaint);
        }

        mLinePaint.setColor(gridLineColor);
        mLinePaint.setAlpha(50);
        /** 绘制竖线*/
        for (int i = 0; i < gridVerticalNum + 1; i++) {
            int startX;
            if (i == 0) {
                startX = 2;
            } else {
                int offset = offsetIndex > 0 ? (offsetIndex % ROW_S * WAVE_LINE_WIDTH) : 0;
                startX = i * mVerticalLineScale - offset;
            }
            canvas.drawLine(startX + mOffsetX, mOffsetY,
                    startX + mOffsetY, bottom, mLinePaint);
        }
    }

    /**
     * 绘制刻度和图名称
     *
     * @param canvas
     */
    private void drawScale(Canvas canvas) {
        mTextPaint.setColor(getResources().getColor(R.color.theme_color));
        if (!StringUtils.isEmpty(yAxisDesc)) {
            mTextPaint.getTextBounds(yAxisDesc, 0, yAxisDesc.length(), mScaleTextRect);
            int yDescLeft = 15;
            int yDescBottom = (mOffsetY + mScaleTextRect.height()) / 2;
            canvas.drawText(yAxisDesc, yDescLeft, yDescBottom, mTextPaint);
        }
        if (!StringUtils.isEmpty(xAxisDesc)) {
            mTextPaint.getTextBounds(xAxisDesc, 0, xAxisDesc.length(), mScaleTextRect);
            int xDescLeft = getWidth() - mScaleTextRect.width() - 5;
            int xDescBottom = mOscillographHeight + mOffsetY + mScaleTextRect.height();
            canvas.drawText(xAxisDesc, xDescLeft, xDescBottom, mTextPaint);
            canvas.save();
        }

        int index = 0;
        for (int i = 0, l = mChannelStatus.length; i < l; i++) {
            boolean status = this.mChannelStatus[i];
            if (status) {
                // 绘制通道名称
                String desc = (i + 1) + "";

                mTextPaint.getTextBounds(desc, 0, desc.length(), mTextRect);
                int height = mTextRect.height() / 2 + mOffsetX + (2 * index + 1) * mHorizontalLineScale;
                int left = (mOffsetX - mTextRect.height()) / 2;
                canvas.drawText(desc, left, height, mTextPaint);
                // 绘制区间标志
                mLinePaint.setTextSize(SizeUtils.dp2px(10));
                String maxValue = String.valueOf(MAX_VALUE);
                String minValue = String.valueOf(MIN_VALUE);
                mLinePaint.getTextBounds(maxValue, 0, maxValue.length(), mTextRect);
                int scaleOneLeft = mOffsetX - mTextRect.width() - 8;
                int scaleOneTop = mOffsetY + (index * 2) * mHorizontalLineScale + mTextRect.height() + 2;
                canvas.drawText(maxValue, scaleOneLeft, scaleOneTop, mLinePaint);

                mLinePaint.getTextBounds(minValue, 0, minValue.length(), mTextRect);
                int scaleTwoLeft = mOffsetX - mTextRect.width() - 8;
                int scaleTwoTop = mOffsetY + (index + 1) * 2 * mHorizontalLineScale - 3;
                canvas.drawText(minValue, scaleTwoLeft, scaleTwoTop, mLinePaint);
                index++;
            }
        }

        for (int i = 0; i < gridVerticalNum + 1; i++) {
            // 绘制横坐标刻度
            mLinePaint.setTextSize(SizeUtils.dp2px(10));
            mLinePaint.getTextBounds(i + "", 0, 1, mTextRect);
            int left;
            if (i == 0) {
                left = mOffsetX;
                if (drawMode == NORMAL_MODE && offsetIndex % ROW_S > 10) {
                    continue;
                }
            } else {
                int offset = offsetIndex > 0 ? (offsetIndex % ROW_S * WAVE_LINE_WIDTH) : 0;
                left = i * mVerticalLineScale + mOffsetX - mTextRect.width() / 2 - offset;
            }
            int bottom = mOscillographHeight + mOffsetY + mTextRect.height() + 2;
            canvas.drawText((i + offsetIndex / ROW_S) + "", left, bottom, mLinePaint);

        }
    }

    private boolean isRefresh;

    public boolean isRefresh() {
        return isRefresh;
    }

    public void setRefresh(boolean refresh) {
        isRefresh = refresh;
    }

    public void clearChannelData() {
        clearChannelData(true);
    }

    public void clearChannelData(boolean clearIndex) {
        if (clearIndex) {
            offsetIndex = 0;
        }
        for (LinkedList<Double> linkedList : totalDataArray) {
            linkedList.clear();
        }
    }

    /**
     * 常规模式绘制折线
     *
     * @param canvas
     */
    private boolean isDrawWaveLine;

    private void drawWaveLineNormal(Canvas canvas) {
        if (totalDataArray.size() == 0) {
            return;
        }

        isDrawWaveLine = true;
        int index = 0;
        for (int i = 0; i < mChannelStatus.length; i++) {
            if (mChannelStatus[i]) {
                LinkedList<Double> floats = totalDataArray.get(i);
                if (floats.isEmpty()) {
                    index++;
                    continue;
                }
                drawPathFromDatas(canvas, 0, floats.size() - 1, index, i);
                index++;
            }
        }
        isDrawWaveLine = false;
    }

    public void updateWaveLine() {
//        if (isDrawWaveLine) {
//            return;
//        }
        postInvalidate();
    }

//    /**
//     * 循环模式绘制折线
//     *
//     * @param canvas
//     */
//    private void drawWaveLineLoop(Canvas canvas) {
//        drawPathFromDatas(canvas, (row - 1) - draw_index > 8 ? 0 : 8 - ((row - 1) - draw_index), draw_index);
//        drawPathFromDatas(canvas, Math.min(draw_index + 8, row - 1), row - 1);
//    }


    /**
     * 取数组中的指定一段数据来绘制折线
     *
     * @param start 起始数据位
     * @param end   结束数据位
     */
    private void drawPathFromDatas(Canvas canvas, int start, int end, int index, int dataPosition) {
        if (index == 0) {
            canvas.restore();
        }
        mPath.reset();
        LinkedList<Double> dataArray = totalDataArray.get(dataPosition);
        int initOffsetY = mOffsetY + (2 * index + 1) * mHorizontalLineScale + GRID_LINE_WIDTH / 2;
        double initOffsetY2 = (mHorizontalLineScale) * 1.0f / (MAX_VALUE);

        for (int i = 0; i < end; i++) {
            if (isRefresh) {
                isRefresh = false;
                return;
            }

//            if (Math.abs(dataArray.get(i)) > MAX_VALUE) {
//                continue;
//            }
            /**
             * 当前的x，y坐标
             */
            float nowX = i * WAVE_LINE_WIDTH;
            double dataValue = dataArray.get(i);
            /** 判断数据为正数还是负数  超过最大值的数据按最大值来绘制*/
            if (dataValue > 0) {
                if (dataValue > MAX_VALUE) {
                    dataValue = MAX_VALUE;
                }
            } else {
                if (dataValue < MIN_VALUE) {
                    dataValue = MIN_VALUE;
                }
            }

            float nowY = (float) (initOffsetY - dataValue * initOffsetY2);
//            float nowY = mOscillographHeight / 2 - dataValue * (mOscillographHeight / (MAX_VALUE * 2));
            if (dataPosition == 0) {
                LogUtils.d("line_data" + dataPosition + " index = " + i);
            }
            if (i == 0) {
                mPath.moveTo(nowX + mOffsetX, nowY);
            }
            mPath.lineTo(nowX + mOffsetX, nowY);
        }
        canvas.drawPath(mPath, index % 2 == 0 ? mWavePaintOne : mWavePaintTwo);
    }

    public boolean checkChannelStatus(int position) {
        return mChannelStatus[position];
    }

    private int offsetIndex;

    /**
     * 添加新的数据
     */
    public void addData(int position, List<Double> data) {
        if(isDrawWaveLine){
            return;
        }
        LinkedList<Double> dataArray = totalDataArray.get(position);
        switch (drawMode) {
            case NORMAL_MODE:
                // 常规模式数据添加至最后一位
                if (dataArray.size() == row) {
                    dataArray.removeFirst();
                    if (position == 0) {
                        offsetIndex++;
                    }
                }
//                dataArray.addLast(line);
                break;
            case LOOP_MODE:
                // 循环模式数据添加至当前绘制的位
                if (dataArray.size() == row) {
                    if (position == 0) {
                        offsetIndex += row;
                        clearChannelData(false);
                    }
                }
//                dataArray.addLast(line);
                for (int i = 0; i < data.size(); i++) {
                    dataArray.addLast(data.get(i));
                }
//                dataArray.addAll(data);
                break;
        }
    }

    /**
     * 添加新的数据
     */
    public void addData(int position, double data) {
        if(isDrawWaveLine){
            return;
        }
        LinkedList<Double> dataArray = totalDataArray.get(position);
        switch (drawMode) {
            case NORMAL_MODE:
                // 常规模式数据添加至最后一位
                if (dataArray.size() == row) {
                    dataArray.removeFirst();
                    if (position == 0) {
                        offsetIndex++;
                    }
                }
//                dataArray.addLast(line);
                break;
            case LOOP_MODE:
                // 循环模式数据添加至当前绘制的位
                if (dataArray.size() == row) {
                    if (position == 0) {
                        offsetIndex += row;
                        clearChannelData(false);
                    }
                }
                dataArray.addLast(data);
                break;
        }
    }

    public void setMaxValue(int value) {
        this.MAX_VALUE = value;
    }

    public void setMinValue(int value) {
        this.MIN_VALUE = value;
    }
}