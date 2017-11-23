package com.gzligo.ebizzcardstranslator.business.me;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.image.ImageLoader;
import com.gzligo.ebizzcardstranslator.image.glide.GlideImageConfig;
import com.gzligo.ebizzcardstranslator.persistence.AddLanguageBean;
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

public class UploadCertificateActivity extends BaseActivity<PersonalPresenter> implements IView {

    @BindView(R.id.upload_certificate_student_txt)
    TextView mStudentTxt;
    @BindView(R.id.upload_certificate_student_iv)
    ImageView mStudentIv;
    @BindView(R.id.upload_certificate_student_rl)
    RelativeLayout mStudentRl;
    @BindView(R.id.upload_certificate_other_recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.upload_certificate_btn)
    Button mButton;

    private String languages,studentJson;
    private Dialog mPictureDialog,mLoadingDialog;
    private PhotoBean mStudentPhoto;
    private List<PhotoBean> photoList;
    private Gson gson;
    private UploadCertificateAdapter mAdapter;
    private String imgFrom;
    public static final int REQUEST_TAKE_PHOTO = 0x00;
    public static final int UPLOAD_AUDIT_CERS = 0x01;
    public static final int UPLOAD_ADD_LANGUAGES = 0x02;
    public static final int RESULT_CROP = 0x03;

    @Override
    public PersonalPresenter createPresenter() {
        return new PersonalPresenter(new PersonalRepository());
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_upload_certificate;
    }

    @Override
    public void initData() {
        if (!TextUtils.isEmpty(getIntent().getStringExtra(TranslatorConstants.Common.FROM))&&
                getIntent().getStringExtra(TranslatorConstants.Common.FROM).equals("apply")){
            mStudentTxt.setVisibility(View.GONE);
            mStudentRl.setVisibility(View.GONE);
        }
        languages = getIntent().getStringExtra(TranslatorConstants.Common.JSON);
        gson = new Gson();
        photoList = new ArrayList<>();
        PhotoBean photoBean = new PhotoBean();
        photoBean.setFileName("add");
        photoList.add(photoBean);
        mAdapter = new UploadCertificateAdapter(photoList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initEvents() {
        mButton.setClickable(false);
        mAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int viewType, Object data, final int position) {
                view.findViewById(R.id.photo_close_iv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        photoList.remove(position);
                        if (photoList.size()==0){
                            mButton.setClickable(false);
                            mButton.setBackgroundResource(R.mipmap.green_btn_normal);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                });
                if (!TextUtils.isEmpty(photoList.get(position).getFileName())&&photoList.get(position).getFileName().equals("add")){
                    handleChoosePhoto();
                }
            }
        });
    }

    @OnClick({R.id.tv_close,R.id.upload_certificate_student_rl,R.id.upload_certificate_btn})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_close:
                finish();
                break;
            case R.id.upload_certificate_student_rl:
                imgFrom = "student";
                selectPhotoResult();
                break;
            case R.id.upload_certificate_btn:
                if (TextUtils.isEmpty(getIntent().getStringExtra(TranslatorConstants.Common.FROM))){
                    if (mStudentIv.getDrawable()==null){
                        Toast.makeText(this,getResources().getString(R.string.upload_certificate_tip1),Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                        uploadCertification();
                    }
                }else {
                    uploadCertification();
                }
                break;
        }
    }

    private void selectPhotoResult() {
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

    private void uploadCertification() {
        mLoadingDialog = DialogUtils.showLoadingDialog(this,getResources().getString(R.string.submitting));
        if (mStudentPhoto!=null){
            //注册入口进来的，有上传学生证书的
            getPresenter().requestObjectId(mStudentPhoto.getFilePath(), mStudentPhoto.getFileName(), new PersonalCallback() {
                @Override
                public void upLoadFileSuccess(String obj_id) {
                    List<String> studentObj_idList = new ArrayList<>();
                    studentObj_idList.add(obj_id);
                    studentJson = gson.toJson(studentObj_idList);
                    final List<String> obj_idList = new ArrayList<>();
                    for (PhotoBean photoBean : photoList) {
                        if (photoBean.getFilePath()!=null) {
                            getPresenter().requestObjectId(photoBean.getFilePath(), photoBean.getFileName(), new PersonalCallback() {
                                @Override
                                public void upLoadFileSuccess(String obj_id) {
                                    obj_idList.add(obj_id);
                                    if (obj_idList.size() == photoList.size()-1) {
                                        String languageStr = getLanguagesString(obj_idList);
                                        getPresenter().uploadAuditCers(Message.obtain(UploadCertificateActivity.this, new String[]{languageStr, studentJson}), true);
                                    }
                                }
                            }, true);
                        }
                    }
                }
            },true);
        }else {
            //从申请语言入口进来，没有上传学生证书的
            final List<String> obj_idList = new ArrayList<>();
            for (PhotoBean photoBean : photoList) {
                if (photoBean.getFilePath()!=null) {
                    getPresenter().requestObjectId(photoBean.getFilePath(), photoBean.getFileName(), new PersonalCallback() {
                        @Override
                        public void upLoadFileSuccess(String obj_id) {
                            obj_idList.add(obj_id);
                            if (obj_idList.size() == photoList.size()-1) {
                                String languageStr = getLanguagesString(obj_idList);
                                getPresenter().uploadAddLanguages(Message.obtain(UploadCertificateActivity.this, new String[]{languageStr}), true);
                            }
                        }
                    }, true);
                }
            }
        }
    }

    private String getLanguagesString(List<String> obj_idList) {
        List<AddLanguageBean> lanList = new ArrayList<>();
        List<Integer> language_idList = gson.fromJson(languages,new TypeToken<List<Integer>>(){}.getType());
        for (Integer id : language_idList) {
            AddLanguageBean addLanguageBean = new AddLanguageBean();
            addLanguageBean.setLanguage_id(id);
            addLanguageBean.setCers(obj_idList);
            lanList.add(addLanguageBean);
        }
        return gson.toJson(lanList);
    }

    private void handleChoosePhoto() {
        imgFrom = "cers";
        selectPhotoResult();
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what){
            case UPLOAD_ADD_LANGUAGES:
            case UPLOAD_AUDIT_CERS:
                if (mLoadingDialog!=null){
                    mLoadingDialog.dismiss();
                    mLoadingDialog = null;
                }
                if (((ErrorMessageBean)message.obj).getError()==0) {
                    Toast.makeText(this,getResources().getString(R.string.submitting_success),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this,SubmitCompleteActivity.class);
                    intent.putExtra(TranslatorConstants.Common.FROM,getIntent().getStringExtra(TranslatorConstants.Common.FROM));
                    startActivity(intent);
                    finish();
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
                    handleShowImg(imgFile);
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

    private void handleShowImg(File file){
        if (imgFrom.equals("student")){
            loadImage(file.getAbsolutePath(),mStudentIv);
            mStudentPhoto = new PhotoBean();
            mStudentPhoto.setFileName(file.getName());
            mStudentPhoto.setFilePath(file.getAbsolutePath());
        }else if (imgFrom.equals("cers")){
            PhotoBean photoBean = new PhotoBean();
            photoBean.setFileName(file.getName());
            photoBean.setFilePath(file.getAbsolutePath());
            photoList.add(photoBean);
            mButton.setClickable(true);
            mButton.setBackgroundResource(R.drawable.onclick_green_btn_selector);
            try {
                mAdapter.notifyDataSetChanged(photoList);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
