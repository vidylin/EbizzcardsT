package com.gzligo.ebizzcardstranslator.business.account.view.login;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.business.MainActivity;
import com.gzligo.ebizzcardstranslator.common.ToolActionBar;
import com.gzligo.ebizzcardstranslator.constants.CommonConstants;
import com.gzligo.ebizzcardstranslator.persistence.District;
import com.gzligo.ebizzcardstranslator.persistence.LoginBean;
import com.gzligo.ebizzcardstranslator.utils.DialogUtils;
import com.gzligo.ebizzcardstranslator.utils.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.OnClick;

import static com.gzligo.ebizzcardstranslator.R.id.tv_district_num;

/**
 * Created by ZuoJian on 2017/5/24.
 */

public class LoginActivity extends BaseActivity<LoginPresenter> {

    @BindView(R.id.login_actionbar)
    ToolActionBar mActionBar;
    @BindView(R.id.txt_register)
    TextView registerTxt;
    @BindView(R.id.txt_forget)
    TextView forgetTxt;
    @BindView(R.id.btn_login)
    Button mLoginBtn;
    @BindView(R.id.tv_phone_num)
    EditText mPhoneTxt;
    @BindView(R.id.tv_pwd)
    EditText mPwdTxt;
    @BindView(R.id.pwd_eye_iv)
    ImageView mEye;
    @BindView(tv_district_num)
    TextView mDistrictNum;
    @BindView(R.id.tv_country_name) TextView tvCountryName;

    private boolean isEyeOpen = false;
    private Dialog mLoadingDialog;
    private District district;
    private static final int COUNTRY_SELECT_RESUlAT = 0x00;
    private String phoneNum;
    private String pwd;
    public static final int MSG_LOGIN = 0x01;


    @Override
    public LoginPresenter createPresenter() {
        return new LoginPresenter(new LoginRepository(),this);
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_login;
    }

    @Override
    public void initViews() {
        mActionBar.setActionbarCenterTitleMaxEms(12);
        mLoginBtn.setClickable(false);
        mPwdTxt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mPwdTxt.setTypeface(Typeface.DEFAULT);
        mPwdTxt.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    @Override
    public void initData() {
        phoneNum = (String) SharedPreferencesUtils.getSharedPreferences(AppManager.get()
                .getApplication(), CommonConstants.USER_PHONE,"",CommonConstants.USER_INFO_PRE_NAME);
        pwd = (String) SharedPreferencesUtils.getSharedPreferences(AppManager.get()
                .getApplication(), CommonConstants.USER_PWD,"",CommonConstants.USER_INFO_PRE_NAME);
    }

    @Override
    public void initEvents() {
        mPwdTxt.addTextChangedListener(watcher);
        mPhoneTxt.addTextChangedListener(watcher);
        mPhoneTxt.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        mPhoneTxt.setText(phoneNum);
        mPwdTxt.setText(pwd);
    }

    @OnClick({R.id.ll_selector_country, R.id.btn_login, R.id.txt_register, R.id.txt_forget, R.id.pwd_eye_iv,R.id.phone_clear_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_selector_country:
                startActivityForResult(new Intent(this, CountrySelectActivity.class), COUNTRY_SELECT_RESUlAT);
                break;
            case R.id.btn_login:
                if(null==district){
                    district = new District();
                    district.setAreaNumber("+86");
                    district.setLocalName(getResources().getString(R.string.login_china));
                }
                getPresenter().setUserCountry(district);
                String[] permissions = new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.CAMERA};
                requestPermission(1, permissions, new Runnable() {
                    @Override
                    public void run() {
                        handleLogin();
                    }
                });
                break;
            case R.id.txt_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.txt_forget:
                if(null==district){
                    district = new District();
                    district.setAreaNumber("+86");
                    district.setLocalName(getResources().getString(R.string.login_china));
                }
                startActivity(new Intent(this, ForgetPhoneActivity.class).putExtra("DISTRICT",district));
                break;
            case R.id.pwd_eye_iv:
                handleEyeShow();
                break;
            case R.id.phone_clear_iv:
                mPhoneTxt.setText("");
                break;
        }
    }

    private void handleLogin() {
        String cc_codeDistrict = mDistrictNum.getText().toString().trim().substring(1,mDistrictNum.getText().toString().trim().length());
        mLoadingDialog = DialogUtils.showLoadingDialog(this,getResources().getString(R.string.login_being_login));
        getPresenter().login(Message.obtain(this, new String[]{mPhoneTxt.getText().toString(),
                mPwdTxt.getText().toString()}), true,cc_codeDistrict);
    }

    private void handleEyeShow() {
        isEyeOpen = !isEyeOpen;
        if (isEyeOpen) {
            mEye.setBackgroundResource(R.mipmap.login_eye_yes);
            mPwdTxt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            mEye.setBackgroundResource(R.mipmap.login_eye_no);
            mPwdTxt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        String text = mPwdTxt.getText().toString();
        mPwdTxt.setSelection(text.length());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case COUNTRY_SELECT_RESUlAT:
                    district = (District) data.getExtras().getSerializable("DISTRICT");
                    mDistrictNum.setText(district.getAreaNumber());
                    tvCountryName.setText(district.getLocalName());
                    break;
            }
        }
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s)) {
                if (!TextUtils.isEmpty(mPhoneTxt.getText().toString().trim()) &&
                        !TextUtils.isEmpty(mPwdTxt.getText().toString().trim())) {
                    mLoginBtn.setBackgroundResource(R.drawable.onclick_green_btn_selector);
                    mLoginBtn.setClickable(true);
                } else {
                    mLoginBtn.setBackgroundResource(R.mipmap.green_btn_normal);
                    mLoginBtn.setClickable(false);
                }
            } else {
                mLoginBtn.setBackgroundResource(R.mipmap.green_btn_normal);
                mLoginBtn.setClickable(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what) {
            case MSG_LOGIN:
                if (mLoadingDialog!=null){
                    mLoadingDialog.dismiss();
                    mLoadingDialog = null;
                }
                if (((LoginBean)message.obj).getAccess_token()!=null){
                    Toast.makeText(this,getResources().getString(R.string.login_success),Toast.LENGTH_SHORT).show();
                    MainActivity.startMainActivity();
                }else {
                    if (((LoginBean)message.obj).getError().equals("invalid_grant")){
                        //密码错误
                        Toast.makeText(this,getResources().getString(R.string.login_password_wrong),Toast.LENGTH_SHORT).show();
                    }else if (((LoginBean)message.obj).getError().equals("20010201")){
                        //用户不存在
                        Toast.makeText(this,getResources().getString(R.string.login_user_not_registered),Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(this, getResources().getString(R.string.login_internet_error), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
        }
    }
}
