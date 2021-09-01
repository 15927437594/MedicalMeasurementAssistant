package cn.com.medicalmeasurementassistant.ui.adapter

import cn.com.medicalmeasurementassistant.R
import cn.com.medicalmeasurementassistant.view.recyclerview.BaseSimpleRecyclerAdapter
import cn.com.medicalmeasurementassistant.view.recyclerview.ViewHolder

class SettingParamsAdapter : BaseSimpleRecyclerAdapter<String>() {
    override fun getLayoutId(): Int {
        return 0
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> R.layout.item_setting_params_global_layout
            datas.size - 1 -> R.layout.item_setting_params_save_layout
            else -> R.layout.item_setting_params_channel_layout
        }
    }

    override fun convert(holder: ViewHolder, t: String, position: Int) {

        if (position == 0) return
        holder.setText(R.id.tv_title_name, t)

    }
}