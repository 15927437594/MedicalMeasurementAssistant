package cn.com.medicalmeasurementassistant.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.com.medicalmeasurementassistant.R
import cn.com.medicalmeasurementassistant.base.BaseKotlinActivity

class CalibrationAngleActivity : BaseKotlinActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_calibration_angle
    }

    override fun title(): String {
        return getString(R.string.collect_angle)
    }

    override fun initView() {
    }
}