package com.gzligo.ebizzcardstranslator.business.message;

import android.util.Log;
import android.view.View;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.persistence.TranslatorSelectedBean;
import com.gzligo.ebizzcardstranslator.utils.TimeUtils;

import java.util.List;

/**
 * Created by Lwd on 2017/6/7.
 */

public class RecentContactsAdapter extends BaseAdapter<TranslatorSelectedBean> {
    private List<TranslatorSelectedBean> selectedBeanList;

    public RecentContactsAdapter(List<TranslatorSelectedBean> selectedBeanList) {
        super(selectedBeanList);
        this.selectedBeanList = selectedBeanList;
    }

    @Override
    public BaseHolder getHolder(View v, int viewType) {
        return new RecentContactsHolder(v, selectedBeanList);
    }

    @Override
    public int getLayoutResId(int viewType) {
        return R.layout.fragment_recent_contacts_adapter_content_item;
    }

    @Override
    public void onBindViewHolder(BaseHolder<TranslatorSelectedBean> holder, int position, List<Object> payloads) {
        if(!payloads.isEmpty()){
            RecentContactsHolder chatActivityHolder = (RecentContactsHolder) holder;
            TranslatorSelectedBean data = selectedBeanList.get(position);
            chatActivityHolder.newMsgTv.setText(data.getTranslatorMsg());
            if (null != data.getUnTransMsg() && data.getUnTransMsg() > 0) {
                String number = String.valueOf(data.getUnTransMsg());
                chatActivityHolder.redPointTv.showTextBadge(String.valueOf(data.getUnTransMsg()));
                Log.e("sdf----====",number);
            } else {
                chatActivityHolder.redPointTv.hiddenBadge();
            }
            if (null != data.getNotifyTime()) {
                chatActivityHolder.msgTimeTv.setText(TimeUtils.getHMS(data.getNotifyTime()));
            }
        }else{
            onBindViewHolder(holder, position);
        }
    }

    public void notificationList(List<TranslatorSelectedBean> selectedBeanList){
        this.selectedBeanList = selectedBeanList;
    }
}
