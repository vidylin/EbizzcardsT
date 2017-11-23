package com.gzligo.ebizzcardstranslator.business.me;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.constants.CommonConstants;
import com.gzligo.ebizzcardstranslator.image.ImageLoader;
import com.gzligo.ebizzcardstranslator.image.glide.GlideImageConfig;
import com.gzligo.ebizzcardstranslator.image.transformation.CustomShapeTransformation;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;
import com.gzligo.ebizzcardstranslator.persistence.ErrorMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.UserBean;
import com.gzligo.ebizzcardstranslator.utils.CropImageActivity;
import com.gzligo.ebizzcardstranslator.utils.DialogUtils;
import com.gzligo.ebizzcardstranslator.utils.FileManager;
import com.gzligo.ebizzcardstranslator.utils.FileUtils;
import com.gzligo.ebizzcardstranslator.utils.ScaleAvatarActivity;
import com.gzligo.ebizzcardstranslator.utils.TimeUtils;
import com.gzligo.ebizzcardstranslator.utils.TranslatorCallBack;
import com.tangxiaolv.telegramgallery.GalleryActivity;
import com.tangxiaolv.telegramgallery.GalleryConfig;
import com.tangxiaolv.telegramgallery.ModelAdapter.PhotoModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by ZuoJian on 2017/6/1.
 */

public class MyProfileActivity extends BaseActivity<PersonalPresenter> {

    @BindView(R.id.myprofile_arrow) ImageView myprofileArrow;
    @BindView(R.id.myprofile_avatar_iv) ImageView myprofileAvatarIv;
    @BindView(R.id.myprofile_avatar_rl) RelativeLayout myprofileAvatarRl;
    @BindView(R.id.myprofile_nickname_arrow) ImageView myprofileNicknameArrow;
    @BindView(R.id.myprofile_nickname_txt) TextView myprofileNicknameTxt;
    @BindView(R.id.myprofile_nickname_rl) RelativeLayout myprofileNicknameRl;
    @BindView(R.id.myprofile_account_txt) TextView myprofileAccountTxt;
    @BindView(R.id.myprofile_account_rl) RelativeLayout myprofileAccountRl;
    @BindView(R.id.myprofile_star_1) ImageView myprofileStar1;
    @BindView(R.id.myprofile_star_2) ImageView myprofileStar2;
    @BindView(R.id.myprofile_star_3) ImageView myprofileStar3;
    @BindView(R.id.myprofile_star_4) ImageView myprofileStar4;
    @BindView(R.id.myprofile_star_5) ImageView myprofileStar5;
    @BindView(R.id.myprofile_star_lvl_rl) RelativeLayout myprofileStarLvlRl;
    @BindView(R.id.myprofile_birthday_arrow) ImageView myprofileBirthdayArrow;
    @BindView(R.id.myprofile_birthday_txt) TextView myprofileBirthdayTxt;
    @BindView(R.id.myprofile_birthday_rl) RelativeLayout myprofileBirthdayRl;
    @BindView(R.id.myprofile_sex_arrow) ImageView myprofileSexArrow;
    @BindView(R.id.myprofile_sex_txt) TextView myprofileSexTxt;
    @BindView(R.id.myprofile_sex_rl) RelativeLayout myprofileSexRl;
    @BindView(R.id.myprofile_location_txt) TextView myprofileLocationTxt;
    @BindView(R.id.myprofile_location_rl) RelativeLayout myprofileLocationRl;
    private Dialog mDialog, mLoadingDialog, mPictureDialog;
    public static final int RESULT_CROP = 0x00;
    public static final int REQUEST_TAKE_PHOTO = 0x05;

    public static final int UPLOAD_NICKNAME = 0x01;
    public static final int UPLOAD_BIRTHDAY = 0x02;
    public static final int UPLOAD_SEX = 0x03;
    public static final int UPLOAD_PORTRAIT = 0x04;
    private String userCountryName,filePath;
    private UserBean userBean;
    private long updateBirthDay;

    @Override
    public PersonalPresenter createPresenter() {
        return new PersonalPresenter(new PersonalRepository());
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_myprofile;
    }

    @Override
    public void initData() {
        SharedPreferences preferences = AppManager.get()
                .getApplication()
                .getSharedPreferences(CommonConstants.USER_INFO_PRE_NAME, Context.MODE_PRIVATE);
        userCountryName = preferences.getString(CommonConstants.USER_COUNTRY_NAME,"");
    }

