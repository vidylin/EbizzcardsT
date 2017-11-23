package com.gzligo.ebizzcardstranslator.business.chat;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.image.transformation.CustomShapeTransformation;
import com.gzligo.ebizzcardstranslator.image.ImageLoader;
import com.gzligo.ebizzcardstranslator.image.glide.GlideImageConfig;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;
import com.gzligo.ebizzcardstranslator.persistence.NewTransOrderBean;

import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import cn.bingoogolapple.badgeview.BGABadgeImageView;

/**
 * Created by Lwd on 2017/6/29.
 */

public class ChatActivityHolder extends BaseHolder<NewTransOrderBean> {
    private TreeMap<Integer, Integer> integerTreeMap;
    private List<Boolean> onLineLists;
    private int choiceWhich;
    @BindView(R.id.from_people_img) ImageView fromPeopleImg;
    @BindView(R.id.to_people_img) ImageView toPeopleImg;
    @BindView(R.id.people_img_rl) BGABadgeImageView peopleImgRl;
    @BindView(R.id.trans_groups_item) LinearLayout transGroupsItem;

    public ChatActivityHolder(View itemView, TreeMap<Integer, Integer> integerTreeMap, List<Boolean> onLineLists, int choiceWhich) {
        super(itemView);
        this.integerTreeMap = integerTreeMap;
        this.onLineLists = onLineLists;
        this.choiceWhich = choiceWhich;
    }

    @Override
    public void setData(NewTransOrderBean data, int position) {
        if (position == choiceWhich) {
            transGroupsItem.setBackgroundResource(R.color.white);
        } else {
            transGroupsItem.setBackgroundResource(R.color.chat_head_bg);
        }
        boolean isOnLine = onLineLists.get(position);
        initImageView(data.getFromPortraitId(), fromPeopleImg, isOnLine);
        initImageView(data.getToPortraitId(), toPeopleImg, isOnLine);
        if (integerTreeMap.containsKey(position)) {
            int count = integerTreeMap.get(position);
            if (count <= 0) {
                peopleImgRl.hiddenBadge();
            } else {
                peopleImgRl.showTextBadge(count + "");
            }
        } else {
            peopleImgRl.hiddenBadge();
        }
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
                .isClearMemory(true)
                .transformation(new CustomShapeTransformation(AppManager.get().getApplication(), 39, !isOnline))
                .build());
    }
}
