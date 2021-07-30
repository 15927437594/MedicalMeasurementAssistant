package cn.com.medicalmeasurementassistant.view.recyclerview;



import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class ItemManager<T> {

    private BaseSimpleRecyclerAdapter<T> mAdapter;

    public ItemManager(BaseSimpleRecyclerAdapter<T> adapter) {
        mAdapter = adapter;
    }

    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(BaseSimpleRecyclerAdapter<T> adapter) {
        mAdapter = adapter;
    }

    public abstract void addAllItems(List<T> items);

    public abstract void replaceAllItem(List<T> items);

    //刷新
    public void notifyDataChanged() {
        mAdapter.notifyDataSetChanged();
    }
}