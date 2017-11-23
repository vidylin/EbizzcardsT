package com.gzligo.ebizzcardstranslator.business.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.IPresenter;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.manager.CommonBeanManager;
import com.gzligo.ebizzcardstranslator.persistence.BankPoint;
import com.gzligo.ebizzcardstranslator.persistence.City;
import com.gzligo.ebizzcardstranslator.persistence.Province;
import com.gzligo.ebizzcardstranslator.utils.LanguageUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class OpeningPointActivity extends BaseActivity {

    @BindView(R.id.province_tv) TextView provinceTv;
    @BindView(R.id.city_tv) TextView cityTv;
    @BindView(R.id.point_tv) TextView pointTv;
    @BindView(R.id.confirm_bank_btn) Button confirmBankBtn;
    @BindView(R.id.add_province_rl) RelativeLayout addProvinceRl;
    @BindView(R.id.add_city_rl) RelativeLayout addCityRl;
    @BindView(R.id.add_point_rl) RelativeLayout addPointRl;

    private static final int RESULT_PROVINCE = 0x18;
    private static final int RESULT_CITY = 0x17;
    private static final int RESULT_POINT_BANK = 0x16;
    private int choiceProvincePosition;
    private int choiceCityPosition;
    private int choicePointBankPosition;
    private int stateId;
    private int provId = -1;
    private int bankTypeId;
    private int cityId = -1;
    private int bankId = -1;
    private String openingPoint;
    private String currentLanguage;

    @Override
    public IPresenter createPresenter() {
        return null;
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_opening_point;
    }

    @Override
    public void initData() {
        stateId = getIntent().getExtras().getInt("stateId");
        bankTypeId = getIntent().getExtras().getInt("bankTypeId");
        currentLanguage = LanguageUtils.getLanguage(this);
    }

    @Override
    public void initViews() {
        Province province = CommonBeanManager.getInstance().getProvince();
        City city = CommonBeanManager.getInstance().getCity();
        BankPoint bankPoint = CommonBeanManager.getInstance().getBankPoint();
        if(null!=province){
            provId = province.getProv_id();
            choiceProvincePosition = provId;
            if (TranslatorConstants.SharedPreferences.LANGUAGE_CH.equals(currentLanguage)) {
                provinceTv.setText(province.getZh_name());
            }else if (TranslatorConstants.SharedPreferences.LANGUAGE_EN.equals(currentLanguage)){
                provinceTv.setText(province.getEn_name());
            }
            if(null!=city){
                cityId = city.getCity_id();
                choiceCityPosition = cityId;
                if (TranslatorConstants.SharedPreferences.LANGUAGE_CH.equals(currentLanguage)) {
                    cityTv.setText(city.getZh_name());
                }else if (TranslatorConstants.SharedPreferences.LANGUAGE_EN.equals(currentLanguage)) {
                    cityTv.setText(city.getEn_name());
                }
                if(null!=bankPoint){
                    bankId = bankPoint.getBank_id();
                    choicePointBankPosition = bankId;
                    if (TranslatorConstants.SharedPreferences.LANGUAGE_CH.equals(currentLanguage)) {
                        pointTv.setText(bankPoint.getZh_name());
                    }else if (TranslatorConstants.SharedPreferences.LANGUAGE_EN.equals(currentLanguage)) {
                        pointTv.setText(bankPoint.getEn_name());
                    }
                    confirmBankBtn.setBackgroundResource(R.drawable.onclick_green_btn_selector);
                    confirmBankBtn.setClickable(true);
                }
            }
        }
    }

    @Override
    public void initEvents() {
        provinceTv.addTextChangedListener(watcher);
        cityTv.addTextChangedListener(watcher);
        pointTv.addTextChangedListener(watcher);
        confirmBankBtn.setClickable(false);
    }

    @OnClick({R.id.add_province_rl, R.id.add_city_rl, R.id.add_point_rl, R.id.confirm_bank_btn, R.id.tv_close})
    void click(View view) {
        switch (view.getId()) {
            case R.id.add_province_rl:
                startActivityForResult(new Intent(this, CountryAndBankListActivity.class)
                        .putExtra("TO", "PROVINCE")
                        .putExtra("stateId", stateId)
                        .putExtra("choicePosition", choiceProvincePosition), RESULT_PROVINCE);
                break;
            case R.id.add_city_rl:
                if (provId == -1) {
                    Toast.makeText(this, getResources().getString(R.string.province_name_hint), Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivityForResult(new Intent(this, CountryAndBankListActivity.class)
                        .putExtra("TO", "CITY")
                        .putExtra("provId", provId)
                        .putExtra("choicePosition", choiceCityPosition), RESULT_CITY);
                break;
            case R.id.add_point_rl:
                if (provId == -1) {
                    Toast.makeText(this, getResources().getString(R.string.province_name_hint), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cityId == -1) {
                    Toast.makeText(this, getResources().getString(R.string.city_name_hint), Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivityForResult(new Intent(this, CountryAndBankListActivity.class)
                        .putExtra("TO", "BANK_POINT")
                        .putExtra("bankTypeId", bankTypeId)
                        .putExtra("cityId", cityId)
                        .putExtra("choicePosition", choicePointBankPosition), RESULT_POINT_BANK);
                break;
            case R.id.confirm_bank_btn:
                if(bankId==-1){
                    return;
                }
                openingPoint = provinceTv.getText().toString()+" "+cityTv.getText().toString()+" "+pointTv.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("bankId", bankId);
                intent.putExtra("openingPoint", openingPoint.toString());
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.tv_close:
                finish();
                break;
        }
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s)) {
                if (!TextUtils.isEmpty(provinceTv.getText().toString().trim()) &&
                        !TextUtils.isEmpty(cityTv.getText().toString().trim())
                        && !TextUtils.isEmpty(pointTv.getText().toString().trim())) {
                    confirmBankBtn.setBackgroundResource(R.drawable.onclick_green_btn_selector);
                    confirmBankBtn.setClickable(true);
                } else {
                    confirmBankBtn.setBackgroundResource(R.mipmap.green_btn_normal);
                    confirmBankBtn.setClickable(false);
                }
            } else {
                confirmBankBtn.setBackgroundResource(R.mipmap.green_btn_normal);
                confirmBankBtn.setClickable(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case RESULT_PROVINCE:
                    int choice = data.getExtras().getInt("POSITION");
                    if(choiceProvincePosition!=choice){
                        cityId = -1;
                        bankId = -1;
                        cityTv.setText("");
                        pointTv.setText("");
                        choiceProvincePosition = choice;
                        choiceCityPosition = -1;
                        choicePointBankPosition = -1;
                    }
                    Bundle bundle = data.getBundleExtra("BUNDLE");
                    Province province = (Province) bundle.get("DATA");
                    CommonBeanManager.getInstance().setProvince(province);
                    provId = province.getProv_id();
                    if (TranslatorConstants.SharedPreferences.LANGUAGE_CH.equals(currentLanguage)) {
                        provinceTv.setText(province.getZh_name());
                    }else if (TranslatorConstants.SharedPreferences.LANGUAGE_EN.equals(currentLanguage)){
                        provinceTv.setText(province.getEn_name());
                    }
                    break;
                case RESULT_CITY:
                    int choiceCity = data.getExtras().getInt("POSITION");
                    if(choiceCity!=choiceCityPosition){
                        bankId = -1;
                        pointTv.setText("");
                        choiceCityPosition = choiceCity;
                        choicePointBankPosition = -1;
                    }
                    bundle = data.getBundleExtra("BUNDLE");
                    City city = (City) bundle.get("DATA");
                    cityId = city.getCity_id();
                    CommonBeanManager.getInstance().setCity(city);
                    if (TranslatorConstants.SharedPreferences.LANGUAGE_CH.equals(currentLanguage)) {
                        cityTv.setText(city.getZh_name());
                    }else if (TranslatorConstants.SharedPreferences.LANGUAGE_EN.equals(currentLanguage)) {
                        cityTv.setText(city.getEn_name());
                    }
                    break;
                case RESULT_POINT_BANK:
                    choicePointBankPosition = data.getExtras().getInt("POSITION");
                    bundle = data.getBundleExtra("BUNDLE");
                    BankPoint bankPoint = (BankPoint) bundle.get("DATA");
                    bankId = bankPoint.getBank_id();
                    CommonBeanManager.getInstance().setBankPoint(bankPoint);
                    if (TranslatorConstants.SharedPreferences.LANGUAGE_CH.equals(currentLanguage)) {
                        pointTv.setText(bankPoint.getZh_name());
                    }else if (TranslatorConstants.SharedPreferences.LANGUAGE_EN.equals(currentLanguage)) {
                        pointTv.setText(bankPoint.getEn_name());
                    }
                    break;
            }
        }
    }

}
