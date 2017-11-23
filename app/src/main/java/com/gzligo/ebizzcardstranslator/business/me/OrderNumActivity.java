package com.gzligo.ebizzcardstranslator.business.me;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.business.MainActivity;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.persistence.ErrorMessageBean;
import com.gzligo.ebizzcardstranslator.utils.DialogUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/6/1.
 */

public class OrderNumActivity extends BaseActivity<PersonalPresenter> implements IView{

    @BindView(R.id.order_num_1_iv)
    ImageView mCheckNum1;
    @BindView(R.id.order_num_2_iv)
    ImageView mCheckNum2;
    @BindView(R.id.order_num_3_iv)
    ImageView mCheckNum3;
    @BindView(R.id.order_num_4_iv)
    ImageView mCheckNum4;
    @BindView(R.id.order_num_5_iv)
    ImageView mCheckNum5;

    private Dialog mLoadingDialog;
    public static final int UPLOAD_TRANSLATION_NUM = 0x00;

    @Override
    public PersonalPresenter createPresenter() {
        return new PersonalPresenter(new PersonalRepository());
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_order_num;
    }

    @Override
    public void initData() {
    }

    @Override
    public void initViews() {
        String maxNum = getIntent().getStringExtra(TranslatorConstants.Common.DATA);
        if(maxNum.equals(getResources().getString(R.string.order_num_1))){
            mCheckNum1.setVisibility(View.VISIBLE);
            mCheckNum2.setVisibility(View.GONE);
            mCheckNum3.setVisibility(View.GONE);
            mCheckNum4.setVisibility(View.GONE);
            mCheckNum5.setVisibility(View.GONE);
        }else if (maxNum.equals(getResources().getString(R.string.order_num_2))){
            mCheckNum1.setVisibility(View.GONE);
            mCheckNum2.setVisibility(View.VISIBLE);
            mCheckNum3.setVisibility(View.GONE);
            mCheckNum4.setVisibility(View.GONE);
            mCheckNum5.setVisibility(View.GONE);
        }else if (maxNum.equals(getResources().getString(R.string.order_num_3))){
            mCheckNum1.setVisibility(View.GONE);
            mCheckNum2.setVisibility(View.GONE);
            mCheckNum3.setVisibility(View.VISIBLE);
            mCheckNum4.setVisibility(View.GONE);
            mCheckNum5.setVisibility(View.GONE);
        }else if (maxNum.equals(getResources().getString(R.string.order_num_4))){
            mCheckNum1.setVisibility(View.GONE);
            mCheckNum2.setVisibility(View.GONE);
            mCheckNum3.setVisibility(View.GONE);
            mCheckNum4.setVisibility(View.VISIBLE);
            mCheckNum5.setVisibility(View.GONE);
        }else if (maxNum.equals(getResources().getString(R.string.order_num_5))){
            mCheckNum1.setVisibility(View.GONE);
            mCheckNum2.setVisibility(View.GONE);
            mCheckNum3.setVisibility(View.GONE);
            mCheckNum4.setVisibility(View.GONE);
            mCheckNum5.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initEvents() {

    }

    @OnClick({R.id.tv_close,R.id.order_num_1_rl,R.id.order_num_2_rl,R.id.order_num_3_rl,R.id.order_num_4_rl,R.id.order_num_5_rl})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_close:
                finish();
                break;
            case R.id.order_num_1_rl:
                mCheckNum1.setVisibility(View.VISIBLE);
                mCheckNum2.setVisibility(View.GONE);
                mCheckNum3.setVisibility(View.GONE);
                mCheckNum4.setVisibility(View.GONE);
                mCheckNum5.setVisibility(View.GONE);
                uploadTranslationNum("1");
                break;
            case R.id.order_num_2_rl:
                mCheckNum1.setVisibility(View.GONE);
                mCheckNum2.setVisibility(View.VISIBLE);
                mCheckNum3.setVisibility(View.GONE);
                mCheckNum4.setVisibility(View.GONE);
                mCheckNum5.setVisibility(View.GONE);
                uploadTranslationNum("2");
                break;
            case R.id.order_num_3_rl:
                mCheckNum1.setVisibility(View.GONE);
                mCheckNum2.setVisibility(View.GONE);
                mCheckNum3.setVisibility(View.VISIBLE);
                mCheckNum4.setVisibility(View.GONE);
                mCheckNum5.setVisibility(View.GONE);
                uploadTranslationNum("3");
                break;
            case R.id.order_num_4_rl:
                mCheckNum1.setVisibility(View.GONE);
                mCheckNum2.setVisibility(View.GONE);
                mCheckNum3.setVisibility(View.GONE);
                mCheckNum4.setVisibility(View.VISIBLE);
                mCheckNum5.setVisibility(View.GONE);
                uploadTranslationNum("4");
                break;
            case R.id.order_num_5_rl:
                mCheckNum1.setVisibility(View.GONE);
                mCheckNum2.setVisibility(View.GONE);
                mCheckNum3.setVisibility(View.GONE);
                mCheckNum4.setVisibility(View.GONE);
                mCheckNum5.setVisibility(View.VISIBLE);
                uploadTranslationNum("5");
                break;
        }
    }

    public void uploadTranslationNum(String num){
        mLoadingDialog = DialogUtils.showLoadingDialog(this,getResources().getString(R.string.submitting));
        getPresenter().uploadTranslationNum(Message.obtain(this,new String[]{num}),true);
    }

    private void setIntentResult(String data) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(TranslatorConstants.Common.DATA,data);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void handlePresenterCallback(Message message) {
        if (mLoadingDialog!=null){
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
        switch (message.what){
            case UPLOAD_TRANSLATION_NUM:
                if (((ErrorMessageBean)message.obj).getError()==0) {
                    Toast.makeText(this,getResources().getString(R.string.submitting_success),Toast.LENGTH_SHORT).show();
                    setIntentResult((String) message.objs[0]);
                }
                break;
        }
    }
}
