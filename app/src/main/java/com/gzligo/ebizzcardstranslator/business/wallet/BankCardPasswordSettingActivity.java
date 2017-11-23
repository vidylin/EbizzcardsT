package com.gzligo.ebizzcardstranslator.business.wallet;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DialerKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.business.account.view.login.RegisterPhoneVerActivity;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.constants.CommonConstants;
import com.gzligo.ebizzcardstranslator.persistence.District;
import com.gzligo.ebizzcardstranslator.utils.PayPwdEditView;
import com.gzligo.ebizzcardstranslator.utils.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/6/7.
 */

public class BankCardPasswordSettingActivity extends BaseActivity<AddBankCardPresenter> {

    @BindView(R.id.bank_card_setting_pwd_ppedv) PayPwdEditView mBankPwdEdt;
    @BindView(R.id.bank_card_setting_pwd_tip) TextView mBankTip;
    @BindView(R.id.bank_pwd_btn) Button mButton;

    private static final int CONFIRM_PWD = 0x87;
    private static final int SETTING_PWD = 0x86;
    private static final int MSG_CODE = 0x84;
    private static final int VALIDATE_PWD_SUCCESS = 0x83;
    private static final int VALIDATE_PWD_ERROR = 0x82;
    private String pwdOne;
    private String pwdTwo;
    private String pwdNew;
    private String token;
    private String from;
    private int bankId;
    private String bankCardNo;
    private String bankCardUser;
    private String areaNumber;
    private String userPhone;

    @Override
    public AddBankCardPresenter createPresenter() {
        return new AddBankCardPresenter(new AddBankCardRepository(), this);
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_setting_bank_password;
    }

    @Override
    public void initData() {
        areaNumber = (String) SharedPreferencesUtils.getSharedPreferences(this, CommonConstants.USER_COUNTRY_ID, "", CommonConstants.USER_INFO_PRE_NAME);
        userPhone = (String) SharedPreferencesUtils.getSharedPreferences(this, CommonConstants.USER_PHONE, "", CommonConstants.USER_INFO_PRE_NAME);
        from = getIntent().getExtras().getString("FROM", null);
        switch (from) {
            case "MyWalletPasswordManagerActivity":
                break;
            case "AddBankCardActivity":
                pwdOne = getIntent().getExtras().getString("pwdOne", null);
                bankId = getIntent().getExtras().getInt("bankId");
                bankCardNo = getIntent().getExtras().getString("bankCardNo", "");
                bankCardUser = getIntent().getExtras().getString("bankCardUser", "");
                break;
            case "BankCardPasswordSettingActivity":
                pwdOne = getIntent().getExtras().getString("pwdOne", null);
                bankId = getIntent().getExtras().getInt("bankId");
                bankCardNo = getIntent().getExtras().getString("bankCardNo", "");
                bankCardUser = getIntent().getExtras().getString("bankCardUser", "");
                break;
            case "ModifyPassword":
                pwdOne = getIntent().getExtras().getString("pwdOne", "");
                token = getIntent().getExtras().getString("token", "");
                break;
            case "ModifyPasswordConfirm":
                pwdTwo = getIntent().getStringExtra("pwdTwo");
                pwdOne = getIntent().getExtras().getString("pwdOne", "");
                token = getIntent().getExtras().getString("token", "");
                break;
        }
    }

    @Override
    public void initViews() {
        mButton.setClickable(false);
        switch (from) {
            case "MyWalletPasswordManagerActivity":
                mBankTip.setText(getResources().getString(R.string.bank_password_old));
                break;
            case "AddBankCardActivity":
                mBankTip.setText(pwdOne == null ? getResources().getString(R.string.bank_password_setting_tip1) :
                        getResources().getString(R.string.bank_password_setting_confirm));
                break;
            case "ModifyPassword":
                mBankTip.setText(getResources().getString(R.string.bank_password_new));
                break;
            case "ModifyPasswordConfirm":
                mBankTip.setText(getResources().getString(R.string.bank_password_new_again));
                break;
        }

    }

