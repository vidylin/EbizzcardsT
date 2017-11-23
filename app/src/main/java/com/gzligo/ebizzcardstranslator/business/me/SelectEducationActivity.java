package com.gzligo.ebizzcardstranslator.business.me;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.IPresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/5/27.
 */

public class SelectEducationActivity extends BaseActivity implements IView{

    @BindView(R.id.education_college_check_iv)
    ImageView mCollegeIv;
    @BindView(R.id.education_undergraduate_check_iv)
    ImageView mUndergraduateIv;
    @BindView(R.id.education_master_check_iv)
    ImageView mMasterIv;
    @BindView(R.id.education_doctor_check_iv)
    ImageView mDoctorIv;

    private static final int COLLEGE = 1;
    private static final int UNDERGRADUATE = 2;
    private static final int MASTER = 3;
    private static final int DOCTOR = 4;

    @Override
    public IPresenter createPresenter() {
        return null;
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_select_education;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initViews() {
        String text = getIntent().getStringExtra(TranslatorConstants.Common.DATA);
        if (!TextUtils.isEmpty(text)) {
            if (text.equals(getResources().getString(R.string.education_college))) {
                mCollegeIv.setVisibility(View.VISIBLE);
                mUndergraduateIv.setVisibility(View.GONE);
                mMasterIv.setVisibility(View.GONE);
                mDoctorIv.setVisibility(View.GONE);
            }else if (text.equals(getResources().getString(R.string.education_undergraduate))) {
                mCollegeIv.setVisibility(View.GONE);
                mUndergraduateIv.setVisibility(View.VISIBLE);
                mMasterIv.setVisibility(View.GONE);
                mDoctorIv.setVisibility(View.GONE);
            }else if (text.equals(getResources().getString(R.string.education_master))) {
                mCollegeIv.setVisibility(View.GONE);
                mUndergraduateIv.setVisibility(View.GONE);
                mMasterIv.setVisibility(View.VISIBLE);
                mDoctorIv.setVisibility(View.GONE);
            }else if (text.equals(getResources().getString(R.string.education_doctor))) {
                mCollegeIv.setVisibility(View.GONE);
                mUndergraduateIv.setVisibility(View.GONE);
                mMasterIv.setVisibility(View.GONE);
                mDoctorIv.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void initEvents() {

    }

    @OnClick({R.id.tv_close,R.id.education_college_rl,R.id.education_undergraduate_rl,R.id.education_master_rl,R.id.education_doctor_rl})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_close:
                finish();
                break;
            case R.id.education_college_rl:
                mCollegeIv.setVisibility(View.VISIBLE);
                mUndergraduateIv.setVisibility(View.GONE);
                mMasterIv.setVisibility(View.GONE);
                mDoctorIv.setVisibility(View.GONE);
                handleSelected(COLLEGE);
                finish();
                break;
            case R.id.education_undergraduate_rl:
                mCollegeIv.setVisibility(View.GONE);
                mUndergraduateIv.setVisibility(View.VISIBLE);
                mMasterIv.setVisibility(View.GONE);
                mDoctorIv.setVisibility(View.GONE);
                handleSelected(UNDERGRADUATE);
                finish();
                break;
            case R.id.education_master_rl:
                mCollegeIv.setVisibility(View.GONE);
                mUndergraduateIv.setVisibility(View.GONE);
                mMasterIv.setVisibility(View.VISIBLE);
                mDoctorIv.setVisibility(View.GONE);
                handleSelected(MASTER);
                finish();
                break;
            case R.id.education_doctor_rl:
                mCollegeIv.setVisibility(View.GONE);
                mUndergraduateIv.setVisibility(View.GONE);
                mMasterIv.setVisibility(View.GONE);
                mDoctorIv.setVisibility(View.VISIBLE);
                handleSelected(DOCTOR);
                finish();
                break;
        }
    }

    private void handleSelected(int selected) {
        switch (selected){
            case COLLEGE:
                setSelectedResult(getResources().getString(R.string.education_college));
                break;
            case UNDERGRADUATE:
                setSelectedResult(getResources().getString(R.string.education_undergraduate));
                break;
            case MASTER:
                setSelectedResult(getResources().getString(R.string.education_master));
                break;
            case DOCTOR:
                setSelectedResult(getResources().getString(R.string.education_doctor));
                break;
        }
    }

    private void setSelectedResult(String data) {
        Intent intent = new Intent(this,PersonalBaseMessageActivity.class);
        intent.putExtra(TranslatorConstants.Common.DATA,data);
        setResult(RESULT_OK,intent);
    }
}
