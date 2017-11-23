package com.gzligo.ebizzcardstranslator.business.me;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.image.ImageLoader;
import com.gzligo.ebizzcardstranslator.image.glide.GlideImageConfig;
import com.gzligo.ebizzcardstranslator.persistence.PhotoBean;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/6/23.
 */

public class UploadCertificateAdapter extends BaseAdapter<PhotoBean>{
    private List<PhotoBean> mList;

    public UploadCertificateAdapter(List<PhotoBean> mList) {
        super(mList);
        this.mList = mList;
    }

    @Override
    public CertificateHolder getHolder(View v, int viewType) {
        return new CertificateHolder(v);
    }

    @Override
    public int getLayoutResId(int viewType) {
        return R.layout.adapter_upload_certificate;
    }

    public void notifyDataSetChanged(List<PhotoBean> mList) {
        this.mList = mList;
        super.notifyDataSetChanged();
    }

    public static class CertificateHolder extends BaseHolder<PhotoBean>{
        @BindView(R.id.upload_certificate_other_rl)
        RelativeLayout mAddrl;
        @BindView(R.id.photo_close_iv)
        ImageView mCloseIv;
        @BindView(R.id.upload_certificate_item_iv)
        ImageView mPhotoIv;
        @BindView(R.id.upload_certificate_item_plus_iv)
        ImageView mPlusIv;
        private CertificateHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void setData(PhotoBean data, int position) {
            if (!data.getFileName().equals("add")) {
                mPlusIv.setVisibility(View.GONE);
                mCloseIv.setVisibility(View.VISIBLE);
                loadImage(data.getFilePath(),mPhotoIv);
            }
        }

        @Override
        public void onRelease() {

        }

        private void loadImage(String url, final ImageView imageView){
            ImageLoader.get().loadImage(AppManager.get().getApplication(), GlideImageConfig
                    .builder()
                    .url(url)
                    .imageView(imageView)
                    .imgWidth(imageView.getWidth())
                    .imgHeigth(imageView.getHeight())
                    .errorPic(R.mipmap.default_head_portrait)
                    .isClearMemory(false)
                    .build());
        }
    }
}
