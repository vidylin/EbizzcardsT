package com.gzligo.ebizzcardstranslator.net.httputil;

import android.content.Context;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.constants.CommonConstants;
import com.gzligo.ebizzcardstranslator.net.retrofit.Api;
import com.gzligo.ebizzcardstranslator.net.retrofit.OkHttpClientHelper;
import com.gzligo.ebizzcardstranslator.net.retrofit.RetrofitHelper;
import com.gzligo.ebizzcardstranslator.net.rxcache.CacheProvider;
import com.gzligo.ebizzcardstranslator.persistence.HisirUserAndEvents;
import com.gzligo.ebizzcardstranslator.persistence.HisirUserInfoBean;
import com.gzligo.ebizzcardstranslator.persistence.HttpResultBean;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.ProductVideoThumbnailBean;
import com.gzligo.ebizzcardstranslator.utils.ACache;
import com.gzligo.ebizzcardstranslator.utils.RequestBodyUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.rx_cache2.EvictProvider;
import io.rx_cache2.internal.RxCache;
import io.victoralbertos.jolyglot.GsonSpeaker;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


/**
 * Created by Lwd on 2017/5/23.
 */

public class HttpUtils {
    private static Api api;
    private static boolean isTestHost = false; //是否在测试环境下
    private static String BASE_URL;
    private static CacheProvider cacheProvider;
    public static final int cacheTime = 5;//缓存5分钟
    public static final String MEDIA_HOST;
    public static final String MQTT_SOCKET;
    public static final String MEDIA_HOST_DYNAMIC;
    public static final String UP_LOAD_PUSH_INFO;
    public static final String CLEAN_PUSH_INFO;
    private static String accessToken;

    static {
        if (!isTestHost) {//正式服务器
            BASE_URL = "https://srv-app-trans.hisir.net/translator_server/";
            MQTT_SOCKET = "tcp://srv-mqtt-trans.hisir.net";
            MEDIA_HOST = "https://srv-cdn.hisir.net/";
            MEDIA_HOST_DYNAMIC = "https:srv-app-dynamicfile.hisir.net";//动态文件服务器，不走CDN，比如聊天图片
            UP_LOAD_PUSH_INFO = "http://58.64.196.101:8002/upload_push_info";
            CLEAN_PUSH_INFO = "http://58.64.196.101:8002/del_push_info";
        }else{//测试服务器
            BASE_URL = "https://dev-app-trans.hisir.net/translator_server/";
            MQTT_SOCKET = "tcp://dev-mqtt-trans.hisir.net";
            MEDIA_HOST = "https://dev-cdn.hisir.net/";
            MEDIA_HOST_DYNAMIC = "https://dev-app-dynamicfile.hisir.net";//动态文件服务器，不走CDN，比如聊天图片
            UP_LOAD_PUSH_INFO = "https://58.64.196.109:8443/upload_push_info";
            CLEAN_PUSH_INFO = "https://58.64.196.109:8443/del_push_info";
        }
    }

    public static String getAccessToken() {
        if (TextUtils.isEmpty(accessToken)) {
            Context mContext = AppManager.get().getApplication();
            accessToken = ACache.get(mContext).getAsString(CommonConstants.ACacheContacts.LOGIN_ACCESS_TOKEN);
        }
        return accessToken;
    }

    public static void setAccessToken() {
        accessToken = null;
    }

