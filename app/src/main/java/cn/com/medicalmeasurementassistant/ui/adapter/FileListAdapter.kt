package cn.com.medicalmeasurementassistant.ui.adapter

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat
import cn.com.medicalmeasurementassistant.R
import cn.com.medicalmeasurementassistant.app.ProjectApplication
import cn.com.medicalmeasurementassistant.entity.FileItemBean
import cn.com.medicalmeasurementassistant.utils.FileUtils
import cn.com.medicalmeasurementassistant.utils.StringUtils
import cn.com.medicalmeasurementassistant.view.recyclerview.BaseSimpleRecyclerAdapter
import cn.com.medicalmeasurementassistant.view.recyclerview.ViewHolder
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FileListAdapter : BaseSimpleRecyclerAdapter<FileItemBean>() {
    var mSearchKey: String? = null
    override fun getLayoutId(): Int {
        return R.layout.layout_item_file_list
    }


    override fun convert(holder: ViewHolder, t: FileItemBean, position: Int) {
//        val imageView = holder.getImageView(R.id.iv_file_type)
//        imageView.setImageResource(if (t.isDirectory) R.drawable.icon_folder else R.drawable.icon_file)
//        holder.setText(R.id.tv_file_date, t.lastModified)
        if (StringUtils.isEmpty(mSearchKey)) {
            holder.setText(R.id.tv_file_name, t.fileName)
            holder.setText(R.id.tv_file_date, t.lastModified)
            return
        }
        mSearchKey?.let {
            val highLightText = highLightText(t.lastModified, it, holder.getView(R.id.tv_file_date))
            val highLightText1 = highLightText(t.fileName, it, holder.getView(R.id.tv_file_name))
            if (highLightText || highLightText1) {
                return
            }
        }
        holder.setText(R.id.tv_file_date, t.lastModified)
        holder.setText(R.id.tv_file_name, t.fileName)

    }


    private fun highLightText(content: String, searchKey: String, textView: TextView): Boolean {
        var highLight = false
        val spannableString = SpannableString(content)

        var index = 0
        var lastIndex = 0
        while (index != -1) {
            index = content.indexOf(searchKey, lastIndex, true)
            if (index != -1) {
                highLight = true
                lastIndex = index + searchKey.length

                val span = ForegroundColorSpan(ContextCompat.getColor(ProjectApplication.getApp(), R.color.red))
                spannableString.setSpan(span, index, lastIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }
        }
        textView.text = spannableString
        return highLight
    }
}

fun getFileLastModifiedTime(file: File): String {
    val mFormatType = "yyyy.MM.dd"
    val cal = Calendar.getInstance()
    val time = file.lastModified()
    val formatter = SimpleDateFormat(mFormatType)
    cal.timeInMillis = time
    // 输出：修改时间[2] 2009-08-17 10:32:38
    return formatter.format(cal.time)
}