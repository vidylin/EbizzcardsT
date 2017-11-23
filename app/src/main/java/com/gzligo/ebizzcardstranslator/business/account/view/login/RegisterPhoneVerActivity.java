package com.gzligo.ebizzcardstranslator.business.account.view.login;

import android.app.Dialog;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.business.wallet.BankCardPasswordSettingActivity;
import com.gzligo.ebizzcardstranslator.persistence.District;
import com.gzligo.ebizzcardstranslator.persistence.ErrorMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.HttpResultBean;
import com.gzligo.ebizzcardstranslator.persistence.RegisterBean;
import com.gzligo.ebizzcardstranslator.utils.DialogUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/5/26.
 */

public class RegisterPhoneVerActivity extends BaseActivity<LoginPresenter> implements IView{

    @BindView(R.id.ver_phone_tv)
    TextView mPhoneTxt;
    @BindView(R.id.ver_code_edt)
    EditText mCode;
    @BindView(R.id.ver_next)
    Button nextBtn;
    @BindView(R.id.ver_resend_tv)
    Button verResendTv;

    private String comeFrom;
    private String jsonStr;
    private Gson gson;
    private String phoneNum;
    private District district;
    private RegisterBean registerBean;
    private Dialog mLoadingDialog;
    private static final int COUNT_DOWN_TIME = 0x01;
    public static final int MSG_INPUTTED_CODE = 0x00;
    private static final int COUNTRY_CODE = 0x02;
    private static final int VERIFY_CODE = 0x03;
    private static final int BIND_BANK_CARD = 0x04;
    private static final int VERIFY_CODE_ERROR = 0x05;
    private static final int BANK_CARD_ALREADY_BIND = 0x06;
    private String bankCardNo;
    private String bankCardUser;
    private int bankId;
    private String pwdOne;

