package com.gzligo.ebizzcardstranslator.business.wallet;

import android.view.View;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;

import java.util.List;

/**
 * Created by Lwd on 2017/7/12.
 */

public class CountryAndBankAdapter extends BaseAdapter {
    private int choicePosition;

    public CountryAndBankAdapter(List list, int choicePosition) {
        super(list);
        this.choicePosition = choicePosition;
    }

    @Override
    public BaseHolder getHolder(View v, int viewType) {
        return new CountryAndBankHolder(v, choicePosition);
    }

    @Override
    public int getLayoutResId(int viewType) {
        return R.layout.country_select_item;
    }
}
