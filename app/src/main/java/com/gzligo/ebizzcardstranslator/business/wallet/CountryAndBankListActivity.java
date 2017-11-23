package com.gzligo.ebizzcardstranslator.business.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.common.ToolActionBar;
import com.gzligo.ebizzcardstranslator.persistence.Bank;
import com.gzligo.ebizzcardstranslator.persistence.BankPoint;
import com.gzligo.ebizzcardstranslator.persistence.City;
import com.gzligo.ebizzcardstranslator.persistence.Country;
import com.gzligo.ebizzcardstranslator.persistence.Province;
import com.gzligo.ebizzcardstranslator.utils.LanguageUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Lwd on 2017/7/12.
 */

public class CountryAndBankListActivity extends BaseActivity<CountryAndBankListPresenter> {

    @BindView(R.id.register_success_actionbar) ToolActionBar registerSuccessActionbar;
    @BindView(R.id.country_search_iv) ImageView countrySearchIv;
    @BindView(R.id.search_country_ed) EditText searchCountryEd;
    @BindView(R.id.country_list) RecyclerView countryList;

    private static final int GET_BANK_LIST = 0x89;
    private static final int GET_PROVINCE_LIST = 0x88;
    private static final int GET_CITY_LIST = 0x87;
    private static final int GET_BANK_POINT_LIST = 0x86;
    private CountryAndBankAdapter countryAndBankAdapter;
    private String to;
    private List<Country> list = new ArrayList<>();
    private List<Bank> bankList = new ArrayList<>();
    private List<Province> provinceList = new ArrayList<>();
    private List<City> cityList = new ArrayList<>();
    private List<BankPoint> bankPointList = new ArrayList<>();
    private int choicePosition;

    @Override
    public CountryAndBankListPresenter createPresenter() {
        return new CountryAndBankListPresenter(new CountryAndBankListRepository(), this);
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_country_select;
    }

