package com.gzligo.ebizzcardstranslator.business.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.business.account.view.login.RegisterPhoneVerActivity;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.constants.CommonConstants;
import com.gzligo.ebizzcardstranslator.manager.CommonBeanManager;
import com.gzligo.ebizzcardstranslator.persistence.Bank;
import com.gzligo.ebizzcardstranslator.persistence.Country;
import com.gzligo.ebizzcardstranslator.persistence.District;
import com.gzligo.ebizzcardstranslator.persistence.MyWalletRecordBean;
import com.gzligo.ebizzcardstranslator.persistence.UserBean;
import com.gzligo.ebizzcardstranslator.utils.LanguageUtils;
import com.gzligo.ebizzcardstranslator.utils.SharedPreferencesUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by LWD on 2017/6/2.
 */

public class AddBankCardActivity extends BaseActivity<AddBankCardPresenter> {

    private static final int RESULT_COUNTRY = 0x00;
    private static final int GET_COUNTRY = 0x10;
    private static final int RESULT_BANK = 0x20;
    private static final int RESULT_BANK_POINT = 0x30;
    private static final int MSG_CODE = 0x84;
    private static final int ADD_BANK = 0x83;

    @BindView(R.id.add_bank_name_txt) TextView mNameTxt;
    @BindView(R.id.add_bank_card_num_edt) EditText mCardNumEdt;
    @BindView(R.id.add_bank_country_txt) TextView mCountryTxt;
    @BindView(R.id.add_bank_bank_txt) TextView mBankTxt;
    @BindView(R.id.add_bank_open_account_txt) TextView mOpenAccountTxt;
    @BindView(R.id.add_bank_btn) Button addBankBtn;

    private UserBean userBean;
    private ArrayList<Country> countryList;
    private int choiceBankPosition = -1;
    private int choiceCountryPosition = 1;
    private int stateId = 1;
    private int bankTypeId = -1;
    private int bankId;
    private int hasPwd;
    private String areaNumber;
    private String userPhone;

    @Override
    public AddBankCardPresenter createPresenter() {
        return new AddBankCardPresenter(new AddBankCardRepository(), this);
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_add_bankcard;
    }

    @Override
    public void initData() {
        areaNumber = (String) SharedPreferencesUtils.getSharedPreferences(this, CommonConstants.USER_COUNTRY_ID, "", CommonConstants.USER_INFO_PRE_NAME);
        userPhone = (String) SharedPreferencesUtils.getSharedPreferences(this, CommonConstants.USER_PHONE, "", CommonConstants.USER_INFO_PRE_NAME);
        hasPwd = getIntent().getIntExtra("hasPwd", -1);
        userBean = getPresenter().getUserBeanInfo();
        getPresenter().requestGetState();
    }

    @Override
    public void initViews() {
        mNameTxt.setText(userBean.getUsername());
    }

    @Override
    public void initEvents() {
        mNameTxt.addTextChangedListener(watcher);
        mCardNumEdt.addTextChangedListener(watcher);
        mCountryTxt.addTextChangedListener(watcher);
        mBankTxt.addTextChangedListener(watcher);
        mOpenAccountTxt.addTextChangedListener(watcher);
    }

