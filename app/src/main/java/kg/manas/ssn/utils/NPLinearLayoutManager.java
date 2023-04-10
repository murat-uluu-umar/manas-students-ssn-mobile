package kg.manas.ssn.utils;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NPLinearLayoutManager extends LinearLayoutManager {
    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

    public NPLinearLayoutManager(Context context) {
        super(context);
        setOrientation(LinearLayoutManager.VERTICAL);
    }

    public NPLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        setOrientation(LinearLayoutManager.VERTICAL);
    }

    public NPLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setOrientation(LinearLayoutManager.VERTICAL);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