    @Override
    public void initData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        countryList.setLayoutManager(layoutManager);
        to = getIntent().getExtras().getString("TO");
        choicePosition = getIntent().getExtras().getInt("choicePosition");
        switch (to) {
            case "BANK":
                getPresenter().requestGetBankType();
                break;
            case "COUNTRY":
                List<Country> countries = (List<Country>) getIntent().getExtras().getSerializable("countryList");
                list.addAll(countries);
                if (null == countryAndBankAdapter) {
                    countryAndBankAdapter = new CountryAndBankAdapter(list, choicePosition);
                    countryList.setAdapter(countryAndBankAdapter);
                }
                break;
            case "PROVINCE":
                int stateId = getIntent().getExtras().getInt("stateId");
                getPresenter().requestAddOrderGetState(stateId + "");
                break;
            case "CITY":
                int provId = getIntent().getExtras().getInt("provId");
                getPresenter().requestGetCity(provId + "");
                break;
            case "BANK_POINT":
                int cityId = getIntent().getExtras().getInt("cityId");
                int bankTypeId = getIntent().getExtras().getInt("bankTypeId");
                getPresenter().requestGetBank(bankTypeId + "", cityId + "");
                break;
        }
    }

    @Override
    public void initViews() {
        switch (to) {
            case "BANK":
                registerSuccessActionbar.setCenterTitle(getResources().getString(R.string.choice_bank));
                break;
            case "COUNTRY":
                registerSuccessActionbar.setCenterTitle(getResources().getString(R.string.choice_country));
                break;
            case "PROVINCE":
                registerSuccessActionbar.setCenterTitle(getResources().getString(R.string.choice_province));
                break;
            case "CITY":
                registerSuccessActionbar.setCenterTitle(getResources().getString(R.string.choice_city));
                break;
            case "BANK_POINT":
                registerSuccessActionbar.setCenterTitle(getResources().getString(R.string.choice_bank_point));
                break;
        }
    }

    @Override
    public void initEvents() {
        if (null != countryAndBankAdapter) {
            onClickItem();
        }
        searchCountryEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchContent = charSequence.toString().trim();
                search(searchContent);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @OnClick({R.id.tv_close})
    void click(View view) {
        switch (view.getId()) {
            case R.id.tv_close:
                finish();
                break;
        }
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what) {
            case GET_BANK_LIST:
                List<Bank> banks = (List<Bank>) message.obj;
                if(null!=banks&&banks.size()>0){
                    bankList.addAll(banks);
                }
                if (null == countryAndBankAdapter) {
                    countryAndBankAdapter = new CountryAndBankAdapter(bankList, choicePosition);
                    countryList.setAdapter(countryAndBankAdapter);
                    onClickItem();
                }
                break;
            case GET_PROVINCE_LIST:
                List<Province> provinces = (List<Province>) message.obj;
                if(null!=provinces&&provinces.size()>0){
                    provinceList.addAll(provinces);
                }
                if (null == countryAndBankAdapter) {
                    countryAndBankAdapter = new CountryAndBankAdapter(provinceList, choicePosition);
                    countryList.setAdapter(countryAndBankAdapter);
                    onClickItem();
                }
                break;
            case GET_CITY_LIST:
                List<City> cities = (List<City>) message.obj;
                if(null!=cities&&cities.size()>0){
                    cityList.addAll(cities);
                }
                if (null == countryAndBankAdapter) {
                    countryAndBankAdapter = new CountryAndBankAdapter(cityList, choicePosition);
                    countryList.setAdapter(countryAndBankAdapter);
                    onClickItem();
                }
                break;
            case GET_BANK_POINT_LIST:
                List<BankPoint> bankPoints = (List<BankPoint>) message.obj;
                if(null!=bankPoints&&bankPoints.size()>0){
                    bankPointList.addAll(bankPoints);
                }
                if (null == countryAndBankAdapter) {
                    countryAndBankAdapter = new CountryAndBankAdapter(bankPointList, choicePosition);
                    countryList.setAdapter(countryAndBankAdapter);
                    onClickItem();
                }
                break;
        }
    }

    private void onClickItem() {
        countryAndBankAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int viewType, Object data, int position) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                if (data instanceof Bank) {
                    bundle.putSerializable("DATA", (Bank) data);
                    intent.putExtra("POSITION", ((Bank) data).getType_id());
                }
                if (data instanceof Country) {
                    bundle.putSerializable("DATA", (Country) data);
                    intent.putExtra("POSITION", ((Country) data).getState_id());
                }
                if (data instanceof Province) {
                    bundle.putSerializable("DATA", (Province) data);
                    intent.putExtra("POSITION", ((Province) data).getProv_id());
                }
                if (data instanceof City) {
                    bundle.putSerializable("DATA", (City) data);
                    intent.putExtra("POSITION", ((City) data).getCity_id());
                }
                if (data instanceof BankPoint) {
                    bundle.putSerializable("DATA", (BankPoint) data);
                    intent.putExtra("POSITION", ((BankPoint) data).getBank_id());
                }
                intent.putExtra("BUNDLE", bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void search(String searchContent){
        String language = LanguageUtils.getLanguage(AppManager.get().getTopActivity());
        switch (to){
            case "BANK":
                if(bankList.size()>0){
                    if(!TextUtils.isEmpty(searchContent)){
                        List<Bank> searchList = new ArrayList<>();
                        if (language.equals(TranslatorConstants.SharedPreferences.LANGUAGE_CH)) {
                            for(Bank bank : bankList){
                                if (bank.getZh_name().contains(searchContent)) {
                                    searchList.add(bank);
                                }
                            }
                        }else if (language.equals(TranslatorConstants.SharedPreferences.LANGUAGE_EN)){
                            for(Bank bank : bankList){
                                if (bank.getEn_name().contains(searchContent)) {
                                    searchList.add(bank);
                                }
                            }
                        }
                        countryAndBankAdapter = new CountryAndBankAdapter(searchList, choicePosition);
                        countryList.setAdapter(countryAndBankAdapter);
                        onClickItem();
                    }else{
                        countryAndBankAdapter = new CountryAndBankAdapter(bankList, choicePosition);
                        countryList.setAdapter(countryAndBankAdapter);
                        onClickItem();
                    }
                }
                break;
            case "COUNTRY":
                if(list.size()>0){
                    if(!TextUtils.isEmpty(searchContent)){
                        List<Country> searchList = new ArrayList<>();
                        if (language.equals(TranslatorConstants.SharedPreferences.LANGUAGE_CH)) {
                            for(Country country : list){
                                if (country.getZh_name().contains(searchContent)) {
                                    searchList.add(country);
                                }
                            }
                        }else if (language.equals(TranslatorConstants.SharedPreferences.LANGUAGE_EN)){
                            for(Country country : list){
                                if (country.getEn_name().contains(searchContent)) {
                                    searchList.add(country);
                                }
                            }
                        }
                        countryAndBankAdapter = new CountryAndBankAdapter(searchList, choicePosition);
                        countryList.setAdapter(countryAndBankAdapter);
                        onClickItem();
                    }else{
                        countryAndBankAdapter = new CountryAndBankAdapter(list, choicePosition);
                        countryList.setAdapter(countryAndBankAdapter);
                        onClickItem();
                    }
                }
                break;
            case "PROVINCE":
                if(provinceList.size()>0){
                    if(!TextUtils.isEmpty(searchContent)){
                        List<Province> searchList = new ArrayList<>();
                        if (language.equals(TranslatorConstants.SharedPreferences.LANGUAGE_CH)) {
                            for(Province province : provinceList){
                                if (province.getZh_name().contains(searchContent)) {
                                    searchList.add(province);
                                }
                            }
                        }else if (language.equals(TranslatorConstants.SharedPreferences.LANGUAGE_EN)){
                            for(Province province : provinceList){
                                if (province.getEn_name().contains(searchContent)) {
                                    searchList.add(province);
                                }
                            }
                        }
                        countryAndBankAdapter = new CountryAndBankAdapter(searchList, choicePosition);
                        countryList.setAdapter(countryAndBankAdapter);
                        onClickItem();
                    }else{
                        countryAndBankAdapter = new CountryAndBankAdapter(provinceList, choicePosition);
                        countryList.setAdapter(countryAndBankAdapter);
                        onClickItem();
                    }
                }
                break;
            case "CITY":
                if(cityList.size()>0){
                    if(!TextUtils.isEmpty(searchContent)){
                        List<City> searchList = new ArrayList<>();
                        if (language.equals(TranslatorConstants.SharedPreferences.LANGUAGE_CH)) {
                            for(City city : cityList){
                                if (city.getZh_name().contains(searchContent)) {
                                    searchList.add(city);
                                }
                            }
                        }else if (language.equals(TranslatorConstants.SharedPreferences.LANGUAGE_EN)){
                            for(City city : cityList){
                                if (city.getEn_name().contains(searchContent)) {
                                    searchList.add(city);
                                }
                            }
                        }
                        countryAndBankAdapter = new CountryAndBankAdapter(searchList, choicePosition);
                        countryList.setAdapter(countryAndBankAdapter);
                        onClickItem();
                    }else{
                        countryAndBankAdapter = new CountryAndBankAdapter(cityList, choicePosition);
                        countryList.setAdapter(countryAndBankAdapter);
                        onClickItem();
                    }
                }
                break;
            case "BANK_POINT":
                if(!TextUtils.isEmpty(searchContent)){
                    if(bankPointList.size()>0){
                        List<BankPoint> searchList = new ArrayList<>();
                        if (language.equals(TranslatorConstants.SharedPreferences.LANGUAGE_CH)) {
                            for(BankPoint bankPoint : bankPointList){
                                if (bankPoint.getZh_name().contains(searchContent)) {
                                    searchList.add(bankPoint);
                                }
                            }
                        }else if (language.equals(TranslatorConstants.SharedPreferences.LANGUAGE_EN)){
                            for(BankPoint bankPoint : bankPointList){
                                if (bankPoint.getEn_name().contains(searchContent)) {
                                    searchList.add(bankPoint);
                                }
                            }
                        }
                        countryAndBankAdapter = new CountryAndBankAdapter(searchList, choicePosition);
                        countryList.setAdapter(countryAndBankAdapter);
                        onClickItem();
                    }
                }else{
                    countryAndBankAdapter = new CountryAndBankAdapter(bankPointList, choicePosition);
                    countryList.setAdapter(countryAndBankAdapter);
                    onClickItem();
                }
                break;
        }
    }

}
