package com.gzligo.ebizzcardstranslator.business.account.view.login;

import android.content.Context;
import android.content.SharedPreferences;

import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.mvp.IModel;
import com.gzligo.ebizzcardstranslator.constants.CommonConstants;
import com.gzligo.ebizzcardstranslator.manager.CommonBeanManager;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;
import com.gzligo.ebizzcardstranslator.persistence.District;
import com.gzligo.ebizzcardstranslator.persistence.HttpResultBean;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.utils.FileManager;

import java.util.List;
import java.util.TreeMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xfast on 2017/5/26.
 */

public class LoginRepository implements IModel {
    public void login(String username, String password, boolean isCache, Observer observer) {
        HttpUtils.requestLogin(username, password, "info", "translator", "password", isCache, observer);
    }

    public void register(String cc_code, String password, String phone, String nickname, String code, String portrait, boolean isCache, Observer observer){
        HttpUtils.requestRegister(cc_code,password,phone,nickname,code,portrait,isCache,observer);
    }

    //发送验证码
    public void validateSms(String phone, String smsType, boolean isCache, Observer observer) {
        HttpUtils.requestValidateSms(phone,smsType,isCache,observer);
    }

    //获取messageToken
    public void messageToken(String uuid, boolean isCache,Observer observer){
        HttpUtils.requesMessageToken(uuid,isCache,observer);
    }

    //messageToken
    public void requestRefreshToken(String accessToken,Observer observer){
        HttpUtils.requestRefreshToken(accessToken,observer);
    }

    private void requestLanguagesList(){
        HttpUtils.requestLanguagesList(new Function<HttpResultBean<LanguagesBean>,ObservableSource<TreeMap<Integer,LanguagesBean>>>() {

            @Override
            public ObservableSource<TreeMap<Integer,LanguagesBean>> apply(@NonNull HttpResultBean<LanguagesBean> o) throws Exception {
                TreeMap<Integer,LanguagesBean> languageList = new TreeMap<>();
                if(o.getError()==0){
                    List<LanguagesBean> list = o.getData();
                    for(LanguagesBean languagesBean : list){
                        languageList.put(languagesBean.getLanguage_id(),languagesBean);
                    }
                    CommonBeanManager.getInstance().setTreeMap(languageList);
                    FileManager.getFileManager(AppManager.get().getApplication()).saveConfigBeanFile(languageList, LanguagesBean.class.getSimpleName().toLowerCase());
                }
                return Observable.just(languageList);
            }
        }, new Consumer<TreeMap<Integer,LanguagesBean>>() {
            @Override
            public void accept(@NonNull TreeMap<Integer,LanguagesBean> map) throws Exception {
            }
        });
    }

    public void isExitLanguagesList(){
        FileManager.getFileManager(AppManager.get().getApplication())
                .readConfigBeanFileObservableMap(LanguagesBean.class)
                .subscribe(new Consumer<TreeMap<Integer,LanguagesBean>>() {
                    @Override
                    public void accept(@NonNull TreeMap<Integer,LanguagesBean> list) throws Exception {
                        if(null==list||list.size()==0){
                            requestLanguagesList();
                        }
                    }
                });
    }

    public void requestValidateSms(String phone, String smsType, boolean isCache, Observer observable){
        HttpUtils.requestValidateSms(phone,smsType,isCache,observable);
    }

    public void requestValidateVerify(String phone, String validateCode, boolean isCache, Observer observable){
        HttpUtils.requestValidateVerify(phone,validateCode,isCache,observable);
    }

    public void setUserCountry(final District district){
        Observable.create(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> e) throws Exception {
                SharedPreferences sharedPreferences = AppManager.get()
                        .getApplication()
                        .getSharedPreferences(CommonConstants.USER_INFO_PRE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(CommonConstants.USER_COUNTRY_NAME, district.getLocalName());
                editor.putString(CommonConstants.USER_COUNTRY_ID, district.getAreaNumber());
                editor.commit();
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    public void requestAddBankCard(String bankId, String bankCardUser,
                                          String bankCardNo, String validateCode, boolean isCache, Observer observer) {
        HttpUtils.requestAddBankCard(bankId,bankCardUser,bankCardNo,validateCode,isCache,observer);
    }
}
