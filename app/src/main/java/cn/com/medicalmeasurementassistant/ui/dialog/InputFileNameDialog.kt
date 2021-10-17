package cn.com.medicalmeasurementassistant.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import cn.com.medicalmeasurementassistant.R
import cn.com.medicalmeasurementassistant.utils.ToastHelper
import com.blankj.utilcode.util.ScreenUtils

/**
 *  错误提示Dialog
 */
fun showInputFileNameDialog(activity: Activity, listen: FileNameDialogListener) {
    val dialog = Dialog(activity, R.style.MyDialogStyle)
    val layoutId: Int = R.layout.layout_dialog_input_file_name
    val view = LayoutInflater.from(activity).inflate(layoutId, null)
    val editText = view.findViewById<EditText>(R.id.tv_content)
    val cancelTv = view.findViewById<TextView>(R.id.tv_cancel)
    val sureTv = view.findViewById<TextView>(R.id.tv_sure)
    sureTv.setOnClickListener {
        dialog.dismiss()
        listen.sure(editText.text.toString())
        ToastHelper.showShort("数据保存成功");
    }
    cancelTv.setOnClickListener { dialog.dismiss() }
    dialog.setCanceledOnTouchOutside(true)
    dialog.setContentView(view)
    setFullScreenDialog(dialog)

}

interface FileNameDialogListener {
    fun sure(fileName: String)
}

fun setFullScreenDialog( dialog: Dialog) {
    val window = dialog.window
    val p = window!!.attributes // 获取对话框当前的参数值
    p.height = ScreenUtils.getScreenHeight()
    p.width = ScreenUtils.getScreenWidth()
    window.attributes = p
    window.setGravity(Gravity.CENTER) //此处可以设置dialog显示的位置
    dialog.show()
}