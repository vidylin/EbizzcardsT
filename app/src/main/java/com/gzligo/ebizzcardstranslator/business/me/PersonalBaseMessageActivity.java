package com.gzligo.ebizzcardstranslator.business.me;

import android.app.Dialog;
import android.content.Intent;
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
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.persistence.ErrorMessageBean;
import com.gzligo.ebizzcardstranslator.utils.DialogUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/5/27.
 */

public class PersonalBaseMessageActivity extends BaseActivity<PersonalPresenter> implements IView{

    @BindView(R.id.base_name_edt)
    EditText mNameEdt;
    @BindView(R.id.base_idcard_edt)
    EditText mIdCardEdt;
    @BindView(R.id.base_education_txt)
    TextView mEducationTxt;
    @BindView(R.id.base_profession_edt)
    EditText mProfessionEdt;
    @BindView(R.id.base_language_txt)
    TextView mLanguageTxt;
    @BindView(R.id.base_btn)
    Button mNextBtn;

    private static final int RESULT_EDUCATION = 0x00;
    private static final int RESULT_LANGUAGE = 0x01;
    public static final int UPLOAD_BASE_MESSAGE = 0x03;

    private Dialog mLoadingDialog;
    private String mLanguageJson,mLanguageList;

    @Override
    public PersonalPresenter createPresenter() {
        return new PersonalPresenter(new PersonalRepository());
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_base_msg;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initViews() {

    }

    @Override
    public void initEvents() {
        mNextBtn.setClickable(false);
        mNameEdt.addTextChangedListener(watcher);
        mIdCardEdt.addTextChangedListener(watcher);
        mEducationTxt.addTextChangedListener(watcher);
        mProfessionEdt.addTextChangedListener(watcher);
        mLanguageTxt.addTextChangedListener(watcher);
    }

    @OnClick({R.id.tv_close,R.id.base_btn,R.id.base_education_rl,R.id.base_language_rl})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_close:
                finish();
                break;
            case R.id.base_btn:
                uploadMessage();
                break;
            case R.id.base_education_rl:
                Intent educationIntent = new Intent(this,SelectEducationActivity.class);
                educationIntent.putExtra(TranslatorConstants.Common.DATA,mEducationTxt.getText().toString());
                startActivityForResult(educationIntent,RESULT_EDUCATION);
                break;
            case R.id.base_language_rl:
                Intent languageIntent = new Intent(this,SelectLanguageActivity.class);
                if (!TextUtils.isEmpty(mLanguageList)) {
                    languageIntent.putExtra(TranslatorConstants.Common.DATA, mLanguageList);
                }
                startActivityForResult(languageIntent,RESULT_LANGUAGE);
                break;
        }
    }

    private void uploadMessage() {
        int education = 2;
        if (mEducationTxt.getText().toString().equals(getResources().getString(R.string.education_college))){
            education = 2;
        }else if (mEducationTxt.getText().toString().equals(getResources().getString(R.string.education_undergraduate))){
            education = 3;
        }else if (mEducationTxt.getText().toString().equals(getResources().getString(R.string.education_master))){
            education = 4;
        }else if (mEducationTxt.getText().toString().equals(getResources().getString(R.string.education_doctor))){
            education = 5;
        }
        mLoadingDialog = DialogUtils.showLoadingDialog(this,getResources().getString(R.string.submitting));
        getPresenter().uploadBaseMessage(Message.obtain(this,new String[]{mNameEdt.getText().toString(),mIdCardEdt.getText().toString(),
        String.valueOf(education),mProfessionEdt.getText().toString(),mLanguageJson}),true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null){
            switch (requestCode){
                case RESULT_EDUCATION:
                    mEducationTxt.setText(data.getStringExtra(TranslatorConstants.Common.DATA));
                    break;
                case RESULT_LANGUAGE:
                    mLanguageTxt.setText(data.getStringExtra(TranslatorConstants.Common.DATA));
                    mLanguageJson = data.getStringExtra(TranslatorConstants.Common.JSON);
                    mLanguageList = data.getStringExtra("language_list");
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
                if (!TextUtils.isEmpty(mNameEdt.getText().toString().trim())&&
                        !TextUtils.isEmpty(mIdCardEdt.getText().toString().trim())&&
                        !TextUtils.isEmpty(mProfessionEdt.getText().toString().trim())&&
                        !TextUtils.isEmpty(mEducationTxt.getText().toString().trim())&&
                        !TextUtils.isEmpty(mLanguageTxt.getText().toString().trim())) {
                    mNextBtn.setBackgroundResource(R.drawable.onclick_green_btn_selector);
                    mNextBtn.setClickable(true);
                }else {
                    mNextBtn.setBackgroundResource(R.mipmap.green_btn_normal);
                    mNextBtn.setClickable(false);
                }
            }else {
                mNextBtn.setBackgroundResource(R.mipmap.green_btn_normal);
                mNextBtn.setClickable(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what){
            case UPLOAD_BASE_MESSAGE:
                if (mLoadingDialog!=null){
                    mLoadingDialog.dismiss();
                    mLoadingDialog = null;
                }
                if (((ErrorMessageBean)message.obj).getError()==0) {
                    Toast.makeText(this, getResources().getString(R.string.submitting_success), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this,UploadIdCardActivity.class);
                    intent.putExtra(TranslatorConstants.Common.JSON,mLanguageJson);
                    intent.putExtra(TranslatorConstants.Common.FROM,getIntent().getStringExtra(TranslatorConstants.Common.FROM));
                    startActivity(intent);
                }
                break;
        }
    }
}
