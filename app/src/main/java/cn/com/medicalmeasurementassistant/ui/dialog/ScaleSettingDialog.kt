package cn.com.medicalmeasurementassistant.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import cn.com.medicalmeasurementassistant.R
import cn.com.medicalmeasurementassistant.entity.Constant
import cn.com.medicalmeasurementassistant.utils.ToastHelper
import java.lang.Exception


fun showTimeScaleDialog(activity: Activity, settingType: Int, listen: SettingDialogListener) {
    val dialog = Dialog(activity, R.style.MyDialogStyle)
    val layoutId: Int = R.layout.layout_dialog_setting_scale
    val view = LayoutInflater.from(activity).inflate(layoutId, null)
    val etContent = view.findViewById<EditText>(R.id.tv_content)
    val tvTitle = view.findViewById<TextView>(R.id.tv_title)
    when (settingType) {
        Constant.SETTING_TYPE_TIME_LENGTH -> {
            tvTitle.text = "显示时长"
            etContent.hint = "(2~5)"
            etContent.maxEms = 1
        }
        Constant.SETTING_TYPE_EMG_SCALE_RANGE -> {
            tvTitle.text = "EMG刻度范围"
            etContent.hint = "(1~5)"
            etContent.maxEms = 1
        }
        Constant.SETTING_TYPE_CAP_SCALE_RANGE -> {
            tvTitle.text = "电容刻度范围"
            etContent.hint = "(1~60)"
            etContent.maxEms = 2
        }

    }

    etContent.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            var toInt: Int
            toInt = if (s.toString().isEmpty()) {
                0
            } else {
                s.toString().toInt()
            }
            when (settingType) {
                Constant.SETTING_TYPE_TIME_LENGTH -> {
                    if (toInt > 5) {
                        toInt = 5
                        etContent.setText(toInt.toString())
                        etContent.setSelection(toInt.toString().length)
                    } else if (toInt < 2) {
                        toInt = 2
                        etContent.setText(toInt.toString())
                        etContent.setSelection(toInt.toString().length)
                    }

                }
                Constant.SETTING_TYPE_EMG_SCALE_RANGE -> {
                    if (toInt > 5) {
                        toInt = 5
                        etContent.setText(toInt.toString())
                        etContent.setSelection(toInt.toString().length)
                    } else if (toInt < 1) {

                        toInt = 2
                        etContent.setText(toInt.toString())
                        etContent.setSelection(toInt.toString().length)
                    }
                }
                Constant.SETTING_TYPE_CAP_SCALE_RANGE -> {
                    if (toInt > 60) {
                        toInt = 60
                        etContent.setText(toInt.toString())
                        etContent.setSelection(toInt.toString().length)
                    } else if (toInt < 1) {
                        toInt = 1
                        etContent.setText(toInt.toString())
                        etContent.setSelection(toInt.toString().length)
                    }
                }
            }


        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    })

    val tvCancel = view.findViewById<TextView>(R.id.tv_cancel)
    val tvSure = view.findViewById<TextView>(R.id.tv_sure)
    tvSure.setOnClickListener {
        val toString = etContent.text.toString()
        if (toString.isEmpty()) {
            ToastHelper.showShort("请输入内容")
            return@setOnClickListener
        }
        dialog.dismiss()
        listen.settingComplete(toString.toInt())
        ToastHelper.showShort("设置成功")
    }
    tvCancel.setOnClickListener { dialog.dismiss() }
    dialog.setCanceledOnTouchOutside(true)
    dialog.setContentView(view)
    setFullScreenDialog(dialog)
}

interface SettingDialogListener {
    fun settingComplete(settingValue: Int)
}