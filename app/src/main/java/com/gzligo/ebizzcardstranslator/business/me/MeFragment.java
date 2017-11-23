package com.gzligo.ebizzcardstranslator.business.me;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.BaseFragment;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.business.wallet.MyWalletActivity;
import com.gzligo.ebizzcardstranslator.image.ImageLoader;
import com.gzligo.ebizzcardstranslator.image.glide.GlideImageConfig;
import com.gzligo.ebizzcardstranslator.image.transformation.CustomShapeTransformation;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;
import com.gzligo.ebizzcardstranslator.persistence.UserBean;
import com.gzligo.ebizzcardstranslator.persistence.WorkTimeBean;
import com.gzligo.ebizzcardstranslator.utils.ScaleAvatarActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/5/31.
 */

public class MeFragment extends BaseFragment<PersonalPresenter> {
    private static final int RESULT_MY_PROFILE = 0x02;
    private static final int RESULT_ORDER_NUM = 0x00;
    private static final int RESULT_WORK_TIME = 0x01;
    private static final int IS_AUTHENTICATED = 0x03;

    @BindView(R.id.me_avatar_iv)
    ImageView mAvatar;
    @BindView(R.id.me_nickname_txt)
    TextView mNickNameTxt;
    @BindView(R.id.me_unconfirmed_txt)
    TextView mUnconfirmedTxt;
    @BindView(R.id.me_evaluation_lvl_rl)
    RelativeLayout mLevelStartRl;
    @BindView(R.id.me_star_1)
    ImageView mStart1;
    @BindView(R.id.me_star_2)
    ImageView mStart2;
    @BindView(R.id.me_star_3)
    ImageView mStart3;
    @BindView(R.id.me_star_4)
    ImageView mStart4;
    @BindView(R.id.me_star_5)
    ImageView mStart5;
    @BindView(R.id.me_order_num_txt)
    TextView mOrderNumTxt;
    @BindView(R.id.me_work_time_num_txt_am)
    TextView mWorkTimeAmTxt;
    @BindView(R.id.me_work_time_num_txt_pm)
    TextView mWorkTimePmTxt;
    @BindView(R.id.me_my_application_iv)ImageView myApplicationIv;

    private UserBean userBean;
    public static final int GET_USER_INFO = 0x00;

    @Override
    public PersonalPresenter createPresenter() {
        return new PersonalPresenter(new PersonalRepository());
    }