    @Override
    public void initViews() {
        myprofileLocationTxt.setText(userCountryName);
        userBean = getPresenter().getUserBean();
        showAvatar(userBean.getPortrait_id());
        myprofileNicknameTxt.setText(userBean.getNickname());
        SharedPreferences sharedPreferences = AppManager.get()
                .getApplication()
                .getSharedPreferences(CommonConstants.USER_INFO_PRE_NAME, Context.MODE_PRIVATE);
        myprofileAccountTxt.setText(sharedPreferences.getString(CommonConstants.USER_ID,""));
        if(userBean.getSex()==1){
            myprofileSexTxt.setText(getResources().getString(R.string.myprofile_male));
        }else{
            myprofileSexTxt.setText(getResources().getString(R.string.myprofile_female));
        }
        myprofileBirthdayTxt.setText(TimeUtils.getyMd(userBean.getBirthday()));
        int level = (int) userBean.getLevel();
        switch (level) {
            case 0:
                break;
            case 1:
                myprofileStar1.setVisibility(View.VISIBLE);
                break;
            case 2:
                myprofileStar1.setVisibility(View.VISIBLE);
                myprofileStar2.setVisibility(View.VISIBLE);
                break;
            case 3:
                myprofileStar1.setVisibility(View.VISIBLE);
                myprofileStar2.setVisibility(View.VISIBLE);
                myprofileStar3.setVisibility(View.VISIBLE);
                break;
            case 4:
                myprofileStar1.setVisibility(View.VISIBLE);
                myprofileStar2.setVisibility(View.VISIBLE);
                myprofileStar3.setVisibility(View.VISIBLE);
                myprofileStar4.setVisibility(View.VISIBLE);
                break;
            case 5:
                myprofileStar1.setVisibility(View.VISIBLE);
                myprofileStar2.setVisibility(View.VISIBLE);
                myprofileStar3.setVisibility(View.VISIBLE);
                myprofileStar4.setVisibility(View.VISIBLE);
                myprofileStar5.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void initEvents() {

    }

    @OnClick({R.id.tv_close,R.id.myprofile_avatar_iv, R.id.myprofile_avatar_rl, R.id.myprofile_nickname_rl, R.id.myprofile_birthday_rl, R.id.myprofile_sex_rl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_close:
                Intent intent = getIntent();
                if (!myprofileNicknameTxt.getText().toString().trim().equals(userBean.getNickname())) {
                    intent.putExtra(TranslatorConstants.Login.NICKNAME, myprofileNicknameTxt.getText().toString().trim());
                }
                if (!TextUtils.isEmpty(filePath)) {
                    intent.putExtra(TranslatorConstants.Login.PORTRAIT, filePath);
                }
                setResult(RESULT_OK,intent);
                finish();
                break;
            case R.id.myprofile_avatar_iv:
                ArrayList<String> mAvatarList = new ArrayList<>();
                if (!TextUtils.isEmpty(filePath)){
                    mAvatarList.add(filePath);
                }else {
                    mAvatarList.add(userBean.getPortrait_id());
                }
                ScaleAvatarActivity.startScaleAvatarActivity(this,mAvatarList,0,myprofileAvatarIv);
                break;
            case R.id.myprofile_avatar_rl:
                handleAvatar();
                break;
            case R.id.myprofile_nickname_rl:
                handleEditNickName();
                break;
            case R.id.myprofile_birthday_rl:
                handleBirthday();
                break;
            case R.id.myprofile_sex_rl:
                handleSex();
                break;
        }
    }

    private void handleAvatar() {
        mPictureDialog = DialogUtils.selectPictureDialog(this, new TranslatorCallBack.onSelectPictureClickLister() {
            @Override
            public void onTakePhoto() {
                requestSystemTakePhoto(new TakePhotoListener() {
                    @Override
                    public void takePhoto(File filePath) {
//                        //裁剪不要用系统的，不过优先级比较低，后面再说吧
                        startCropImage(Uri.parse("file://" + filePath));
                    }
                });
            }

            @Override
            public void onFromAlbum() {
                requestSystemPhoto(new OnPhotoListener() {
                    @Override
                    public void onPhoto(ArrayList<PhotoModel> photos) {
                        filePath = photos.get(0).getOriginalPath();
                        startCropImage(Uri.parse("file://" + filePath));
                    }
                });
            }
        });
    }

    private void handleSex() {
        final List<String> mSexList = new ArrayList<>();
        mSexList.add(getResources().getString(R.string.myprofile_male));
        mSexList.add(getResources().getString(R.string.myprofile_female));
        OptionsPickerView opvSex = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String sex;
                mLoadingDialog = DialogUtils.showLoadingDialog(MyProfileActivity.this, getResources().getString(R.string.submitting));
                if (mSexList.get(options1).equals(getResources().getString(R.string.myprofile_male))) {
                    sex = "1";
                } else {
                    sex = "0";
                }
                getPresenter().uploadSex(Message.obtain(MyProfileActivity.this, new String[]{sex}), true);
            }
        }).setCancelText(getResources().getString(R.string.cancel))
                .setSubmitText(getResources().getString(R.string.confirm))
                .setTitleText(getResources().getString(R.string.myprofile_choose_sex))
                .setLineSpacingMultiplier(2.0f)
                .setCyclic(false, false, false)
                .build();
        opvSex.setPicker(mSexList);
        opvSex.show();
    }

    private void handleBirthday() {
        TimePickerView pvBirthday = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                mLoadingDialog = DialogUtils.showLoadingDialog(MyProfileActivity.this, getResources().getString(R.string.submitting));
                updateBirthDay = date.getTime();
                String birthday = TimeUtils.getyMdHms(date.getTime());
                myprofileBirthdayTxt.setText(TimeUtils.getyMd(updateBirthDay));
                getPresenter().uploadBirthday(Message.obtain(MyProfileActivity.this, new String[]{birthday}), true);
            }
        }).setCancelText(getResources().getString(R.string.cancel))
                .setSubmitText(getResources().getString(R.string.confirm))
                .setLabel(getResources().getString(R.string.year), getResources().getString(R.string.month), getResources().getString(R.string.day), "", "", "")
                .isCenterLabel(false)
                .setType(new boolean[]{true, true, true, false, false, false})//设置为年月日显示
                .build();
        pvBirthday.show();
    }

    private void handleEditNickName() {
        mDialog = DialogUtils.showEditDialog(this, getResources().getString(R.string.myprofile_edit_nickname),
                getResources().getString(R.string.hint_input_name), myprofileNicknameTxt.getText().toString(), new TranslatorCallBack.OnEditNameClickListener() {
                    @Override
                    public void onConfirm(String name) {
                        if (TextUtils.isEmpty(name)) {
                            Toast.makeText(MyProfileActivity.this, getResources().getString(R.string.myprofile_input_nickname), Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            handleUploadNickname(name);
                        }
                    }
                });
    }

    private void handleUploadNickname(String name) {
        mLoadingDialog = DialogUtils.showLoadingDialog(this, getResources().getString(R.string.submitting));
        getPresenter().uploadNickname(Message.obtain(this, new String[]{name}), true);
    }

    @Override
    public void handlePresenterCallback(Message message) {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
        if (((ErrorMessageBean) message.obj).getError() == 0) {
            Toast.makeText(MyProfileActivity.this, getResources().getString(R.string.submitting_success), Toast.LENGTH_SHORT).show();
            switch (message.what) {
                case UPLOAD_NICKNAME:
                    myprofileNicknameTxt.setText((String) message.objs[0]);
                    break;
                case UPLOAD_BIRTHDAY:
                    break;
                case UPLOAD_SEX:
                    if ((message.objs[0]).equals("1")) {
                        myprofileSexTxt.setText(getResources().getString(R.string.myprofile_male));
                    } else {
                        myprofileSexTxt.setText(getResources().getString(R.string.myprofile_female));
                    }
                    break;
                case UPLOAD_PORTRAIT:
                    filePath = (String) message.objs[0];
                    ImageLoader.get().loadImage(AppManager.get().getApplication(), GlideImageConfig
                            .builder()
                            .url((String) message.objs[0])
                            .imageView(myprofileAvatarIv)
                            .imgWidth(myprofileAvatarIv.getHeight())
                            .imgHeigth(myprofileAvatarIv.getHeight())
                            .errorPic(R.mipmap.default_head_portrait)
                            .isClearMemory(false)
                            .transformation(new CustomShapeTransformation(AppManager.get().getApplication(), 50))
                            .build());
                    break;
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.submitting_filed) + ((ErrorMessageBean) message.obj).getMessage(), Toast.LENGTH_SHORT).show();
        }
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
        mTakePhotoFile = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
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
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                systemTakePhoto();
                break;
            case PhotoModel.RESULT_SINGLE:
                singlePhoto(data);
                break;
            case RESULT_CROP:
                if (data!=null&&data.getByteArrayExtra(TranslatorConstants.Common.DATA)!=null) {
                    mLoadingDialog = DialogUtils.showLoadingDialog(MyProfileActivity.this, getResources().getString(R.string.submitting));
                    byte[] mUseAvatarByte = data.getByteArrayExtra(TranslatorConstants.Common.DATA);
                    File file = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
                    File imgFile = FileUtils.saveFile(file.getAbsolutePath(), mUseAvatarByte);
                    getPresenter().uploadPortrait(Message.obtain(MyProfileActivity.this, new String[]{imgFile.getAbsolutePath()}), true);
                }
                break;
        }
    }

    private void showAvatar(String portraitId) {
        ImageLoader.get().loadImage(AppManager.get().getApplication(), GlideImageConfig
                .builder()
                .url(HttpUtils.MEDIA_HOST + portraitId)
                .imageView(myprofileAvatarIv)
                .errorPic(R.mipmap.default_head_portrait)
                .isClearMemory(false)
                .transformation(new CustomShapeTransformation(AppManager.get().getApplication(), 39))
                .build());
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
