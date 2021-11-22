package cn.com.medicalmeasurementassistant.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Switch
import android.widget.TextView
import androidx.core.content.ContextCompat
import cn.com.medicalmeasurementassistant.R
import cn.com.medicalmeasurementassistant.entity.Constant
import cn.com.medicalmeasurementassistant.entity.SettingParamsBean
import cn.com.medicalmeasurementassistant.manager.DeviceManager
import cn.com.medicalmeasurementassistant.manager.WaveManager
import cn.com.medicalmeasurementassistant.view.recyclerview.BaseSimpleRecyclerAdapter
import cn.com.medicalmeasurementassistant.view.recyclerview.ViewHolder
import com.hjq.shape.layout.ShapeLinearLayout
import com.hjq.shape.view.ShapeTextView

class SettingParamsAdapter : BaseSimpleRecyclerAdapter<SettingParamsBean.SettingBean>() {
    private val channelStatus =
            Constant.getDefaultChannelStatus()
    private var allChannelStatus = true

    init {
        val settingBeans = SettingParamsBean.getInstance().channelBeans
        var status = false
        for (m in settingBeans.indices) {
            val channelStatus1 = (settingBeans[m] as SettingParamsBean.ChannelBean).channelStatus

            if (channelStatus1) {
                status = true
            }
            channelStatus[m] = channelStatus1
        }
        allChannelStatus = status
    }

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

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun convert(holder: ViewHolder, t: SettingParamsBean.SettingBean, position: Int) {
        val isTheLast = datas.size - 1
        when (position) {
            0 -> {
                val globalBean = t as SettingParamsBean.GlobalBean
                // 通道开启
                val channelSwitch = holder.getView<Switch>(R.id.switch_channel_status)
                // 高通滤波
                val highPassFilter = holder.getView<Switch>(R.id.switch_filtering_status)
                // 工频陷波
                val frequencySwitch = holder.getView<Switch>(R.id.switch_frequency_status)

                channelSwitch.isChecked = allChannelStatus

                channelSwitch.setOnCheckedChangeListener { _, isChecked ->
                    channelSwitch.setOnCheckedChangeListener(null)
                    allChannelStatus = isChecked
                    for (m in channelStatus.indices) {
                        channelStatus[m] = isChecked
                    }
//                    globalBean.channelStatus = isChecked
                    channelSwitch.post {
                        notifyDataSetChanged()
//                    notifyItemRangeChanged(1,8)

                    }
                }

                highPassFilter.isChecked = globalBean.highPassFilterStatus
                highPassFilter.setOnCheckedChangeListener { _, isChecked ->
                    globalBean.highPassFilterStatus = isChecked
                    DeviceManager.getInstance().highPassFilterState = isChecked
                }


                frequencySwitch.isChecked = globalBean.frequencyNotchStatus
                frequencySwitch.setOnCheckedChangeListener { _, isChecked ->
                    globalBean.frequencyNotchStatus = isChecked
                    DeviceManager.getInstance().notchFilterState = isChecked
                }

                holder.setText(R.id.tv_angle_value, globalBean.angle)
                // 电极状态描述View
                val shapeTextView = holder.getView<ShapeLinearLayout>(R.id.slv_electrode_status)
                val electrodeStatusTip = holder.getView<TextView>(R.id.tv_electrode_status_tip)
                val electrodeStatusTv = holder.getView<TextView>(R.id.tv_electrode_status)

                // 电极状态View样式设置
                val solidColor =
                        if (globalBean.electrodeStatus) R.color.electrode_bg_color_on else R.color.electrode_bg_color_off
                shapeTextView.solidColor = ContextCompat.getColor(holder.context, solidColor)
                shapeTextView.solidPressedColor = shapeTextView.solidColor

                val textColor =
                        if (globalBean.electrodeStatus) R.color.electrode_text_color_on else R.color.electrode_text_color_off
                electrodeStatusTip.setTextColor(ContextCompat.getColor(holder.context, textColor))
                electrodeStatusTv.setTextColor(ContextCompat.getColor(holder.context, textColor))

//                val electrodeStatus = if (globalBean.electrodeStatus) R.string.text_electrode_status_on else R.string.text_electrode_status_off
//                electrodeStatusTv.text = holder.context.getString(electrodeStatus)
                shapeTextView.intoBackground()
                shapeTextView.invalidate()

            }
            isTheLast -> {
                // 最后一个
                holder.setOnClickListener(R.id.stv_params_submit) {
                    var flag = false
                    val channelBeans = SettingParamsBean.getInstance().channelBeans
                    for (i in 0..7) {
                        if (channelStatus[i] != channelBeans[i].channelStatus) {
                            channelBeans[i].channelStatus = channelStatus[i]
                            flag = true
                        }
                    }
                    if (flag) {
                        WaveManager.getInstance().WaveCountChange()
                    }
                    (holder.context as Activity).finish()
                }
                return
            }
            else -> {
                // 通道Bean
                val channelBean = t as SettingParamsBean.ChannelBean
                // 电极状态  true 未脱落，false 脱落

                // 通道名称
                holder.setText(R.id.tv_title_name, t.channelName)
                // 量程
                holder.setText(R.id.tv_value, t.channelAngle)
                // 电极状态描述View
                val shapeTextView = holder.getView<ShapeTextView>(R.id.tv_status_desc)
                // 电极状态View样式设置
                val solidColor =
                        if (channelBean.electrodeStatus) R.color.electrode_bg_color_on else R.color.electrode_bg_color_off
                shapeTextView.solidColor = ContextCompat.getColor(holder.context, solidColor)
                shapeTextView.solidPressedColor = shapeTextView.solidColor
                val textColor =
                        if (channelBean.electrodeStatus) R.color.electrode_text_color_on else R.color.electrode_text_color_off
                shapeTextView.setTextColor(ContextCompat.getColor(holder.context, textColor))
//                val electrodeStatus = if (channelBean.electrodeStatus) R.string.text_electrode_on else R.string.text_electrode_off
//                shapeTextView.text = holder.context.getString(electrodeStatus)
                shapeTextView.intoBackground()
                shapeTextView.invalidate()

                // 电极状态SwitchView 设置监听
                val channelSwitch = holder.getView<Switch>(R.id.switch_status_desc)
                channelSwitch.setOnCheckedChangeListener(null)
                channelSwitch.isChecked = channelStatus[position - 1]
                channelSwitch.setOnCheckedChangeListener { _, isChecked ->
                    channelStatus[position - 1] = isChecked
                }
            }
        }
    }


}