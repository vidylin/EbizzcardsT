package com.gzligo.ebizzcardstranslator.business.wallet;

import android.view.View;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.persistence.MyWalletRecord;

import java.util.List;

/**
 * Created by Lwd on 2017/7/12.
 */

public class MyWalletRecordAdapter extends BaseAdapter<MyWalletRecord> {
    private List<MyWalletRecord> cardInfoList;

    public MyWalletRecordAdapter(List<MyWalletRecord> listBeen) {
        super(listBeen);
        this.cardInfoList = listBeen;
    }

    @Override
    public BaseHolder<MyWalletRecord> getHolder(View v, int viewType) {
        return new MyWalletRecordHolder(v, cardInfoList);
    }

    @Override
    public int getLayoutResId(int viewType) {
        return R.layout.my_wallet_record_item;
    }
}