    //订阅事件
    private static <T> void setSubscriber(Observable<T> observable, Observer<T> observer) {
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    //获取服务对象
    private static Api getService() {
        if (api == null) {
            api = RetrofitHelper.getInstance()
                    .getRetrofit(BASE_URL)
                    .create(Api.class);
        }
        return api;
    }

    private static CacheProvider getCacheProvider() {
        if (cacheProvider == null) {
            cacheProvider = new RxCache.Builder()
                    .persistence(AppManager.get().getApplication().getFilesDir(), new GsonSpeaker())
                    .using(CacheProvider.class);
        }
        return cacheProvider;
    }

    //注册用户
    public static void requestRegister(String cc_code,String password,String phone,String nickname,
                                       String code,String portrait, boolean isCache, Observer observer) {
        Map<String, RequestBody> params = new HashMap<>();
        params.put("cc_code", RequestBodyUtils.getRequestBody(cc_code));
        params.put("password", RequestBodyUtils.getRequestBody(password));
        params.put("phone", RequestBodyUtils.getRequestBody(phone));
        params.put("nickname", RequestBodyUtils.getRequestBody(nickname));
        params.put("code", RequestBodyUtils.getRequestBody(code));
        if(!TextUtils.isEmpty(portrait)){
            MultipartBody.Part part = RequestBodyUtils.getMultipartBody(portrait,"portrait");
            setSubscriber(getCacheProvider().requestRegister(getService().register(params,part), new EvictProvider(isCache)), observer);
        }else{
            setSubscriber(getCacheProvider().requestRegister(getService().register(params), new EvictProvider(isCache)), observer);
        }
    }

    //用户登录

    /**
     * @param isCache 是否对返回的数据结果进行缓存，true表示不缓存，false表示缓存
     */
    public static void requestLogin(String username, String password, String scope, String client_id, String grant_type, boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requestLogin(getService().login(username, password, scope, client_id, grant_type), new EvictProvider(isCache)), observer);
    }

