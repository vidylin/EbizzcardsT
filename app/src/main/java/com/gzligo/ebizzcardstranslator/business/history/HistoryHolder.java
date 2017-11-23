package com.gzligo.ebizzcardstranslator.business.history;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.image.transformation.CustomShapeTransformation;
import com.gzligo.ebizzcardstranslator.persistence.ChatMsgUserBean;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.TranslatorOrderList;
import com.gzligo.ebizzcardstranslator.utils.CurrencyTypeUtil;
import com.gzligo.ebizzcardstranslator.utils.GlideUtils;
import com.gzligo.ebizzcardstranslator.utils.LanguageUtils;
import com.gzligo.ebizzcardstranslator.utils.ScaleAvatarActivity;
import com.gzligo.ebizzcardstranslator.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;

/**
 * Created by Lwd on 2017/6/5.
 */

public class HistoryHolder extends BaseHolder {
    private List<TranslatorOrderList> translatorOrderLists;
    private TreeMap<Integer, LanguagesBean> treeMap;
    @BindView(R.id.time_tv) TextView timeTv;
    @BindView(R.id.start_time_tv) TextView startTimeTv;
    @BindView(R.id.to_tv) TextView toTv;
    @BindView(R.id.end_time_tv) TextView endTimeTv;
    @BindView(R.id.from_people_img) ImageView fromPeopleImg;
    @BindView(R.id.to_people_img) ImageView toPeopleImg;
    @BindView(R.id.one_grade_img) ImageView oneGradeImg;
    @BindView(R.id.two_grade_img) ImageView twoGradeImg;
    @BindView(R.id.three_grade_img) ImageView threeGradeImg;
    @BindView(R.id.four_grade_img) ImageView fourGradeImg;
    @BindView(R.id.five_grade_img) ImageView fiveGradeImg;
    @BindView(R.id.score_ll) LinearLayout scoreLl;
    @BindView(R.id.from_language_tv) TextView fromLanguageTv;
    @BindView(R.id.to_language_img) ImageView toLanguageImg;
    @BindView(R.id.to_language_tv) TextView toLanguageTv;
    @BindView(R.id.translation_fee_tv) TextView translationFeeTv;
    @BindView(R.id.remarks_tv) TextView remarksTv;
    @BindView(R.id.remarks_rl) RelativeLayout remarksRl;

    public HistoryHolder(View itemView, List<TranslatorOrderList> translatorOrderLists, TreeMap<Integer, LanguagesBean> treeMap) {
        super(itemView);
        this.translatorOrderLists = translatorOrderLists;
        this.treeMap = treeMap;
    }

    @Override
    public void setData(Object data, int position) {
        TranslatorOrderList translatorOrderList = (TranslatorOrderList) data;
        if (position == 0) {
            timeTv.setVisibility(View.VISIBLE);
            timeTv.setText(TimeUtils.getYMD(translatorOrderList.getEnd_time()));
        } else {
            TranslatorOrderList exTranslatorOrderList = translatorOrderLists.get(position - 1);
            String headTime = TimeUtils.getYMD(exTranslatorOrderList.getEnd_time());
            String time = TimeUtils.getYMD(translatorOrderList.getEnd_time());
            if (!headTime.equals(time)) {
                timeTv.setVisibility(View.VISIBLE);
                timeTv.setText(time);
            } else {
                timeTv.setVisibility(View.GONE);
            }
        }
        int score = translatorOrderList.getScore();
        scoreLl.setVisibility(View.VISIBLE);
        soreShow(score);
        startTimeTv.setText(TimeUtils.getHM(translatorOrderList.getStart_time()));
        endTimeTv.setText(TimeUtils.getHM(translatorOrderList.getEnd_time()));
        int currencyType = translatorOrderList.getCurrency();
        String transFee = TimeUtils.getDoubleDigit(translatorOrderList.getTrans_fee());
        translationFeeTv.setText(CurrencyTypeUtil.getCurrencySymbol(AppManager.get().getApplication(), currencyType) + " " + transFee);
        String remark = translatorOrderList.getRemark();
        if (!TextUtils.isEmpty(remark)) {
            remarksRl.setVisibility(View.VISIBLE);
            remarksTv.setText(remark);
        } else {
            remarksRl.setVisibility(View.GONE);
        }
        ChatMsgUserBean chatMsgUserBeanOne = translatorOrderList.getUser1();
        ChatMsgUserBean chatMsgUserBeanTwo = translatorOrderList.getUser2();
        int langOne = chatMsgUserBeanOne.getLang_id();
        int langTwo = chatMsgUserBeanTwo.getLang_id();
        if (treeMap.containsKey(langOne)) {
            fromLanguageTv.setText(LanguageUtils.getLanguageName(langOne,treeMap));
        }
        if (treeMap.containsKey(langTwo)) {
            toLanguageTv.setText(LanguageUtils.getLanguageName(langTwo,treeMap));
        }
        GlideUtils.initFullScreenImg(fromPeopleImg,chatMsgUserBeanOne.getPortrait_id(),
                new CustomShapeTransformation(AppManager.get().getApplication(), 39),R.mipmap.default_head_portrait);
        GlideUtils.initFullScreenImg(toPeopleImg,chatMsgUserBeanTwo.getPortrait_id(),
                new CustomShapeTransformation(AppManager.get().getApplication(), 39),R.mipmap.default_head_portrait);
        preUserImg(translatorOrderList, fromPeopleImg, 0);
        preUserImg(translatorOrderList, toPeopleImg, 1);
    }

    @Override
    public void onRelease() {
    }

    private void soreShow(int score) {
        switch (score) {
            case 0:
                oneGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars_gray));
                twoGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars_gray));
                threeGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars_gray));
                fourGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars_gray));
                fiveGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars_gray));
                break;
            case 1:
                oneGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars_gray));
                twoGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars_gray));
                threeGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars_gray));
                fourGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars_gray));
                fiveGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars));
                break;
            case 2:
                oneGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars_gray));
                twoGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars_gray));
                threeGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars_gray));
                fourGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars));
                fiveGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars));
                break;
            case 3:
                oneGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars_gray));
                twoGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars_gray));
                threeGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars));
                fourGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars));
                fiveGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars));
                break;
            case 4:
                oneGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars_gray));
                twoGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars));
                threeGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars));
                fourGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars));
                fiveGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars));
                break;
            case 5:
                oneGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars));
                twoGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars));
                threeGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars));
                fourGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars));
                fiveGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars));
                break;
        }
    }

    private void preUserImg(final TranslatorOrderList data, final ImageView mAvatar, final int pos) {
        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> mAvatarList = new ArrayList<>();
                mAvatarList.add(data.getUser1().getPortrait_id());
                mAvatarList.add(data.getUser2().getPortrait_id());
                ScaleAvatarActivity.startScaleAvatarActivity(AppManager.get().getTopActivity(), mAvatarList, pos, mAvatar);
            }
        });
    }
}
