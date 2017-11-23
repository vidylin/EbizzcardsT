package com.gzligo.ebizzcardstranslator.business.me;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.persistence.LanguageSkillBean;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/6/2.
 */

public class ApplyLanguageActivity extends BaseActivity<PersonalPresenter> implements IView{

    private String languagesJson = "";
    private List<LanguageSkillBean> skillBeen;
    private TreeMap<Integer,LanguagesBean> beanTreeMap;
    private ApplyLanguagePassedAdapter mPassedAdapter;
    private TreeMap<Integer,LanguagesBean> leftBeanTreeMap;
    private List<LanguageSkillBean> mSelectedList;
    private static final int UNCONFIRMED_LANGUAGE = 0x03;

    @BindView(R.id.master_language_passed_recycler) RecyclerView mPassedRecyclerView;
    @BindView(R.id.apply_language_next_rl) RelativeLayout mSelectedRl;
    @BindView(R.id.apply_language_selected_txt) TextView mSelectedTv;

    @Override
    public PersonalPresenter createPresenter() {
        return new PersonalPresenter(new PersonalRepository());
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_apply_language;
    }

    @Override
    public void initData() {
        Gson gson = new Gson();
        skillBeen = gson.fromJson(getIntent().getStringExtra(TranslatorConstants.Common.DATA), new TypeToken<List<LanguageSkillBean>>(){}.getType());
        beanTreeMap = getPresenter().getLanguageList();
        leftBeanTreeMap = new TreeMap<>();
        for (Map.Entry<Integer,LanguagesBean> entry:beanTreeMap.entrySet()){
            leftBeanTreeMap.put(entry.getKey(),entry.getValue());
        }
        mSelectedList = new ArrayList<>();
    }

    @Override
    public void initViews() {
        mSelectedRl.setVisibility(View.GONE);
        int titlePosition;
        if (skillBeen==null||skillBeen.size()==0) {
            titlePosition = 0;
        }else {
            List<LanguageSkillBean> passedLanguages = new ArrayList<>();
            for (LanguageSkillBean languageSkillBean : skillBeen) {
                leftBeanTreeMap.remove(languageSkillBean.getLanguage_id());
                if (languageSkillBean.getResult()==2) {
                    passedLanguages.add(languageSkillBean);
                }
            }
            skillBeen.clear();
            skillBeen.addAll(passedLanguages);
            titlePosition = passedLanguages.size();
        }
        for (Map.Entry<Integer,LanguagesBean> entry:leftBeanTreeMap.entrySet()){
            LanguageSkillBean bean = new LanguageSkillBean();
            bean.setLanguage_id(entry.getKey());
            bean.setResult(0);
            skillBeen.add(bean);
        }
        mPassedAdapter = new ApplyLanguagePassedAdapter(skillBeen,beanTreeMap,titlePosition);
        setUnconfirmedAdapterOnClick();
        mPassedRecyclerView.setAdapter(mPassedAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mPassedRecyclerView.setLayoutManager(layoutManager);
    }

    private void setUnconfirmedAdapterOnClick() {
        mPassedAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int viewType, Object data, int position) {
                if (viewType == UNCONFIRMED_LANGUAGE) {
                    ImageView checkedIv = (ImageView) view.findViewById(R.id.apply_language_unconfirmed_iv);
                    if (!mSelectedList.contains((LanguageSkillBean) data)) {
                        checkedIv.setBackgroundResource(R.mipmap.apply_language_unconfirmed_selected);
                        mSelectedList.add((LanguageSkillBean) data);
                        ((LanguageSkillBean) data).setStatus(1);
                    } else {
                        checkedIv.setBackgroundResource(R.mipmap.apply_language_unconfirmed_nomarl);
                        mSelectedList.remove((LanguageSkillBean) data);
                        ((LanguageSkillBean) data).setStatus(0);
                    }
                    if (mSelectedList.size() == 0) {
                        mSelectedRl.setVisibility(View.GONE);
                    } else {
                        mSelectedRl.setVisibility(View.VISIBLE);
                        mSelectedTv.setText(getResources().getString(R.string.selected, String.valueOf(mSelectedList.size())));
                    }
                }
            }
        });
    }

    @Override
    public void initEvents() {

    }

    @OnClick({R.id.tv_close,R.id.apply_language_next_btn})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_close:
                finish();
                break;
            case R.id.apply_language_next_btn:
                Intent intent = new Intent(this,UploadCertificateActivity.class);
                intent.putExtra(TranslatorConstants.Common.FROM,"apply");
                List<Integer> languageIdList = new ArrayList<>();
                for (LanguageSkillBean languageSelectBean : mSelectedList) {
                    languageIdList.add(languageSelectBean.getLanguage_id());
                }
                Gson gson = new Gson();
                languagesJson = gson.toJson(languageIdList);
                intent.putExtra(TranslatorConstants.Common.JSON,languagesJson);
                startActivity(intent);
                finish();
                break;
        }
    }
}
