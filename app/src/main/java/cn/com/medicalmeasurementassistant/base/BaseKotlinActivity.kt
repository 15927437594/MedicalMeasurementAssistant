package cn.com.medicalmeasurementassistant.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.com.medicalmeasurementassistant.R
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.StringUtils

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

    open fun setStatusBar() {
        val viewStatus = findViewById<View>(R.id.view_status)
        viewStatus?.let {
//            BarUtils.setStatusBarAlpha(it)
        }
        BarUtils.setStatusBarLightMode(this, true)
    }

    open fun title(): String {
        return ""
    }

    open fun initTitleView() {
        titleTv = findViewById(R.id.tv_title)
        setText(titleTv,title())
        backLayout = findViewById(R.id.iv_back)
        backLayout?.setOnClickListener { finish() }
    }

    open fun initTitleData() {}
    abstract fun initView()
    open fun initBeforeSetContentView() {}
    open fun initListener() {}
    open fun initData() {}
    fun getActivity(): Activity {
        return this
    }

    companion object {
        fun setText(tv: TextView?, content: String?) {
            tv?.text = if (StringUtils.isEmpty(content)) "" else content
        }


    }

}