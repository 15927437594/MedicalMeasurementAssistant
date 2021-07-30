package cn.com.medicalmeasurementassistant.ui.adapter

import cn.com.medicalmeasurementassistant.R
import cn.com.medicalmeasurementassistant.view.recyclerview.BaseSimpleRecyclerAdapter
import cn.com.medicalmeasurementassistant.view.recyclerview.ViewHolder
import java.io.File

class FileListAdapter : BaseSimpleRecyclerAdapter<File>() {
    override fun getLayoutId(): Int {
        return R.layout.layout_item_file_list
    }

    override fun convert(holder: ViewHolder, t: File, position: Int) {
        val imageView = holder.getImageView(R.id.iv_file_type)
        imageView.setImageResource(if (t.isDirectory) R.drawable.icon_folder else R.drawable.icon_file)
        holder.setText(R.id.tv_file_name, "${t.name}")
    }
}