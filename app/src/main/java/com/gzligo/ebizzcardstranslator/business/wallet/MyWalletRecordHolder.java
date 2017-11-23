package com.gzligo.ebizzcardstranslator.business.wallet;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.persistence.MyWalletRecord;
import com.gzligo.ebizzcardstranslator.utils.CurrencyTypeUtil;
import com.gzligo.ebizzcardstranslator.utils.TimeUtils;

import java.util.List;

import butterknife.BindView;

import static com.gzligo.ebizzcardstranslator.R.id.withdrawals_time_tv;


/**
 * Created by Lwd on 2017/7/12.
 */

public class MyWalletRecordHolder extends BaseHolder<MyWalletRecord> {

    @BindView(R.id.my_wallet_time_tv) TextView myWalletTimeTv;
    @BindView(R.id.my_wallet_head_ll) LinearLayout myWalletHeadLl;
    @BindView(R.id.withdrawals_tv) TextView withdrawalsTv;
    @BindView(R.id.withdrawals_status_tv) TextView withdrawalsStatusTv;
    @BindView(withdrawals_time_tv) TextView withdrawalsTimeTv;
    @BindView(R.id.withdrawals_sum_tv) TextView withdrawalsSumTv;

    private List<MyWalletRecord> cardInfoList;

    public MyWalletRecordHolder(View itemView, List<MyWalletRecord> cardInfoList) {
        super(itemView);
        this.cardInfoList = cardInfoList;
    }

    @Override
    public void setData(MyWalletRecord data, int position) {
        String amount = TimeUtils.getDoubleDigit(data.getAmount());
        withdrawalsSumTv.setText(CurrencyTypeUtil.getCurrencySymbol(AppManager.get().getApplication(), data.getCurrency_type()) + amount);
        switch (data.getStatus()) {
            case 0:
                withdrawalsStatusTv.setTextColor(AppManager.get().getApplication().getResources().getColor(R.color.black));
                withdrawalsStatusTv.setText(AppManager.get().getApplication().getResources().getString(R.string.get_cash_ing));
                break;
            case 1:
                withdrawalsStatusTv.setTextColor(AppManager.get().getApplication().getResources().getColor(R.color.green));
                withdrawalsStatusTv.setText(AppManager.get().getApplication().getResources().getString(R.string.get_cash_success));
                break;
            case 2:
                withdrawalsStatusTv.setTextColor(AppManager.get().getApplication().getResources().getColor(R.color.red));
                withdrawalsStatusTv.setText(AppManager.get().getApplication().getResources().getString(R.string.get_cash_error));
                break;
        }

        withdrawalsTimeTv.setText(TimeUtils.getMdHm(data.getCreate_time()));

        if (0 == position) {
            myWalletHeadLl.setVisibility(View.VISIBLE);
            myWalletTimeTv.setText(TimeUtils.getyM(data.getCreate_time()));
        } else {
            MyWalletRecord myWalletRecord = cardInfoList.get(position - 1);
            String headTime = TimeUtils.getyM(myWalletRecord.getCreate_time());
            String recordTime = TimeUtils.getyM(data.getCreate_time());
            if (headTime.equals(recordTime)) {
                myWalletHeadLl.setVisibility(View.GONE);
            } else {
                myWalletHeadLl.setVisibility(View.VISIBLE);
                myWalletTimeTv.setText(recordTime);
            }
        }
    }

    @Override
    public void onRelease() {
    }
}
