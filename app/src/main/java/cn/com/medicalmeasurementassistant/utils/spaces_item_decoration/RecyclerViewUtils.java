package cn.com.medicalmeasurementassistant.utils.spaces_item_decoration;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.blankj.utilcode.util.SizeUtils;

import cn.com.medicalmeasurementassistant.R;


/**
 * <pre>
 *     @author : shixuhui
 *     e-mail : shixuhui1993@163.com
 *     time   : 2017/11/20
 *     version: 2.1.2
 * </pre>
 */
public class RecyclerViewUtils {
    public static void setRecyclerViewDivider(RecyclerView recyclerView, Context context) {
        setRecyclerViewDivider(recyclerView,context,R.drawable.divider_tran_shape);
    }

    public static void setRecyclerViewDividerBgMain(RecyclerView recyclerView, Context context) {
        setRecyclerViewDividerBgMain(recyclerView, LinearLayoutManager.VERTICAL, context);
    }

    public static void setRecyclerViewDividerBgMain(RecyclerView recyclerView, int orientation, Context context) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context, orientation, false));
        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(context, orientation);
        ColorDrawable colorDrawable = new ColorDrawable();
        spacesItemDecoration.setDrawable(colorDrawable);
        recyclerView.addItemDecoration(spacesItemDecoration);
    }

    public static void setRecyclerViewDivider(RecyclerView recyclerView, int orientation, Context context) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context, orientation, false));
    }

    public static void setRecyclerViewDivider(RecyclerView recyclerView, Context context, int dividerShape) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        if (dividerShape > 0) {
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
            dividerItemDecoration.setDrawable(ContextCompat.getDrawable(context, dividerShape));
            recyclerView.addItemDecoration(dividerItemDecoration);
        }
    }

    public static void setGridLayoutManager(RecyclerView recyclerView, int sPan) {
        setGridLayoutManager(recyclerView, sPan, 0);

    }

    public static void setGridLayoutManager(RecyclerView recyclerView, int spanCount, float itemDecoration) {
        recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), spanCount, LinearLayoutManager.VERTICAL, false));
        if (recyclerView.getItemDecorationCount() == 0 && itemDecoration > 0) {
            GridSpaceItemDecoration gridSpaceItemDecoration = new GridSpaceItemDecoration(spanCount, SizeUtils.dp2px(itemDecoration), false);
            recyclerView.addItemDecoration(gridSpaceItemDecoration);
        }
    }

    public static void setStaggeredGridManager(RecyclerView recyclerView, int spanCount, int itemDecoration) {
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(spanCount,
                StaggeredGridLayoutManager.VERTICAL));
        if (itemDecoration > 0) {
            GridSpaceItemDecoration gridSpaceItemDecoration = new GridSpaceItemDecoration(spanCount, SizeUtils.dp2px(itemDecoration), false);
            recyclerView.addItemDecoration(gridSpaceItemDecoration);
        }
    }

    public static void setGridItemDecoration(RecyclerView recyclerView, int spanCount, int itemDecoration) {
        if (itemDecoration > 0) {
            GridSpaceItemDecoration gridSpaceItemDecoration = new GridSpaceItemDecoration(spanCount, SizeUtils.dp2px(itemDecoration), false);
            recyclerView.addItemDecoration(gridSpaceItemDecoration);
        }
    }

}
