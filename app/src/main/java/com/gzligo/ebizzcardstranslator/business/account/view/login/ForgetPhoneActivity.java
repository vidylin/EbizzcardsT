package com.gzligo.ebizzcardstranslator.business.account.view.login;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.common.ToolActionBar;
import com.gzligo.ebizzcardstranslator.persistence.District;
import com.gzligo.ebizzcardstranslator.persistence.ErrorMessageBean;

import butterknife.BindView;
import butterknife.OnClick;

import static com.gzligo.ebizzcardstranslator.R.id.forget_next_btn;

/**
 * Created by ZuoJian on 2017/5/27.
 */

public class ForgetPhoneActivity extends BaseActivity<ForgetPhonePresenter>{
    private static final int COUNTRY_CODE = 0x50;
    @BindView(R.id.forget_tv_phone_num) EditText mPhoneEdt;
    @BindView(R.id.register_actionbar) ToolActionBar registerActionbar;
    @BindView(R.id.tv_region) TextView tvRegion;
    @BindView(R.id.tv_country_name) TextView tvCountryName;
    @BindView(R.id.ll_selector_country) LinearLayout llSelectorCountry;
    @BindView(R.id.v_slector_line) View vSlectorLine;
    @BindView(R.id.rl_item) RelativeLayout rlItem;
    @BindView(R.id.forget_tv_district_num) TextView forgetTvDistrictNum;
    @BindView(forget_next_btn) Button forgetNextBtn;

    private District district;
    private String from;

    @Override
    public ForgetPhonePresenter createPresenter() {
        return new ForgetPhonePresenter(new ForgetPhoneRepository());
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_forget_phone;
    }

    @Override
    public void initViews() {
        forgetTvDistrictNum.setText(district.getAreaNumber());
        tvCountryName.setText(district.getLocalName());
    }

    @Override
    public void initData() {
        district = (District) getIntent().getExtras().getSerializable("DISTRICT");
        from = getIntent().getStringExtra("FROM");
    }

    @Override
    public void initEvents() {
        forgetNextBtn.setClickable(false);
        mPhoneEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String inputNum = editable.toString().trim();
                if(TextUtils.isEmpty(inputNum)){
                    forgetNextBtn.setBackgroundResource(R.mipmap.green_btn_normal);
                    forgetNextBtn.setClickable(false);
                }else{
                    forgetNextBtn.setBackgroundResource(R.drawable.onclick_green_btn_selector);
                    forgetNextBtn.setClickable(true);
                }
            }
        });
    }

    @OnClick({R.id.tv_close, R.id.ll_selector_country, forget_next_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_close:
                finish();
                break;
            case R.id.ll_selector_country:
                startActivityForResult(new Intent(this, CountrySelectActivity.class), COUNTRY_CODE);
                break;
            case forget_next_btn:
                getPresenter().requestValidateSms(Message.obtain(this,new String[]{district.getAreaNumber().replace("+","")+mPhoneEdt.getText().toString().trim()
                        ,"1"}));
                break;
        }
    }

    private void handleNext() {
        Intent intent = new Intent(this, RegisterPhoneVerActivity.class);
        intent.putExtra(TranslatorConstants.Common.FROM, "forget");
        intent.putExtra(TranslatorConstants.Login.PHONE_NUMBER, mPhoneEdt.getText().toString().trim());
        intent.putExtra("DISTRICT",district);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case COUNTRY_CODE:
                    district = (District) data.getExtras().getSerializable("DISTRICT");
                    forgetTvDistrictNum.setText(district.getAreaNumber());
                    tvCountryName.setText(district.getLocalName());
                    break;
            }
        }
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what){
            case COUNTRY_CODE:
                if (((ErrorMessageBean)message.obj).getError()==0) {
                    if ("MyWalletPasswordManagerActivity".equals(from)) {
                        Intent intent = new Intent(this, RegisterPhoneVerActivity.class);
                        intent.putExtra(TranslatorConstants.Common.FROM, "MyWalletPasswordManagerActivity");
                        intent.putExtra(TranslatorConstants.Login.PHONE_NUMBER, mPhoneEdt.getText().toString().trim());
                        intent.putExtra("DISTRICT", district);
                        startActivity(intent);
                        finish();
                    } else {
                        handleNext();
                    }
                }else {
                    Toast.makeText(this,getResources().getString(R.string.login_user_not_registered),Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
