package com.gzligo.ebizzcardstranslator.net.rxcache;

import com.gzligo.ebizzcardstranslator.persistence.AccountBean;
import com.gzligo.ebizzcardstranslator.persistence.Bank;
import com.gzligo.ebizzcardstranslator.persistence.BankPoint;
import com.gzligo.ebizzcardstranslator.persistence.BindBankCardBean;
import com.gzligo.ebizzcardstranslator.persistence.CardInfo;
import com.gzligo.ebizzcardstranslator.persistence.City;
import com.gzligo.ebizzcardstranslator.persistence.Country;
import com.gzligo.ebizzcardstranslator.persistence.ErrorMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.HisirUserInfoBean;
import com.gzligo.ebizzcardstranslator.persistence.HttpResultBean;
import com.gzligo.ebizzcardstranslator.persistence.LoginBean;
import com.gzligo.ebizzcardstranslator.persistence.MessageTokenBean;
import com.gzligo.ebizzcardstranslator.persistence.MyApplicationResultBean;
import com.gzligo.ebizzcardstranslator.persistence.MyWalletRecord;
import com.gzligo.ebizzcardstranslator.persistence.MyWalletRecordBean;
import com.gzligo.ebizzcardstranslator.persistence.Province;
import com.gzligo.ebizzcardstranslator.persistence.TranslatorOrderListBean;
import com.gzligo.ebizzcardstranslator.persistence.UserBean;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.rx_cache2.EvictProvider;
import io.rx_cache2.LifeCache;
import okhttp3.ResponseBody;

import static com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils.cacheTime;

/**
 * Created by Lwd on 2017/6/2.
 */

public interface CacheProvider {

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable <ErrorMessageBean> requestRegister(Observable observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable<LoginBean> requestLogin(Observable<LoginBean> observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable<ErrorMessageBean> requestValidateSms(Observable observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable<TranslatorOrderListBean> requestTranslatorOrderList(Observable<TranslatorOrderListBean> observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable<HttpResultBean> requestValidateVerify(Observable<HttpResultBean> observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable <ErrorMessageBean>requesTaccountUpdate(Observable observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable <UserBean>requesProfileSelf(Observable observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable <ErrorMessageBean>requesAuditBaseInfo(Observable observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable <ErrorMessageBean>requesAuditIdCards(Observable observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable <ErrorMessageBean>requesAuditCers(Observable observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable <ErrorMessageBean>requesAuditStartup(Observable observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable <ErrorMessageBean>requesAddLanguages(Observable observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable<MyApplicationResultBean> requesAuditHistory(Observable<MyApplicationResultBean> observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable<HttpResultBean> requesLanguagesKillStatus(Observable<HttpResultBean> observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable<HttpResultBean> requesResetPassword(Observable<HttpResultBean> observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable<ErrorMessageBean> requesOrderObtaind(Observable<ErrorMessageBean> observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable requesOrderFinish(Observable observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable<HttpResultBean> requesTaccountStatusUpdate(Observable<HttpResultBean> observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable requesMiscComplaints(Observable observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable requesMiscWarning(Observable observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable<AccountBean> requestGetTaccount(Observable<AccountBean> observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable<MyWalletRecordBean<CardInfo>> requestGetCardInfo(Observable<MyWalletRecordBean<CardInfo>> observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable<MyWalletRecordBean<MyWalletRecord>> requestGetWithdrawOrder(Observable<MyWalletRecordBean<MyWalletRecord>> observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable<BindBankCardBean> requestAddBankCard(Observable<BindBankCardBean> observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable<HttpResultBean> getWithdrawOrder(Observable<HttpResultBean> observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable<HttpResultBean> requestaddWithdrawOrder(Observable<HttpResultBean> observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable<MyWalletRecordBean<Country>> requestGetState(Observable<MyWalletRecordBean<Country>> observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable<MyWalletRecordBean<Province>> requesAddOrderGetState(Observable observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable<MyWalletRecordBean<City>> requesGetCity(Observable observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable<MyWalletRecordBean<Bank>> requestGetBankType(Observable<MyWalletRecordBean<Bank>> observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable<MyWalletRecordBean<BankPoint>> requestGetBank(Observable observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable<HttpResultBean> requesResetWithdrawPasswd(Observable<HttpResultBean> observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable<HttpResultBean> requesValidateWpasswd(Observable<HttpResultBean> observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable<MessageTokenBean> requesMessageToken(Observable<MessageTokenBean> observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable<HttpResultBean> requesNotificationClose(Observable<HttpResultBean> observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable<HttpResultBean> requesUploadGeneral(Observable<HttpResultBean> observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable<ResponseBody> downLoadingFile(Observable<ResponseBody> observable, EvictProvider evictProvider);

    @LifeCache(duration = cacheTime, timeUnit = TimeUnit.MINUTES)
    Observable<HisirUserInfoBean> hisirUserInfo(Observable<HisirUserInfoBean> observable, EvictProvider evictProvider);
}
