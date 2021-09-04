package cn.com.medicalmeasurementassistant.utils

import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.SizeUtils

fun showDrawable(textView: TextView?, @DrawableRes drawableId: Int) {
    showDrawable(textView, drawableId, 16f)
}


fun showDrawable(textView: TextView?, @DrawableRes drawableId: Int, size: Float) {
    textView?.let {
        val drawable = ContextCompat.getDrawable(
            it.context,
            drawableId
        )
        drawable?.setBounds(0, 0, SizeUtils.dp2px(size), SizeUtils.dp2px(size))
        it.setCompoundDrawables(drawable, null, null, null)
    }
}


fun showDrawableForDirection(textView: TextView?, @DrawableRes drawableId: Int) {
    textView?.let {
        val drawable = ContextCompat.getDrawable(
            it.context,
            drawableId
        )
        drawable?.setBounds(0, 0, SizeUtils.dp2px(16f), SizeUtils.dp2px(16f))
        it.setCompoundDrawables(drawable, null, null, null)
    }
}


fun hideDrawable(textView: TextView?) {
    textView?.setCompoundDrawables(null, null, null, null)

}