package com.gzligo.ebizzcardstranslator.business.account.view.login;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.mvp.BasePresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.business.MainActivity;
import com.gzligo.ebizzcardstranslator.constants.CommonConstants;
import com.gzligo.ebizzcardstranslator.db.BaseDBManager;
import com.gzligo.ebizzcardstranslator.mqtt.MqttManager;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;
import com.gzligo.ebizzcardstranslator.net.retrofit.BaseObserver;
import com.gzligo.ebizzcardstranslator.net.retrofit.OkHttpClientHelper;
import com.gzligo.ebizzcardstranslator.persistence.BindBankCardBean;
import com.gzligo.ebizzcardstranslator.persistence.District;
import com.gzligo.ebizzcardstranslator.persistence.ErrorMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.HttpResultBean;
import com.gzligo.ebizzcardstranslator.persistence.LoginBean;
import com.gzligo.ebizzcardstranslator.persistence.MessageTokenBean;
import com.gzligo.ebizzcardstranslator.persistence.RegisterBean;
import com.gzligo.ebizzcardstranslator.utils.ACache;
import com.gzligo.ebizzcardstranslator.utils.CommonUtils;
import com.gzligo.ebizzcardstranslator.utils.FileManager;
import com.gzligo.ebizzcardstranslator.utils.NetworkUtil;
import com.gzligo.ebizzcardstranslator.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by xfast on 2017/5/26.
 */

public class LoginPresenter extends BasePresenter<LoginRepository> {
    private static final int COUNT_DOWN_TIME = 0x01;
    private static final int COUNTRY_CODE = 0x02;
    private static final int VERIFY_CODE = 0x03;
    private static final int BIND_BANK_CARD = 0x04;
    private static final int VERIFY_CODE_ERROR = 0x05;
    private static final int BANK_CARD_ALREADY_BIND = 0x06;
    private IView iView;

    public LoginPresenter(LoginRepository model,IView iView) {
        super(model);
        this.iView = iView;
    }

    public LoginPresenter(LoginRepository model) {
        super(model);
    }

    public void sendSmsForCode(final Message msg,boolean isCache){
        getModel().validateSms((String) msg.objs[0], (String) msg.objs[1], isCache, new BaseObserver<ErrorMessageBean>() {
            @Override
            public void onNext(ErrorMessageBean o) {
                msg.what = RegisterActivity.MSG_SEND_FOR_CODE;
                msg.obj = o;
                msg.dispatchToIView();
            }
        });
    }

