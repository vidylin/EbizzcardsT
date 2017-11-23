package com.gzligo.ebizzcardstranslator.net.retrofit;

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
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.LoginBean;
import com.gzligo.ebizzcardstranslator.persistence.MessageTokenBean;
import com.gzligo.ebizzcardstranslator.persistence.MyApplicationResultBean;
import com.gzligo.ebizzcardstranslator.persistence.MyWalletRecord;
import com.gzligo.ebizzcardstranslator.persistence.MyWalletRecordBean;
import com.gzligo.ebizzcardstranslator.persistence.Province;
import com.gzligo.ebizzcardstranslator.persistence.Token;
import com.gzligo.ebizzcardstranslator.persistence.TranslatorOrderListBean;
import com.gzligo.ebizzcardstranslator.persistence.TravelTranslateBean;
import com.gzligo.ebizzcardstranslator.persistence.UserBean;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;


/**
 * Created by Lwd on 2017/5/23.
 */

public interface Api {

    //用户注册
    @Multipart
    @POST("account/register")
    Observable<ErrorMessageBean> register(@PartMap() Map<String, RequestBody> params);

    //用户注册包括图片
    @Multipart
    @POST("account/register")
    Observable<ErrorMessageBean> register(@PartMap() Map<String, RequestBody> params,@Part MultipartBody.Part data);

    //用户登录
    @FormUrlEncoded
    @POST("oauth/token")
    Observable<LoginBean> login(@Field("username") String username, @Field("password") String password, @Field("scope") String scope,
                                @Field("client_id") String client_id, @Field("grant_type") String grant_type);

    //用户登录
    @FormUrlEncoded
    @POST("oauth/token")
    Observable<LoginBean> refreshToken(@Field("refresh_token") String refreshToken, @Field("client_id") String client_id, @Field("grant_type") String grant_type);

    //发送验证码
    @FormUrlEncoded
    @POST("account/validate/sms")
    Observable<ErrorMessageBean> validateSms(@Field("phone") String phone, @Field("sms_type") String sms_type);

    //校验验证码
    @FormUrlEncoded
    @POST("account/validate/verify")
    Observable<HttpResultBean> validateVerify(@Field("phone") String phone, @Field("validate_code") String validate_code);

    //更新个人信息
    @Multipart
    @POST("account/update")
    Observable<ErrorMessageBean> accountUpdate(@Part MultipartBody.Part data,@PartMap() Map<String, RequestBody> params);

    //更新个人信息不包括图片
    @Multipart
    @POST("account/update")
    Observable<ErrorMessageBean> accountUpdate(@PartMap() Map<String, RequestBody> params);

    //更新个人信息图片
    @Multipart
    @POST("account/update")
    Observable<ErrorMessageBean> accountUpdate(@Part MultipartBody.Part data);

    //获取本人信息
    @GET("account/profile/self")
    Observable<UserBean> profileSelf();

    //上传基本审核资料
    @FormUrlEncoded
    @POST("account/audit/basedinfo")
    Observable<ErrorMessageBean> auditBaseInfo( @FieldMap Map<String, String> fields, @Field("education") int education);

    //上传身份证照片
    @FormUrlEncoded
    @POST("account/audit/idcards")
    Observable<ErrorMessageBean> auditIdCards( @Field("id_cards") String id_cards);

    //上传资格证书
    @FormUrlEncoded
    @POST("account/audit/cers")
    Observable<ErrorMessageBean> auditCers( @Field("languages") String languages, @Field("degree_cers") String degree_cers);

    //开始审核
    @FormUrlEncoded
    @POST("account/audit/startup")
    Observable<ErrorMessageBean> auditStartup(@Field("defaultParam") String defaultParam);

    //添加语言
    @FormUrlEncoded
    @POST("account/audit/addlanguages")
    Observable<ErrorMessageBean> addLanguages( @Field("languages") String languages);

    //查询审核记录
    @GET("account/audit/history")
    Observable<MyApplicationResultBean> auditHistory();

    //修改语言技能状态
    @FormUrlEncoded
    @POST("account/languageskill/status")
    Observable<HttpResultBean> languagesKillStatus( @Field("language_id") String language_id, @Field("status") String status);

    //重置密码
    @FormUrlEncoded
    @POST("account/validate/reset_password")
    Observable<HttpResultBean> resetPassword(@Field("phone") String phone, @Field("password") String password, @Field("token") String token);

    //抢单
    @FormUrlEncoded
    @POST("order/obtain")
    Observable<ErrorMessageBean> orderObtain(@Field("session_id") String session_id);

    //结束翻译
    @FormUrlEncoded
    @POST("order/finish")
    Observable<String> orderFinish(@Field("session_id") String session_id);

    //查询聊天翻译订单
    @FormUrlEncoded
    @POST("order/translator_order_list")
    Observable<TranslatorOrderListBean> translatorOrderList(@Field("until") String until, @Field("since") String since, @Field("count") String count);

    //用户更新状态
    /*status:0: 下线;1: 在线;2: 忙碌;3: 离开*/
    @FormUrlEncoded
    @POST("account/status/update")
    Observable<HttpResultBean> accountStatusUpdate( @Field("status") String status);

    //投诉
    @FormUrlEncoded
    @POST("misc/complaints")
    Observable<String> miscComplaints(@Field("defendant_id") String defendant_id, @Field("contents") String contents,
                                      @Field("remark") String remark);

