package com.gzligo.ebizzcardstranslator.business.order;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.common.ChronometerView;
import com.gzligo.ebizzcardstranslator.image.transformation.CustomShapeTransformation;
import com.gzligo.ebizzcardstranslator.manager.CommonBeanManager;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.NewTravelTransOrderBean;
import com.gzligo.ebizzcardstranslator.utils.GlideUtils;
import com.gzligo.ebizzcardstranslator.utils.LanguageUtils;
import com.gzligo.ebizzcardstranslator.utils.ScaleAvatarActivity;
import com.gzligo.ebizzcardstranslator.utils.TimeUtils;

import java.util.ArrayList;
import java.util.TreeMap;

import butterknife.BindView;

/**
 * Created by Lwd on 2017/9/13.
 */

public class CallOrderHolder extends BaseHolder<NewTravelTransOrderBean> {

    @BindView(R.id.estimate_time_tv) TextView estimateTimeTv;
    @BindView(R.id.order_fee_tv) TextView orderFeeTv;
    @BindView(R.id.from_people_img) ImageView fromPeopleImg;
    @BindView(R.id.user_name_tv) TextView userNameTv;
    @BindView(R.id.call_time_tv) TextView callTimeTv;
    @BindView(R.id.grab_order_img) ImageView grabOrderImg;
    @BindView(R.id.from_language_tv) TextView fromLanguageTv;
    @BindView(R.id.to_language_tv) TextView toLanguageTv;
    @BindView(R.id.from_language_ll) LinearLayout fromLanguageLl;
    @BindView(R.id.order_time_tv) ChronometerView orderTimeTv;
    @BindView(R.id.remarks_tv) TextView remarksTv;
    @BindView(R.id.remarks_rl) RelativeLayout remarksRl;
    private IView iView;
    private static final int ORDER_IS_TIME_OUT = 0x42;

    public CallOrderHolder(View itemView,IView iView) {
        super(itemView);
        this.iView = iView;
    }

    @Override
    public void setData(final NewTravelTransOrderBean data, int position) {
        estimateTimeTv.setText(TimeUtils.getyMdHmss(data.getStartTime()));
        GlideUtils.initFullScreenImg(fromPeopleImg,data.getPortrait(),
                new CustomShapeTransformation(AppManager.get().getApplication(), 39, !true)
                ,R.mipmap.default_head_portrait);
        userNameTv.setText(data.getUserName());
        String desc = data.getDesc();
        if(data.getTransType()==0){
            callTimeTv.setText(AppManager.get().getApplication().getResources().getString(R.string.voice_calls_title)+":"+desc);
            grabOrderImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.grab_order));
        }else{
            callTimeTv.setText(AppManager.get().getApplication().getResources().getString(R.string.video_calls_title)+":"+desc);
            grabOrderImg.setImageDrawable(AppManager.get().getApplication().getResources().getDrawable(R.mipmap.video_img));
        }
        long effectiveTime = data.getEffectTime();
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
                    msg.what = ORDER_IS_TIME_OUT;
                    msg.obj = data;
                    msg.dispatchToIView();
                }
            }
        });
        preUserImg(data,fromPeopleImg,0);
        TreeMap<Integer, LanguagesBean> treeMap = CommonBeanManager.getInstance().getTreeMap();
        String fromLanguageName = LanguageUtils.getLanguageName(data.getLanguageFromId(),treeMap);
        String toLanguageName = LanguageUtils.getLanguageName(data.getLanguageToId(),treeMap);
        fromLanguageTv.setText(fromLanguageName);
        toLanguageTv.setText(toLanguageName);
    }

    @Override
    public void onRelease() {
    }

    private void preUserImg(final NewTravelTransOrderBean data, final ImageView mAvatar, final int pos) {
        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> mAvatarList = new ArrayList<>();
                mAvatarList.add(data.getPortrait());
                ScaleAvatarActivity.startScaleAvatarActivity(AppManager.get().getTopActivity(), mAvatarList, pos, mAvatar);
            }
        });
    }
}