    public void register(final Message msg, boolean isCache) {
        RegisterBean registerBean = (RegisterBean) msg.obj;
        try {
            getModel().register(registerBean.getCc_code(), registerBean.getPassword(), registerBean.getPhone(),
                    registerBean.getNickname(), registerBean.getCode(), registerBean.getPortrait(),isCache, new BaseObserver<ErrorMessageBean>() {

                        @Override
                        public void onNext(ErrorMessageBean o) {
                            msg.what = RegisterPhoneVerActivity.MSG_INPUTTED_CODE;
                            msg.obj = o;
                            msg.dispatchToIView();
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void autoLogin() {
        Context context = AppManager.get().getApplication();
        if (!NetworkUtil.isNetworkConnected(context)) {
            return;
        }
        if (MqttManager.SOCKET_STATE == MqttManager.SocketState.inConnection) {
            return;
        }
        String userName = (String) SharedPreferencesUtils.getSharedPreferences(context, CommonConstants.USER_PHONE, "", CommonConstants.USER_INFO_PRE_NAME);
        String password = (String) SharedPreferencesUtils.getSharedPreferences(context, CommonConstants.USER_PWD, "", CommonConstants.USER_INFO_PRE_NAME);
        if (TextUtils.isEmpty(userName)||TextUtils.isEmpty(password)) {
            Log.e("empty-> userName=", userName + ", password=" + password);
            return;
        }
        if (MqttManager.get().isLogout()) {
            return;
        }
        String accessToken = ACache.get(context).getAsString(CommonConstants.ACacheContacts.LOGIN_ACCESS_TOKEN);
        if (TextUtils.isEmpty(accessToken)) {
            String areaNum = (String) SharedPreferencesUtils.getSharedPreferences(context, CommonConstants.USER_COUNTRY_ID, "", CommonConstants.USER_INFO_PRE_NAME);
            loginHttp(areaNum.replace("+","")+userName, password);
            return;
        }
        messageRefreshToken();
    }

    private void loginHttp(final String user, final String pwd) {
        HttpUtils.setAccessToken();
        getModel().login(user, pwd, true, new BaseObserver<LoginBean>() {
            @Override
            public void onNext(@NonNull final LoginBean loginBean) {
                if (loginBean.getError()==null) {
                    putACache(loginBean).subscribe(new BaseObserver() {
                        @Override
                        public void onNext(@NonNull Object o) {
                            new SharedPreferencesUtils<String>().putSharedPreference(AppManager.get().getApplication()
                                    , CommonConstants.REFRESH_TOKEN,
                                    loginBean.getRefresh_token(), CommonConstants.USER_INFO_PRE_NAME);
                            messageRefreshToken();
                        }
                    });
                }
            }
        });
    }

    public void login(final Message msg, boolean isCache,String areaNum) {
        getModel().login(areaNum+msg.objs[0], (String) msg.objs[1], isCache, new BaseObserver<LoginBean>() {

            @Override
            public void onNext(final LoginBean loginBean) {
                if (loginBean.getError()==null) {
                    putACache(loginBean).subscribe(new BaseObserver() {
                        @Override
                        public void onNext(@NonNull Object o) {
                            new SharedPreferencesUtils<String>().putSharedPreference(AppManager.get().getApplication()
                                    , CommonConstants.REFRESH_TOKEN, loginBean.getRefresh_token(), CommonConstants.USER_INFO_PRE_NAME);
                            String uuid = CommonUtils.getDeviceID();
                            messageToken(msg, loginBean, uuid, true);
                            getModel().isExitLanguagesList();//检查本地是否存在语言列表
                        }
                    });
                }else {
                    msg.what = LoginActivity.MSG_LOGIN;
                    msg.obj = loginBean;
                    msg.dispatchToIView();
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.e("d", Log.getStackTraceString(t));
                LoginBean loginBean = new LoginBean();
                if(t instanceof CompositeException){
                    CompositeException compositeException = (CompositeException) t;
                    List<Throwable> throwableList = compositeException.getExceptions();
                    for(int i=0;i<throwableList.size();i++){
                        Throwable throwable = throwableList.get(i);
                        if(throwable instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException){
                            com.jakewharton.retrofit2.adapter.rxjava2.HttpException httpException = (com.jakewharton.retrofit2.adapter.rxjava2.HttpException) throwable;
                            Response response = httpException.response();
                            ResponseBody responseBody = response.errorBody();
                            try {
                                String httpExceptionStr = new String(responseBody.bytes());
                                if (httpExceptionStr.contains("invalid_grant")) {
                                    Log.e("d", "密码错误");
                                    loginBean.setError("invalid_grant");
                                    break;
                                }else{
                                    loginBean.setError("internet_error");
                                    Log.e("d", "网络错误");
                                }
                            } catch (IOException e) {
                                Log.e("d", "网络错误");
                            }
                        }else{
                            loginBean.setError("internet_error");
                            Log.e("d", "网络错误");
                        }
                    }
                }else{
                    loginBean.setError("internet_error");
                    Log.e("d", "网络错误");
                }
                msg.what = LoginActivity.MSG_LOGIN;
                msg.obj = loginBean;
                msg.dispatchToIView();
            }
        });
    }

    public void requestRefreshToken(final String accessToken){
        final Context mContext = AppManager.get().getApplication();
        getModel().requestRefreshToken(accessToken, new BaseObserver<LoginBean>() {
            @Override
            public void onNext(@NonNull final LoginBean loginBean) {
                putACache(null).subscribe(new BaseObserver() {
                    @Override
                    public void onNext(@NonNull Object o) {
                        new SharedPreferencesUtils<String>().putSharedPreference(mContext, CommonConstants.REFRESH_TOKEN,
                                loginBean.getRefresh_token(), CommonConstants.USER_INFO_PRE_NAME);
                        messageRefreshToken();
                    }
                });
            }

            @Override
            public void onError(@NonNull Throwable e) {
                super.onError(e);
                putACache(null).subscribe();
            }
        });
    }

    private void messageToken(final Message msg, final LoginBean loginBean, String uuid, boolean isCache){
        getModel().messageToken(uuid, isCache, new BaseObserver<MessageTokenBean>() {

            @Override
            public void onNext(MessageTokenBean messageTokenBean) {
                onLoginSuccess(messageTokenBean);
                new SharedPreferencesUtils<String>().putSharedPreference(AppManager.get().getApplication(),
                        CommonConstants.USER_PHONE,msg.objs[0],CommonConstants.USER_INFO_PRE_NAME);
                new SharedPreferencesUtils<String>().putSharedPreference(AppManager.get().getApplication(),
                        CommonConstants.USER_PWD,msg.objs[1],CommonConstants.USER_INFO_PRE_NAME);
                new SharedPreferencesUtils<Boolean>().putSharedPreference(AppManager.get().getApplication(),
                        CommonConstants.FIRST_LOGIN,false,CommonConstants.USER_INFO_PRE_NAME);
                msg.what = LoginActivity.MSG_LOGIN;
                msg.obj = loginBean;
                msg.dispatchToIView();

            }

            @Override
            public void onError(@NonNull Throwable e) {
                super.onError(e);
                putACache(null).subscribe();
            }
        });
    }

    private void messageRefreshToken(){
        String uuid = CommonUtils.getDeviceID();
        getModel().messageToken( uuid, true, new BaseObserver<MessageTokenBean>() {

            @Override
            public void onNext(MessageTokenBean messageTokenBean) {
                onLoginSuccess(messageTokenBean);
                MainActivity.startMainActivity();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                super.onError(e);
                putACache(null).subscribe(new BaseObserver() {
                    @Override
                    public void onNext(@NonNull Object o) {
                        MainActivity.startMainActivity();
                    }
                });
            }
        });
    }

    private void onLoginSuccess(MessageTokenBean messageTokenBean){
        String[] msgToken = messageTokenBean.getMsg_token2().split("\\.");
        String jsonToken = new String(Base64.decode(msgToken[1], Base64.DEFAULT));
        try {
            JSONObject clientObject = new JSONObject(jsonToken);
            final String clientID = clientObject.getString(CommonConstants.PARAM_JTI);
            String userName = clientObject.getString(CommonConstants.UD_ID);
            BaseDBManager.initOpenHelper(AppManager.get().getApplication(),userName + clientID);
            FileManager.getFileManager(AppManager.get().getApplication()).createDir(userName + clientID);
            MqttManager.get().loginMqttAndroidSever(clientID,userName,messageTokenBean.getMsg_token2(),messageTokenBean.getMsg_token1());
            new SharedPreferencesUtils<String>().putSharedPreference(AppManager.get().getApplication(),
                    CommonConstants.USER_DB_NAME,userName + clientID,CommonConstants.USER_INFO_PRE_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void requestValidateSms(final Message message){
        getModel().requestValidateSms((String) message.objs[0],(String)message.objs[1], true, new BaseObserver<ErrorMessageBean>() {

            @Override
            public void onNext(ErrorMessageBean errorMessageBean) {
                if(errorMessageBean.getError()==0){
                    message.what = COUNTRY_CODE;
                    message.dispatchToIView();
                }
            }
        });
    }

    private Message msg;
    private Disposable disposable;
    public void countdown(final Message message) {
        msg = message;
        int time = (int) message.objs[0];
        if (time < 0) time = 0;
        final int countTime = time;
        disposable = Observable.interval(0, 1, TimeUnit.SECONDS)
                .map(new Function<Long, Integer>() {
                    @Override
                    public Integer apply(@NonNull Long aLong) throws Exception {
                        return countTime - aLong.intValue();
                    }
                })
                .take(countTime + 1)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        message.what = COUNT_DOWN_TIME;
                        message.obj = integer;
                        message.dispatchToIViewNoRecycle();
                        if(integer==0){
                            message.recycle();
                            disposable.dispose();
                        }
                    }
                });
    }

    public void requestValidateVerify(String phoneNum, String code, final IView iView) {
        getModel().requestValidateVerify(phoneNum,code, true, new BaseObserver<HttpResultBean>() {

            @Override
            public void onNext(HttpResultBean o) {
                if (disposable.isDisposed()){
                    msg = Message.obtain(iView);
                    msg.what = VERIFY_CODE;
                    msg.obj = o;
                    msg.dispatchToIView();
                }else{
                    disposable.dispose();
                    msg.obj = o;
                    msg.what = VERIFY_CODE;
                    msg.dispatchToIView();
                }
            }
        });
    }

    public void setUserCountry(District district){
        getModel().setUserCountry(district);
    }

    public void requestAddBankCard(String bankId, String bankCardUser,
                                   String bankCardNo, String validateCode) {
        getModel().requestAddBankCard(bankId, bankCardUser, bankCardNo, validateCode, true, new BaseObserver<BindBankCardBean>() {

            @Override
            public void onNext(BindBankCardBean bindBankCardBean) {
                Message message = Message.obtain(iView);
                String retMsg = bindBankCardBean.getMessage();
                switch (retMsg){
                    case "bank card already bind":
                        message.what = BANK_CARD_ALREADY_BIND;
                        break;
                    case "success":
                        message.what = BIND_BANK_CARD;
                        break;
                    case "verification code error":
                        message.what = VERIFY_CODE_ERROR;
                        break;
                }
                if(!TextUtils.isEmpty(retMsg)&&retMsg.equals("success")){

                }else{

                }
                message.obj = bindBankCardBean;
                message.dispatchToIView();
            }
        });
    }

    private Observable putACache(final LoginBean loginBean){
        final Context mContext = AppManager.get().getApplication();
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> e) throws Exception {
                String accessToken ="";
                int expiresIn = CommonConstants.ACacheContacts.ACCESS_TOKEN_EXPIRED;
                if(null!=loginBean){
                    accessToken = loginBean.getAccess_token();
                    expiresIn = loginBean.getExpires_in();
                }
                OkHttpClientHelper.getInstance().setClient();
                HttpUtils.setAccessToken();
                ACache.get(mContext).put(CommonConstants.ACacheContacts.LOGIN_ACCESS_TOKEN, accessToken, expiresIn);
                e.onNext("");
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