    @Override
    public View onLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_me, container, false);
        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        userBean = getPresenter().getUserBean();
        if(null!=userBean){
            initUserData(userBean);
        }
    }

    @Override
    public void initViews() {
    }

    @Override
    public void initEvents() {
    }

    public void initUserData(UserBean mUserBean) {
        userBean = mUserBean;
        if (!TextUtils.isEmpty(userBean.getNickname())) {
            mNickNameTxt.setText(userBean.getNickname());
        }
        if (!TextUtils.isEmpty(String.valueOf(userBean.getMax_translation_number()))) {
            String mOrderNum = "";
            switch (userBean.getMax_translation_number()) {
                case 1:
                    mOrderNum = getResources().getString(R.string.order_num_1);
                    break;
                case 2:
                    mOrderNum = getResources().getString(R.string.order_num_2);
                    break;
                case 3:
                    mOrderNum = getResources().getString(R.string.order_num_3);
                    break;
                case 4:
                    mOrderNum = getResources().getString(R.string.order_num_4);
                    break;
                case 5:
                    mOrderNum = getResources().getString(R.string.order_num_5);
                    break;
            }
            mOrderNumTxt.setText(mOrderNum);
        }
        int identity_status = userBean.getIdentity_status();
        switch (identity_status) {
            case 0:
                mUnconfirmedTxt.setText(getResources().getString(R.string.me_not_applied));
                break;
            case 1:
                mUnconfirmedTxt.setText(getResources().getString(R.string.me_unconfirmed));
                break;
            case 2:
                mUnconfirmedTxt.setVisibility(View.GONE);
                mLevelStartRl.setVisibility(View.VISIBLE);
                myApplicationIv.setBackgroundResource(R.mipmap.me_passed_zh);
                int level = (int) userBean.getLevel();
                switch (level) {
                    case 0:
                        mUnconfirmedTxt.setVisibility(View.VISIBLE);
                        mLevelStartRl.setVisibility(View.GONE);
                        mUnconfirmedTxt.setText(getResources().getString(R.string.me_authenticated));
                        mUnconfirmedTxt.setTextColor(getResources().getColor(R.color.green));
                        break;
                    case 1:
                        mStart1.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        mStart1.setVisibility(View.VISIBLE);
                        mStart2.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        mStart1.setVisibility(View.VISIBLE);
                        mStart2.setVisibility(View.VISIBLE);
                        mStart3.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        mStart1.setVisibility(View.VISIBLE);
                        mStart2.setVisibility(View.VISIBLE);
                        mStart3.setVisibility(View.VISIBLE);
                        mStart4.setVisibility(View.VISIBLE);
                        break;
                    case 5:
                        mStart1.setVisibility(View.VISIBLE);
                        mStart2.setVisibility(View.VISIBLE);
                        mStart3.setVisibility(View.VISIBLE);
                        mStart4.setVisibility(View.VISIBLE);
                        mStart5.setVisibility(View.VISIBLE);
                        break;
                }
                break;
            case 3:
                mUnconfirmedTxt.setText(getResources().getString(R.string.me_unauthenticated));
                break;
            case 4:
                mUnconfirmedTxt.setText(getResources().getString(R.string.me_confirming));
                break;
        }
        if (userBean.getWork_times() != null) {
            List<WorkTimeBean> mWorkList = userBean.getWork_times();
            String workTimeAm = mWorkList.get(0).getOn() + "-" + mWorkList.get(0).getOff();
            String workTimePm = mWorkList.get(1).getOn() + "-" + mWorkList.get(1).getOff();
            mWorkTimeAmTxt.setText(workTimeAm);
            mWorkTimePmTxt.setText(workTimePm);
        }
        if (userBean.getPortrait_id() != null) {
            showAvatar(userBean.getPortrait_id());
        }
    }

    private void showAvatar(String portraitId) {
        ImageLoader.get().loadImage(AppManager.get().getApplication(), GlideImageConfig
                .builder()
                .url(HttpUtils.MEDIA_HOST+portraitId)
                .imageView(mAvatar)
                .errorPic(R.mipmap.default_head_portrait)
                .isClearMemory(false)
                .transformation(new CustomShapeTransformation(AppManager.get().getApplication(),39))
                .build());
    }

    @OnClick({R.id.me_avatar_iv,R.id.me_my_profile_rl,R.id.me_setting_rl,R.id.me_order_num_rl,R.id.me_work_time_rl,R.id.me_language_rl,R.id.me_my_wallet_rl,R.id.me_my_application_rl})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.me_avatar_iv:
                ArrayList<String> mAvatarList = new ArrayList<>();
                mAvatarList.add(userBean.getPortrait_id());
                ScaleAvatarActivity.startScaleAvatarActivity(getActivity(),mAvatarList,0,mAvatar);
                break;
            case R.id.me_my_profile_rl:
                Intent intent = new Intent(getActivity(),MyProfileActivity.class);
                intent.putExtra(TranslatorConstants.Common.DATA,userBean);
                startActivityForResult(intent,RESULT_MY_PROFILE);
                break;
            case R.id.me_setting_rl:
                startActivity(new Intent(getActivity(),SettingActivity.class));
                break;
            case R.id.me_order_num_rl:
                Intent orderIntent = new Intent(getActivity(),OrderNumActivity.class);
                orderIntent.putExtra(TranslatorConstants.Common.DATA,mOrderNumTxt.getText().toString());
                startActivityForResult(orderIntent,RESULT_ORDER_NUM);
                break;
            case R.id.me_work_time_rl:
                Intent workIntent = new Intent(getActivity(),WorkTimeActivity.class);
                String work = mWorkTimeAmTxt.getText().toString()+"-"+mWorkTimePmTxt.getText().toString();
                workIntent.putExtra(TranslatorConstants.Common.DATA,work);
                startActivityForResult(workIntent,RESULT_WORK_TIME);
                break;
            case R.id.me_language_rl:
                if (userBean.getIdentity_status()==0){
                    Toast.makeText(getActivity(),getResources().getString(R.string.me_complete_identity_first),Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(getActivity(),MasterLanguageActivity.class));
                break;
            case R.id.me_my_wallet_rl:
                if (userBean.getIdentity_status()==0){
                    Toast.makeText(getActivity(),getResources().getString(R.string.me_complete_identity_first),Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(getActivity(),MyWalletActivity.class));
                break;
            case R.id.me_my_application_rl:
                if (userBean.getIdentity_status()==0){
                    Toast.makeText(getActivity(),getResources().getString(R.string.me_complete_identity_first),Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(getActivity(),MyApplicationActivity.class));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null) {
            switch (requestCode) {
                case RESULT_ORDER_NUM:
                    String orderNumStr = data.getStringExtra(TranslatorConstants.Common.DATA);
                    switch (orderNumStr){
                        case "1":
                            mOrderNumTxt.setText(getResources().getString(R.string.order_num_1));
                            break;
                        case "2":
                            mOrderNumTxt.setText(getResources().getString(R.string.order_num_2));
                            break;
                        case "3":
                            mOrderNumTxt.setText(getResources().getString(R.string.order_num_3));
                            break;
                        case "4":
                            mOrderNumTxt.setText(getResources().getString(R.string.order_num_4));
                            break;
                        case "5":
                            mOrderNumTxt.setText(getResources().getString(R.string.order_num_5));
                            break;
                    }
                    break;
                case RESULT_WORK_TIME:
                    String workTime = data.getStringExtra(TranslatorConstants.Common.DATA);
                    String amTime = workTime.split("-")[0]+"-"+workTime.split("-")[1];
                    String pmTime = workTime.split("-")[2]+"-"+workTime.split("-")[3];
                    mWorkTimeAmTxt.setText(amTime);
                    mWorkTimePmTxt.setText(pmTime);
                    break;
                case RESULT_MY_PROFILE:
                    String nickName = data.getStringExtra(TranslatorConstants.Login.NICKNAME);
                    String portrait = data.getStringExtra(TranslatorConstants.Login.PORTRAIT);
                    if (!TextUtils.isEmpty(nickName)||!TextUtils.isEmpty(portrait)){
                        getPresenter().getUserBeanInfo(Message.obtain(this), true);
                    }
                    break;
            }
        }
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what){
            case IS_AUTHENTICATED:
                userBean = getPresenter().getUserBean();
                initUserData(userBean);
                break;
        }
    }

}
