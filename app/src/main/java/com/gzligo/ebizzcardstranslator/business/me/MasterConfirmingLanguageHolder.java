package com.gzligo.ebizzcardstranslator.business.me;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.common.TxtSwitch;
import com.gzligo.ebizzcardstranslator.persistence.LanguageSkillBean;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.utils.LanguageUtils;

import java.util.TreeMap;

import butterknife.BindView;

/**
 * Created by Lwd on 2017/7/6.
 */

public class MasterConfirmingLanguageHolder extends BaseHolder<LanguageSkillBean>{
    @BindView(R.id.language_item_rl) RelativeLayout languageItemRl;
    @BindView(R.id.language_name_txt) TextView languageNameTxt;

    private TreeMap<Integer,LanguagesBean> beanTreeMap;
    private int firstPosition;

    public MasterConfirmingLanguageHolder(View itemView, TreeMap<Integer,LanguagesBean> beanTreeMap,int firstPosition) {
        super(itemView);
        this.beanTreeMap = beanTreeMap;
        this.firstPosition = firstPosition;
    }

    @Override
    public void setData(final LanguageSkillBean data, final int position) {
        if(position==firstPosition){
            languageItemRl.setVisibility(View.VISIBLE);
        }else{
            languageItemRl.setVisibility(View.GONE);
        }
        final int language = data.getLanguage_id();
        if (LanguageUtils.getLanguage(AppManager.get().getTopActivity()).equals(TranslatorConstants.SharedPreferences.LANGUAGE_CH)) {
            languageNameTxt.setText(beanTreeMap.get(language).getZh_name());
        }else if (LanguageUtils.getLanguage(AppManager.get().getTopActivity()).equals(TranslatorConstants.SharedPreferences.LANGUAGE_EN)){
            languageNameTxt.setText(beanTreeMap.get(language).getEn_name());
        }
    }

    @Override
    public void onRelease() {
    }
}
