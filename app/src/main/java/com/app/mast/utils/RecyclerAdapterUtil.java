package com.app.mast.utils;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by pawansingh on 19/05/18.
 */

public class RecyclerAdapterUtil extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List itemList = new ArrayList();
    private RecyclerMultipleViewHelper recyclerMultipleViewHelper;
    private int viewHolderLayoutResource;

    private List mViewsList = new ArrayList();
    private BindDataHelper mOnDataBindListener;
    private ItemClickListener mOnClickListener;
    private ItemLongClickListener mOnLongClickListener;

    public interface RecyclerMultipleViewHelper {
        RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType);

        void onBindViewHolder(RecyclerView.ViewHolder holder, int position);

        int getItemViewType(int position);
    }

    @Override
    public int getItemViewType(int position) {
        if (recyclerMultipleViewHelper != null) {
            return recyclerMultipleViewHelper.getItemViewType(position);
        } else {
            return super.getItemViewType(position);
        }
    }

    public void setItemList(List itemList) {
        this.itemList = itemList;
    }

    public void updateItemList(List itemList) {
        if(this.itemList != null && !this.itemList.isEmpty()) {
            this.itemList.addAll(itemList);
        } else{
            this.itemList = itemList;
        }
    }

    public interface BindDataHelper {
        void bindView(int position, Map<Integer, View> viewMap);
    }


    public interface ItemClickListener {
        void onItemClick(View view, int position, RecyclerAdapterUtil recyclerAdapterUtil);
    }

    public interface ItemLongClickListener {
        void onLongItemClick(View view, int position, RecyclerAdapterUtil recyclerAdapterUtil);
    }


    public void addOnDataBindListener(BindDataHelper mOnDataBindListener) {
        this.mOnDataBindListener = mOnDataBindListener;
    }

    public final void addOnClickListener(ItemClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    public final void addOnLongClickListener(ItemLongClickListener mOnLongClickListener) {
        this.mOnLongClickListener = mOnLongClickListener;
    }

    public RecyclerAdapterUtil(Context context, List itemList, int viewHolderLayoutResource) {
        this.context = context;
        this.itemList = itemList;
        this.viewHolderLayoutResource = viewHolderLayoutResource;
    }

    public RecyclerAdapterUtil(Context context, List itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public final void addViewsList(int... viewsList) {
        this.mViewsList = Arrays.asList(viewsList);
    }

    public final void addViewsList(List viewsList) {
        this.mViewsList = viewsList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (recyclerMultipleViewHelper != null) {
            return recyclerMultipleViewHelper.onCreateViewHolder(parent, viewType);
        } else {
            View view = LayoutInflater.from(context).inflate(this.viewHolderLayoutResource, parent, false);
            return new RecyclerAdapterUtil.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (recyclerMultipleViewHelper != null) {
            recyclerMultipleViewHelper.onBindViewHolder(holder, position);
        } else {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.bindData(position);
        }
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private Map<Integer, View> map = new HashMap<>();

        public ViewHolder(View itemView) {
            super(itemView);
            for (Object o : mViewsList) {
                View view = itemView.findViewById((int) o);
                view.setOnClickListener(this);
                view.setOnLongClickListener(this);
                map.put(((Integer) o).intValue(), view);

            }
        }

        public void bindData(int position) {
            mOnDataBindListener.bindView(position, map);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onItemClick(view, getAdapterPosition(), RecyclerAdapterUtil.this);
        }

        @Override
        public boolean onLongClick(View view) {
            mOnLongClickListener.onLongItemClick(view, getAdapterPosition(), RecyclerAdapterUtil.this);
            return true;
        }
    }

    public static class RVEmptyObserver extends RecyclerView.AdapterDataObserver {
        private View emptyView;
        private RecyclerView recyclerView;


        /**
         * Constructor to set an Empty View for the RV
         */
        public RVEmptyObserver(RecyclerView rv, View ev) {
            this.recyclerView = rv;
            this.emptyView = ev;
            checkIfEmpty();
        }


        /**
         * Check if Layout is empty and show the appropriate view
         */
        private void checkIfEmpty() {
            if (emptyView != null && recyclerView.getAdapter() != null) {
                boolean emptyViewVisible = recyclerView.getAdapter().getItemCount() == 0;
                emptyView.setVisibility(emptyViewVisible ? View.VISIBLE : View.GONE);
                recyclerView.setVisibility(emptyViewVisible ? View.GONE : View.VISIBLE);
            }
        }


        /**
         * Abstract method implementations
         */
        @Override
        public void onChanged() {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }

    }

    public static abstract class RecyclerViewEndlessScrollListener extends RecyclerView.OnScrollListener {
        // The minimum amount of items to have below your current scroll position
        // before loading more.
        private int visibleThreshold = 5;
        // The current offset index of data you have loaded
        private int currentPage = 0;
        // The total number of items in the dataset after the last load
        private int previousTotalItemCount = 0;
        // True if we are still waiting for the last set of data to load.
        private boolean loading = true;
        // Sets the starting page index
        private int startingPageIndex = 0;

        RecyclerView.LayoutManager mLayoutManager;

        public RecyclerViewEndlessScrollListener(LinearLayoutManager layoutManager) {
            this.mLayoutManager = layoutManager;
        }

        public RecyclerViewEndlessScrollListener(GridLayoutManager layoutManager) {
            this.mLayoutManager = layoutManager;
            visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
        }

        public RecyclerViewEndlessScrollListener(StaggeredGridLayoutManager layoutManager) {
            this.mLayoutManager = layoutManager;
            visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
        }

        public int getLastVisibleItem(int[] lastVisibleItemPositions) {
            int maxSize = 0;
            for (int i = 0; i < lastVisibleItemPositions.length; i++) {
                if (i == 0) {
                    maxSize = lastVisibleItemPositions[i];
                } else if (lastVisibleItemPositions[i] > maxSize) {
                    maxSize = lastVisibleItemPositions[i];
                }
            }
            return maxSize;
        }

        // This happens many times a second during a scroll, so be wary of the code you place here.
        // We are given a few useful parameters to help us work out if we need to load some more data,
        // but first we check if we are waiting for the previous load to finish.
        @Override
        public void onScrolled(final RecyclerView view, int dx, int dy) {
            int lastVisibleItemPosition = 0;
            final int totalItemCount = mLayoutManager.getItemCount();

            if (mLayoutManager instanceof StaggeredGridLayoutManager) {
                int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) mLayoutManager).findLastVisibleItemPositions(null);
                // get maximum element within the list
                lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);
            } else if (mLayoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();
            } else if (mLayoutManager instanceof LinearLayoutManager) {
                lastVisibleItemPosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
            }

            // If the total item count is zero and the previous isn't, assume the
            // list is invalidated and should be reset back to initial state
            if (totalItemCount < previousTotalItemCount) {
                this.currentPage = this.startingPageIndex;
                this.previousTotalItemCount = totalItemCount;
                if (totalItemCount == 0) {
                    this.loading = true;
                }
            }
            // If it’s still loading, we check to see if the dataset count has
            // changed, if so we conclude it has finished loading and update the current page
            // number and total item count.
            if (loading && (totalItemCount > previousTotalItemCount)) {
                loading = false;
                previousTotalItemCount = totalItemCount;
            }

            // If it isn’t currently loading, we check to see if we have breached
            // the visibleThreshold and need to reload more data.
            // If we do need to reload some more data, we execute onLoadMore to fetch the data.
            // threshold should reflect how many total columns there are too
            if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
                currentPage++;
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        onLoadMore(currentPage, totalItemCount, view);
                    }
                });

                loading = true;
            }
        }

        // Call this method whenever performing new searches
        public void resetState() {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = 0;
            this.loading = true;
        }

        // Defines the process for actually loading more data based on page
        public abstract void onLoadMore(int page, int totalItemsCount, RecyclerView view);

    }


    public static final class Builder {
        private RecyclerAdapterUtil mRecyclerAdapter;
        private RVEmptyObserver rvEmptyObserver;
        private RecyclerViewEndlessScrollListener recyclerViewEndlessScrollListener;

        public final RecyclerAdapterUtil.Builder addMultipleViewHelper(RecyclerMultipleViewHelper recyclerMultipleViewHelper) {
            this.mRecyclerAdapter.recyclerMultipleViewHelper = recyclerMultipleViewHelper;
            return this;
        }

        public final RecyclerAdapterUtil.Builder addObserver(RVEmptyObserver rvEmptyObserver) {
            this.rvEmptyObserver = rvEmptyObserver;
            return this;
        }

        public final RecyclerAdapterUtil.Builder addOnScrollListener(RecyclerViewEndlessScrollListener recyclerViewEndlessScrollListener) {
            this.recyclerViewEndlessScrollListener = recyclerViewEndlessScrollListener;
            return this;
        }

        public final RecyclerAdapterUtil.Builder addClickListener(ItemClickListener listener) {
            this.mRecyclerAdapter.addOnClickListener(listener);
            return this;
        }

        public final RecyclerAdapterUtil.Builder addLongClickListener(ItemLongClickListener listener) {
            this.mRecyclerAdapter.addOnLongClickListener(listener);
            return this;
        }

        public final RecyclerAdapterUtil.Builder bindView(BindDataHelper listener) {
            this.mRecyclerAdapter.addOnDataBindListener(listener);
            return this;
        }


        public final RecyclerAdapterUtil.Builder viewsList(List viewsList) {
            this.mRecyclerAdapter.addViewsList(viewsList);
            return this;
        }


        public final RecyclerAdapterUtil.Builder viewsList(int... viewsList) {
            List list = new ArrayList();
            for (int ID : viewsList) {
                list.add(ID);
            }
            this.mRecyclerAdapter.addViewsList(list);
            return this;
        }

        public final RecyclerAdapterUtil build() {
            return this.mRecyclerAdapter;
        }

        public final void into(RecyclerView recyclerView) {
            recyclerView.setAdapter((RecyclerView.Adapter) this.mRecyclerAdapter);
            if (this.recyclerViewEndlessScrollListener != null) {
                recyclerView.addOnScrollListener(recyclerViewEndlessScrollListener);
            }
            if (this.rvEmptyObserver != null) {
                this.mRecyclerAdapter.registerAdapterDataObserver(rvEmptyObserver);
            }
        }

        public Builder(Context context, List itemList, int viewHolderLayoutRecourse) {
            super();
            this.mRecyclerAdapter = new RecyclerAdapterUtil(context, itemList, viewHolderLayoutRecourse);
        }

        public Builder(Context context, List itemList) {
            super();
            this.mRecyclerAdapter = new RecyclerAdapterUtil(context, itemList);
        }


    }
}
