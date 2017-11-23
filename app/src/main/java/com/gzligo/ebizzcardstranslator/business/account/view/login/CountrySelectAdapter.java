package com.gzligo.ebizzcardstranslator.business.account.view.login;

import android.view.View;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.persistence.District;

import java.util.List;

/**
 * Created by Lwd on 2017/7/6.
 */

public class CountrySelectAdapter extends BaseAdapter<District> {

    public CountrySelectAdapter(List<District> list) {
        super(list);
    }

    @Override
    public CountrySelectHolder getHolder(View v, int viewType) {
        return new CountrySelectHolder(v);
    }

    @Override
    public int getLayoutResId(int viewType) {
        return R.layout.country_select_item;
    }
}
