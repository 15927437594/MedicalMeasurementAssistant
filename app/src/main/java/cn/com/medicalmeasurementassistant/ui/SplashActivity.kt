package cn.com.medicalmeasurementassistant.ui

import android.content.Intent
import android.os.Handler
import cn.com.medicalmeasurementassistant.R
import cn.com.medicalmeasurementassistant.base.BaseKotlinActivity

class SplashActivity : BaseKotlinActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun initView() {
        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(getActivity(), JavaInformationCollectionActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000)

    }
}