package com.gzligo.ebizzcardstranslator.business.me;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.common.refreshlayout.OnRefreshListener;
import com.gzligo.ebizzcardstranslator.common.refreshlayout.RefreshLayout;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.MyApplicationBean;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/6/9.
 */

public class MyApplicationActivity extends BaseActivity<PersonalPresenter>{

    @BindView(R.id.my_application_rv_trl) RefreshLayout mAudioLayoutRl;
    @BindView(R.id.my_application_recycler) RecyclerView mApplicationRecycler;

    private List<MyApplicationBean> mList = new ArrayList<>();
    private MyApplicationAdapter mAdapter;
    private TreeMap<Integer,LanguagesBean> beanTreeMap;
    public static final int REQUEST_AUDIT = 0x88;
    public static final int REQUEST_AUDIT_FAILED = 0x89;

    @Override
    public PersonalPresenter createPresenter() {
        return new PersonalPresenter(new PersonalRepository());
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_my_application;
    }

    @Override
    public void initData() {
        beanTreeMap = getPresenter().getLanguageList();
        requestAuditList();
        mAdapter = new MyApplicationAdapter(mList,beanTreeMap);
        mApplicationRecycler.setAdapter(mAdapter);
    }

    @Override
    public void initViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mApplicationRecycler.setLayoutManager(layoutManager);
        mAudioLayoutRl.setEnableLoadmore(false);
        mAudioLayoutRl.setEnableRefresh(true);
    }

    @Override
    public void initEvents() {
        mAudioLayoutRl.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                super.onRefresh();
                requestAuditList();
            }
        });
        mAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int viewType, Object data, int position) {
                if (mList.get(position).getAudit_type()==0){
                    Intent intent = new Intent(MyApplicationActivity.this,PersonalInfoConfirmActivity.class);
                    intent.putExtra(TranslatorConstants.Common.DATA,mList.get(position).getIdentity());
                    intent.putExtra("result",mList.get(position).getResult());
                    intent.putExtra("conclusion",mList.get(position).getConclusion());
                    startActivity(intent);
                }else {
                    TextView nameTv = (TextView) view.findViewById(R.id.my_application_item_name_txt);
                    String actionBarName = nameTv.getText().toString().trim();
                    Intent intent = new Intent(MyApplicationActivity.this,LanguageConfirmActivity.class);
                    intent.putExtra("actionBar_name",actionBarName);
                    intent.putExtra("result",mList.get(position).getResult());
                    intent.putExtra("conclusion",mList.get(position).getConclusion());
                    intent.putExtra(TranslatorConstants.Common.DATA,mList.get(position).getLanguageskill());
                    startActivity(intent);
                }
            }
        });
    }

    @OnClick({R.id.tv_close})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_close:
                finish();
                break;
        }
    }

    private void requestAuditList() {
        Message msg = Message.obtain(this);
        getPresenter().requestAudit(msg);
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what){
            case REQUEST_AUDIT:
                List<MyApplicationBean> auditList = getPresenter().getAuditList();
                mList.clear();
                mList.addAll(auditList);
                for (int i = 1 ; i < mList.size(); i++){
                    for (int j = 0; j < mList.size()-i; j++){
                        if (mList.get(j).getCreate_time() < mList.get(j+1).getCreate_time()){
                            MyApplicationBean bean = new MyApplicationBean();
                            bean = mList.get(j);
                            mList.set(j,mList.get(j+1));
                            mList.set(j+1,bean);
                        }
                    }
                }
                mAdapter.updateMyList(mList);
                mAdapter.notifyDataSetChanged();
                mAudioLayoutRl.finishRefreshing();
                break;
            case REQUEST_AUDIT_FAILED:
                break;
        }
    }
}
