package cn.com.medicalmeasurementassistant.ui

import android.view.View
import android.widget.Switch
import androidx.annotation.IdRes
import cn.com.medicalmeasurementassistant.R
import cn.com.medicalmeasurementassistant.base.BaseKotlinActivity
import cn.com.medicalmeasurementassistant.manager.DeviceManager
import cn.com.medicalmeasurementassistant.manager.ServerManager
import cn.com.medicalmeasurementassistant.ui.dialog.FileNameDialogListener
import cn.com.medicalmeasurementassistant.ui.dialog.showInputFileNameDialog
import cn.com.medicalmeasurementassistant.utils.LogUtils
import cn.com.medicalmeasurementassistant.utils.MeasurementFileUtils
import cn.com.medicalmeasurementassistant.utils.SocketUtils

class InformationCollectionActivity : BaseKotlinActivity(), View.OnClickListener {

    private val collectionSwitch by lazy { findViewById<Switch>(R.id.iv_collect_operate_top) }
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
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_file_list -> launcherActivity(this, FileSearchActivity::class.java)
            R.id.iv_file_save -> {
                showInputFileNameDialog(this, object : FileNameDialogListener {
                    override fun sure(fileName: String) {
                        MeasurementFileUtils.saveMeasurementFile(fileName,DeviceManager.getInstance().originalData, DeviceManager.getInstance().highPassFilterData)
                    }
                })

            }
            R.id.stv_setting_params -> launcherActivity(this, SettingParamsActivity::class.java)
            R.id.stv_collect_angle -> launcherActivity(this, CalibrationAngleActivity::class.java)
            else -> {
            }
        }
    }

}