package cn.com.medicalmeasurementassistant.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cn.com.medicalmeasurementassistant.R
import cn.com.medicalmeasurementassistant.base.BaseKotlinActivity
import cn.com.medicalmeasurementassistant.manager.DeviceManager
import cn.com.medicalmeasurementassistant.utils.LogUtils
import cn.com.medicalmeasurementassistant.utils.ToastHelper
import com.hjq.shape.view.ShapeTextView
import kotlin.math.abs

class CalibrationAngleActivity : BaseKotlinActivity(), View.OnClickListener {
    private val tvRealAngle by lazy { findViewById<TextView>(R.id.tv_real_angle) }
    private val tvRealCapacitance by lazy { findViewById<TextView>(R.id.tv_real_capacitance) }
    private val ivCalibrate by lazy { findViewById<ImageView>(R.id.iv_calibrate) }
    private val tvStepTip by lazy { findViewById<TextView>(R.id.tv_step_tip) }
    private val tvCalibrateSure by lazy { findViewById<ShapeTextView>(R.id.tv_calibrate_sure) }
    private val tvCalibrateNext by lazy { findViewById<ShapeTextView>(R.id.tv_calibrate_next) }
    var stepOneExecuted = false
    var stepTwoExecuted = false

    override fun getLayoutId(): Int {
        return R.layout.activity_calibration_angle
    }

    override fun title(): String {
        return getString(R.string.collect_angle)
    }

    override fun initView() {

    }

    override fun initListener() {
        tvCalibrateSure.setOnClickListener(this)
        tvCalibrateNext.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_calibrate_sure -> updateAngleCapacitance()
            R.id.tv_calibrate_next -> executeStepNext()
        }
    }

    private fun executeStepNext() {
        LogUtils.i("executeStepNext")
        if (!stepTwoExecuted) {
            tvRealAngle.text = getString(R.string.text_value_ninety)
            tvStepTip.text = getString(R.string.text_step_two)
            ivCalibrate.background = resources.getDrawable(R.mipmap.icon_angle_90, null)
            tvCalibrateNext.text = getString(R.string.text_complete)
            tvCalibrateNext.isEnabled = false
        } else {
            if (abs(DeviceManager.getInstance().p1 - DeviceManager.getInstance().p2) < 10) {
                ToastHelper.showShort("两次测得的电容值过于接近, 校准失败")
            } else {
                ToastHelper.showShort("校准成功")
            }
            onBackPressed()
        }
    }

    private fun updateAngleCapacitance() {
        LogUtils.i("updateAngleCapacitance")
        tvCalibrateNext.isEnabled = true
        if (!stepOneExecuted) {
            stepOneExecuted = true
            DeviceManager.getInstance().p1 = DeviceManager.getInstance().currentCapacitance
        } else {
            stepTwoExecuted = true
            DeviceManager.getInstance().p2 = DeviceManager.getInstance().currentCapacitance
        }
    }
}