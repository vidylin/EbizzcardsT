package com.gzligo.ebizzcardstranslator.business.wallet;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.IPresenter;
import com.gzligo.ebizzcardstranslator.common.ToolActionBar;
import com.gzligo.ebizzcardstranslator.manager.CommonBeanManager;
import com.gzligo.ebizzcardstranslator.persistence.CardInfo;
import com.gzligo.ebizzcardstranslator.persistence.MyWalletRecord;
import com.gzligo.ebizzcardstranslator.utils.CurrencyTypeUtil;
import com.gzligo.ebizzcardstranslator.utils.TimeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Lwd on 2017/7/14.
 */

public class MyWalletRecordDetailActivity extends BaseActivity {

    @BindView(R.id.my_wallet_actionbar) ToolActionBar myWalletActionbar;
    @BindView(R.id.get_cash_amount_tv) TextView getCashAmountTv;
    @BindView(R.id.cash_of_order) TextView cashOfOrder;
    @BindView(R.id.cash_of_status_tv) TextView cashOfStatusTv;
    @BindView(R.id.cash_of_bank_tv) TextView cashOfBankTv;
    @BindView(R.id.cash_of_time_tv) TextView cashOfTimeTv;
    @BindView(R.id.reason_tv) TextView reasonTv;
    @BindView(R.id.cash_failed_reason_ll) LinearLayout cashFailedReasonLl;

    private MyWalletRecord myWalletRecord;

    @Override
    public IPresenter createPresenter() {
        return null;
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_my_wallet_record_detail;
    }

    @Override
    public void initData() {
        myWalletRecord = (MyWalletRecord) getIntent().getSerializableExtra("MyWalletRecord");
    }

    @Override
    public void initViews() {
        cashOfOrder.setText(myWalletRecord.getOrder_no());
        String amount = TimeUtils.getDoubleDigit(myWalletRecord.getAmount());
        getCashAmountTv.setText(CurrencyTypeUtil.getCurrencySymbol(AppManager.get().getApplication(), myWalletRecord.getCurrency_type()) + amount);

        switch (myWalletRecord.getStatus()) {
            case 0:
                cashOfStatusTv.setTextColor(getResources().getColor(R.color.black));
                cashOfStatusTv.setText(getResources().getString(R.string.get_cash_ing));
                break;
            case 1:
                cashOfStatusTv.setTextColor(getResources().getColor(R.color.green));
                cashOfStatusTv.setText(getString(R.string.get_cash_success));
                break;
            case 2:
                cashOfStatusTv.setTextColor(getResources().getColor(R.color.red));
                cashOfStatusTv.setText(getResources().getString(R.string.get_cash_error));
                cashFailedReasonLl.setVisibility(View.VISIBLE);
                reasonTv.setText(myWalletRecord.getRemark());
                break;
        }
        int bankCardId = myWalletRecord.getCard_info_id();
        cashOfTimeTv.setText(TimeUtils.getMdHm(myWalletRecord.getLast_modify_time()));
        List<CardInfo> cardInfos = CommonBeanManager.getInstance().getCardInfoList();
        if (null != cardInfos && cardInfos.size() > 0) {
            for (CardInfo cardInfo : cardInfos) {
                if (cardInfo.getCard_info_id() == bankCardId) {
                    String bankNum = cardInfo.getBank_card_no();
                    int len = bankNum.length();
                    cashOfBankTv.setText(cardInfo.getBankCardNameZh() + "-" + getResources().getString(R.string.bank_type)
                            + "(" + (len > 4 ? bankNum.subSequence(len - 4, len) : bankNum)
                            + ")");
                    return;
                }
            }
        }

    }

    @Override
    public void initEvents() {
    }

    @OnClick({R.id.tv_close})
    void click() {
        finish();
    }
}
