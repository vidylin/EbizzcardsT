package com.gzligo.ebizzcardstranslator.business.wallet;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.common.ToolActionBar;
import com.gzligo.ebizzcardstranslator.persistence.AccountBean;
import com.gzligo.ebizzcardstranslator.persistence.Bank;
import com.gzligo.ebizzcardstranslator.persistence.CardInfo;
import com.gzligo.ebizzcardstranslator.utils.CurrencyTypeUtil;
import com.gzligo.ebizzcardstranslator.utils.DialogUtils;
import com.gzligo.ebizzcardstranslator.utils.LanguageUtils;
import com.gzligo.ebizzcardstranslator.utils.TimeUtils;
import com.gzligo.ebizzcardstranslator.utils.TranslatorCallBack;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by LWD on 2017/6/2.
 */

public class MyWalletActivity extends BaseActivity<MyWalletPresenter> {

    @BindView(R.id.my_wallet_actionbar) ToolActionBar mActionbar;
    @BindView(R.id.money_icon_iv) ImageView moneyIconIv;
    @BindView(R.id.money_icon_title) TextView moneyIconTitle;
    @BindView(R.id.my_wallet_money_txt) TextView myWalletMoneyTxt;
    @BindView(R.id.my_wallet_take_money_btn) Button myWalletTakeMoneyBtn;
    @BindView(R.id.add_bank_iv) ImageView addBankIv;
    @BindView(R.id.my_wallet_add_bank_card_rl) RelativeLayout myWalletAddBankCardRl;
    @BindView(R.id.bank_card_iv) ImageView bankCardIv;
    @BindView(R.id.my_bank_arrow) ImageView myBankArrow;
    @BindView(R.id.my_wallet_my_bank_card_txt) TextView myWalletMyBankCardTxt;
    @BindView(R.id.my_wallet_my_bank_card_rl) RelativeLayout myWalletMyBankCardRl;
    @BindView(R.id.limit_txt) TextView limitTxt;

    private static final int GET_ACCOUNT_INFO = 0x94;
    private static final int GET_CARD_INFO = 0x93;
    private static final int GET_BANK_LIST = 0x89;
    private static final int TAKE_CASH = 0x91;
    private static final int ADD_BANK = 0x99;
    private static final int GET_CARD_INFO_NO = 0x90;
    private Dialog mMoreDialog;
    private int hasPwd;
    private CardInfo cardInfo;
    private String totalAmount;
    private ArrayList<CardInfo> cardInfos;

    @Override
    public MyWalletPresenter createPresenter() {
        return new MyWalletPresenter(new MyWalletRepository(), this);
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_my_wallet;
    }

    @Override
    public void initData() {
        getPresenter().requestGetAccount();
        getPresenter().requestGetCardInfo();

    }

    @Override
    public void initViews() {
        mActionbar.setRight1Icon(R.drawable.more_selector);
    }

    @Override
    public void initEvents() {
    }

    @OnClick({R.id.tv_close, R.id.tv_right1, R.id.my_wallet_take_money_btn, R.id.my_wallet_add_bank_card_rl, R.id.my_wallet_my_bank_card_rl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_close:
                finish();
                break;
            case R.id.tv_right1:
                mMoreDialog = DialogUtils.myWalletMoreDialog(this, new TranslatorCallBack.onMyWalletMoreClickListener() {
                    @Override
                    public void onAddBankCard() {
                        startActivity(new Intent(MyWalletActivity.this, AddBankCardActivity.class)
                                .putExtra("hasPwd", hasPwd));
                    }

                    @Override
                    public void onRecord() {
                        startActivity(new Intent(MyWalletActivity.this, MyWalletRecordActivity.class));
                    }

                    @Override
                    public void onPassword() {
                        startActivity(new Intent(MyWalletActivity.this, MyWalletPasswordManagerActivity.class));
                    }
                });
                break;
            case R.id.my_wallet_take_money_btn:
                ArrayList<CardInfo> cardInfos = (ArrayList<CardInfo>) getPresenter().getCardInfos();
                if (null == cardInfos || cardInfos.size() == 0) {
                    Toast.makeText(this, getResources().getString(R.string.bind_bank_card), Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivityForResult(new Intent(this, TakeExtraCashActivity.class)
                        .putExtra("totalAmount", totalAmount)
                        .putExtra("CardInfoList", cardInfos), TAKE_CASH);
                break;
            case R.id.my_wallet_add_bank_card_rl:
                startActivityForResult(new Intent(this, AddBankCardActivity.class)
                        .putExtra("hasPwd", hasPwd),ADD_BANK);
                break;
            case R.id.my_wallet_my_bank_card_rl:
                ArrayList<CardInfo> lists = (ArrayList<CardInfo>) getPresenter().getCardInfos();
                startActivityForResult(new Intent(this, MyBankCardActivity.class)
                        .putExtra("CardInfoList", lists),ADD_BANK);
                break;
        }
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what) {
            case GET_ACCOUNT_INFO:
                AccountBean accountBean = (AccountBean) message.obj;
                String amount = TimeUtils.getDoubleDigit(accountBean.getTotal_amount() - accountBean.getFrozen_amount());
                totalAmount = CurrencyTypeUtil.getCurrencySymbol(this, accountBean.getCurrency_type()) + amount;
                myWalletMoneyTxt.setText(totalAmount);
                hasPwd = accountBean.getHas_wpasswd();
                break;
            case GET_CARD_INFO:
                cardInfo = (CardInfo) message.obj;
                myWalletMyBankCardRl.setVisibility(View.VISIBLE);
                myWalletAddBankCardRl.setVisibility(View.GONE);
                cardInfos = (ArrayList<CardInfo>) getPresenter().getCardInfos();
                if(null==cardInfos||cardInfos.size()==0){
                    mActionbar.getRight1Txt().setClickable(false);
                    mActionbar.setRight1Icon(R.mipmap.my_wallet_more_normal);
                }else{
                    mActionbar.getRight1Txt().setClickable(true);
                    mActionbar.setRight1Icon(R.mipmap.my_wallet_more_select);
                }
                getPresenter().requestGetBankType();
                break;
            case GET_BANK_LIST:
                initMyBank(cardInfo);
                break;
            case GET_CARD_INFO_NO:
                myWalletMyBankCardRl.setVisibility(View.GONE);
                myWalletAddBankCardRl.setVisibility(View.VISIBLE);
                mActionbar.getRight1Txt().setClickable(false);
                mActionbar.setRight1Icon(R.mipmap.my_wallet_more_normal);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != data) {
            switch (requestCode) {
                case TAKE_CASH:
                    getPresenter().requestGetAccount();
                    break;
            }
        }else{
            getPresenter().requestGetCardInfo();
        }
    }

    private void initMyBank(CardInfo cardInfo){
        String cardId = cardInfo.getBank_card_no();
        Map<Integer, Bank> bankMap = getPresenter().getBankMap();
        Bank bank = bankMap.get(cardInfo.getBank_type());
        int len = cardId.length();
        if (LanguageUtils.getLanguage(this).equals(TranslatorConstants.SharedPreferences.LANGUAGE_CH)) {
            myWalletMyBankCardTxt.setText(len > 4 ? bank.getZh_name() + cardId.subSequence(len - 4, len) : bank.getZh_name() + cardId);
        }else if (LanguageUtils.getLanguage(this).equals(TranslatorConstants.SharedPreferences.LANGUAGE_EN)) {
            myWalletMyBankCardTxt.setText(len > 4 ? bank.getEn_name() + cardId.subSequence(len - 4, len) : bank.getEn_name() + cardId);
        }
    }
}
