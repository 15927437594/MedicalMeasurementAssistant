package cn.com.medicalmeasurementassistant.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.widget.TextView
import cn.com.medicalmeasurementassistant.R

/**
 *  错误提示Dialog
 */
fun showErrorTipDialog(activity: Activity, content: String, sureStr: String, listen: DialogListener) {
    val dialog = Dialog(activity, R.style.MyDialogStyle)
    dialog.setCancelable(false)
    val layoutId: Int = R.layout.layout_dialog_error_tip
    val view = LayoutInflater.from(activity).inflate(layoutId, null)
//    val titleTv = view.findViewById<TextView>(R.id.tv_title)
    val contentTv = view.findViewById<TextView>(R.id.tv_content)
    val sureTv = view.findViewById<TextView>(R.id.tv_sure)
    contentTv.text = content
    sureTv.text = sureStr
    sureTv.setOnClickListener {
        dialog.dismiss()
        listen.sure()
    }
    dialog.setContentView(view)
    setFullScreenDialog(dialog)
}

interface DialogListener {
    fun sure()
}