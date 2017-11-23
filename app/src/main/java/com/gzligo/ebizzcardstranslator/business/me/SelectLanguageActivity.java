package com.gzligo.ebizzcardstranslator.business.me;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.IPresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.persistence.LanguageSelectBean;
import com.gzligo.ebizzcardstranslator.persistence.LanguageSkillBean;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.utils.LanguageUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/5/31.
 */

public class SelectLanguageActivity extends BaseActivity<PersonalPresenter> {

    @BindView(R.id.language_select_rcylist)
    RecyclerView mLanguageRecycler;
    @BindView(R.id.language_next)
    RelativeLayout mSelectedRl;
    @BindView(R.id.language_selected_txt)
    TextView mSelectNum;

    private List<LanguageSkillBean> mLanguageList;
    private ApplyLanguageUnconfirmedAdapter mUnconfirmedAdapter;
    private String selectLanguage;
    private TreeMap<Integer,LanguagesBean> beanTreeMap;
    private Gson gson;

    @Override
    public PersonalPresenter createPresenter() {
        return new PersonalPresenter(new PersonalRepository());
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_select_language;
    }

    @Override
    public void initData() {
        gson = new Gson();
        mLanguageList = new ArrayList<>();
        if (!TextUtils.isEmpty(getIntent().getStringExtra(TranslatorConstants.Common.DATA))){
            mLanguageList = gson.fromJson(getIntent().getStringExtra(TranslatorConstants.Common.DATA), new TypeToken<List<LanguageSkillBean>>(){}.getType());
            for (LanguageSkillBean skillBean : mLanguageList) {
                skillBean.setStatus(1);
            }
        }
        beanTreeMap = getPresenter().getLanguageList();
        List<LanguageSkillBean> languageSelectBeen = new ArrayList<>();
        for (Map.Entry<Integer,LanguagesBean> entry:beanTreeMap.entrySet()){
            LanguageSkillBean languageBean = new LanguageSkillBean();
            languageBean.setLanguage_id(entry.getKey());
            if (mLanguageList.size()!=0&&languageSelectBeen.size()<mLanguageList.size()){
                for (LanguageSkillBean skillBean : mLanguageList) {
                    if (entry.getKey()==skillBean.getLanguage_id()){
                        languageBean.setStatus(1);
                        languageSelectBeen.add(skillBean);
                        break;
                    }
                }
                continue;
            }
            languageSelectBeen.add(languageBean);
        }
        mUnconfirmedAdapter = new ApplyLanguageUnconfirmedAdapter(languageSelectBeen,beanTreeMap,0);
        setUnconfirmedAdapterOnClick();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mLanguageRecycler.setLayoutManager(layoutManager);
        mLanguageRecycler.setAdapter(mUnconfirmedAdapter);
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initEvents() {

    }

    @OnClick({R.id.tv_close,R.id.language_next_btn})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_close:
                finish();
                break;
            case R.id.language_next_btn:
                setIntentResult();
                finish();
                break;
        }
    }

    private void setUnconfirmedAdapterOnClick() {
        mUnconfirmedAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int viewType, Object data, int position) {
                ImageView checkedIv = (ImageView) view.findViewById(R.id.apply_language_unconfirmed_iv);
                if (!mLanguageList.contains((LanguageSkillBean)data)) {
                    checkedIv.setBackgroundResource(R.mipmap.apply_language_unconfirmed_selected);
                    mLanguageList.add((LanguageSkillBean) data);
                }else {
                    checkedIv.setBackgroundResource(R.mipmap.apply_language_unconfirmed_nomarl);
                    mLanguageList.remove((LanguageSkillBean) data);
                }
                if (mLanguageList.size()==0){
                    mSelectedRl.setVisibility(View.GONE);
                }else {
                    mSelectedRl.setVisibility(View.VISIBLE);
                    mSelectNum.setText(getResources().getString(R.string.selected, String.valueOf(mLanguageList.size())));
                }
            }
        });
    }

    private void setIntentResult() {
        List<Integer> jsonList = new ArrayList<>();
        for (LanguageSkillBean languageSelectBean : mLanguageList) {
            if (selectLanguage!=null) {
                if (LanguageUtils.getLanguage(this).equals(TranslatorConstants.SharedPreferences.LANGUAGE_CH)) {
                    selectLanguage += " " + beanTreeMap.get(languageSelectBean.getLanguage_id()).getZh_name();
                }else if (LanguageUtils.getLanguage(this).equals(TranslatorConstants.SharedPreferences.LANGUAGE_EN)) {
                    selectLanguage += " " + beanTreeMap.get(languageSelectBean.getLanguage_id()).getEn_name();
                }
            }else {
                if (LanguageUtils.getLanguage(this).equals(TranslatorConstants.SharedPreferences.LANGUAGE_CH)) {
                    selectLanguage = beanTreeMap.get(languageSelectBean.getLanguage_id()).getZh_name();
                }else if (LanguageUtils.getLanguage(this).equals(TranslatorConstants.SharedPreferences.LANGUAGE_EN)) {
                    selectLanguage = beanTreeMap.get(languageSelectBean.getLanguage_id()).getEn_name();
                }
            }
            jsonList.add(languageSelectBean.getLanguage_id());
        }
        String languageJson = gson.toJson(jsonList);
        String mLanguageListStr = gson.toJson(mLanguageList);
        Intent intent = new Intent(this,PersonalBaseMessageActivity.class);
        intent.putExtra(TranslatorConstants.Common.DATA,selectLanguage);
        intent.putExtra("language_list",mLanguageListStr);
        intent.putExtra(TranslatorConstants.Common.JSON,languageJson);
        setResult(RESULT_OK,intent);
    }
}
