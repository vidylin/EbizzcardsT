package com.gzligo.ebizzcardstranslator.business.wallet;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.persistence.Bank;
import com.gzligo.ebizzcardstranslator.persistence.BankPoint;
import com.gzligo.ebizzcardstranslator.persistence.City;
import com.gzligo.ebizzcardstranslator.persistence.Country;
import com.gzligo.ebizzcardstranslator.persistence.Province;
import com.gzligo.ebizzcardstranslator.utils.LanguageUtils;

import butterknife.BindView;

/**
 * Created by Lwd on 2017/7/12.
 */

public class CountryAndBankHolder extends BaseHolder {
    @BindView(R.id.country_name) TextView countryName;
    @BindView(R.id.country_code) TextView countryCode;

    private int choicePosition;

    public CountryAndBankHolder(View itemView, int choicePosition) {
        super(itemView);
        this.choicePosition = choicePosition;
    }

    @Override
    public void setData(Object data, int position) {
        String itemName = "";
        String language = LanguageUtils.getLanguage(AppManager.get().getApplication());
        int type = 0;
        if (data instanceof Bank) {
            Bank bank = (Bank) data;
            if (language.equals(TranslatorConstants.SharedPreferences.LANGUAGE_CH)) {
                itemName = bank.getZh_name();
            }else if (language.equals(TranslatorConstants.SharedPreferences.LANGUAGE_EN)){
                itemName = bank.getEn_name();
            }
            type = bank.getType_id();
        }
        if (data instanceof Country) {
            Country country = (Country) data;
            if (language.equals(TranslatorConstants.SharedPreferences.LANGUAGE_CH)) {
                itemName = country.getZh_name();
            }else if (language.equals(TranslatorConstants.SharedPreferences.LANGUAGE_EN)){
                itemName = country.getEn_name();
            }
            type = country.getState_id();
        }
        if (data instanceof Province) {
            Province province = (Province) data;
            if (language.equals(TranslatorConstants.SharedPreferences.LANGUAGE_CH)) {
                itemName = province.getZh_name();
            }else if (language.equals(TranslatorConstants.SharedPreferences.LANGUAGE_EN)){
                itemName = province.getEn_name();
            }
            type = province.getProv_id();
        }
        if (data instanceof City) {
            City city = (City) data;
            if (language.equals(TranslatorConstants.SharedPreferences.LANGUAGE_CH)) {
                itemName = city.getZh_name();
            }else if (language.equals(TranslatorConstants.SharedPreferences.LANGUAGE_EN)){
                itemName = city.getEn_name();
            }
            type = city.getCity_id();
        }
        if (data instanceof BankPoint) {
            BankPoint bankPoint = (BankPoint) data;
            if (language.equals(TranslatorConstants.SharedPreferences.LANGUAGE_CH)) {
                itemName = bankPoint.getZh_name();
            }else if (language.equals(TranslatorConstants.SharedPreferences.LANGUAGE_EN)){
                itemName = bankPoint.getEn_name();
            }
            type = bankPoint.getBank_id();
        }
        if (choicePosition == type) {
            Drawable drawable = AppManager.get().getApplication().getResources().getDrawable(R.mipmap.language_selected);
            drawable.setBounds(0, 0, 36, 36);
            countryCode.setCompoundDrawables(drawable, null, null, null);
        } else {
            countryCode.setCompoundDrawables(null, null, null, null);
        }
        countryName.setText(itemName);
    }

    @Override
    public void onRelease() {

    }
}
