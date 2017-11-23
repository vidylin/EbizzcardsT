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
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.TravelTranslateBean;
import com.gzligo.ebizzcardstranslator.utils.CurrencyTypeUtil;
import com.gzligo.ebizzcardstranslator.utils.GlideUtils;
import com.gzligo.ebizzcardstranslator.utils.LanguageUtils;
import com.gzligo.ebizzcardstranslator.utils.ScaleAvatarActivity;
import com.gzligo.ebizzcardstranslator.utils.TimeUtils;

import java.util.ArrayList;
import java.util.TreeMap;

import butterknife.BindView;

/**
 * Created by Lwd on 2017/9/15.
 */

public class CallRecordsHolder extends BaseHolder<TravelTranslateBean.DataBean>{

    @BindView(R.id.time_tv) TextView timeTv;
    @BindView(R.id.from_people_img) ImageView fromPeopleImg;
    @BindView(R.id.to_people_img) TextView toPeopleImg;
    @BindView(R.id.one_grade_img) ImageView oneGradeImg;
    @BindView(R.id.two_grade_img) ImageView twoGradeImg;
    @BindView(R.id.three_grade_img) ImageView threeGradeImg;
    @BindView(R.id.four_grade_img) ImageView fourGradeImg;
    @BindView(R.id.five_grade_img) ImageView fiveGradeImg;
    @BindView(R.id.score_ll) LinearLayout scoreLl;
    @BindView(R.id.call_img) ImageView callImg;
    @BindView(R.id.from_language_tv) TextView fromLanguageTv;
    @BindView(R.id.to_language_img) ImageView toLanguageImg;
    @BindView(R.id.to_language_tv) TextView toLanguageTv;
    @BindView(R.id.trans_time_tv) TextView transTimeTv;
    @BindView(R.id.trans_type_tv) TextView transTypeTv;
    @BindView(R.id.trans_fee_tv) TextView transFeeTv;
    @BindView(R.id.remarks_tv) TextView remarksTv;
    @BindView(R.id.remarks_rl) RelativeLayout remarksRl;

    private TreeMap<Integer, LanguagesBean> languagesBeanTreeMap;

    public CallRecordsHolder(View itemView,TreeMap<Integer, LanguagesBean> languagesBeanTreeMap) {
        super(itemView);
        this.languagesBeanTreeMap = languagesBeanTreeMap;
    }

    @Override
    public void setData(TravelTranslateBean.DataBean data, int position) {
        TravelTranslateBean.DataBean.HuserBean userBean = data.getHuser();
        String timeStr = TimeUtils.getTravelTransTime(data.getStart_time(),data.getEnd_time());
        timeTv.setText(timeStr);
        GlideUtils.initFullScreenImg(fromPeopleImg,userBean.getPortrait(),
                new CustomShapeTransformation(AppManager.get().getApplication(), 39, !true)
                ,R.mipmap.default_head_portrait);
        toPeopleImg.setText(userBean.getNickname());
        soreShow(data.getScore());
        int languageFrom = data.getLang_id1();
        int languageTo = data.getLang_id2();
        String lanFromName = LanguageUtils.getLanguageName(languageFrom,languagesBeanTreeMap);
        String lanToName = LanguageUtils.getLanguageName(languageTo,languagesBeanTreeMap);
        fromLanguageTv.setText(lanFromName);
        toLanguageTv.setText(lanToName);
        String currencyStr = CurrencyTypeUtil.getCurrencySymbol(AppManager.get().getApplication(),data.getCurrency_type());
        String transFee = TimeUtils.getDoubleDigit(data.getTrans_fee());
        transFeeTv.setText(AppManager.get().getApplication().getResources().getString(R.string.history_order_translation_fee)+" "+currencyStr+" "+transFee);
        String remark = data.getRemark();
        if(TextUtils.isEmpty(remark)){
            remarksRl.setVisibility(View.GONE);
        }else{
            remarksRl.setVisibility(View.VISIBLE);
            remarksTv.setText(remark);
        }
        int type = data.getType();
        switch (type){
            case 1:
                transTypeTv.setText(AppManager.get().getApplication().getResources().getString(R.string.voice_calls_title));
                callImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.grab_order));
                break;
            case 2:
                transTypeTv.setText(AppManager.get().getApplication().getResources().getString(R.string.video_calls_title));
                callImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.grab_viceo_order));
                break;
        }
        transTimeTv.setText(TimeUtils.getMinute(data.getTrans_time()));
        preUserImg(data,fromPeopleImg,0);
    }

    @Override
    public void onRelease() {
    }

    private void soreShow(int score) {
        switch (score/20) {
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
            default:
                oneGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars_gray));
                twoGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars_gray));
                threeGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars_gray));
                fourGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars_gray));
                fiveGradeImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.record_stars_gray));
                break;
        }
    }

    private void preUserImg(final TravelTranslateBean.DataBean data, final ImageView mAvatar, final int pos) {
        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> mAvatarList = new ArrayList<>();
                mAvatarList.add(data.getHuser().getPortrait());
                ScaleAvatarActivity.startScaleAvatarActivity(AppManager.get().getTopActivity(), mAvatarList, pos, mAvatar);
            }
        });
    }
}
