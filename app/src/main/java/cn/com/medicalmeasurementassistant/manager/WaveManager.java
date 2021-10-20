package cn.com.medicalmeasurementassistant.manager;

import java.util.ArrayList;
import java.util.List;

import cn.com.medicalmeasurementassistant.listener.OnWaveCountChangeListener;

public class WaveManager {
    private final static WaveManager waveManager = new WaveManager();

    private final List<OnWaveCountChangeListener> waveCountChangeListeners = new ArrayList<>();

    private WaveManager() {
    }

    public static WaveManager getInstance() {
        return waveManager;
    }

    public void addCallback(OnWaveCountChangeListener listener) {
        waveCountChangeListeners.add(listener);
    }

    public void removeCallback(OnWaveCountChangeListener listener) {
        waveCountChangeListeners.remove(listener);
    }

    public void WaveCountChange(boolean isAdd, int position) {
        for (OnWaveCountChangeListener listener : waveCountChangeListeners) {
            listener.waveCount(isAdd, position);
        }
    }

}
