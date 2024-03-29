package tk.beason.pulltorefresh.recycleview;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import tk.beason.pulltorefresh.IPullToRefreshFooter;
import tk.beason.pulltorefresh.R;


public class LoadMoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IPullToRefreshFooter {
    /**
     * 是否是加载更多的Item
     */
    private static final int TYPE_LOAD_MORE = Integer.MAX_VALUE - 2;
    /**
     * 加载动画的时间
     */
    private static final int ROTATE_ANIM_DURATION = 380;

    /**
     * 包裹的Adapter
     */
    private RecyclerView.Adapter mInnerAdapter;
    /**
     * LayoutInflater
     */
    private LayoutInflater mLayoutInflater;
    /**
     * LoadMore de Views
     */
    private View mLoadMoreView;
    private TextView mLoadMoreTextView;
    private ImageView mLoadMoreLoadingView;
    /**
     * 旋转动画
     */
    private RotateAnimation mRotateLoading;
    /**
     * 当前的Load More状态
     */
    private int mStatus;

    private boolean isEnabled;

    public LoadMoreAdapter(@NonNull Context context, @NonNull RecyclerView.Adapter adapter) {
        isEnabled = true;

        mInnerAdapter = adapter;
        mInnerAdapter.registerAdapterDataObserver(mDataObserver);

        mLayoutInflater = LayoutInflater.from(context);

        mRotateLoading = new RotateAnimation(0, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateLoading.setDuration(ROTATE_ANIM_DURATION * 2);
        mRotateLoading.setRepeatCount(Animation.INFINITE);
        mRotateLoading.setFillAfter(false);
    }


    @Override
    public int getItemCount() {
        if (isEnabled) {
            return mInnerAdapter.getItemCount() + 1;
        } else {
            return mInnerAdapter.getItemCount();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (!isEnabled) {
            return mInnerAdapter.getItemViewType(position);
        }

        int realCount = mInnerAdapter.getItemCount();
        if (position >= realCount) {
            return TYPE_LOAD_MORE;
        } else {
            return mInnerAdapter.getItemViewType(position);
        }
    }

    /**
     * 是否是 LoadMore状态
     */
    private boolean isLoadMoreItem(int position) {
        int realCount = mInnerAdapter.getItemCount();
        return position >= realCount;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_LOAD_MORE) {
            mLoadMoreView = mLayoutInflater.inflate(R.layout.pull2refresh_footer, parent, false);
            mLoadMoreTextView = mLoadMoreView.findViewById(R.id.pull_to_refresh_footer_text);
            mLoadMoreLoadingView = mLoadMoreView.findViewById(R.id.pull_to_refresh_footer_loading);
            return new ViewHolder(mLoadMoreView);
        }else {
            return mInnerAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        if (itemType == TYPE_LOAD_MORE) {
            setFooterStatus(mStatus);
        } else {
            mInnerAdapter.onBindViewHolder(holder, position);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        mInnerAdapter.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        if (!(layoutManager instanceof GridLayoutManager)) {
            // 如果不是GridLayoutManager 那么不进行任何操作
            return;
        }

        final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
        final GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (isLoadMoreItem(position)) {
                    return gridLayoutManager.getSpanCount();
                }
                if (spanSizeLookup != null) {
                    return spanSizeLookup.getSpanSize(position);
                }
                return 1;
            }
        });
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mInnerAdapter.unregisterAdapterDataObserver(mDataObserver);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        mInnerAdapter.onViewAttachedToWindow(holder);
        if (isLoadMoreItem(holder.getLayoutPosition())) {
            setFullSpan(holder);
            setFooterStatus(mStatus);
        }
    }

    private void setFullSpan(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(true);
        }
    }

    @Override
    public void init(ViewGroup parent) {
        mStatus = IPullToRefreshFooter.STATUS_NORMAL;
    }

    @Override
    public void setStatus(@status int status) {
        mStatus = status;
        setFooterStatus(status);
    }

    @Override
    public int getStatus() {
        return mStatus;
    }

    @Override
    public int loadSuccess(ViewGroup parent) {
        return 0;
    }

    @Override
    public int loadFailed(ViewGroup parent) {
        return 0;
    }

    @Override
    public void setEnabled(boolean enable) {
        isEnabled = enable;
        if (mLoadMoreView != null) {
            mLoadMoreView.setVisibility(enable ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void setOnClickFooterListener(View.OnClickListener listener) {

    }

    private void setFooterStatus(int status) {
        if (mLoadMoreView == null) {
            return;
        }
        switch (status) {
            case STATUS_NORMAL:
                mLoadMoreLoadingView.clearAnimation();
                mLoadMoreLoadingView.setVisibility(View.GONE);
                mLoadMoreTextView.setText(R.string.pull2refresh_footer_normal);
                break;
            case STATUS_LOADING:
                mLoadMoreLoadingView.clearAnimation();
                mLoadMoreLoadingView.startAnimation(mRotateLoading);
                mLoadMoreLoadingView.setVisibility(View.VISIBLE);
                mLoadMoreTextView.setText(R.string.pull2refresh_footer_loading);
                break;
            case STATUS_FAILED:
                mLoadMoreLoadingView.clearAnimation();
                mLoadMoreLoadingView.setVisibility(View.GONE);
                mLoadMoreTextView.setText(R.string.pull2refresh_footer_fail);
                break;
            case STATUS_END:
                mLoadMoreLoadingView.clearAnimation();
                mLoadMoreLoadingView.setVisibility(View.GONE);
                mLoadMoreTextView.setText(R.string.pull2refresh_footer_end);
                break;
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }
    }

    @SuppressWarnings("unused")
    public RecyclerView.Adapter getInnerAdapter() {
        return mInnerAdapter;
    }

    /**
     * 注册监听
     */
    private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
            notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            notifyItemMoved(fromPosition, toPosition);
        }

    };
}
