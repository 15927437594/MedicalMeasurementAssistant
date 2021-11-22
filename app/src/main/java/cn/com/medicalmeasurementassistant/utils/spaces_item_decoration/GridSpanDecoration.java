package cn.com.medicalmeasurementassistant.utils.spaces_item_decoration;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

class GridSpanDecoration extends RecyclerView.ItemDecoration {
    private final int padding;

    public GridSpanDecoration(int padding) {
        this.padding = padding;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
        int spanCount = gridLayoutManager.getSpanCount();

        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();

        int spanIndex = params.getSpanIndex();
        int spanSize = params.getSpanSize();

        // If it is in column 0 you apply the full offset on the start side, else only half
        if (spanIndex == 0) {
            outRect.left = padding;
        } else {
            outRect.left = padding / 2;
        }

        // If spanIndex + spanSize equals spanCount (it occupies the last column) you apply the full offset on the end, else only half.
        if (spanIndex + spanSize == spanCount) {
            outRect.right = padding;
        } else {
            outRect.right = padding / 2;
        }

        // just add some vertical padding as well
        outRect.top = padding / 2;
        outRect.bottom = padding / 2;

        if (isLayoutRTL(parent)) {
            int tmp = outRect.left;
            outRect.left = outRect.right;
            outRect.right = tmp;
        }
    }

    @SuppressLint({"NewApi", "WrongConstant"})
    private static boolean isLayoutRTL(RecyclerView parent) {
        return parent.getLayoutDirection() == ViewCompat.LAYOUT_DIRECTION_RTL;
    }
}