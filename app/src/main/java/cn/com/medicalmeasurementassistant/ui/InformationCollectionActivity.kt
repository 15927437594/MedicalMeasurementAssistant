package cn.com.medicalmeasurementassistant.ui

import android.view.View
import android.widget.RadioGroup
import androidx.annotation.IdRes
import cn.com.medicalmeasurementassistant.R
import cn.com.medicalmeasurementassistant.base.BaseKotlinActivity
import cn.com.medicalmeasurementassistant.utils.MeasurementFileUtils

class InformationCollectionActivity : BaseKotlinActivity(), View.OnClickListener {

    override fun getLayoutId(): Int {
        return R.layout.activity_information_collection
    }

    override fun initView() {
    }

    override fun initListener() {
        setClick(R.id.iv_file_list)
        setClick(R.id.iv_file_save)
        setClick(R.id.stv_setting_params)
        setClick(R.id.stv_collect_angle)
    }

    private fun setClick(@IdRes id: Int) {
        findViewById<View>(id).setOnClickListener(this)

        findViewById<RadioGroup>(R.id.rg_contain).setOnCheckedChangeListener { group, checkedId ->

        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_file_list -> launcherActivity(this, FileSelectorActivity::class.java)
            R.id.iv_file_save -> {
                MeasurementFileUtils.saveMeasurementFile("这是刚刚创建的" + System.currentTimeMillis())
            }
            R.id.stv_setting_params -> launcherActivity(this, SettingParamsActivity::class.java)
            R.id.stv_collect_angle -> launcherActivity(this, CalibrationAngleActivity::class.java)
            else -> {
            }
        }
    }

}