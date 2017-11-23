package com.gzligo.ebizzcardstranslator.business.wallet;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.common.ToolActionBar;
import com.gzligo.ebizzcardstranslator.persistence.CardInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/6/8.
 */

public class MyBankCardActivity extends BaseActivity<MyBankCardPresenter> {

    @BindView(R.id.my_bankcard_actionbar) ToolActionBar myBankcardActionbar;
    @BindView(R.id.my_bankcard_release_bind_txt) TextView myBankcardReleaseBindTxt;
    @BindView(R.id.bank_list_rl) RelativeLayout bankListRl;
    @BindView(R.id.my_bank_card_recycler) RecyclerView myBankCardRecycler;
    @BindView(R.id.add_bank_iv) ImageView addBankIv;
    @BindView(R.id.my_bankcard_add_rl) RelativeLayout myBankcardAddRl;
    private static final int ADD_BANK = 0x99;
    private static final int DELETE_BANK_CARD = 0x40;
    private MyBankCardAdapter myBankCardAdapter;
    private String accessToken;
    private String cardInfoId;
    private List<CardInfo> lists;
    private int pos;
    private String from;

    @Override
    public MyBankCardPresenter createPresenter() {
        return new MyBankCardPresenter(new MyBankCardRepository());
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_my_bankcard;
    }

    @Override
    public void initData() {
        lists = getPresenter().getListCardInfo();
        accessToken = getIntent().getExtras().getString("accessToken");
        pos = getIntent().getIntExtra("position", 0);
        from = getIntent().getStringExtra("FROM");
        myBankCardAdapter = new MyBankCardAdapter(lists, pos);
        myBankCardRecycler.setAdapter(myBankCardAdapter);
        if (null != lists && lists.size() > 0) {
            cardInfoId = lists.get(0).getCard_info_id() + "";
        }
    }

    @Override
    public void initViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myBankCardRecycler.setLayoutManager(layoutManager);
    }

    @Override
    public void initEvents() {
        myBankCardAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener<CardInfo>() {
            @Override
            public void onItemClick(View view, int viewType, CardInfo data, int position) {
                cardInfoId = data.getCard_info_id() + "";
                if (pos != position) {
                    List<String> payloads = new ArrayList<>();
                    payloads.add("choice");
                    myBankCardAdapter.notifyItemChanged(position, payloads);
                    List<String> pList = new ArrayList<>();
                    pList.add("unChoice");
                    myBankCardAdapter.notifyItemChanged(pos, pList);
                    pos = position;
                }
                if ("TakeExtraCashActivity".equals(from)) {
                    Intent intent = new Intent();
                    intent.putExtra("position", position);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    @OnClick({R.id.tv_close, R.id.my_bankcard_add_rl, R.id.my_bankcard_release_bind_txt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_close:
                finish();
                break;
            case R.id.my_bankcard_add_rl:
                startActivityForResult(new Intent(MyBankCardActivity.this, AddBankCardActivity.class)
                        .putExtra("accessToken", accessToken)
                        .putExtra("hasPwd", 1),ADD_BANK);
                break;
            case R.id.my_bankcard_release_bind_txt:
                if(lists.size()>0){
                    getPresenter().requestGetWithdrawOrder(Message.obtain(this, new String[]{ cardInfoId}));
                }
                break;
        }
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what) {
            case DELETE_BANK_CARD:
                if(lists.size()>0){
                    lists.remove(pos);
                    pos = 0;
                    myBankCardAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(null==data){
            finish();
        }
    }
}
