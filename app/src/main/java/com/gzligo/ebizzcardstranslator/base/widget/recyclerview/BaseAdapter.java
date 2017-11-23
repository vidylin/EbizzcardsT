package com.gzligo.ebizzcardstranslator.base.widget.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by xfast on 2017/5/31.
 */

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseHolder<T>> {
    private List<T> mData;
    private OnItemClickListener<T> mOnItemClickListener = null;
    private BaseHolder<T> mHolder;

    public BaseAdapter(List<T> infos) {
        super();
        this.mData = infos;
    }

    /**
     * 创建Hodler
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public BaseHolder<T> onCreateViewHolder(ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getLayoutResId(viewType), parent, false);
        mHolder = getHolder(view, viewType);
        mHolder.setOnClickListener(new BaseHolder.OnClickListener() {//设置Item点击事件
            @Override
            public void onClick(View view, int position) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, viewType, mData.get(position), position);
                }
            }
        });
        return mHolder;
    }

    @Override
    public void onBindViewHolder(BaseHolder<T> holder, int position) {
        holder.setData(mData.get(position), position);
//        if (needShowImage) {
//            ImageLoader.get().loadImage(holder.);
//        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public List<T> getData() {
        return mData;
    }

    public T getItem(int position) {
        return mData == null ? null : mData.get(position);
    }

    public abstract BaseHolder<T> getHolder(View v, int viewType);

    public abstract int getLayoutResId(int viewType);

    public static void release(RecyclerView recyclerView) {
        if (recyclerView == null) return;
        for (int i = recyclerView.getChildCount() - 1; i >= 0; i--) {
            final View view = recyclerView.getChildAt(i);
            RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
            if (viewHolder != null && viewHolder instanceof BaseHolder) {
                ((BaseHolder) viewHolder).onRelease();
            }
        }
    }


    public interface OnItemClickListener<T> {
        void onItemClick(View view, int viewType, T data, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

}
