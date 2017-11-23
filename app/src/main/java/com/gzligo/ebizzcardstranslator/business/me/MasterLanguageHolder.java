package com.gzligo.ebizzcardstranslator.business.me;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.common.TxtSwitch;
import com.gzligo.ebizzcardstranslator.persistence.LanguageSkillBean;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.utils.DialogUtils;
import com.gzligo.ebizzcardstranslator.utils.LanguageUtils;
import com.gzligo.ebizzcardstranslator.utils.TranslatorCallBack;

import java.util.TreeMap;

import butterknife.BindView;

/**
 * Created by Lwd on 2017/7/6.
 */

public class MasterLanguageHolder extends BaseHolder<LanguageSkillBean>{
    @BindView(R.id.language_item_rl) RelativeLayout languageItemRl;
    @BindView(R.id.language_txt_status) TxtSwitch languageTxtStatus;

    private TreeMap<Integer,LanguagesBean> beanTreeMap;
    private static final int CLOSE_OR_OPEN_LANGUAGE = 0x40;
    private IView iView;
    private View itemView;

    public MasterLanguageHolder(View itemView,TreeMap<Integer,LanguagesBean> beanTreeMap,IView iView) {
        super(itemView);
        this.beanTreeMap = beanTreeMap;
        this.iView = iView;
        this.itemView = itemView;
    }

    @Override
    public void setData(final LanguageSkillBean data, final int position) {
        if(position==0){
            languageItemRl.setVisibility(View.VISIBLE);
        }else{
            languageItemRl.setVisibility(View.GONE);
        }
        final int language = data.getLanguage_id();
        String languageStr = null;
        if (LanguageUtils.getLanguage(AppManager.get().getTopActivity()).equals(TranslatorConstants.SharedPreferences.LANGUAGE_CH)) {
            languageStr = beanTreeMap.get(language).getZh_name();
        }else if (LanguageUtils.getLanguage(AppManager.get().getTopActivity()).equals(TranslatorConstants.SharedPreferences.LANGUAGE_EN)) {
            languageStr = beanTreeMap.get(language).getEn_name();
        }
        Context context = AppManager.get().getApplication();
        switch (data.getLevel()){
            case 1:
                languageStr = languageStr + context.getResources().getString(R.string.commonly);
                break;
            case 2:
                languageStr = languageStr + context.getResources().getString(R.string.skilled);
                break;
            case 3:
                languageStr = languageStr + context.getResources().getString(R.string.master);
                break;
        }
        if(data.getStatus()==0){
            languageTxtStatus.setChecked(false);
        }else{
            languageTxtStatus.setChecked(true);
        }
        languageTxtStatus.setTxtName(languageStr);
        languageTxtStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = null;
                String confirm = null;
                if (languageTxtStatus.isChecked()){
                    content = itemView.getResources().getString(R.string.master_forbid_language);
                    confirm = itemView.getResources().getString(R.string.master_forbid);
                }else {
                    content = itemView.getResources().getString(R.string.master_start_language);
                    confirm = itemView.getResources().getString(R.string.master_start);
                }
                DialogUtils.bottomContentConfirmDialog((Activity) iView,content ,confirm,
                        new TranslatorCallBack.onConfirmDialogListener() {
                            @Override
                            public void onConfirm() {
                                if (languageTxtStatus.isChecked()){
                                    languageTxtStatus.setChecked(false);
                                }else {
                                    languageTxtStatus.setChecked(true);
                                }
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
            }
        });
        languageTxtStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int status = b? 1:0;
                Message msg = Message.obtain(iView);
                msg.what = CLOSE_OR_OPEN_LANGUAGE;
                msg.objs = new Integer[]{language,status,position};
                msg.dispatchToIView();
            }
        });
    }

    @Override
    public void onRelease() {
    }
}
