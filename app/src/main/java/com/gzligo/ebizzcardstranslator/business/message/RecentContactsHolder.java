package com.gzligo.ebizzcardstranslator.business.message;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.image.transformation.CustomShapeTransformation;
import com.gzligo.ebizzcardstranslator.image.ImageLoader;
import com.gzligo.ebizzcardstranslator.image.glide.GlideImageConfig;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;
import com.gzligo.ebizzcardstranslator.persistence.TranslatorSelectedBean;
import com.gzligo.ebizzcardstranslator.utils.ScaleAvatarActivity;
import com.gzligo.ebizzcardstranslator.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.bingoogolapple.badgeview.BGABadgeImageView;

/**
 * Created by Lwd on 2017/6/7.
 */

public class RecentContactsHolder extends BaseHolder<TranslatorSelectedBean> {
    @BindView(R.id.from_people_img) ImageView fromPeopleImg;
    @BindView(R.id.to_people_img) ImageView toPeopleImg;
    @BindView(R.id.people_img_rl) RelativeLayout peopleImgRl;
    @BindView(R.id.people_name_tv) TextView peopleNameTv;
    @BindView(R.id.msg_time_tv) TextView msgTimeTv;
    @BindView(R.id.new_msg_tv) TextView newMsgTv;
    @BindView(R.id.red_point_tv) BGABadgeImageView redPointTv;
    @BindView(R.id.history_trans_uer_ll) LinearLayout historyTransUerLl;
    private List<TranslatorSelectedBean> selectedBeanList;

    public RecentContactsHolder(View itemView, List<TranslatorSelectedBean> selectedBeanList) {
        super(itemView);
        this.selectedBeanList = selectedBeanList;
    }

    @Override
    public void setData(final TranslatorSelectedBean data, int position) {
        boolean isOnline = data.getIsTranslating();
        initImageView(data.getFromPortraitId(), fromPeopleImg, isOnline);
        initImageView(data.getToPortraitId(), toPeopleImg, isOnline);
        newMsgTv.setText(data.getTranslatorMsg());
        peopleNameTv.setText(data.getFromName() + "、" + data.getToName());
        if (null != data.getNotifyTime()) {
            msgTimeTv.setText(TimeUtils.getHMS(data.getNotifyTime()));
        }
        if (null != data.getUnTransMsg() && data.getUnTransMsg() > 0) {
            redPointTv.showTextBadge(String.valueOf(data.getUnTransMsg()));
        } else {
            redPointTv.hiddenBadge();
        }
        if (position != 0) {
            if (!isOnline) {
                if (null != selectedBeanList && selectedBeanList.size() > 1) {
                    TranslatorSelectedBean selectedBean = selectedBeanList.get(position - 1);
                    if (selectedBean.getIsTranslating()) {
                        historyTransUerLl.setVisibility(View.VISIBLE);
                    } else {
                        historyTransUerLl.setVisibility(View.GONE);
                    }
                }
            } else {
                historyTransUerLl.setVisibility(View.GONE);
            }
        } else {
            historyTransUerLl.setVisibility(View.GONE);
        }
        preUserImg(data, fromPeopleImg, 0);
        preUserImg(data, toPeopleImg, 1);
    }

    @Override
    public void onRelease() {
    }

    private void initImageView(String url, ImageView img, boolean isOnline) {
        ImageLoader.get().loadImage(AppManager.get().getApplication(), GlideImageConfig
                .builder()
                .url(HttpUtils.MEDIA_HOST + url)
                .imageView(img)
                .errorPic(R.mipmap.default_head_portrait)
                .cacheStrategy(1)
                .isClearMemory(false)
                .transformation(new CustomShapeTransformation(AppManager.get().getApplication(), 39, !isOnline))
                .build());
    }

    private void preUserImg(final TranslatorSelectedBean data, final ImageView mAvatar, final int pos) {
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
