package cn.com.medicalmeasurementassistant.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SizeUtils;

import java.util.LinkedList;

import cn.com.medicalmeasurementassistant.R;
import cn.com.medicalmeasurementassistant.utils.StringUtils;

public class MyCapWaveView extends View {
    private final String NAMESPACE = "http://schemas.android.com/apk/res-auto";
    // 示波器宽度
    private int mOscillographWidth;
    // 示波器高度
    private int mOscillographHeight;
    private final static int mOffsetX = 60;
    private final static int mOffsetY = 60;
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
    private final int drawMode = LOOP_MODE;

    /**
     * 网格画笔
     */
    private Paint mLinePaint;

    private Paint mTextPaint;
    private Paint mScalePaint;

    /**
     * 数据线画笔
     */
    private Paint mWavePaintOne;
    /**
     * 线条的路径
     */
    private Path mPath;

    private int mShowTimeLength = 5;

    /**
     * 保存已绘制的数据坐标
     */
    private final LinkedList<Double> dataArray = new LinkedList<>();

    /**
     * 数据最小值
     */
    private int MIN_VALUE = 0;

    /**
     * 数据最大值
     */
    private int MAX_VALUE = 60;

    /**
     * 线条的长度，可用于控制横坐标
     */
    private int wave_line_width = 2;

    /**
     * 每秒点数
     */
    private final static int ROW = 20;
    /**
     * 点的总数量
     */
    private int totalRow = 20;
    /**
     * 网格线条的粗细
     */
    private final static int GRID_LINE_WIDTH = 2;

    /**
     * 网格的横线和竖线的数量
     */
    private final static int mHorizontalLineNum = 2;

    private int gridVerticalNum;
    private int mHorizontalLineScale, mVerticalLineScale;

    /**
     * 网格颜色
     */
    private final int gridLineColor = Color.parseColor("#6836B8");

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

    public MyCapWaveView(Context context) {
        this(context, null);
    }

    public MyCapWaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyCapWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
         *  波纹线画笔
         */
        mWavePaintOne = new Paint();
        mWavePaintOne.setStyle(Paint.Style.STROKE);
        mWavePaintOne.setColor(getResources().getColor(R.color.electrode_text_color_on));
        mWavePaintOne.setAntiAlias(true);

        /**
         *  刻度及描述 画笔
         */
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(getResources().getColor(R.color.theme_color));
        mTextPaint.setTextSize(SizeUtils.dp2px(12));

        mScalePaint = new Paint();
        mScalePaint.setAntiAlias(true);
        mScalePaint.setColor(getResources().getColor(R.color.theme_color));
        mScalePaint.setTextSize(SizeUtils.dp2px(8));

