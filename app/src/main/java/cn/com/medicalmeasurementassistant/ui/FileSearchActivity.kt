package cn.com.medicalmeasurementassistant.ui

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import cn.com.medicalmeasurementassistant.R
import cn.com.medicalmeasurementassistant.base.BaseKotlinActivity
import cn.com.medicalmeasurementassistant.entity.FileItemBean
import cn.com.medicalmeasurementassistant.ui.adapter.FileListAdapter
import cn.com.medicalmeasurementassistant.ui.adapter.getFileLastModifiedTime
import cn.com.medicalmeasurementassistant.utils.PathUtils
import cn.com.medicalmeasurementassistant.utils.StringUtils
import cn.com.medicalmeasurementassistant.utils.TestUtil
import cn.com.medicalmeasurementassistant.utils.ToastHelper
import cn.com.medicalmeasurementassistant.utils.spaces_item_decoration.RecyclerViewUtils
import com.blankj.utilcode.util.FileUtils
import java.io.File
import java.io.FileFilter
import java.lang.reflect.Field


class FileSearchActivity : BaseKotlinActivity(), View.OnClickListener {
    private val mSearchView by lazy { findViewById<SearchView>(R.id.search_view) }
    private val mRecyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }
    private val fileListAdapter by lazy { FileListAdapter() }
    private var mParentDirectoryName: String = PathUtils.getMeasurementDataPath()
    private var isCanUpdateList: Boolean = true
    override fun getLayoutId(): Int {
        return R.layout.activity_file_selector2
    }

    override fun title(): String {
        return "已保存数据"
    }

    override fun initView() {


        try {        //--拿到字节码
            val argClass: Class<SearchView> = mSearchView.javaClass
            //--指定某个私有属性,mSearchPlate是搜索框父布局的名字
            val ownField: Field = argClass.getDeclaredField("mSearchPlate")
            //--暴力反射,只有暴力反射才能拿到私有属性
            ownField.isAccessible = true
            val mView = ownField[mSearchView] as View
            //--设置背景
            mView.setBackgroundColor(Color.TRANSPARENT)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        mSearchView.visibility = View.VISIBLE
        //设置该SearchView默认是否自动缩小为图标
        mSearchView.isIconifiedByDefault = false
        //设置该SearchView显示搜索按钮
        mSearchView.isSubmitButtonEnabled = true
        mSearchView.queryHint = getString(R.string.text_search_hint)
        RecyclerViewUtils.setRecyclerViewDivider(mRecyclerView, this, R.drawable.divider_tran_shape_8dp)
        mRecyclerView.adapter = fileListAdapter
        updateDirectory("")
    }

    private fun updateDirectory(searchKey: String) {
        val listFilesInDir = FileUtils.listFilesInDirWithFilter(mParentDirectoryName, getFileFilter(searchKey), true)
        val listFiles = sss(listFilesInDir)

        listFiles.sortWith(Comparator { o1, o2 ->
            o2.file.lastModified().compareTo(o1.file.lastModified())
        })
        if (isCanUpdateList) {
            fileListAdapter.mSearchKey = searchKey
            fileListAdapter.datas = listFiles
        }
    }

    private fun sss(fileList: List<File>): ArrayList<FileItemBean> {
        val mutableList = ArrayList<FileItemBean>()
        for (file in fileList) {
            val fileItemBean = FileItemBean()
            fileItemBean.file = file
            fileItemBean.lastModified = getFileLastModifiedTime(file)
            fileItemBean.fileName = getFileNameNoEx(file.name)
            mutableList.add(fileItemBean)
        }
        return mutableList
    }

    /*
    * Java文件操作 获取不带扩展名的文件名
    * */
    private fun getFileNameNoEx(filename: String): String {
        if (filename.isNotEmpty()) {
            val dot = filename.lastIndexOf('.')
            if (dot > -1 && dot < filename.length) {
                return filename.substring(0, dot)
            }
        }
        return filename
    }

    // 筛选文件
    private fun getFileFilter(searchKey: String): FileFilter {
        return FileFilter { pathname ->
            pathname.name.endsWith(".csv") && pathname.isFile && (getFileNameNoEx(pathname.name).contains(searchKey) || getFileLastModifiedTime(pathname).contains(searchKey))
        }
    }

    override fun initListener() {
        backLayout?.setOnClickListener(this)
        fileListAdapter.setOnItemClickListener { _, position ->
            val file = fileListAdapter.datas[position]
            TestUtil.shareFile(getActivity(), file.file)
        }

        fileListAdapter.setOnItemLongClickListener { _, position ->
            val file = fileListAdapter.datas[position]
            val path = file.file.path.replace("$mParentDirectoryName/", "")
            ToastHelper.showLong(path)
            true

        }


        //为该SearchView组件设置事件监听器
        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            //单机搜索按钮时激发该方法
            override fun onQueryTextSubmit(query: String): Boolean {
                //实际应用中应该在该方法内执行实际查询，此处仅使用Toast显示用户输入的查询内容
                return false
            }

            //用户输入字符时激发该方法
            override fun onQueryTextChange(newText: String): Boolean {
                //如果newText不是长度为0的字符串
                if (TextUtils.isEmpty(newText)) {
                    //清除ListView的过滤
                    updateDirectory("")

                } else {
                    //使用用户输入的内容对ListView的列表项进行过滤
                    updateDirectory(newText)
                }
                return true
            }
        })
    }

    override fun onBackPressed() {
        isCanUpdateList = false
        super.onBackPressed()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> onBackPressed()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            window.decorView.postDelayed({
                val view = currentFocus
                if (isShouldHideInput(view, ev)) {
                    val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view!!.windowToken, 0)
                    view.clearFocus()
                }
            }, 100)
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun isShouldHideInput(v: View?, event: MotionEvent): Boolean {
        if (v is EditText) {
            val leftTop = intArrayOf(0, 0)

//获取输入框当前的location位置
            v.getLocationInWindow(leftTop)
            val left = leftTop[0]
            val top = leftTop[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            return !(event.rawX > left && event.rawX < right && event.rawY > top && event.rawY < bottom)
        }
        return false
    }

}