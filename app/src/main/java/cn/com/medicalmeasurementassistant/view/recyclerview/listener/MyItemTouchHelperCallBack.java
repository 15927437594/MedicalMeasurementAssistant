package cn.com.medicalmeasurementassistant.view.recyclerview.listener;

import android.graphics.Color;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * <pre>
 *     @author : shixuhui
 *     e-mail : shixuhui1993@163.com
 *     time   : 2018/04/10
 *     version: 2.1.2
 * </pre>
 */
public class MyItemTouchHelperCallBack extends ItemTouchHelper.Callback {
//    private List<DoorControlEntity> beanList;
//    private MyDragSelectorAdapter mAdapter;
//
//    public MyItemTouchHelperCallBack(MyDragSelectorAdapter adapter) {
//        mAdapter = adapter;
//        beanList = mAdapter.getDatas();
//    }
//
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            final int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        } else {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            final int swipeFlags = 0;
//                    final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //得到当拖拽的viewHolder的Position
//        int fromPosition = viewHolder.getAdapterPosition();
//        //拿到当前拖拽到的item的viewHolder
//        int toPosition = target.getAdapterPosition();
//        if (fromPosition < toPosition) {
//            for (int i = fromPosition; i < toPosition; i++) {
//                Collections.swap(beanList, i, i + 1);
//            }
//        } else {
//            for (int i = fromPosition; i > toPosition; i--) {
//                Collections.swap(beanList, i, i - 1);
//            }
//        }
//        mAdapter.notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    /**
     * 重写拖拽可用
     *
     * @return s
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }
    /**
     * 长按选中Item的时候开始调用
     *
     * @param viewHolder viewHolder
     * @param actionState 状态
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder.itemView.setBackgroundColor(Color.WHITE);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }
    /**
     * 手指松开的时候还原
     * @param recyclerView recyclerView
     * @param viewHolder viewHolder
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setBackgroundColor(0);
    }
}
