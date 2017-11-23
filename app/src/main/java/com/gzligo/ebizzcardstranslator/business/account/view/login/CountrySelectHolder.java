package com.gzligo.ebizzcardstranslator.business.account.view.login;

import android.view.View;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.persistence.District;

import butterknife.BindView;

/**
 * Created by Lwd on 2017/7/6.
 */

public class CountrySelectHolder extends BaseHolder<District>{
    @BindView(R.id.country_name) TextView countryName;
    @BindView(R.id.country_code) TextView countryCode;

    public CountrySelectHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setData(District data, int position) {
        countryName.setText(data.getLocalName());
        countryCode.setText(data.getAreaNumber());
    }

    @Override
    public void onRelease() {

    }
}