    //发送验证码
    public static void requestValidateSms(String phone, String smsType, boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requestValidateSms(getService().validateSms(phone, smsType), new EvictProvider(isCache)), observer);
    }

    //校验验证码
    public static void requestValidateVerify(String phone, String validateCode, boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requestValidateVerify(getService().validateVerify(phone, validateCode), new EvictProvider(isCache)), observer);
    }

    //更新个人信息
    public static void requestAccountUpdate(Map<String, RequestBody> fields,
                                            MultipartBody.Part part, boolean isCache, Observer observer) {
        if(fields!=null&&part!=null){
            setSubscriber(getCacheProvider().requesTaccountUpdate(getService().
                    accountUpdate(part,fields), new EvictProvider(isCache)), observer);
        }else if(fields==null&&part!=null){
            setSubscriber(getCacheProvider().requesTaccountUpdate(getService().
                    accountUpdate(part), new EvictProvider(isCache)), observer);
        }else if(fields!=null&&part==null){
            setSubscriber(getCacheProvider().requesTaccountUpdate(getService().
                    accountUpdate(fields), new EvictProvider(isCache)), observer);
        }
    }

    //获取本人信息
    public static void requestProfileSelf(boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requesProfileSelf(getService().profileSelf(), new EvictProvider(isCache)), observer);
    }

    //上传基本审核资料
    public static void requestAuditBaseInfo(Map<String, String> fields,int education, boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requesAuditBaseInfo(getService().auditBaseInfo( fields,education), new EvictProvider(isCache)), observer);
    }

    //上传身份证照片
    public static void requestAuditIdCards(String idCards, boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requesAuditIdCards(getService().auditIdCards( idCards), new EvictProvider(isCache)), observer);
    }

    //上传资格证书
    public static void requestAuditCers(String languages, String degreeCers, boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requesAuditCers(getService().auditCers( languages, degreeCers), new EvictProvider(isCache)), observer);
    }

    //开始审核
    public static void requestAuditStartup(boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requesAuditStartup(getService().auditStartup(""), new EvictProvider(isCache)), observer);
    }

    //添加语言
    public static void requestAddLanguages(String languages, boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requesAddLanguages(getService().addLanguages( languages), new EvictProvider(isCache)), observer);
    }

    //查询审核记录
    public static void requestAuditHistory(boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requesAuditHistory(getService().auditHistory(), new EvictProvider(isCache)), observer);
    }

    //修改语言技能状态
    public static void requestLanguagesKillStatus(String languageId, String status, boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requesLanguagesKillStatus(getService().languagesKillStatus( languageId, status), new EvictProvider(isCache)), observer);
    }

    //重置密码
    public static void requestResetPassword(String phone, String password, String token, boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requesResetPassword(getService().resetPassword(phone, password, token), new EvictProvider(isCache)), observer);
    }

    //抢单
    public static void requesOrderObtaind(String sessionId, boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requesOrderObtaind(getService().orderObtain(sessionId), new EvictProvider(isCache)), observer);
    }

    //结束翻译
    public static void requestOrderFinish(String sessionId, boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requesOrderFinish(getService().orderFinish(sessionId), new EvictProvider(isCache)), observer);
    }

    //查询聊天翻译订单
    public static void requestTranslatorOrderList(String until, String since, String count, boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requestTranslatorOrderList(getService().translatorOrderList( until, since, count), new EvictProvider(isCache)), observer);
    }

    //更新状态
    public static void requesTaccountStatusUpdate(String status, boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requesTaccountStatusUpdate(getService().accountStatusUpdate( status), new EvictProvider(isCache)), observer);
    }

    //投诉
    public static void requestMiscComplaints(String defendantId, String contents, String remark, boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requesMiscComplaints(getService().miscComplaints(defendantId, contents, remark), new EvictProvider(isCache)), observer);
    }

    //警告
    public static void requestMiscWarning(String targetId, String relatedId, boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requesMiscWarning(getService().miscWarning(targetId, relatedId), new EvictProvider(isCache)), observer);
    }

    //查询翻译官账户
    public static void requestGetTaccount(boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requestGetTaccount(getService().getAccount(""), new EvictProvider(isCache)), observer);
    }

    //查询翻译官银行卡信息
    public static void requestGetCardInfo(boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requestGetCardInfo(getService().getCardInfo(""), new EvictProvider(isCache)), observer);
    }

    //查询翻译官提现记录
    public static void requestGetWithdrawOrder(String until,String since,
                                               String count, boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requestGetWithdrawOrder(getService().getWithdrawOrder( until,since,count), new EvictProvider(isCache)), observer);
    }

    //绑定翻译官银行卡信息
    public static void requestAddBankCard(String bankId, String bankCardUser,
                                         String bankCardNo, String validateCode, boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requestAddBankCard(getService().addBankCard( bankId, bankCardUser, bankCardNo, validateCode), new EvictProvider(isCache)), observer);
    }

    //解绑翻译官银行卡信息
    public static void requestGetWithdrawOrder(String cardInfoId, boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().getWithdrawOrder(getService().getWithdrawOrder( cardInfoId), new EvictProvider(isCache)), observer);
    }

    //翻译官提现
    public static void requestAddWithdrawOrder(String amount, String cardInfoId,
                                              String wpasswd, boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requestaddWithdrawOrder(getService().addWithdrawOrder( amount, cardInfoId, wpasswd), new EvictProvider(isCache)), observer);
    }

    //查询国家
    public static void requestGetState(boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requestGetState(getService().getState(""), new EvictProvider(isCache)), observer);
    }

    //查询省份
    public static void requestAddOrderGetState(String stateId, boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requesAddOrderGetState(getService().orderGetState( stateId), new EvictProvider(isCache)), observer);
    }

    //查询城市
    public static void requestGetCity(String provId, boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requesGetCity(getService().getCity( provId), new EvictProvider(isCache)), observer);
    }

    //查询银行定义
    public static void requestGetBankType(boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requestGetBankType(getService().getBankType(""), new EvictProvider(isCache)), observer);
    }

    //查询银行
    public static void requestGetBank(String bankTypeId, String cityId, boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requestGetBank(getService().getBank( bankTypeId, cityId), new EvictProvider(isCache)), observer);
    }

    //重置提现密码
    public static void requestResetWithdrawPasswd(String fields,String oldPwd,String token, boolean isCache, Observer observer) {
        if(!TextUtils.isEmpty(token)){
            setSubscriber(getCacheProvider().requesResetWithdrawPasswd(getService().resetWithdrawPasswdByToken( fields,token)
                    , new EvictProvider(isCache)), observer);
        }else if(!TextUtils.isEmpty(oldPwd)){
            setSubscriber(getCacheProvider().requesResetWithdrawPasswd(getService().resetWithdrawPasswd( fields,oldPwd)
                    , new EvictProvider(isCache)), observer);
        }

    }

    //验证提现密码
    public static void requestValidateWpasswd(String wpasswd, boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requesValidateWpasswd(getService().validateWpasswd( wpasswd), new EvictProvider(isCache)), observer);
    }

    //获取消息令牌
    public static void requesMessageToken(String uuid, boolean isCache, Observer observer) {
        api = null;
        OkHttpClientHelper.getInstance().setClient();
        setSubscriber(getCacheProvider().requesMessageToken(getService().messageToken( uuid), new EvictProvider(isCache)), observer);
    }

    //关闭推送
    public static void requestNotificationClose(boolean isCache, Observer observer) {
        setSubscriber(getCacheProvider().requesNotificationClose(getService().notificationClose(), new EvictProvider(isCache)), observer);
    }

    //上传文件
    public static void requestUploadGeneral(String filePath,String fileName, boolean isCache, Observer observer) {
        MultipartBody.Part part = RequestBodyUtils.getMultipartBody(filePath,fileName,"data");
        setSubscriber(getCacheProvider().requesUploadGeneral(getService().uploadGeneral( part)
                , new EvictProvider(isCache)), observer);
    }

    //下载文件
    public static void downLoadingFile(String url, Function<ResponseBody, ObservableSource<ProductVideoThumbnailBean>> mapper, Consumer<ProductVideoThumbnailBean> consumer){
        getService().downLoadingFile(url)
                .subscribeOn(Schedulers.io())
                .flatMap(mapper)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }

    //获取缩略图
    public static void requestVideoThumbnail(final String url, SimpleTarget simpleTarget){
        Glide.with(AppManager.get().getApplication())
                .asBitmap()
                .load(url)
                .thumbnail(0.1f).into(simpleTarget);
    }

    //获取语言列表
    public static void requestLanguagesList(Function<HttpResultBean<LanguagesBean>,ObservableSource<TreeMap<Integer,LanguagesBean>>> mapper,
                                            Consumer<TreeMap<Integer,LanguagesBean>> consumer){
        getService().languagesList()
                .subscribeOn(Schedulers.io())
                .flatMap(mapper)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }

    //获取翻译用户信息
    public static Observable<HisirUserAndEvents> requestZipHisirUserInfo(String hisirUserIdOne,String hisirUserIdTwo,BiFunction biFunction){
        Observable<HisirUserInfoBean> observableOne = getService().hisirUserInfo( hisirUserIdOne)
                .subscribeOn(Schedulers.newThread());
        Observable<HisirUserInfoBean> observableTwo = getService().hisirUserInfo( hisirUserIdTwo)
                .subscribeOn(Schedulers.newThread());

        return Observable.zip(observableOne, observableTwo, new BiFunction<HisirUserInfoBean, HisirUserInfoBean, HisirUserAndEvents>() {
            @Override
            public HisirUserAndEvents apply(@NonNull HisirUserInfoBean hisirUserInfoBean, @NonNull HisirUserInfoBean hisirUserInfoBean2) throws Exception {
                return new HisirUserAndEvents(hisirUserInfoBean, hisirUserInfoBean2);
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    //旅游翻译--抢单
    public static void requestTravelTuserObtain(String orderId, Observer observer ){
        setSubscriber(getService().requestTravelTuserObtain(orderId),observer);
    }

    //旅游翻译--结束翻译
    public static void requestTravelTuserFinish(String orderId,String reason, Observer observer ){
        setSubscriber(getService().requestTravelTuserFinish(orderId,reason),observer);
    }

    //旅游翻译--接受翻译
    public static void requestTravelTuserAccept(String orderId, Observer observer ){
        setSubscriber(getService().requestTravelTuserAccept(orderId),observer);
    }

    //旅游翻译--拒绝翻译
    public static void requestTravelTuserReject(String orderId, Observer observer ){
        setSubscriber(getService().requestTravelTuserReject(orderId),observer);
    }

    //旅游翻译--查询聊天翻译订单
    public static void requestTranslatorTravelOrderList(String until,String since,String count, Observer observer ){
        setSubscriber(getService().translatorTravelOrderList(until,since,count),observer);
    }

    public static void requestRefreshToken(String refreshToken, Observer observer ){
        setSubscriber(getService().refreshToken(refreshToken,"translator","refresh_token"),observer);
    }
}