    @OnClick({R.id.tv_close, R.id.add_bank_card_country_rl, R.id.add_bank_card_bank_rl, R.id.add_bank_open_account_rl,
            R.id.add_bank_protocol_txt, R.id.add_bank_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_close:
                CommonBeanManager.getInstance().initMyWalletInfo();
                finish();
                break;
            case R.id.add_bank_card_country_rl:
                startActivityForResult(new Intent(this, CountryAndBankListActivity.class)
                        .putExtra("countryList", countryList)
                        .putExtra("TO", "COUNTRY")
                        .putExtra("choicePosition", choiceCountryPosition), RESULT_COUNTRY);
                break;
            case R.id.add_bank_card_bank_rl:
                startActivityForResult(new Intent(this, CountryAndBankListActivity.class)
                        .putExtra("TO", "BANK")
                        .putExtra("choicePosition", choiceBankPosition), RESULT_BANK);
                break;
            case R.id.add_bank_open_account_rl:
                if (bankTypeId == -1) {
                    Toast.makeText(this, getResources().getString(R.string.add_bank_bank_hint), Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivityForResult(new Intent(this, OpeningPointActivity.class)
                        .putExtra("stateId", stateId)
                        .putExtra("bankTypeId", bankTypeId), RESULT_BANK_POINT);
                break;
            case R.id.add_bank_protocol_txt:
                startActivity(new Intent(this, UserServiceAgreementActivity.class));
                break;
            case R.id.add_bank_btn:
                String bankCardNo = mCardNumEdt.getText().toString().trim();
                if (hasPwd == 0) {
                    startActivityForResult(new Intent(this, BankCardPasswordSettingActivity.class)
                            .putExtra("FROM", "AddBankCardActivity")
                            .putExtra("bankId", bankId)
                            .putExtra("bankCardNo", bankCardNo)
                            .putExtra("bankCardUser", userBean.getUsername()), ADD_BANK);
                } else {
                    getPresenter().requestValidateSms(Message.obtain(this, new String[]{areaNumber.replace("+", "") + userPhone, "1"}));
                }
                break;
        }
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what) {
            case GET_COUNTRY:
                MyWalletRecordBean<Country> myWalletRecordBean = (MyWalletRecordBean<Country>) message.obj;
                countryList = (ArrayList<Country>) myWalletRecordBean.getList();
                if (null != countryList && countryList.size() > 0) {
                    if (LanguageUtils.getLanguage(this).equals(TranslatorConstants.SharedPreferences.LANGUAGE_CH)) {
                        mCountryTxt.setText(countryList.get(0).getZh_name());
                    }else if (LanguageUtils.getLanguage(this).equals(TranslatorConstants.SharedPreferences.LANGUAGE_EN)){
                        mCountryTxt.setText(countryList.get(0).getEn_name());
                    }
                }
                break;
            case MSG_CODE:
                handleNext();
                break;
        }
    }

    private void handleNext() {
        String bankCardNo = mCardNumEdt.getText().toString().trim();
        District district = new District();
        district.setAreaNumber(areaNumber);
        Intent intent = new Intent(this, RegisterPhoneVerActivity.class);
        intent.putExtra(TranslatorConstants.Common.FROM, "BANK_CARD");
        intent.putExtra(TranslatorConstants.Login.PHONE_NUMBER, userPhone);
        intent.putExtra("DISTRICT", district);
        intent.putExtra("bankId", bankId);
        intent.putExtra("bankCardNo", bankCardNo);
        intent.putExtra("bankCardUser", userBean.getUsername());
        startActivityForResult(intent, ADD_BANK);
    }


    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s)) {
                if (!TextUtils.isEmpty(mBankTxt.getText().toString().trim()) &&
                        !TextUtils.isEmpty(mCountryTxt.getText().toString().trim())
                        && !TextUtils.isEmpty(mOpenAccountTxt.getText().toString().trim())
                        && !TextUtils.isEmpty(mCardNumEdt.getText().toString().trim())) {
                    addBankBtn.setBackgroundResource(R.drawable.onclick_green_btn_selector);
                    addBankBtn.setClickable(true);
                } else {
                    addBankBtn.setBackgroundResource(R.mipmap.green_btn_normal);
                    addBankBtn.setClickable(false);
                }
            } else {
                addBankBtn.setBackgroundResource(R.mipmap.green_btn_normal);
                addBankBtn.setClickable(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case RESULT_BANK:
                    choiceBankPosition = data.getExtras().getInt("POSITION");
                    Bundle bundle = data.getBundleExtra("BUNDLE");
                    Bank bank = (Bank) bundle.get("DATA");
                    bankTypeId = bank.getType_id();
                    if (LanguageUtils.getLanguage(this).equals(TranslatorConstants.SharedPreferences.LANGUAGE_CH)) {
                        mBankTxt.setText(bank.getZh_name());
                    }else if (LanguageUtils.getLanguage(this).equals(TranslatorConstants.SharedPreferences.LANGUAGE_EN)){
                        mBankTxt.setText(bank.getEn_name());
                    }
                    break;
                case RESULT_COUNTRY:
                    choiceCountryPosition = data.getExtras().getInt("POSITION");
                    bundle = data.getBundleExtra("BUNDLE");
                    Country country = (Country) bundle.get("DATA");
                    stateId = country.getState_id();
                    if (LanguageUtils.getLanguage(this).equals(TranslatorConstants.SharedPreferences.LANGUAGE_CH)) {
                        mCountryTxt.setText(country.getZh_name());
                    }else if (LanguageUtils.getLanguage(this).equals(TranslatorConstants.SharedPreferences.LANGUAGE_EN)){
                        mCountryTxt.setText(country.getEn_name());
                    }
                    break;
                case RESULT_BANK_POINT:
                    bankId = data.getExtras().getInt("bankId");
                    String openingPoint = data.getExtras().getString("openingPoint");
                    mOpenAccountTxt.setText(openingPoint);
                    break;
                case ADD_BANK:
                    CommonBeanManager.getInstance().initMyWalletInfo();
                    finish();
                    break;
            }
        }
    }

}
