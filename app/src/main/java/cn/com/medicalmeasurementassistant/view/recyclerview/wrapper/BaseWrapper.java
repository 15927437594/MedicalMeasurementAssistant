package cn.com.medicalmeasurementassistant.view.recyclerview.wrapper;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.com.medicalmeasurementassistant.view.recyclerview.BaseSimpleRecyclerAdapter;
import cn.com.medicalmeasurementassistant.view.recyclerview.ItemManager;
import cn.com.medicalmeasurementassistant.view.recyclerview.ViewHolder;


/**
 *
 * @author baozi
 * @date 2017/5/16
 */

public class BaseWrapper<T> extends BaseSimpleRecyclerAdapter<T> {

    protected BaseSimpleRecyclerAdapter<T> mAdapter;

    public BaseWrapper(BaseSimpleRecyclerAdapter<T> adapter) {
        mAdapter = adapter;
        mAdapter.getItemManager().setAdapter(this);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mAdapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        mAdapter.onViewAttachedToWindow(holder);
    }

    @Override
    public int getItemViewType(int position) {
        return mAdapter.getItemViewType(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return mAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mAdapter.onBindViewHolder(holder, position);
    }

    @Override
    public void convert(ViewHolder holder, T t, int position) {

    }

    @Override
    public int getItemCount() {
        return mAdapter.getItemCount();
    }

    @Override
    public int getLayoutId() {
        return mAdapter.getLayoutId();
    }


    @Override
    public List<T> getDatas() {
        return mAdapter.getDatas();
    }

    @Override
    public void setDatas(List<T> datas) {
        mAdapter.setDatas(datas);
    }

    @Override
    public CheckItem getCheckItem() {
        return mAdapter.getCheckItem();
    }

    @Override
    public void setCheckItem(CheckItem checkItem) {
        mAdapter.setCheckItem(checkItem);
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mAdapter.setOnItemClickListener(onItemClickListener);
    }

    @Override
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mAdapter.setOnItemLongClickListener(onItemLongClickListener);
    }

    @Override
    public void setOnItemContentClickListener(OnItemContentClickListener mOnItemContentClickListener) {
        mAdapter.setOnItemContentClickListener(mOnItemContentClickListener);
    }

    @Override
    public ItemManager<T> getItemManager() {
        return mAdapter.getItemManager();
    }

    @Override
    public void setItemManager(ItemManager<T> itemManager) {
        mAdapter.setItemManager(itemManager);
    }

}