    @Override
    public void initEvents() {
        mBankPwdEdt.addTextChangedListener(watcher);
        mBankPwdEdt.setKeyListener(DialerKeyListener.getInstance());
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s)) {
                if (!TextUtils.isEmpty(mBankPwdEdt.getText().toString().trim()) &&
                        (mBankPwdEdt.getText().toString().length() == 6)) {
                    switch (from) {
                        case "AddBankCardActivity":
                            pwdOne = mBankPwdEdt.getText().toString().trim();
                            startActivityForResult(new Intent(BankCardPasswordSettingActivity.this, BankCardPasswordSettingActivity.class)
                                    .putExtra("pwdOne", pwdOne)
                                    .putExtra("bankId", bankId)
                                    .putExtra("bankCardNo", bankCardNo)
                                    .putExtra("FROM", "BankCardPasswordSettingActivity")
                                    .putExtra("bankCardUser", bankCardUser), CONFIRM_PWD);
                            break;
                        case "BankCardPasswordSettingActivity":
                            pwdTwo = mBankPwdEdt.getText().toString().trim();
                            if (pwdOne.equals(pwdTwo)) {
                                mButton.setClickable(true);
                                mButton.setVisibility(View.VISIBLE);
                                mButton.setBackgroundResource(R.drawable.onclick_green_btn_selector);
                            } else {
                                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.pwd_error), Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case "MyWalletPasswordManagerActivity":
                            pwdOne = mBankPwdEdt.getText().toString().trim();
                            getPresenter().requestValidateWpasswd(pwdOne);
                            break;
                        case "ModifyPassword":
                            pwdTwo = mBankPwdEdt.getText().toString().trim();
                            startActivityForResult(new Intent(BankCardPasswordSettingActivity.this, BankCardPasswordSettingActivity.class)
                                    .putExtra("pwdTwo", pwdTwo)
                                    .putExtra("pwdOne", pwdOne)
                                    .putExtra("token", token)
                                    .putExtra("FROM", "ModifyPasswordConfirm"), CONFIRM_PWD);
                            break;
                        case "ModifyPasswordConfirm":
                            pwdNew = mBankPwdEdt.getText().toString().trim();
                            if (pwdNew.equals(pwdTwo)) {
                                mButton.setClickable(true);
                                mButton.setVisibility(View.VISIBLE);
                                mButton.setBackgroundResource(R.drawable.onclick_green_btn_selector);
                            } else {
                                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.pwd_error), Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }

                } else {
                    mButton.setClickable(false);
                    mButton.setBackgroundResource(R.mipmap.green_btn_normal);
                }
            } else {
                mButton.setClickable(false);
                mButton.setBackgroundResource(R.mipmap.green_btn_normal);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @OnClick({R.id.tv_close, R.id.bank_pwd_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_close:
                finish();
                break;
            case R.id.bank_pwd_btn:
                getPresenter().requestResetWithdrawPasswd(pwdTwo, pwdOne, token);
                break;
        }
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what) {
            case SETTING_PWD:
                switch (from) {
                    case "ModifyPasswordConfirm":
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    default:
                        getPresenter().requestValidateSms(Message.obtain(this, new String[]{areaNumber.replace("+", "") + userPhone, "1"}));
                        break;
                }
                break;
            case MSG_CODE:
                handleNext();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                break;
            case VALIDATE_PWD_SUCCESS:
                startActivityForResult(new Intent(BankCardPasswordSettingActivity.this, BankCardPasswordSettingActivity.class)
                        .putExtra("pwdOne", pwdOne)
                        .putExtra("FROM", "ModifyPassword"), CONFIRM_PWD);
                break;
            case VALIDATE_PWD_ERROR:
                Toast.makeText(this, getResources().getString(R.string.bank_password_error), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void handleNext() {
        District district = new District();
        district.setAreaNumber(areaNumber);
        Intent intent = new Intent(this, RegisterPhoneVerActivity.class);
        intent.putExtra(TranslatorConstants.Common.FROM, "BANK_CARD");
        intent.putExtra(TranslatorConstants.Login.PHONE_NUMBER, userPhone);
        intent.putExtra("DISTRICT", district);
        intent.putExtra("bankId", bankId);
        intent.putExtra("bankCardNo", bankCardNo);
        intent.putExtra("bankCardUser", bankCardUser);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CONFIRM_PWD:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
}
