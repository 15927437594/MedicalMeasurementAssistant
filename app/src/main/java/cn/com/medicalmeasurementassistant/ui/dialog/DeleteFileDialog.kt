package cn.com.medicalmeasurementassistant.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.widget.TextView
import cn.com.medicalmeasurementassistant.R
import cn.com.medicalmeasurementassistant.utils.ToastHelper


fun showDeleteFileDialog(activity: Activity, listen: DeleteDialogListener) {
    val dialog = Dialog(activity, R.style.MyDialogStyle)
    val layoutId: Int = R.layout.layout_dialog_delete_file
    val view = LayoutInflater.from(activity).inflate(layoutId, null)
    val tvTitle = view.findViewById<TextView>(R.id.tv_title)
    tvTitle.text = "是否删除此文件"

    val tvCancel = view.findViewById<TextView>(R.id.tv_cancel)
    val tvSure = view.findViewById<TextView>(R.id.tv_sure)
    tvSure.setOnClickListener {
        dialog.dismiss()
        listen.delete()
        ToastHelper.showShort("删除成功")
    }
    tvCancel.setOnClickListener { dialog.dismiss() }
    dialog.setCanceledOnTouchOutside(true)
    dialog.setContentView(view)
    setFullScreenDialog(dialog)
}

interface DeleteDialogListener {
    fun delete()
}