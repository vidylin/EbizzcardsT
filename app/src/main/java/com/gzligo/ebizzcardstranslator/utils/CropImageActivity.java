package com.gzligo.ebizzcardstranslator.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.IPresenter;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.common.ToolActionBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by YJZ on 2016/8/3 0003.
 */
public class CropImageActivity extends BaseActivity implements View.OnClickListener {

    private ToolActionBar mActionbar;
    private ClipImageLayout mClipImageLayout;
    private Intent mIntent;
    private Uri mInputPath;
    private Uri mOutputPath;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initEvents() {
        mActionbar.setOnCloseClickListener(this);
        mActionbar.setOnRight1ClickListener(this);
    }

    @Override
    public IPresenter createPresenter() {
        return null;
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_crop_img;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initViews() {
        mActionbar = (ToolActionBar) findViewById(R.id.crop_image_actionbar);
        mClipImageLayout = (ClipImageLayout) findViewById(R.id.clip_image);
        mIntent = getIntent();
        Bundle extras = mIntent.getExtras();
        mInputPath = mIntent.getData();
        mOutputPath = extras.getParcelable(MediaStore.EXTRA_OUTPUT);
        if (mInputPath == null) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        if (mOutputPath != null && mOutputPath.toString().startsWith(("file://"))) {
            File rootPhotoFile = FileManager.getFileManager(this).getTempDir();
            String imageName = "rtp.png";
            mOutputPath = Uri.fromFile(FileUtils.createFile(this, rootPhotoFile, imageName));
        }

        mBitmap = loadBitmap(mInputPath);

        if(mBitmap == null){
            setResult(RESULT_CANCELED);
            finish();
            return;
        }else{
            mBitmap = FileUtils.pictureRotate(mBitmap, mInputPath.getPath());
        }

        mClipImageLayout.setZoomImageBitmap(mBitmap);
    }

    private Bitmap loadBitmap(Uri uri) {

        Bitmap bitmap = null;
        InputStream in = null;
        try {
            Log.e("", "Uri is : " + uri.toString() + "----uri path is :" + uri.getPath());
            bitmap = FileUtils.compressImageFromFile((int) (ScreenUtils.getScreenWidth(this) * 0.4), (int) (ScreenUtils.getScreenHeight(this) * 0.4), uri.getPath());
        } catch (Exception e) {
            Log.e("", " " + e.toString());
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_close:
                finish();
                break;
            case R.id.tv_right1:
                Bitmap bitmap = mClipImageLayout.clip();
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                    byte[] datas = baos.toByteArray();
                    setResult(RESULT_OK, new Intent().putExtra(TranslatorConstants.Common.DATA, datas));
                    finish();
                } catch (Exception e) {
                }
                break;
        }
    }
}
