package cn.com.medicalmeasurementassistant.ui

import androidx.recyclerview.widget.RecyclerView
import cn.com.medicalmeasurementassistant.R
import cn.com.medicalmeasurementassistant.base.BaseKotlinActivity
import cn.com.medicalmeasurementassistant.ui.adapter.SettingParamsAdapter
import cn.com.medicalmeasurementassistant.utils.spaces_item_decoration.RecyclerViewUtils

class SettingParamsActivity : BaseKotlinActivity() {
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }

    override fun getLayoutId(): Int {
        return R.layout.activity_setting_params
    }

    override fun title(): String {
        return getString(R.string.setting_params)
    }

    override fun initView() {
        RecyclerViewUtils.setRecyclerViewDivider(recyclerView, this, R.drawable.divider_tran_shape_8dp)

        val paramsAdapter = SettingParamsAdapter()
        recyclerView.adapter = paramsAdapter

        val mutableListOf = mutableListOf<String>()
        for (i in 0..9) {
            mutableListOf.add("通道$i")
        }
        paramsAdapter.datas = mutableListOf


    }
}