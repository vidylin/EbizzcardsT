package com.gzligo.ebizzcardstranslator.business.wallet;

import android.view.View;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.persistence.CardInfo;

import java.util.List;

/**
 * Created by Lwd on 2017/7/13.
 */

public class MyBankCardAdapter extends BaseAdapter<CardInfo> {
    private int pos;

    public MyBankCardAdapter(List<CardInfo> infos, int pos) {
        super(infos);
        this.pos = pos;
    }

    @Override
    public BaseHolder<CardInfo> getHolder(View v, int viewType) {
        return new MyBankCardHolder(v, pos);
    }

    @Override
    public int getLayoutResId(int viewType) {
        return R.layout.my_bank_card_item;
    }

    @Override
    public void onBindViewHolder(BaseHolder<CardInfo> holder, int position, List<Object> payloads) {
        if (!payloads.isEmpty()) {
            MyBankCardHolder myBankCardHolder = (MyBankCardHolder) holder;
            String string = payloads.get(0).toString().replace("[", "").replace("]", "");
            switch (string) {
                case "choice":
                    myBankCardHolder.checkImg.setVisibility(View.VISIBLE);
                    break;
                case "unChoice":
                    myBankCardHolder.checkImg.setVisibility(View.GONE);
                    break;
            }
        } else {
            onBindViewHolder(holder, position);
        }
    }
}
