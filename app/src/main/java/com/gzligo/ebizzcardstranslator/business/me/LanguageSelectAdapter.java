package com.gzligo.ebizzcardstranslator.business.me;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.persistence.LanguageSelectBean;

import java.util.List;

/**
 * Created by ZuoJian on 2017/5/31.
 */

public class LanguageSelectAdapter extends RecyclerView.Adapter <LanguageSelectAdapter.MyViewHolder>{

    private List<LanguageSelectBean> mList;
    private LayoutInflater inflater;
    private OnItemClickListener mItemClickListener;

    public LanguageSelectAdapter(List<LanguageSelectBean> mList, Context mContext) {
        this.mList = mList;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_select_language,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.mLanguageTxt.setText(mList.get(position).getName());
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mList.get(position).getSelect().equals("0")){
                    mList.get(position).setSelect("1");
                    holder.mSelectIv.setBackgroundResource(R.mipmap.language_select_checked);
                }else {
                    mList.get(position).setSelect("0");
                    holder.mSelectIv.setBackgroundResource(R.mipmap.language_select_normal);
                }
                mItemClickListener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mLanguageTxt;
        ImageView mSelectIv;
        RelativeLayout mLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            mLanguageTxt = (TextView) itemView.findViewById(R.id.item_language_txt);
            mSelectIv = (ImageView) itemView.findViewById(R.id.item_language_iv);
            mLayout = (RelativeLayout) itemView.findViewById(R.id.item_language_rl);
        }
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }

    public void setOnClickListener(OnItemClickListener onItemClickListener){
        mItemClickListener = onItemClickListener;
    }
}
