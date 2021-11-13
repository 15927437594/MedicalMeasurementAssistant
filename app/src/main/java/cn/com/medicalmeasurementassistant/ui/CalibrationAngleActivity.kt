package cn.com.medicalmeasurementassistant.ui

import android.os.Handler
import android.os.Looper
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
    private val etRealAngle by lazy { findViewById<TextView>(R.id.et_real_angle) }
    private val etRealCapacitance by lazy { findViewById<TextView>(R.id.et_real_capacitance) }
    private val ivCalibrate by lazy { findViewById<ImageView>(R.id.iv_calibrate) }
    private val tvStepTip by lazy { findViewById<TextView>(R.id.tv_step_tip) }
    private val tvCalibrateAuto by lazy { findViewById<ShapeTextView>(R.id.tv_calibrate_auto) }
    private val tvCalibrateNext by lazy { findViewById<ShapeTextView>(R.id.tv_calibrate_next) }
    private var stepOneExecuted = false
    var handler: Handler = Handler(Looper.getMainLooper())


    override fun getLayoutId(): Int {
        return R.layout.activity_calibration_angle
    }

    override fun title(): String {
        return getString(R.string.collect_angle)
    }

    override fun initView() {
        updateRealCapacitance()
    }

    override fun initListener() {
        tvCalibrateAuto.setOnClickListener(this)
        tvCalibrateNext.setOnClickListener(this)
//        handler.postDelayed(updateCapacitanceRunnable,500L)
    }

    private fun updateRealCapacitance() {
        LogUtils.i("updateRealCapacitance")
        etRealCapacitance.text = DeviceManager.getInstance().currentCapacitance.toString()
    }

//    private val updateCapacitanceRunnable: Runnable = object : Runnable {
//        override fun run() {
//            updateRealCapacitance()
//            handler.postDelayed(this, 500L)
//        }
//    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_calibrate_auto -> updateRealCapacitance()
            R.id.tv_calibrate_next -> executeCalibrate()
        }
    }

    private fun executeCalibrate() {
        LogUtils.i("executeCalibrate")
        if (!stepOneExecuted) {
            val angle = etRealAngle.text.toString().toDouble()
            val capacitance = etRealCapacitance.text.toString().toDouble()
            LogUtils.i("angle=$angle, capacitance=$capacitance")
            DeviceManager.getInstance().angle1 = angle
            DeviceManager.getInstance().p1 = capacitance
            updateRealCapacitance()

            etRealAngle.text = getString(R.string.text_value_ninety)
            tvStepTip.text = getString(R.string.text_step_two)
            ivCalibrate.background = resources.getDrawable(R.mipmap.icon_angle_90, null)
            tvCalibrateNext.text = getString(R.string.text_complete)
            stepOneExecuted = true
        } else {
            val angle = etRealAngle.text.toString().toDouble()
            val capacitance = etRealCapacitance.text.toString().toDouble()
            LogUtils.i("angle=$angle, capacitance=$capacitance")
            DeviceManager.getInstance().angle2 = angle
            DeviceManager.getInstance().p2 = capacitance

            if (abs(DeviceManager.getInstance().p2 - DeviceManager.getInstance().p1) < 10) {
                DeviceManager.getInstance().calibrateState = false
                DeviceManager.getInstance().calibrateFail()
                ToastHelper.showShort("两次测得的电容值过于接近, 校准失败")
                handler.postDelayed({ onBackPressed() }, 2000L)
                DeviceManager.getInstance().resetCalibrate()
            } else {
                DeviceManager.getInstance().calibrateState = true
                DeviceManager.getInstance().calibrateSuccess()
                ToastHelper.showShort("校准成功")
                handler.postDelayed({ onBackPressed() }, 1000L)
            }
        }
    }

}