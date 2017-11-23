package com.gzligo.ebizzcardstranslator.business.wallet;

import android.app.Dialog;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.image.ImageLoader;
import com.gzligo.ebizzcardstranslator.image.glide.GlideImageConfig;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;
import com.gzligo.ebizzcardstranslator.persistence.CardInfo;
import com.gzligo.ebizzcardstranslator.utils.DialogUtils;
import com.gzligo.ebizzcardstranslator.utils.TranslatorCallBack;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/6/13.
 */

public class TakeExtraCashActivity extends BaseActivity<TakeExtraCashPresenter> {

    @BindView(R.id.take_extra_bank_icon_iv) ImageView mBankIconIv;
    @BindView(R.id.take_extra_bank_name_tv) TextView mBankNameTv;
    @BindView(R.id.take_extra_bank_card_num_tv) TextView mBankCardNumTv;
    @BindView(R.id.take_extra_money_currency_tv) TextView mTakeExtraCurrencyTv;
    @BindView(R.id.take_extra_take_money_num_edt) EditText mTakeExtraNumEdt;
    @BindView(R.id.take_extra_btn) Button mTakeExtraBtn;
    @BindView(R.id.take_extra_select_bank_rl) RelativeLayout takeExtraSelectBankRl;
    @BindView(R.id.take_extra_tv) TextView takeExtraTv;
    @BindView(R.id.take_extra_rl) RelativeLayout takeExtraRl;
    @BindView(R.id.take_extra_extra_tv) TextView takeExtraExtraTv;
    @BindView(R.id.take_extra_money_num_tv) TextView takeExtraMoneyNumTv;
    @BindView(R.id.take_extra_take_all_tv) TextView takeExtraTakeAllTv;
    @BindView(R.id.take_extra_take_all_rl) RelativeLayout takeExtraTakeAllRl;
    @BindView(R.id.take_extra_money_over_extra_tv) TextView takeExtraMoneyOverExtraTv;
    @BindView(R.id.take_extra_above_btn_tv) TextView takeExtraAboveBtnTv;

    private static final int TAKE_EXTRA_CASH = 0x67;
    private static final int TAKE_EXTRA_CASH_SUCCESS = 0x78;
    private static final int TAKE_EXTRA_CASH_PWD_ERROR = 0x77;
    private static final int TAKE_EXTRA_CASH_TIMES_MORE = 0x76;
    private Dialog mDialog;
    private String totalAmount;
    private ArrayList<CardInfo> infoArrayList;
    private String accessToken;
    private int position;

    @Override
    public TakeExtraCashPresenter createPresenter() {
        return new TakeExtraCashPresenter(new TakeExtraCashRepository());
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_take_extra_cash;
    }

    @Override
    public void initData() {
        totalAmount = getIntent().getStringExtra("totalAmount");
        infoArrayList = (ArrayList<CardInfo>) getIntent().getSerializableExtra("CardInfoList");
        accessToken = getIntent().getStringExtra("accessToken");
    }

    @Override
    public void initViews() {
        takeExtraMoneyNumTv.setText(totalAmount);
        mTakeExtraCurrencyTv.setText(totalAmount.substring(0, 1));
        CardInfo cardInfo = infoArrayList.get(position);
        loadImage(cardInfo.getCardPortrait(), mBankIconIv);
        mBankNameTv.setText(cardInfo.getBankCardNameZh());
        String cardId = cardInfo.getBank_card_no() + "";
        int len = cardId.length();
        String cardLastNum = (len > 4 ? cardId.subSequence(len - 4, len).toString() : cardId);
        mBankCardNumTv.setText(getResources().getString(R.string.bank_card_last_num) + " " + cardLastNum + " " + getResources().getString(R.string.bank_type));
    }

