package com.gzligo.ebizzcardstranslator.business.account.view.login;

import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/5/27.
 */

public class ResetPasswordActivity extends BaseActivity<ForgetPhonePresenter>{

    @BindView(R.id.reset_new_pwd_edt)
    EditText mNewPwd;
    @BindView(R.id.reset_confirm_pwd)
    EditText mConfirmPwd;
    @BindView(R.id.reset_pwd_btn)
    Button mNext;
    private String token;
    private String phoneNum;
    private static final int RESET_PWD_SUCCESS = 0x51;

    @Override
    public ForgetPhonePresenter createPresenter() {
        return new ForgetPhonePresenter(new ForgetPhoneRepository());
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_reset_password;
    }

    @Override
    public void initViews() {
        mNewPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mConfirmPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    @Override
    public void initData() {
        token = getIntent().getExtras().getString("TOKEN");
        phoneNum = getIntent().getExtras().getString("PHONE_NUM");
    }

    @Override
    public void initEvents() {
        mNewPwd.addTextChangedListener(watcher);
        mConfirmPwd.addTextChangedListener(watcher);
        mNext.setClickable(false);
    }

    @OnClick({R.id.tv_close,R.id.reset_pwd_btn})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_close:
                finish();
                break;
            case R.id.reset_pwd_btn:
                if(comparePwdValue()){
                    Pattern pNum = Pattern.compile("[0-9]*");
                    Pattern pChar = Pattern.compile("[a-zA-Z]+");
                    Pattern pAll = Pattern.compile("[A-Za-z0-9]+");

                    String pwd = mConfirmPwd.getText().toString().trim();
                    if (pwd.length()<6||pwd.equals(phoneNum)
                            ||pNum.matcher(pwd).matches()
                            ||pChar.matcher(pwd).matches()
                            ||!pAll.matcher(pwd).matches()){
                        Toast.makeText(this,getResources().getString(R.string.hint_pwd_form),Toast.LENGTH_SHORT).show();
                        return;
                    }
                    getPresenter().requestResetPassword(Message.obtain(this,new String[]{phoneNum,pwd,token}));
                }else{
                    Toast.makeText(this,getResources().getString(R.string.pwd_dissimilarity),Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void handleReset() {
        startActivity(new Intent(this,ResetCompleteActivity.class));
        finish();
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s)) {
                if (!TextUtils.isEmpty(mNewPwd.getText().toString().trim())&&
                        !TextUtils.isEmpty(mConfirmPwd.getText().toString().trim())) {
                    mNext.setBackgroundResource(R.drawable.onclick_green_btn_selector);
                    mNext.setClickable(true);
                }else {
                    mNext.setBackgroundResource(R.mipmap.green_btn_normal);
                    mNext.setClickable(false);
                }
            }else {
                mNext.setBackgroundResource(R.mipmap.green_btn_normal);
                mNext.setClickable(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private boolean comparePwdValue(){
        String pwdOne = mNewPwd.getText().toString().trim();
        String pwdTwo = mConfirmPwd.getText().toString().trim();
        if(pwdOne.equals(pwdTwo)){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what){
            case RESET_PWD_SUCCESS:
                handleReset();
                break;
        }
    }
}
