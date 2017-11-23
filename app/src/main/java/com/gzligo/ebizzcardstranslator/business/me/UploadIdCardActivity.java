package com.gzligo.ebizzcardstranslator.business.me;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.image.ImageLoader;
import com.gzligo.ebizzcardstranslator.image.glide.GlideImageConfig;
import com.gzligo.ebizzcardstranslator.persistence.ErrorMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.PhotoBean;
import com.gzligo.ebizzcardstranslator.utils.CropImageActivity;
import com.gzligo.ebizzcardstranslator.utils.DialogUtils;
import com.gzligo.ebizzcardstranslator.utils.FileManager;
import com.gzligo.ebizzcardstranslator.utils.FileUtils;
import com.gzligo.ebizzcardstranslator.utils.TranslatorCallBack;
import com.tangxiaolv.telegramgallery.GalleryActivity;
import com.tangxiaolv.telegramgallery.GalleryConfig;
import com.tangxiaolv.telegramgallery.ModelAdapter.PhotoModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/5/31.
 */

public class UploadIdCardActivity extends BaseActivity<PersonalPresenter> implements IView {

    @BindView(R.id.my_idcard_positive_iv)
    ImageView mIdCardPositive;
    @BindView(R.id.my_idcard_negative_iv)
    ImageView mIdCardNegative;
    @BindView(R.id.my_idcard_half_body_iv)
    ImageView mIdCardHalfBody;
    @BindView(R.id.upload_idcard_btn)
    Button mButton;

    private Dialog mPictureDialog,mLoadingDialog;
    private List<PhotoBean> photoList;
    private List<String> mPhotoObjidList;
    private ImageView mCurrentImg;
    public static final int REQUEST_TAKE_PHOTO = 0x00;
    public static final int UPLOAD_IDCARDS = 0x01;
    public static final int RESULT_CROP = 0x02;

    @Override
    public PersonalPresenter createPresenter() {
        return new PersonalPresenter(new PersonalRepository());
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_upload_idcard;
    }