    @Override
    public LoginPresenter createPresenter() {
        return new LoginPresenter(new LoginRepository(),this);
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_phone_ver;
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initData() {
        getPresenter().countdown(Message.obtain(this,new Integer[]{60}));
        comeFrom = getIntent().getExtras().getString(TranslatorConstants.Common.FROM,"");
        switch (comeFrom){
            case "forget":
                verResendTv.setClickable(false);
                phoneNum = getIntent().getExtras().getString(TranslatorConstants.Login.PHONE_NUMBER);
                district = (District) getIntent().getExtras().getSerializable("DISTRICT");
                mPhoneTxt.setText(district.getAreaNumber().replace("+","")+phoneNum);
                break;
            case "BANK_CARD":
                verResendTv.setClickable(false);
                phoneNum = getIntent().getExtras().getString(TranslatorConstants.Login.PHONE_NUMBER);
                district = (District) getIntent().getExtras().getSerializable("DISTRICT");
                mPhoneTxt.setText(district.getAreaNumber().replace("+","")+phoneNum);
                bankId = getIntent().getExtras().getInt("bankId");
                bankCardNo = getIntent().getExtras().getString("bankCardNo");
                bankCardUser = getIntent().getExtras().getString("bankCardUser");
                break;
            case "MyWalletPasswordManagerActivity":
                verResendTv.setClickable(false);
                phoneNum = getIntent().getExtras().getString(TranslatorConstants.Login.PHONE_NUMBER);
                district = (District) getIntent().getExtras().getSerializable("DISTRICT");
                pwdOne = getIntent().getStringExtra("pwdOne");
                mPhoneTxt.setText(district.getAreaNumber().replace("+","")+phoneNum);
                break;
            default:
                jsonStr = getIntent().getStringExtra(TranslatorConstants.Common.JSON);
                gson = new Gson();
                registerBean = gson.fromJson(jsonStr, RegisterBean.class);
                String phone = registerBean.getPhone();
                if(!TextUtils.isEmpty(phone) && phone.length() > 6 ){
                    StringBuilder sb  =new StringBuilder();
                    for (int i = 0; i < phone.length(); i++) {
                        char c = phone.charAt(i);
                        if (i >= 3 && i <= 6) {
                            sb.append('*');
                        } else {
                            sb.append(c);
                        }
                    }
                    mPhoneTxt.setText(sb.toString());
                }else {
                    mPhoneTxt.setText(phone);
                }
                break;
        }
    }

    @Override
    public void initEvents() {
        mCode.addTextChangedListener(watcher);
        nextBtn.setClickable(false);
    }

    @OnClick({R.id.tv_close,R.id.ver_resend_tv,R.id.ver_next})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_close:
                finish();
                break;
            case R.id.ver_resend_tv:
                verResendTv.setClickable(false);
                getPresenter().requestValidateSms(Message.obtain(this,new String[]{district.getAreaNumber().replace("+","")+phoneNum
                        ,"1"}));
                break;
            case R.id.ver_next:
                handleNext();
                break;
        }
    }

    private void handleNext() {
        String code = mCode.getText().toString().trim();
        switch (comeFrom){
            case "MyWalletPasswordManagerActivity":
            case "forget":
                //忘记密码
                getPresenter().requestValidateVerify(district.getAreaNumber().replace("+","")+phoneNum,code,this);
                break;
            case "BANK_CARD":
                getPresenter().requestAddBankCard(bankId+"",bankCardUser,bankCardNo,code);
                break;
            default:
                //注册
                mLoadingDialog = DialogUtils.showLoadingDialog(this,getResources().getString(R.string.ver_registering));
                registerBean.setCode(code);
                Message message = Message.obtain(this,registerBean);
                getPresenter().register(message,true);
                break;
        }
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what){
            case  MSG_INPUTTED_CODE:
                if (mLoadingDialog!=null){
                    mLoadingDialog.dismiss();
                    mLoadingDialog = null;
                }
                if (((ErrorMessageBean)message.obj).getError()==0){
                    if (getIntent().getStringExtra(TranslatorConstants.Common.FROM)==null) {
                        Toast.makeText(this,getResources().getString(R.string.register_success),Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent(this, RegisterSuccessActivity.class);
                    intent.putExtra(TranslatorConstants.Common.JSON,jsonStr);
                    startActivity(intent);
                    Log.e("TAG","verPhone-------------->>>>>>"+((ErrorMessageBean)message.obj).getMessage());
                    finish();
                }else {
                    Log.e("TAG","verPhone-------------->>>>>>"+((ErrorMessageBean)message.obj).getMessage());
                    Toast.makeText(this,((ErrorMessageBean)message.obj).getMessage(),Toast.LENGTH_SHORT).show();
                }
                break;
            case COUNT_DOWN_TIME:
                int time = (int) message.obj;
                if(null!=verResendTv){
                    if(time>0){
                        verResendTv.setText(time+" "+getResources().getString(R.string.second));
                    }else{
                        verResendTv.setClickable(true);
                        verResendTv.setText(getResources().getString(R.string.ver_resend));
                    }
                }
                break;
            case COUNTRY_CODE:
                getPresenter().countdown(Message.obtain(this,new Integer[]{60}));
                break;
            case VERIFY_CODE:
                switch (comeFrom){
                    case "MyWalletPasswordManagerActivity":
                        startActivity(new Intent(this, BankCardPasswordSettingActivity.class)
                                .putExtra("token",mCode.getText().toString().trim())
                                .putExtra("FROM","ModifyPassword"));
                        finish();
                        break;
                    case "forget":
                        HttpResultBean result = (HttpResultBean) message.obj;
                        if (result.getError()==0){
                            startActivity(new Intent(this,ResetPasswordActivity.class)
                                    .putExtra("TOKEN",result.getToken())
                                    .putExtra("PHONE_NUM",district.getAreaNumber().replace("+","")+phoneNum));
                            finish();
                        }else {
                            Toast.makeText(this,getResources().getString(R.string.ver_code_error),Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                break;
            case BIND_BANK_CARD:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                break;
            case VERIFY_CODE_ERROR:
                Toast.makeText(this,getResources().getString(R.string.ver_code_error),Toast.LENGTH_SHORT).show();
                break;
            case BANK_CARD_ALREADY_BIND:
                Toast.makeText(this,getResources().getString(R.string.bind_card_already_bind),Toast.LENGTH_SHORT).show();
                intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
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
                if (!TextUtils.isEmpty(mCode.getText().toString().trim())){
                    nextBtn.setBackgroundResource(R.drawable.onclick_green_btn_selector);
                    nextBtn.setClickable(true);
                }else {
                    nextBtn.setBackgroundResource(R.mipmap.green_btn_normal);
                    nextBtn.setClickable(false);
                }
            }else {
                nextBtn.setBackgroundResource(R.mipmap.green_btn_normal);
                nextBtn.setClickable(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
