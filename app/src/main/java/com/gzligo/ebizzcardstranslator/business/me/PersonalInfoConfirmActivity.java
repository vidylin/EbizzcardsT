package com.gzligo.ebizzcardstranslator.business.me;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.IPresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.image.ImageLoader;
import com.gzligo.ebizzcardstranslator.image.glide.GlideImageConfig;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;
import com.gzligo.ebizzcardstranslator.persistence.IdentityBean;
import com.gzligo.ebizzcardstranslator.utils.ScaleAvatarActivity;
import com.gzligo.ebizzcardstranslator.utils.SharedPreferencesUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/6/20.
 */

public class PersonalInfoConfirmActivity extends BaseActivity implements IView{

    @BindView(R.id.personal_info_confirm_name_tv) TextView mNameTxt;
    @BindView(R.id.personal_info_confirm_idcard_tv) TextView mIdNumTxt;
    @BindView(R.id.personal_info_confirm_education_tv) TextView mEducationTxt;
    @BindView(R.id.personal_info_confirm_profession_tv) TextView mProfessionTxt;
    @BindView(R.id.personal_info_confirm_state_tv) TextView mStateTxt;
    @BindView(R.id.personal_info_confirm_state_not_pass_tv) TextView mStateReasonTxt;
    @BindView(R.id.personal_info_idcard_1) ImageView mIdcardPositiveIv;
    @BindView(R.id.personal_info_idcard_2) ImageView mIdcardNegativeIv;
    @BindView(R.id.personal_info_idcard_3) ImageView mIdcardHalfIv;
    @BindView(R.id.personal_info_edcation_iv) ImageView mEducationIv;
    @BindView(R.id.personal_info_confirm_passed_reapply_btn) Button mReapplyBtn;

    private IdentityBean mIdentityBean;
    private int result;
    private String conclusion;

    @Override
    public IPresenter createPresenter() {
        return null;
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_personal_info;
    }

    @Override
    public void initData() {
        mIdentityBean = (IdentityBean) getIntent().getSerializableExtra(TranslatorConstants.Common.DATA);
        result = getIntent().getIntExtra("result",0);
        conclusion = getIntent().getStringExtra("conclusion");
    }

    @Override
    public void initViews() {
        mNameTxt.setText(mIdentityBean.getUsername());
        mIdNumTxt.setText(mIdentityBean.getId_number());
        mEducationTxt.setText(getEducationString(mIdentityBean.getEducation()));
        mProfessionTxt.setText(mIdentityBean.getProfession());
        switch (result){
            case 0:
                mStateTxt.setText(getResources().getString(R.string.me_not_applied));
                break;
            case 1:
                mStateTxt.setText(getResources().getString(R.string.me_unconfirmed));
                break;
            case 2:
                mStateTxt.setText(getResources().getString(R.string.my_apply_passed));
                mStateTxt.setTextColor(getResources().getColor(R.color.confirm_green));
                break;
            case 3:
                mStateTxt.setText(getResources().getString(R.string.my_apply_not_passed));
                mStateTxt.setTextColor(getResources().getColor(R.color.red));
                mStateReasonTxt.setText(conclusion);
                mReapplyBtn.setVisibility(View.VISIBLE);
                break;
            case 4:
                mStateTxt.setText(getResources().getString(R.string.my_apply_confirming)+"...");
                break;
        }
        loadImage(mIdentityBean.getId_cards().get(0),mIdcardPositiveIv);
        loadImage(mIdentityBean.getId_cards().get(1),mIdcardNegativeIv);
        loadImage(mIdentityBean.getId_cards().get(2),mIdcardHalfIv);
        loadImage(mIdentityBean.getDegree_cers().get(0),mEducationIv);
    }

    @Override
    public void initEvents() {

    }

    @OnClick({R.id.tv_close,R.id.personal_info_confirm_passed_reapply_btn,R.id.personal_info_idcard_1,R.id.personal_info_idcard_2,
            R.id.personal_info_idcard_3,R.id.personal_info_edcation_iv})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_close:
                finish();
                break;
            case R.id.personal_info_confirm_passed_reapply_btn:
                Intent intent = new Intent(this, PersonalBaseMessageActivity.class);
                intent.putExtra(TranslatorConstants.Common.FROM,"main");
                intent.putExtra(TranslatorConstants.Common.DATA,(String) SharedPreferencesUtils.getSharedPreferences(AppManager.get()
                        .getApplication(),"accessToken","","Access_token"));
                startActivity(intent);
                break;
            case R.id.personal_info_idcard_1:
                previewPhoto(mIdcardPositiveIv,mIdentityBean.getId_cards().get(0));
                break;
            case R.id.personal_info_idcard_2:
                previewPhoto(mIdcardNegativeIv,mIdentityBean.getId_cards().get(1));
                break;
            case R.id.personal_info_idcard_3:
                previewPhoto(mIdcardHalfIv,mIdentityBean.getId_cards().get(2));
                break;
            case R.id.personal_info_edcation_iv:
                previewPhoto(mEducationIv,mIdentityBean.getDegree_cers().get(0));
                break;
        }
    }

    private String getEducationString(int education) {
        String educationStr = null;
        switch (education){
            case 1:
                educationStr = getResources().getString(R.string.education_senior);
                break;
            case 2:
                educationStr = getResources().getString(R.string.education_college);
                break;
            case 3:
                educationStr = getResources().getString(R.string.education_undergraduate);
                break;
            case 4:
                educationStr = getResources().getString(R.string.education_master);
                break;
            case 5:
                educationStr = getResources().getString(R.string.education_doctor);
                break;
        }
        return educationStr;
    }

    private void loadImage(String url, final ImageView imageView){
        ImageLoader.get().loadImage(AppManager.get().getApplication(), GlideImageConfig
                .builder()
                .url(HttpUtils.MEDIA_HOST+url)
                .imageView(imageView)
                .imgWidth(imageView.getWidth())
                .imgHeigth(imageView.getHeight())
                .errorPic(R.mipmap.default_head_portrait)
                .isClearMemory(false)
                .build());
    }

    private void previewPhoto(ImageView imageView,String uri){
        ArrayList<String> photoList = new ArrayList<>();
        photoList.add(uri);
        ScaleAvatarActivity.startScaleAvatarActivity(this,photoList,0,imageView);
    }
}
