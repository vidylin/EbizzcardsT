package com.gzligo.ebizzcardstranslator.business.me;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.IPresenter;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.common.ToolActionBar;
import com.gzligo.ebizzcardstranslator.persistence.LanguageMyApplicationBean;
import com.gzligo.ebizzcardstranslator.utils.ScaleAvatarActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/8/4.
 */

public class LanguageConfirmActivity extends BaseActivity {

    @BindView(R.id.confirm_language_actionbar) ToolActionBar mActionbar;
    @BindView(R.id.confirm_language_recycler) RecyclerView mCersRecycler;
    @BindView(R.id.confirm_language_reason) TextView mStateReasonTxt;
    @BindView(R.id.confirm_language_txt) TextView mStateTxt;
    @BindView(R.id.confirm_language_passed_reapply_rl) RelativeLayout mReapplyRl;

    private int result;
    private String conclusion;
    private LanguageMyApplicationBean mLanguageBean;
    private LanguageConfirmAdapter mAdapter;

    @Override
    public IPresenter createPresenter() {
        return null;
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_language_confirm;
    }

    @Override
    public void initData() {
        mActionbar.setTitleTxt(getIntent().getStringExtra("actionBar_name"));
        result = getIntent().getIntExtra("result",0);
        conclusion = getIntent().getStringExtra("conclusion");
        mLanguageBean = (LanguageMyApplicationBean) getIntent().getSerializableExtra(TranslatorConstants.Common.DATA);
    }

    @Override
    public void initViews() {
        mAdapter = new LanguageConfirmAdapter(mLanguageBean.getCers());
        GridLayoutManager manager = new GridLayoutManager(this,3);
        mCersRecycler.setLayoutManager(manager);
        mCersRecycler.setAdapter(mAdapter);
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
                mReapplyRl.setVisibility(View.VISIBLE);
                break;
            case 4:
                mStateTxt.setText(getResources().getString(R.string.my_apply_confirming)+"...");
                break;
        }
    }

    @Override
    public void initEvents() {
        mAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int viewType, Object data, int position) {
                ArrayList<String> photo = new ArrayList<>();
                photo.add((String) data);
                ScaleAvatarActivity.startScaleAvatarActivity(LanguageConfirmActivity.this,photo,0, (ImageView) view.findViewById(R.id.item_cers_iv));
            }
        });
    }

    @OnClick({R.id.tv_close,R.id.confirm_language_passed_reapply_btn})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_close:
                finish();
                break;
            case R.id.confirm_language_passed_reapply_btn:
                Intent intent = new Intent(this,UploadCertificateActivity.class);
                intent.putExtra(TranslatorConstants.Common.FROM,"apply");
                List<Integer> languageIdList = new ArrayList<>();
                languageIdList.add(mLanguageBean.getLanguage_id());
                Gson gson = new Gson();
                String languagesJson = gson.toJson(languageIdList);
                intent.putExtra(TranslatorConstants.Common.JSON,languagesJson);
                startActivity(intent);
                finish();
                break;
        }
    }
}
