package com.gzligo.ebizzcardstranslator.business.account.view.login;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.common.ToolActionBar;
import com.gzligo.ebizzcardstranslator.persistence.District;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/5/27.
 */

public class CountrySelectActivity extends BaseActivity<CountrySelectPresenter> {
    private static final int GET_DISTRICT_LIST = 0x60;
    private List<District> mDataLis;
    private List<District> searchList;
    private CountrySelectAdapter countrySelectAdapter;
    @BindView(R.id.search_country_ed) EditText searchCountryEd;
    @BindView(R.id.register_success_actionbar) ToolActionBar registerSuccessActionbar;
    @BindView(R.id.country_search_iv) ImageView countrySearchIv;
    @BindView(R.id.country_list) RecyclerView countryList;

    @Override
    public CountrySelectPresenter createPresenter() {
        return new CountrySelectPresenter(new CountrySelectRepository());
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_country_select;
    }

    @Override
    public void initViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        countryList.setLayoutManager(layoutManager);
    }

    @Override
    public void initData() {
        if (null == mDataLis) {
            mDataLis = new ArrayList<>();
        }
        getPresenter().getDistrict(Message.obtain(this, new Context[]{this}));
        countrySelectAdapter = new CountrySelectAdapter(mDataLis);
        countryList.setAdapter(countrySelectAdapter);
    }

    @Override
    public void initEvents() {
        onClickItem();
        searchCountryEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(null==searchList){
                    searchList = new ArrayList<>();
                }
                searchList.clear();
                for (District mDataLi : mDataLis) {
                    if (mDataLi.getAreaNumber().contains(charSequence) || mDataLi.getLocalName().contains(charSequence)) {
                        searchList.add(mDataLi);
                    }
                }
                countrySelectAdapter = new CountrySelectAdapter(searchList);
                countryList.setAdapter(countrySelectAdapter);
                onClickItem();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @OnClick(R.id.tv_close)
    public void onClick() {
        finish();
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what) {
            case GET_DISTRICT_LIST:
                mDataLis.addAll(getPresenter().getDistrictList());
                countrySelectAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void onClickItem(){
        countrySelectAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener<District>() {
            @Override
            public void onItemClick(View view, int viewType, District data, int position) {
                Intent intent = new Intent();
                intent.putExtra("DISTRICT", data);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
