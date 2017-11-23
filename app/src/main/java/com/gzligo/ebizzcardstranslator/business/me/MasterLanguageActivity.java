package com.gzligo.ebizzcardstranslator.business.me;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.persistence.LanguageSkillBean;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.UserBean;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/6/2.
 */

public class MasterLanguageActivity extends BaseActivity<PersonalPresenter> {
    @BindView(R.id.master_language_recycler) RecyclerView masterLanguageRecycler;
    @BindView(R.id.master_language_apply_iv) ImageView masterLanguageApplyIv;
    @BindView(R.id.master_language_apply_rl) RelativeLayout masterLanguageApplyRl;

    private static final int CLOSE_OR_OPEN_LANGUAGE = 0x40;
    private static final int CLOSE_OR_OPEN_LANGUAGE_ERROR = 0x41;
    private static final int CLOSE_OR_OPEN_LANGUAGE_SUCCESS = 0x42;
    private MasterLanguageAdapter masterLanguageAdapter;
    private List<LanguageSkillBean> skillBeen;
    private List<LanguageSkillBean> confirmingLanguages;
    private TreeMap<Integer,LanguagesBean> beanTreeMap;
    private int confirmingFirstPosition;

    @Override
    public PersonalPresenter createPresenter() {
        return new PersonalPresenter(new PersonalRepository());
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_master_language;
    }

    @Override
    public void initData() {
        if (null == skillBeen) {
            skillBeen = new ArrayList<>();
        }
        if (null == confirmingLanguages) {
            confirmingLanguages = new ArrayList<>();
        }
        UserBean userBean = getPresenter().getUserBean();
        List<LanguageSkillBean> skillBeanList = userBean.getLanguageskills();
        if (null != skillBeanList && skillBeanList.size() > 0) {
            getLanguageSkillList(skillBeanList);
        }
        beanTreeMap = getPresenter().getLanguageList();
        masterLanguageAdapter = new MasterLanguageAdapter(this,skillBeen,beanTreeMap,confirmingFirstPosition);
        masterLanguageRecycler.setAdapter(masterLanguageAdapter);
    }

    @Override
    public void initViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        masterLanguageRecycler.setLayoutManager(layoutManager);
    }

    @Override
    public void initEvents() {
    }

    @OnClick({R.id.tv_close, R.id.master_language_apply_rl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_close:
                finish();
                break;
            case R.id.master_language_apply_rl:
                Gson gson = new Gson();
                Intent intent = new Intent(this, ApplyLanguageActivity.class);
                intent.putExtra(TranslatorConstants.Common.DATA,gson.toJson(skillBeen));
                startActivity(intent);
                break;
        }
    }

    private void getLanguageSkillList(List<LanguageSkillBean> list) {
        for (LanguageSkillBean skillBean : list) {
            if (skillBean.getResult() == 2) {
                skillBeen.add(skillBean);
            }else {
                confirmingLanguages.add(skillBean);
            }
        }
        confirmingFirstPosition = skillBeen.size();
        if (confirmingLanguages.size()!=0){
            for (LanguageSkillBean confirmingLanguage : confirmingLanguages) {
                skillBeen.add(confirmingLanguage);
            }
        }
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what){
            case CLOSE_OR_OPEN_LANGUAGE:
                Message msg = Message.obtain(this);
                msg.objs = message.objs;
                getPresenter().requestLanguagesKillStatus(msg);
                break;
            case CLOSE_OR_OPEN_LANGUAGE_ERROR:
                masterLanguageAdapter.notifyItemChanged((int) message.objs[2]);
                break;
            case CLOSE_OR_OPEN_LANGUAGE_SUCCESS:
                int position = (int) message.objs[2];
                int status = (int) message.objs[1];
                skillBeen.get(position).setStatus(status);
                break;
        }
    }
}
