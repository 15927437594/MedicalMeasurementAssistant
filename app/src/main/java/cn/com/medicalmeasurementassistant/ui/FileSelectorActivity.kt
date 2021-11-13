package cn.com.medicalmeasurementassistant.ui

import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.com.medicalmeasurementassistant.R
import cn.com.medicalmeasurementassistant.base.BaseKotlinActivity
import cn.com.medicalmeasurementassistant.ui.adapter.FileListAdapter
import cn.com.medicalmeasurementassistant.utils.PathUtils
import cn.com.medicalmeasurementassistant.utils.TestUtil
import cn.com.medicalmeasurementassistant.utils.spaces_item_decoration.RecyclerViewUtils
import com.blankj.utilcode.util.FileUtils
import java.io.File
import java.util.*


class FileSelectorActivity : BaseKotlinActivity(), View.OnClickListener {
    private val mRecyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }
    private val mTvMore by lazy { findViewById<TextView>(R.id.tv_more) }
    private val fileListAdapter by lazy { FileListAdapter() }
    private val mParentDirectoryName: String = PathUtils.getMeasurementDataPath()
    private var directoryName: String = mParentDirectoryName
    private var isCanUpdateList: Boolean = true
    private var mStack: Stack<File> = Stack()

    override fun getLayoutId(): Int {
        return R.layout.activity_file_selector2
    }

    override fun initView() {
        mTvMore.visibility = View.VISIBLE
        mTvMore.setBackgroundResource(R.drawable.icon_search)
        RecyclerViewUtils.setRecyclerViewDivider(mRecyclerView, this, R.drawable.divider_tran_shape_8dp)
        mRecyclerView.adapter = fileListAdapter
        updateDirectory(null)

    }

    private fun updateDirectory(file: File?) {

        if (file == null) {
            titleTv?.text = "文件列表"
            directoryName = mParentDirectoryName
        } else {
            titleTv?.text = file.path.replace("$mParentDirectoryName/", "")
            directoryName = file.path
        }
        val listFilesInDir = FileUtils.listFilesInDir(directoryName)
        listFilesInDir.sortWith(Comparator { o1, o2 ->
            if (o1.isDirectory && o2.isFile) {
                -1
            } else if (o1.isFile && o2.isDirectory) {
                1
            } else {
                if (o1.lastModified() > o2.lastModified()) {
                    -1
                } else {
                   1
                }
//                o1.lastModified().compareTo(o2.lastModified())
            }
        })
//        if (isCanUpdateList)
//            fileListAdapter.datas = listFilesInDir
    }

    override fun initListener() {
//        mTvMore.setOnClickListener(this)
//        backLayout?.setOnClickListener(this)
//        fileListAdapter.setOnItemClickListener { _, position ->
//            val file = fileListAdapter.datas[position]
//            if (file.isDirectory) {
//                mStack.push(file)
//                updateDirectory(file)
//            } else {
//                TestUtil.shareFile(getActivity(), file)
//            }
//        }
    }

    override fun onBackPressed() {
        if (mStack.empty()) {
            isCanUpdateList = false
            super.onBackPressed()
            return
        }
        mStack.pop()
        if (mStack.empty()) {
            updateDirectory(null)
            return
        }
        updateDirectory(mStack.peek())
    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.tv_more -> {
                val intent = Intent(this, FileSearchActivity::class.java)
                intent.putExtra("parentPath", directoryName)
                startActivity(intent)
            }
            R.id.iv_back -> {
                isCanUpdateList = false
                finish()
            }
        }
    }


}