package tech.zettervall.notes.utils;

import android.content.Context;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class RecyclerViewUtil {

    /**
     * Get default LinearLayoutManager for RecyclerView.
     */
    public static LinearLayoutManager getDefaultLinearLayoutManager(Context context) {
        return new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
    }

    /**
     * Set default RecyclerView decoration.
     */
    public static void setRecyclerViewDecoration(LinearLayoutManager linearLayoutManager,
                                                 RecyclerView recyclerView) {
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(recyclerView.getContext(),
                        linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }
}
