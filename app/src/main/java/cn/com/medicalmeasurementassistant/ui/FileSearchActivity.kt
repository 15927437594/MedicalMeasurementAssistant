package cn.com.medicalmeasurementassistant.ui

import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import cn.com.medicalmeasurementassistant.R
import cn.com.medicalmeasurementassistant.base.BaseKotlinActivity
import cn.com.medicalmeasurementassistant.ui.adapter.FileListAdapter
import cn.com.medicalmeasurementassistant.utils.PathUtils
import cn.com.medicalmeasurementassistant.utils.TestUtil
import cn.com.medicalmeasurementassistant.utils.spaces_item_decoration.RecyclerViewUtils
import com.blankj.utilcode.util.FileUtils
import java.io.FileFilter


class FileSearchActivity : BaseKotlinActivity(), View.OnClickListener {
    private val mSearchView by lazy { findViewById<SearchView>(R.id.search_view) }
    private val mRecyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }
    private val fileListAdapter by lazy { FileListAdapter() }
    private val mParentDirectoryName: String = PathUtils.getMeasurementDataPath()
    private var isCanUpdateList: Boolean = true
    override fun getLayoutId(): Int {
        return R.layout.activity_file_selector
    }

    override fun title(): String {
        return "文件查找"
    }

    override fun initView() {
        mSearchView.visibility = View.VISIBLE
        //设置该SearchView默认是否自动缩小为图标
        mSearchView.isIconifiedByDefault = false
        //设置该SearchView显示搜索按钮
        mSearchView.isSubmitButtonEnabled = true
        mSearchView.queryHint = "查找"
        RecyclerViewUtils.setGridLayoutManager(mRecyclerView, 4, 2f)
        mRecyclerView.adapter = fileListAdapter
        updateDirectory("")
    }

    private fun updateDirectory(searchKey: String) {
        val listFilesInDir = FileUtils.listFilesInDirWithFilter(mParentDirectoryName, getFileFilter(searchKey), true)
        listFilesInDir.sortWith(Comparator { o1, o2 ->
            o1.name.compareTo(o2.name)
        })
        if (isCanUpdateList)
            fileListAdapter.datas = listFilesInDir
    }


    private fun getFileFilter(searchKey: String): FileFilter {
        return FileFilter { pathname ->
            pathname.name.endsWith(".txt") && pathname.isFile && pathname.name.contains(searchKey)
        }
    }

    override fun initListener() {
        backLayout?.setOnClickListener(this)
        fileListAdapter.setOnItemClickListener { _, position ->
            val file = fileListAdapter.datas[position]
            TestUtil.shareFile(getActivity(), file)
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


}