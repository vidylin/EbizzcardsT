package com.gzligo.ebizzcardstranslator.business.order;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.common.ChronometerView;
import com.gzligo.ebizzcardstranslator.image.transformation.CustomShapeTransformation;
import com.gzligo.ebizzcardstranslator.manager.CommonBeanManager;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.NewTransOrderBean;
import com.gzligo.ebizzcardstranslator.persistence.OrderDesc;
import com.gzligo.ebizzcardstranslator.utils.GlideUtils;
import com.gzligo.ebizzcardstranslator.utils.LanguageUtils;
import com.gzligo.ebizzcardstranslator.utils.ScaleAvatarActivity;

import java.util.ArrayList;
import java.util.TreeMap;

import butterknife.BindView;

import static com.gzligo.ebizzcardstranslator.business.order.OrderFragment.ORDER_DELETE;

/**
 * Created by Lwd on 2017/6/8.
 */

public class OrderHolder extends BaseHolder<NewTransOrderBean> {
    @BindView(R.id.grab_order_time_img) ImageView grabOrderTimeImg;
    @BindView(R.id.about_time_tv) TextView aboutTimeTv;
    @BindView(R.id.estimate_time_tv) TextView estimateTimeTv;
    @BindView(R.id.order_fee_tv) TextView orderFeeTv;
    @BindView(R.id.from_people_img) ImageView fromPeopleImg;
    @BindView(R.id.to_img) ImageView toImg;
    @BindView(R.id.to_people_img) ImageView toPeopleImg;
    @BindView(R.id.from_people_name_tv) TextView fromPeopleNameTv;
    @BindView(R.id.to_people_name_tv) TextView toPeopleNameTv;
    @BindView(R.id.from_language_tv) TextView fromLanguageTv;
    @BindView(R.id.from_language_ll) LinearLayout fromLanguageLl;
    @BindView(R.id.to_language_tv) TextView toLanguageTv;
    @BindView(R.id.to_language_ll) LinearLayout toLanguageLl;
    @BindView(R.id.order_time_tv) ChronometerView orderTimeTv;
    @BindView(R.id.remarks_tv) TextView remarksTv;
    @BindView(R.id.remarks_rl) RelativeLayout remarksRl;
    private IView iView;

    public OrderHolder(View itemView, IView iView) {
        super(itemView);
        this.iView = iView;
    }

    @Override
    public void setData(final NewTransOrderBean data, int position) {
        Object fromJson = new Gson().fromJson(data.getDesc(), Object.class);
        if (fromJson instanceof LinkedTreeMap) {
            OrderDesc orderDesc = new Gson().fromJson(data.getDesc(), OrderDesc.class);
            switch (orderDesc.getEstimateTimeType()) {
                case 0:
                    estimateTimeTv.setText(AppManager.get().getApplication().getResources().getString(R.string.order_during_one_hour));
                    break;
                case 1:
                    estimateTimeTv.setText(AppManager.get().getApplication().getResources().getString(R.string.order_during_one_to_two_hour));
                    break;
                case 2:
                    estimateTimeTv.setText(AppManager.get().getApplication().getResources().getString(R.string.order_during_three_hour));
                    break;
            }
            if (!TextUtils.isEmpty(orderDesc.getDescriptions())) {
                remarksRl.setVisibility(View.VISIBLE);
                remarksTv.setText(orderDesc.getDescriptions());
            } else {
                remarksRl.setVisibility(View.GONE);
            }
        } else {
            estimateTimeTv.setText(fromJson.toString());
        }
        GlideUtils.initFullScreenImg(fromPeopleImg,data.getFromPortraitId(),
                new CustomShapeTransformation(AppManager.get().getApplication(), 39, !true)
                ,R.mipmap.default_head_portrait);
        GlideUtils.initFullScreenImg(toPeopleImg,data.getToPortraitId(),
                new CustomShapeTransformation(AppManager.get().getApplication(), 39, !true)
                ,R.mipmap.default_head_portrait);
        preUserImg(data, fromPeopleImg, 0);
        preUserImg(data, toPeopleImg, 1);
        long effectiveTime = data.getEffectiveTime();
        long nowTime = System.currentTimeMillis();
        long time = (effectiveTime - nowTime) / 1000;
        if (orderTimeTv.ismStarted()) {
            orderTimeTv.stop();
        }
        orderTimeTv.setBaseSeconds(time);
        orderTimeTv.start();
        orderTimeTv.setOnTickChangeListener(new ChronometerView.OnTickChangeListener() {
            @Override
            public void onTickChanged(ChronometerView view, long remainSeconds) {
                boolean isStarted = orderTimeTv.ismStarted();
                if (remainSeconds == 0 && isStarted) {
                    Message msg = Message.obtain(iView);
                    msg.what = ORDER_DELETE;
                    msg.obj = data;
                    msg.dispatchToIView();
                }
            }
        });
        TreeMap<Integer, LanguagesBean> treeMap = CommonBeanManager.getInstance().getTreeMap();
        String fromLanguage = LanguageUtils.getLanguageName(data.getFromLangId(),treeMap);
        String toLanguage = LanguageUtils.getLanguageName(data.getToLangId(),treeMap);
        fromLanguageTv.setText(fromLanguage);
        toLanguageTv.setText(toLanguage);
    }

    @Override
    public void onRelease() {
    }

    private void preUserImg(final NewTransOrderBean data, final ImageView mAvatar, final int pos) {
        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> mAvatarList = new ArrayList<>();
                mAvatarList.add(data.getFromPortraitId());
                mAvatarList.add(data.getToPortraitId());
                ScaleAvatarActivity.startScaleAvatarActivity(AppManager.get().getTopActivity(), mAvatarList, pos, mAvatar);
            }
        });
    }
}
