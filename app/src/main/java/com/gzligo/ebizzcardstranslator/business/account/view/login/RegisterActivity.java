package com.gzligo.ebizzcardstranslator.business.account.view.login;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.business.wallet.UserServiceAgreementActivity;
import com.gzligo.ebizzcardstranslator.common.ToolActionBar;
import com.gzligo.ebizzcardstranslator.image.transformation.CustomShapeTransformation;
import com.gzligo.ebizzcardstranslator.image.ImageLoader;
import com.gzligo.ebizzcardstranslator.image.glide.GlideImageConfig;
import com.gzligo.ebizzcardstranslator.persistence.District;
import com.gzligo.ebizzcardstranslator.persistence.ErrorMessageBean;
import com.gzligo.ebizzcardstranslator.utils.CropImageActivity;
import com.gzligo.ebizzcardstranslator.utils.DialogUtils;
import com.gzligo.ebizzcardstranslator.utils.FileManager;
import com.gzligo.ebizzcardstranslator.utils.FileUtils;
import com.gzligo.ebizzcardstranslator.utils.LanguageUtils;
import com.gzligo.ebizzcardstranslator.utils.TranslatorCallBack;
import com.tangxiaolv.telegramgallery.GalleryActivity;
import com.tangxiaolv.telegramgallery.GalleryConfig;
import com.tangxiaolv.telegramgallery.ModelAdapter.PhotoModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/5/26.
 */

public class RegisterActivity extends BaseActivity<LoginPresenter> implements IView{

    @BindView(R.id.register_actionbar)
    ToolActionBar mActionBar;
    @BindView(R.id.register_nickname_txt)
    EditText mNicknameEdt;
    @BindView(R.id.register_tv_district_num)
    TextView mDistrictNum;
    @BindView(R.id.register_tv_phone_num)
    EditText mPhoneTxt;
    @BindView(R.id.register_tv_pwd)
    EditText mPwdTxt;
    @BindView(R.id.register_avatar_iv)
    ImageView mAvatarIv;
    @BindView(R.id.register_btn)
    Button registerBtn;
    @BindView(R.id.pwd_eye_iv)
    ImageView mEye;
    @BindView(R.id.tv_country_name) TextView tvCountryName;

    private boolean isEyeOpen = false;
    private Dialog mDialog,mLoadingDialog,mPictureDialog;
    private String cc_codeDistrict,mPassword,mPhone,mNickname,mPortrait;
    public static final int MSG_SEND_FOR_CODE = 0x00;
    public static final int REQUEST_TAKE_PHOTO = 0x01;
    public static final int RESULT_CROP = 0x02;
    private static final int COUNTRY_SELECT_RESUlAT = 0x03;
    private District district;
    private JSONObject jsonObject;

    @Override
    public LoginPresenter createPresenter() {
        return new LoginPresenter(new LoginRepository(),this);
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_register;
    }

    @Override
    public void initViews() {
        mActionBar.setActionbarCenterTitleMaxEms(12);
        registerBtn.setClickable(false);
        mPwdTxt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mPwdTxt.setTypeface(Typeface.DEFAULT);
        mPwdTxt.setTransformationMethod(new PasswordTransformationMethod());
    }

    @Override
    public void initData() {

    }
    @Override
    public void initEvents() {
        mPwdTxt.addTextChangedListener(watcher);
        mPhoneTxt.addTextChangedListener(watcher);
        mNicknameEdt.addTextChangedListener(watcher);
    }

    @OnClick({R.id.tv_close,R.id.register_avatar_iv,R.id.ll_selector_country,R.id.register_btn,R.id.register_protocol_txt
                ,R.id.pwd_eye_iv,R.id.phone_clear_iv})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_close:
                finish();
                break;
            case R.id.register_avatar_iv:
                handleAvatar();
                break;
            case R.id.ll_selector_country:
                startActivityForResult(new Intent(this, CountrySelectActivity.class), COUNTRY_SELECT_RESUlAT);
                break;
            case R.id.register_btn:
                handleRegister();
                break;
            case R.id.pwd_eye_iv:
                handleEyeShow();
                break;
            case R.id.register_protocol_txt:
                Intent intent = new Intent(this, UserServiceAgreementActivity.class);
                String url = null;
                if (LanguageUtils.getLanguage(this).equals(TranslatorConstants.SharedPreferences.LANGUAGE_CH)){
                    url = "https://www.hisir.net/protocol/?page=proto_transregister";
                }else {
                    url = "https://www.hisir.net/protocol/?page=proto_transregister";//英文版网址尚未完善，// TODO: 2017/8/25
                }
                intent.putExtra("actionbar_name",getResources().getString(R.string.register_protocol_actionbar));
                intent.putExtra(TranslatorConstants.Common.DATA,url);
                startActivity(intent);
                break;
            case R.id.phone_clear_iv:
                mPhoneTxt.setText("");
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

