package cn.com.medicalmeasurementassistant.ui

import android.text.TextUtils
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import cn.com.medicalmeasurementassistant.R
import cn.com.medicalmeasurementassistant.base.BaseKotlinActivity
import cn.com.medicalmeasurementassistant.ui.adapter.FileListAdapter
import cn.com.medicalmeasurementassistant.utils.PathUtils
import cn.com.medicalmeasurementassistant.utils.spaces_item_decoration.RecyclerViewUtils
import com.blankj.utilcode.util.FileUtils
import java.io.FileFilter


class FileSearchActivity : BaseKotlinActivity() {
    private val mSearchView by lazy { findViewById<SearchView>(R.id.search_view) }
    private val mLlTitleContain by lazy { findViewById<View>(R.id.ll_title_contain) }
    private val mRecyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }
    private val fileListAdapter by lazy { FileListAdapter() }
    private val mParentDirectoryName:String= PathUtils.getMeasurementDataPath()
    private var directoryName: String = mParentDirectoryName
    private var mFileFilter:FileFilter = getDirFileFilter()

    override fun getLayoutId(): Int {
        return R.layout.activity_file_selector
    }

    override fun initView() {
        //设置该SearchView默认是否自动缩小为图标
        mSearchView.isIconifiedByDefault = false
        //设置该SearchView显示搜索按钮
        mSearchView.isSubmitButtonEnabled = true
        mSearchView.queryHint = "查找"
        RecyclerViewUtils.setGridLayoutManager(mRecyclerView, 4, 2f)
        mRecyclerView.adapter = fileListAdapter
//        updateDirectory()
    }

    private fun updateDirectory() {
        val listFilesInDir = FileUtils.listFilesInDirWithFilter(mParentDirectoryName, mFileFilter)
        fileListAdapter.datas = listFilesInDir
    }


    fun getFileFilter(type: String): FileFilter {
        return FileFilter { pathname ->
            var fileName = ""
            pathname?.let { fileName = it.name }
            fileName.contains(type)
            fileName.endsWith(".$type")
        }
    }

    private fun getDirFileFilter(): FileFilter {
        return FileFilter { pathname ->
            pathname.isDirectory
        }
    }

    override fun initListener() {

        //为该SearchView组件设置事件监听器
        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            //单机搜索按钮时激发该方法
            override fun onQueryTextSubmit(query: String): Boolean {
                //实际应用中应该在该方法内执行实际查询，此处仅使用Toast显示用户输入的查询内容
//                Toast.makeText(this@MainActivity, "你的选择是：$query",
//                        Toast.LENGTH_SHORT).show()
                return false
            }

            //用户输入字符时激发该方法
            override fun onQueryTextChange(newText: String): Boolean {
                //如果newText不是长度为0的字符串
                if (TextUtils.isEmpty(newText)) {
                    //清除ListView的过滤
//                    mRecyclerView.clearTextFilter()

                    val listFilesInDir = FileUtils.listFilesInDirWithFilter(PathUtils.getMeasurementDataPath(), getDirFileFilter())
                    fileListAdapter.datas = listFilesInDir

                } else {
                    //使用用户输入的内容对ListView的列表项进行过滤
//                    listView.setFilterText(newText)
                }
                return true
            }
        })
    }


}