    //警告
    @FormUrlEncoded
    @POST("misc/warning")
    Observable<String> miscWarning(@Field("target_id") String target_id, @Field("related_id") String related_id);

    //查询翻译官账户
    @FormUrlEncoded
    @POST("order/get_taccount")
    Observable<AccountBean> getAccount(@Field("defaultParam") String defaultParam);

    //查询翻译官银行卡信息
    @FormUrlEncoded
    @POST("order/get_tcardinfo")
    Observable<MyWalletRecordBean<CardInfo>> getCardInfo( @Field("defaultParam") String defaultParam);

    //查询翻译官提现记录
    @FormUrlEncoded
    @POST("order/get_withdraw_order")
    Observable<MyWalletRecordBean<MyWalletRecord>> getWithdrawOrder(@Field("until") String until,
                                                                    @Field("since") String since,
                                                                    @Field("count") String count);

    //绑定翻译官银行卡信息
    @FormUrlEncoded
    @POST("order/add_bank_card")
    Observable<BindBankCardBean> addBankCard(@Field("bank_id") String bank_id, @Field("bank_card_user") String bank_card_user,
                                             @Field("bank_card_no") String bank_card_no, @Field("validate_code") String validate_code);

    //解绑翻译官银行卡信息
    @FormUrlEncoded
    @POST("order/del_bank_card")
    Observable<HttpResultBean> getWithdrawOrder( @Field("card_info_id") String card_info_id);

    //翻译官提现
    @FormUrlEncoded
    @POST("order/add_withdraw_order")
    Observable<HttpResultBean> addWithdrawOrder(@Field("amount") String amount, @Field("card_info_id") String card_info_id,
                                        @Field("wpasswd") String wpasswd);

    //查询国家
    @FormUrlEncoded
    @POST("order/get_state")
    Observable<MyWalletRecordBean<Country>> getState( @Field("defaultParam") String defaultParam);

    //查询省份
    @FormUrlEncoded
    @POST("order/get_province")
    Observable<MyWalletRecordBean<Province>> orderGetState( @Field("state_id") String state_id);

    //查询城市
    @FormUrlEncoded
    @POST("order/get_city")
    Observable<MyWalletRecordBean<City>> getCity( @Field("prov_id") String prov_id);

    //查询银行定义
    @FormUrlEncoded
    @POST("order/get_banktype")
    Observable<MyWalletRecordBean<Bank>> getBankType( @Field("defaultParam") String defaultParam);

    //查询银行
    @FormUrlEncoded
    @POST("order/get_bank")
    Observable<MyWalletRecordBean<BankPoint>> getBank( @Field("bank_type_id") String bank_type_id, @Field("city_id") String city_id);

    //重置提现密码
    @FormUrlEncoded
    @POST("order/reset_withdraw_passwd")
    Observable<HttpResultBean> resetWithdrawPasswd(@Field("new_wpasswd") String fields, @Field("old_wpasswd") String oldPwd);

    @FormUrlEncoded
    @POST("order/reset_withdraw_passwd")
    Observable<HttpResultBean> resetWithdrawPasswdByToken(@Field("new_wpasswd") String fields, @Field("validate_code") String token);

    //验证提现密码
    @FormUrlEncoded
    @POST("order/validate_wpasswd")
    Observable<HttpResultBean> validateWpasswd( @Field("wpasswd") String wpasswd);


    //获取消息令牌
    @GET("message/token")
    Observable<MessageTokenBean> messageToken( @Query("udid") String uuid);

    //上传文件
    @Multipart
    @POST("fs/upload/general")
    Observable<HttpResultBean> uploadGeneral( @Part MultipartBody.Part data);

    //关闭推送
    @GET("misc/notificationcenter/close")
    Observable<HttpResultBean> notificationClose();

    //下载文件
    @GET
    @Streaming //如果下载较大的文件必须添加该注解
    Observable<ResponseBody> downLoadingFile(@Url String url);

    //获取语言列表
    @GET("misc/languages_list")
    Observable<HttpResultBean<LanguagesBean>> languagesList();

    //获取Hisir用户信息
    @GET("misc/hisir/userinfo")
    Observable<HisirUserInfoBean> hisirUserInfo (@Query("hisir_user_id") String userId);

    //旅游翻译--抢单
    @FormUrlEncoded
    @POST("order/travel_tuser_obtain")
    Observable<Token> requestTravelTuserObtain(@Field("order_id") String orderId);

    //旅游翻译--结束翻译
    @FormUrlEncoded
    @POST("order/travel_tuser_finish")
    Observable<HttpResultBean> requestTravelTuserFinish(@Field("order_id") String orderId,@Field("reason") String reason);

    //旅游翻译--接受翻译
    @FormUrlEncoded
    @POST("order/travel_tuser_accept")
    Observable<Token> requestTravelTuserAccept(@Field("order_id") String orderId);

    //旅游翻译--拒绝翻译
    @FormUrlEncoded
    @POST("order/travel_tuser_reject")
    Observable<HttpResultBean> requestTravelTuserReject(@Field("order_id") String orderId);

    //旅游翻译--查询聊天翻译订单
    @FormUrlEncoded
    @POST("order/translator_travel_order_list")
    Observable<TravelTranslateBean> translatorTravelOrderList(@Field("until") String until,
                                                              @Field("since") String since,
                                                              @Field("count") String count);
}
