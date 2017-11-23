package com.gzligo.ebizzcardstranslator.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.IPresenter;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;


public class ScaleAvatarActivity extends BaseActivity {

    private ArrayList<String> mDatas;
    private int mPosition;
    private int mLocationX;
    private int mLocationY;
    private int mWidth;
    private int mHeight;
    SmoothImageView imageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(imageView);
    }

    @Override
    public void initEvents() {
        imageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                imageOut();
            }
        });
    }

    @Override
    public IPresenter createPresenter() {
        return null;
    }

    @Override
    public int onLayoutResId() {
        return 0;
    }

    @Override
    public void initData() {
        initIntent();
    }

    @Override
    public void initViews() {
        if (!TextUtils.isEmpty(mDatas.get(mPosition))) {
            imageView.setImageURI(formatImageLoadStr(mDatas.get(mPosition)));
        } else {
            imageView.setImageResource(R.mipmap.default_head_portrait);
        }
    }

    private void initIntent() {
        mDatas = (ArrayList<String>) getIntent().getSerializableExtra("images");
        mPosition = getIntent().getIntExtra("position", 0);
        mLocationX = getIntent().getIntExtra("locationX", 0);
        mLocationY = getIntent().getIntExtra("locationY", 0);
        mWidth = getIntent().getIntExtra("width", 0);
        mHeight = getIntent().getIntExtra("height", 0);

        imageView = new SmoothImageView(this);
        imageView.setImageResource(R.mipmap.default_head_portrait);
        imageView.setOriginalInfo(mWidth, mHeight, mLocationX, mLocationY);
        imageView.transformIn();
        imageView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }

    @Override
    public void onBackPressed() {
        imageOut();
    }

    public void imageOut() {
        imageView.setOnTransformListener(new SmoothImageView.TransformListener() {
            @Override
            public void onTransformComplete(int mode) {
                if (mode == 2) {
                    finish();
                }
            }
        });
        imageView.transformOut();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }

    public static void startScaleAvatarActivity(Activity activity, ArrayList<String> data, int position, ImageView imageView) {
        Intent intent = new Intent(activity, ScaleAvatarActivity.class);
        intent.putExtra("images", data);
        intent.putExtra("position", position);
        int[] location = new int[2];
        imageView.getLocationOnScreen(location);
        intent.putExtra("locationX", location[0]);
        intent.putExtra("locationY", location[1]);

        intent.putExtra("width", imageView.getWidth());
        intent.putExtra("height", imageView.getHeight());
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    @Override
    public boolean supportSlideBack() {
        return true;
    }

    @Override
    public boolean isSupportSwipeBack() {
        return false;
    }

    public static String formatImageLoadStr(String filePath) {
        if (TextUtils.isEmpty(filePath)||filePath.equals("null"))
            return null;
        String loadPath = "";
        if (filePath.startsWith("file://")) {
            loadPath = filePath;
            return loadPath;
        }
        if (filePath.startsWith("asset://")) {
            loadPath = filePath;
            return loadPath;
        }
        if (filePath.startsWith("/")) {
            if (filePath.contains("file://")) {
                loadPath = filePath;
            } else {
                loadPath = "file://" + filePath;
            }
        } else {
            if (filePath.contains("https") || filePath.contains("http")) {
                loadPath = filePath;
            } else {
                loadPath = HttpUtils.MEDIA_HOST + filePath;
            }
        }
        return loadPath;
    }
}