        mPath = new Path();

    }


    public void setShowTimeLength(int mShowTimeLength) {
        this.mShowTimeLength = mShowTimeLength;

        initLineNum();
        updateWaveLine();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private boolean isLoaded = false;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.i("MyWaveView---", "  onLayout  ");

        if (isLoaded) {
            return;
        }
        isLoaded = true;
        initLineNum();
    }

    private void initLineNum() {
        clearChannelData();
        /**
         *  设置线条长度
         */
        gridVerticalNum = mShowTimeLength;
        wave_line_width = 1000 / mShowTimeLength / ROW;
        totalRow = gridVerticalNum * ROW;


        mWavePaintOne.setStrokeWidth(4);
        /** 获取控件的宽高*/
        mOscillographWidth = totalRow * wave_line_width;
        mOscillographHeight = getMeasuredHeight() - 2 * mOffsetY;


        mHorizontalLineScale = mOscillographHeight / mHorizontalLineNum;

        mVerticalLineScale = ROW * wave_line_width;
        /**
         * 总数据点的数量
         */


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

        drawWaveLineNormal(canvas);
    }

    /**
     * 绘制网格
     *
     * @param canvas
     */
    private void drawGrid(Canvas canvas) {
        int bottom = mHorizontalLineNum * mHorizontalLineScale + mOffsetY;
        int right = mOffsetX + mOscillographWidth;


//        boolean isZero = MAX_VALUE % mHorizontalLineNum == 0;
        mLinePaint.setColor(gridLineColor);
        mLinePaint.setAlpha(50);
        /** 绘制横线*/
        for (int i = 0; i < mHorizontalLineNum + 1; i++) {
            int startY = i * mHorizontalLineScale;
            if (i == mHorizontalLineNum) {
                canvas.drawLine(mOffsetX, startY + mOffsetY,
                        getRight(), startY + mOffsetY, mLinePaint);
            } else {
                canvas.drawLine(mOffsetX, startY + mOffsetY, right, startY + mOffsetY, mLinePaint);
            }
        }

        canvas.drawLine(mOffsetX, mOffsetY,
                mOffsetX, bottom, mLinePaint);
        canvas.drawLine(mOffsetX + gridVerticalNum * ROW * wave_line_width,
                mOffsetY, mOffsetX + gridVerticalNum * ROW * wave_line_width, bottom, mLinePaint);
        /** 绘制竖线*/
        for (int i = 0; i < gridVerticalNum; i++) {
            if (i == gridVerticalNum - 1 && offsetIndex == 0) {
                continue;
            }
            int startX;
            int offset = offsetIndex > 0 ? (offsetIndex % ROW * wave_line_width) : 0;
            startX = (i + 1) * mVerticalLineScale - offset;
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
            canvas.drawText(yAxisDesc, yDescLeft, mScaleTextRect.height(), mTextPaint);
        }
        if (!StringUtils.isEmpty(xAxisDesc)) {
            mTextPaint.getTextBounds(xAxisDesc, 0, xAxisDesc.length(), mScaleTextRect);
            int xDescLeft = getWidth() - mScaleTextRect.width() - 5;
            canvas.drawText(xAxisDesc, xDescLeft, getHeight() - 5, mTextPaint);
            canvas.save();
        }

        mLinePaint.setTextSize(SizeUtils.dp2px(10));
        boolean isZero = MAX_VALUE % mHorizontalLineNum == 0;
        for (int i = 0; i < mHorizontalLineNum + 1; i++) {
            String yScaleDesc;
            if (i == 0) {
                yScaleDesc = MAX_VALUE + "";
            } else if (i == mHorizontalLineNum) {
                yScaleDesc = MIN_VALUE + "";
            } else {
                if (isZero) {
                    yScaleDesc = (MAX_VALUE + MIN_VALUE) / 2 + "";
                } else {
                    yScaleDesc = (MAX_VALUE + MIN_VALUE) * 1.0f / 2 + "";
                }
            }
            mTextPaint.getTextBounds(yScaleDesc, 0, yScaleDesc.length(), mTextRect);
            int left = mOffsetX < mTextRect.width() >> 1 ? 0 : (mOffsetX - mTextRect.width() / 2) / 2;

            canvas.drawText(yScaleDesc, left, mOffsetY + mHorizontalLineScale * i + (mTextRect.height() >> 1), mScalePaint);
        }

        for (int i = 0; i < gridVerticalNum + 1; i++) {
            // 绘制横坐标刻度
            String textStr = (i + offsetIndex / ROW) + "";
            mLinePaint.getTextBounds(textStr, 0, textStr.length(), mTextRect);
            int left;
            if (i == 0) {
                left = mOffsetX;
                if (drawMode == NORMAL_MODE && offsetIndex % ROW > ROW >> 1) {
                    continue;
                }
            } else {
                int offset = offsetIndex > 0 ? (offsetIndex % ROW * wave_line_width) : 0;
                left = i * mVerticalLineScale + mOffsetX - mTextRect.width() / 2 - offset;
            }
            int bottom = mOscillographHeight + mOffsetY + mTextRect.height() + 2;
            canvas.drawText(textStr, left, bottom, mScalePaint);

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
        dataArray.clear();
    }

    /**
     * 常规模式绘制折线
     *
     * @param canvas
     */
    private boolean isDrawWaveLine;

    private void drawWaveLineNormal(Canvas canvas) {
        if (dataArray.size() == 0) {
            return;
        }

        isDrawWaveLine = true;
        drawPathFromDatas(canvas, 0, dataArray.size() - 1);
        isDrawWaveLine = false;
    }

    public void updateWaveLine() {
        if (isDrawWaveLine) {
            return;
        }
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
    private void drawPathFromDatas(Canvas canvas, int start, int end) {
        mPath.reset();

        double initOffsetY2 = mOscillographHeight * 1.0f / (MAX_VALUE);
        for (int i = 0; i < end; i++) {
            if (isRefresh) {
                isRefresh = false;
                return;
            }
            /**
             * 当前的x，y坐标
             */
            float nowX = i * wave_line_width;
            double dataValue = dataArray.get(i);
            /** 判断数据为正数还是负数  超过最大值的数据按最大值来绘制*/
            if (dataValue < 0) {
                dataValue = 0;
            } else if (dataValue > MAX_VALUE) {
                dataValue = MAX_VALUE;
            }

            float nowY = (float) (mOffsetY + mOscillographHeight - dataValue * initOffsetY2);
            if (i == 0) {
                mPath.moveTo(nowX + mOffsetX, nowY);
            }
            mPath.lineTo(nowX + mOffsetX, nowY);
        }
        canvas.drawPath(mPath, mWavePaintOne);
    }

    private int offsetIndex;


    /**
     * 添加新的数据
     */
    public void addData(double data) {
        if (isDrawWaveLine) {
            return;
        }
        switch (drawMode) {
            case NORMAL_MODE:
                // 常规模式数据添加至最后一位
                if (dataArray.size() == totalRow) {
                    offsetIndex++;
                    dataArray.removeFirst();
                }
                dataArray.addLast(data);
                break;
            case LOOP_MODE:
                // 循环模式数据添加至当前绘制的位
                if (dataArray.size() == totalRow) {
                    offsetIndex += totalRow;
                    clearChannelData(false);
                }
                dataArray.addLast(data);
                break;
        }
    }

    public void setMinValue(int value) {
        this.MIN_VALUE = value;
        clearChannelData();
        initLineNum();
        updateWaveLine();
    }

    public void setMaxValue(int value) {
        this.MAX_VALUE = value;
        clearChannelData();
        initLineNum();
        updateWaveLine();
    }

    public void resetStartTime() {
        offsetIndex = 0;
    }

}
