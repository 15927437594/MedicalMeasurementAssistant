package cn.com.medicalmeasurementassistant.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.com.medicalmeasurementassistant.R

abstract class BaseKotlinActivity : AppCompatActivity() {
    var titleTv: TextView? = null
    var backLayout: View? = null
    var titleBarView: View? = null
    var errorLayout: View? = null
    private val refreshTv: TextView? = null

    private var rootView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBeforeSetContentView()
        if (getLayoutId() != -1) {
            rootView = LayoutInflater.from(this).inflate(getLayoutId(), null)
            setContentView(rootView)
        }
        setStatusBar()
        initTitleView()
        initTitleData()
        initView()
        initData()
        initListener()
    }

    open fun getLayoutId(): Int {
        return -1
    }

    open fun setStatusBar() {}
    open fun initTitleView() {
//        titleTv = findViewById(R.id.title_tv)
//        titleTv?.text = "ss"
    }

    open fun initTitleData() {}
    abstract fun initView()
    open fun initBeforeSetContentView() {}
    open fun initListener() {}
    open fun initData() {}
}