package cn.com.medicalmeasurementassistant.ui.adapter

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.TextView
import cn.com.medicalmeasurementassistant.R
import cn.com.medicalmeasurementassistant.app.ProjectApplication
import cn.com.medicalmeasurementassistant.utils.LogUtil
import cn.com.medicalmeasurementassistant.utils.StringSpanUtils
import cn.com.medicalmeasurementassistant.utils.StringUtils
import cn.com.medicalmeasurementassistant.view.recyclerview.BaseSimpleRecyclerAdapter
import cn.com.medicalmeasurementassistant.view.recyclerview.ViewHolder
import com.blankj.utilcode.util.LogUtils
import java.io.File

class FileListAdapter : BaseSimpleRecyclerAdapter<File>() {
    var mSearchKey: String? = null
    override fun getLayoutId(): Int {
        return R.layout.layout_item_file_list
    }


    override fun convert(holder: ViewHolder, t: File, position: Int) {
        val imageView = holder.getImageView(R.id.iv_file_type)
        imageView.setImageResource(if (t.isDirectory) R.drawable.icon_folder else R.drawable.icon_file)

        if (StringUtils.isEmpty(mSearchKey) || StringUtils.isEmpty(t.name)) {
            holder.setText(R.id.tv_file_name, t.name)
            return
        }
        mSearchKey?.let {

            Log.i("ssssss---","position---------------------------$position")
            if (highLightText(t.name, it, holder.getView(R.id.tv_file_name))) {
                return
            }
        }
        holder.setText(R.id.tv_file_name, t.name)
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
                Log.i("ssssss---","index = $index  lastIndex = $lastIndex")
                val span = ForegroundColorSpan(ProjectApplication.getApp().resources.getColor(R.color.red))
                spannableString.setSpan(span, index, lastIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }
        }
        textView.text = spannableString
        return highLight
    }

}