    @Override
    public void initData() {
        photoList = new ArrayList<>();
        mPhotoObjidList = new ArrayList<>();
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initEvents() {
        mButton.setClickable(false);
    }

    @OnClick({R.id.tv_close, R.id.upload_idcard_btn,R.id.idcard_positive_rl,R.id.idcard_negative_rl,R.id.idcard_half_body_rl})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_close:
                finish();
                break;
            case R.id.upload_idcard_btn:
                handleUploadPhotos();
                break;
            case R.id.idcard_positive_rl:
                mCurrentImg = mIdCardPositive;
                handleChoosePhoto();
                break;
            case R.id.idcard_negative_rl:
                if (mIdCardPositive.getDrawable()!=null){
                    mCurrentImg = mIdCardNegative;
                    handleChoosePhoto();
                }else {
                    Toast.makeText(this,getResources().getString(R.string.upload_idcard_toast),Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.idcard_half_body_rl:
                if (mIdCardPositive.getDrawable()!=null&&mIdCardNegative.getDrawable()!=null){
                    mCurrentImg = mIdCardHalfBody;
                    handleChoosePhoto();
                }else {
                    Toast.makeText(this,getResources().getString(R.string.upload_idcard_toast),Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void handleUploadPhotos() {
        mLoadingDialog = DialogUtils.showLoadingDialog(this,getResources().getString(R.string.submitting));
        for (PhotoBean photoBean : photoList) {
            getPresenter().requestObjectId(photoBean.getFilePath(), photoBean.getFileName(), new PersonalCallback() {
                @Override
                public void upLoadFileSuccess(String obj_id) {
                    mPhotoObjidList.add(obj_id);
                    if (mPhotoObjidList.size()==3){
                        Gson gson = new Gson();
                        String id_cards = gson.toJson(mPhotoObjidList);
                        getPresenter().uploadIdCardPic(Message.obtain(UploadIdCardActivity.this,new String[]{id_cards}),true);
                    }
                }
            }, true);
        }
    }

    private void handleChoosePhoto() {
        mPictureDialog = DialogUtils.selectPictureDialog(this, new TranslatorCallBack.onSelectPictureClickLister() {
            @Override
            public void onTakePhoto() {
                requestSystemTakePhoto(new TakePhotoListener() {
                    @Override
                    public void takePhoto(File filePath) {
                        startCropImage(Uri.parse("file://" + filePath.getAbsolutePath()));
                    }
                });
            }

            @Override
            public void onFromAlbum() {
                requestSystemPhoto(new OnPhotoListener() {
                    @Override
                    public void onPhoto(ArrayList<PhotoModel> photos) {
                        String filePath = photos.get(0).getOriginalPath();
                        startCropImage(Uri.parse("file://" + filePath));
                    }
                });
            }
        });
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what){
            case UPLOAD_IDCARDS:
                if (mLoadingDialog!=null){
                    mLoadingDialog.dismiss();
                    mLoadingDialog = null;
                }
                if (((ErrorMessageBean)message.obj).getError()==0) {
                    Toast.makeText(this,getResources().getString(R.string.submitting_success),Toast.LENGTH_SHORT).show();
                    String mLanguageJson = getIntent().getStringExtra(TranslatorConstants.Common.JSON);
                    Intent intent = new Intent(this,UploadCertificateActivity.class);
                    intent.putExtra(TranslatorConstants.Common.JSON,mLanguageJson);
                    intent.putExtra(TranslatorConstants.Common.FROM,getIntent().getStringExtra(TranslatorConstants.Common.FROM));
                    startActivity(intent);
                }
                break;
        }
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

    //take photo
    private interface TakePhotoListener {
        /**
         * @param filePath 原始路劲
         */
        void takePhoto(File filePath);
    }
    private TakePhotoListener mTakePhotoListener;
    private File mTakePhotoFile;
    private void requestSystemTakePhoto(TakePhotoListener takePhotoListener) {
        mTakePhotoListener = takePhotoListener;
        mTakePhotoFile = new File(getExternalCacheDir(),System.currentTimeMillis()+".jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTakePhotoFile));
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }

    private void systemTakePhoto() {
        if ((mTakePhotoFile != null && mTakePhotoFile.length() <= 0)
                || mTakePhotoListener == null) {
            return;
        }
        mTakePhotoListener.takePhoto(mTakePhotoFile);
        mTakePhotoListener = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_TAKE_PHOTO:
                systemTakePhoto();
                break;
            case PhotoModel.RESULT_SINGLE:
                singlePhoto(data);
                break;
            case RESULT_CROP:
                if (data!=null&&data.getByteArrayExtra(TranslatorConstants.Common.DATA)!=null) {
                    byte[] mUseAvatarByte = data.getByteArrayExtra(TranslatorConstants.Common.DATA);
                    File file = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
                    File imgFile = FileUtils.saveFile(file.getAbsolutePath(), mUseAvatarByte);

                    if (mCurrentImg == mIdCardHalfBody){
                        mButton.setClickable(true);
                        mButton.setBackgroundResource(R.drawable.onclick_green_btn_selector);
                    }
                    loadImage(imgFile.getAbsolutePath(),mCurrentImg);
                    PhotoBean photoBean = new PhotoBean();
                    photoBean.setFilePath(imgFile.getPath());
                    photoBean.setFileName(imgFile.getName());
                    photoList.add(photoBean);
                }
                break;
        }
    }

    public interface OnPhotoListener {
        void onPhoto(ArrayList<PhotoModel> photos);
    }

    private OnPhotoListener mOnPhotoListener;

    public void requestSystemPhoto(OnPhotoListener listener) {
        mOnPhotoListener = listener;
        int requestCode = PhotoModel.RESULT_SINGLE;
        GalleryConfig.Build configBuild = new GalleryConfig.Build();
        configBuild.singlePhoto(true);
        configBuild.video(false);
        GalleryActivity.openActivity(this, requestCode, configBuild.build());
    }

    private void singlePhoto(Intent data) {
        if (mOnPhotoListener == null) {
            return;
        }
        if (data != null) {
            String photoPath = data.getStringExtra("photo");
            if (TextUtils.isEmpty(photoPath)) {
                return;
            }
            final ArrayList<PhotoModel> photos = new ArrayList<>();
            PhotoModel photoModel = new PhotoModel();
            photoModel.setOriginalPath(photoPath);
            photos.add(photoModel);
            mOnPhotoListener.onPhoto(photos);
        }
    }


    //头像裁剪
    private void startCropImage(Uri uri) {
        String imageName = "register.png";
        Intent intent = new Intent(this, CropImageActivity.class);
        intent.setData(uri);
        File mImagePathFile = new File(FileManager.getFileManager(this).getTempDir(), imageName);
        if (mImagePathFile.exists()) {
            try {
                mImagePathFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mImagePathFile));
        startActivityForResult(intent,RESULT_CROP);
    }
}
