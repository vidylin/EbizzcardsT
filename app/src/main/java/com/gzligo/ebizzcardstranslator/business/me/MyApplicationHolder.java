package com.gzligo.ebizzcardstranslator.business.me;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.persistence.LanguageMyApplicationBean;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.MyApplicationBean;
import com.gzligo.ebizzcardstranslator.utils.LanguageUtils;
import com.gzligo.ebizzcardstranslator.utils.TimeUtils;

import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;

/**
 * Created by ZuoJian on 2017/8/3.
 */

public class MyApplicationHolder extends BaseHolder<MyApplicationBean> {

    @BindView(R.id.my_application_item_time_tv) TextView mTimeTxt;
    @BindView(R.id.my_application_item_name_txt) TextView mNameTxt;
    @BindView(R.id.my_application_state_tv) TextView mStateTxt;
    @BindView(R.id.my_application_personal_msg_rl) RelativeLayout mItemRl;

    private TreeMap<Integer,LanguagesBean> beanTreeMap;
    private List<MyApplicationBean> mList;
    private View itemView;

    public MyApplicationHolder(View itemView , List<MyApplicationBean> mList, TreeMap<Integer,LanguagesBean> beanTreeMap) {
        super(itemView);
        this.itemView = itemView;
        this.beanTreeMap = beanTreeMap;
        this.mList = mList;
    }

    @Override
    public void setData(MyApplicationBean data, int position) {
        if (position!=0 && TimeUtils.getYMD(mList.get(position).getCreate_time()).equals(TimeUtils.getYMD(mList.get(position-1).getCreate_time()))){
            mTimeTxt.setVisibility(View.GONE);
        }else {
            mTimeTxt.setVisibility(View.VISIBLE);
            mTimeTxt.setText(TimeUtils.getYMD(mList.get(position).getCreate_time()));
        }
        mStateTxt.setText(getResultString(data.getResult()));
        if (data.getResult()==2){
            mStateTxt.setTextColor(itemView.getResources().getColor(R.color.confirm_green));
        }else if (data.getResult()==3){
            mStateTxt.setTextColor(itemView.getResources().getColor(R.color.red));
        }else {
            mStateTxt.setTextColor(itemView.getResources().getColor(R.color.text_color_hint));
        }
        if (data.getAudit_type()==1){
            //语言认证审核
            LanguageMyApplicationBean bean = data.getLanguageskill();
            if (LanguageUtils.getLanguage(AppManager.get().getTopActivity()).equals(TranslatorConstants.SharedPreferences.LANGUAGE_CH)) {
                mNameTxt.setText(itemView.getResources().getString(R.string.my_apply_item_ex) + "(" + beanTreeMap.get(bean.getLanguage_id()).getZh_name() + ")");
            }else if (LanguageUtils.getLanguage(AppManager.get().getTopActivity()).equals(TranslatorConstants.SharedPreferences.LANGUAGE_EN)){
                mNameTxt.setText(itemView.getResources().getString(R.string.my_apply_item_ex) + "(" + beanTreeMap.get(bean.getLanguage_id()).getEn_name() + ")");
            }
        }else {
            mNameTxt.setText(itemView.getResources().getString(R.string.my_apply_personal_msg));
        }
    }

    @Override
    public void onRelease() {

    }

    private String getResultString(int result) {
        String resultStr = null;
        switch (result){
            //0为未申请，1未审核，2审核成功 3未通过审核 4审核中
            case 0:
                resultStr = itemView.getResources().getString(R.string.me_not_applied);
                break;
            case 1:
                resultStr = itemView.getResources().getString(R.string.me_unconfirmed);
                break;
            case 2:
                resultStr = itemView.getResources().getString(R.string.me_authenticated);
                break;
            case 3:
                resultStr = itemView.getResources().getString(R.string.me_unauthenticated);
                break;
            case 4:
                resultStr = itemView.getResources().getString(R.string.me_confirming);
                break;
        }
        return resultStr;
    }
}
