package cn.com.medicalmeasurementassistant.utils;


import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.medicalmeasurementassistant.ui.WaveView;

/**
 * @Description: 曲线绘制工具类
 * @Author: GiftedCat
 * @Date: 2021-01-01
 */
public class WaveUtils {

    private Timer timer;
    private TimerTask timerTask;

    float point = 0f;
    int pointIndex = 0;

    /**
     * 模拟数据
     */
    public void showWaveData(final WaveView waveShowView, List<Float> data) {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (pointIndex < data.size() - 1) {
                    point = data.get(pointIndex);
                    waveShowView.showLine(point);
                    pointIndex += 1;
                } else {
                    pointIndex = 0;
                }
            }
        };
        //500表示调用schedule方法后等待500ms后调用run方法，50表示以后调用run方法的时间间隔
        timer.schedule(timerTask, 500, 50);
    }

    /**
     * 停止绘制
     */
    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if (null != timerTask) {
            timerTask.cancel();
            timerTask = null;
        }
    }
}