    private void handleEyeShow() {
        isEyeOpen = !isEyeOpen;
        if (isEyeOpen){
            mEye.setBackgroundResource(R.mipmap.login_eye_yes);
            mPwdTxt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }else {
            mEye.setBackgroundResource(R.mipmap.login_eye_no);
            mPwdTxt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        String text = mPwdTxt.getText().toString();
        mPwdTxt.setSelection(text.length());
    }

    private void handleRegister() {
        mNickname = mNicknameEdt.getText().toString().trim();
        mPhone = mPhoneTxt.getText().toString().trim();
        cc_codeDistrict = mDistrictNum.getText().toString().trim().substring(1,mDistrictNum.getText().toString().trim().length());
        mPassword = mPwdTxt.getText().toString().trim();

        Pattern pNum = Pattern.compile("[0-9]*");
        Pattern pChar = Pattern.compile("[a-zA-Z]+");
        Pattern pAll = Pattern.compile("[A-Za-z0-9]+");

        if (mPassword.length()<6||mPassword.equals(mPhone)
                ||pNum.matcher(mPassword).matches()
                ||pChar.matcher(mPassword).matches()
                ||!pAll.matcher(mPassword).matches()){
            Toast.makeText(RegisterActivity.this,getResources().getString(R.string.hint_pwd_form),Toast.LENGTH_SHORT).show();
            return;
        }

        mDialog = DialogUtils.showTitleContentDialog(this, getResources().getString(R.string.register_confirm_phone),
                getResources().getString(R.string.register_sms_code_send_to) +"\n" + mDistrictNum.getText().toString().trim()+
                mPhoneTxt.getText().toString().trim(),getResources().getString(R.string.cancel), getResources().getString(R.string.confirm),
                new TranslatorCallBack.OnDialogClickListener() {
                    @Override
                    public void onConfirm() {
                        register();
                    }
                });
    }

    private void register(){
        mLoadingDialog = DialogUtils.showLoadingDialog(this,getResources().getString(R.string.register_sms_code_sending));
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TranslatorConstants.Login.PHONE,mPhone);
            jsonObject.put(TranslatorConstants.Login.CC_CODE,cc_codeDistrict);
            jsonObject.put(TranslatorConstants.Login.NICKNAME,mNickname);
            jsonObject.put(TranslatorConstants.Login.PASSWORD,mPassword);
            if (mPortrait!=null&&!TextUtils.isEmpty(mPortrait)){
                jsonObject.put(TranslatorConstants.Login.PORTRAIT,mPortrait);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getPresenter().sendSmsForCode(Message.obtain(this,new String[]{cc_codeDistrict+mPhone,TranslatorConstants.Login.SMS_TYPE_REGISTER}),true);
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what){
            case MSG_SEND_FOR_CODE:
                if (mLoadingDialog!=null){
                    mLoadingDialog.dismiss();
                    mLoadingDialog = null;
                }
                if (((ErrorMessageBean)message.obj).getError()==0){
                    Log.e("TAG", "register_send_sms_code------->>>>>>>>" + message.obj.toString());
                    Intent intent = new Intent(this,RegisterPhoneVerActivity.class);
                    intent.putExtra(TranslatorConstants.Common.JSON,jsonObject.toString());
                    startActivity(intent);
                    finish();
                }else {
                    Log.e("TAG", "register_send_sms_code------->>>>>>>>" + ((ErrorMessageBean)message.obj).getMessage());
                    Toast.makeText(this,((ErrorMessageBean)message.obj).getMessage(),Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s)) {
                if (!TextUtils.isEmpty(mPhoneTxt.getText().toString().trim())&&
                        !TextUtils.isEmpty(mPwdTxt.getText().toString().trim())&&
                        !TextUtils.isEmpty(mNicknameEdt.getText().toString().trim())) {
                    registerBtn.setBackgroundResource(R.drawable.onclick_green_btn_selector);
                    registerBtn.setClickable(true);
                }else {
                    registerBtn.setBackgroundResource(R.mipmap.green_btn_normal);
                    registerBtn.setClickable(false);
                }
            }else {
                registerBtn.setBackgroundResource(R.mipmap.green_btn_normal);
                registerBtn.setClickable(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

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

    private void showImageView(String url){
        mPortrait = url;
        ImageLoader.get().loadImage(AppManager.get().getApplication(), GlideImageConfig
                .builder()
                .url(mPortrait)
                .imageView(mAvatarIv)
                .imgWidth(mAvatarIv.getHeight())
                .imgHeigth(mAvatarIv.getHeight())
                .errorPic(R.mipmap.default_head_portrait)
                .isClearMemory(false)
                .transformation(new CustomShapeTransformation(AppManager.get().getApplication(),50))
                .build());
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
                    showImageView(imgFile.getAbsolutePath());
                }
                break;
            case COUNTRY_SELECT_RESUlAT:
                district = (District) data.getExtras().getSerializable("DISTRICT");
                mDistrictNum.setText(district.getAreaNumber());
                tvCountryName.setText(district.getLocalName());
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