    @Override
    public void initEvents() {
        mTakeExtraBtn.setClickable(false);
        mTakeExtraNumEdt.addTextChangedListener(watcher);
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s)) {
                if (!TextUtils.isEmpty(mTakeExtraNumEdt.getText().toString().trim())) {
                    mTakeExtraBtn.setBackgroundResource(R.drawable.onclick_green_btn_selector);
                    mTakeExtraBtn.setClickable(true);
                } else {
                    mTakeExtraBtn.setBackgroundResource(R.mipmap.green_btn_normal);
                    mTakeExtraBtn.setClickable(false);
                }
            } else {
                mTakeExtraBtn.setBackgroundResource(R.mipmap.green_btn_normal);
                mTakeExtraBtn.setClickable(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @OnClick({R.id.tv_close, R.id.take_extra_select_bank_rl, R.id.take_extra_take_all_tv, R.id.take_extra_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_close:
                finish();
                break;
            case R.id.take_extra_select_bank_rl:
                startActivityForResult(new Intent(this, MyBankCardActivity.class)
                        .putExtra("position", position)
                        .putExtra("FROM", "TakeExtraCashActivity")
                        .putExtra("accessToken", accessToken), TAKE_EXTRA_CASH);
                break;
            case R.id.take_extra_take_all_tv:
                int len = totalAmount.length();
                mTakeExtraNumEdt.setText(totalAmount.substring(1, len));
                break;
            case R.id.take_extra_btn:
                handleTakeExtra();
                break;
        }
    }

    private void handleTakeExtra() {
        mDialog = DialogUtils.inputTakeCashPwdDialog(this, getResources().getString(R.string.take_extra_input_pwd),
                getResources().getString(R.string.take_extra_num), mTakeExtraCurrencyTv.getText().toString() + mTakeExtraNumEdt.getText().toString(),
                new TranslatorCallBack.onInputPwdCompleteListener() {
                    @Override
                    public void onComplete(String password) {
                        mDialog.dismiss();
                        String amount = mTakeExtraNumEdt.getText().toString() + "00";
                        CardInfo cardInfo = infoArrayList.get(position);
                        getPresenter().requestAddWithdrawOrder(Message.obtain(TakeExtraCashActivity.this, new String[]{amount, cardInfo.getCard_info_id() + "", password}));
                    }
                });
    }

    private void loadImage(String url, ImageView imageView) {
        ImageLoader.get().loadImage(AppManager.get().getApplication(), GlideImageConfig
                .builder()
                .url(HttpUtils.MEDIA_HOST + url)
                .imageView(imageView)
                .errorPic(R.mipmap.default_head_portrait)
                .isClearMemory(false)
                .build());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != data) {
            switch (requestCode) {
                case TAKE_EXTRA_CASH:
                    int pos = data.getIntExtra("position", 0);
                    if (pos != position) {
                        position = pos;
                        CardInfo cardInfo = infoArrayList.get(position);
                        loadImage(cardInfo.getCardPortrait(), mBankIconIv);
                        mBankNameTv.setText(cardInfo.getBankCardNameZh());
                        String cardId = cardInfo.getBank_card_no() + "";
                        int len = cardId.length();
                        String cardLastNum = (len > 4 ? cardId.subSequence(len - 4, len).toString() : cardId);
                        mBankCardNumTv.setText(getResources().getString(R.string.bank_card_last_num) + " " + cardLastNum + " " + getResources().getString(R.string.bank_type));
                    }
                    break;
                case TAKE_EXTRA_CASH_SUCCESS:
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }
        }
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what) {
            case TAKE_EXTRA_CASH_SUCCESS:
                startActivityForResult(new Intent(TakeExtraCashActivity.this, TakeCashDetailActivity.class), TAKE_EXTRA_CASH_SUCCESS);
                break;
            case TAKE_EXTRA_CASH_PWD_ERROR:
                Toast.makeText(this, getResources().getString(R.string.take_cash_pwd_error), Toast.LENGTH_SHORT).show();
                break;
            case TAKE_EXTRA_CASH_TIMES_MORE:
                Toast.makeText(this, getResources().getString(R.string.take_cash_time_error), Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
