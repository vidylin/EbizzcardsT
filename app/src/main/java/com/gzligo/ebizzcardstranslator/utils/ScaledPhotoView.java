package com.gzligo.ebizzcardstranslator.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.image.ImageLoader;
import com.gzligo.ebizzcardstranslator.image.glide.GlideImageConfig;

import uk.co.senab.photoview.PhotoView;


public class ScaledPhotoView extends PhotoView {

    private static final String TAG = ScaledPhotoView.class.getSimpleName();
    private Uri imageLocation;
    private String mImageStrUri;

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ScaledPhotoView(Context context) {
        this(context, null);
    }

    public ScaledPhotoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaledPhotoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

    }

    @Override
    public void setImageURI(Uri location) {
        if (location == null) {
            return;
        }
        this.imageLocation = location;
        loadImage();
    }

    public void setImageURI(String location) {
        Uri uri = (location != null) ? Uri.parse(location) : null;
        mImageStrUri = location;
        setImageURI(uri);
    }

    private void loadImage() {
        ImageLoader.get().loadImage(AppManager.get().getApplication(), GlideImageConfig
                .builder()
                .url(mImageStrUri)
                .imageView(this)
                .imgWidth(this.getHeight())
                .imgHeigth(this.getHeight())
                .errorPic(R.mipmap.default_head_portrait)
                .isClearMemory(false)
                .build());
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who);
    }

}
