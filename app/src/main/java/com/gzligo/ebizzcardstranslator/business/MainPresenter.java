package com.gzligo.ebizzcardstranslator.business;

import android.text.TextUtils;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.mvp.BasePresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.manager.CommonBeanManager;
import com.gzligo.ebizzcardstranslator.net.retrofit.BaseObserver;
import com.gzligo.ebizzcardstranslator.persistence.HttpResultBean;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.UserBean;
import com.gzligo.ebizzcardstranslator.utils.PopupWindowUtil;

import java.util.TreeMap;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.gzligo.ebizzcardstranslator.business.MainActivity.IS_AUTHENTICATED;
import static com.gzligo.ebizzcardstranslator.business.MainActivity.UN_GRAB_ORDER_NUMBER;

/**
 * Created by Lwd on 2017/6/3.
 */

public class MainPresenter extends BasePresenter<MainRepository>{

    public MainPresenter(MainRepository model) {
        super(model);
    }

    public void userStatusUpdate(final Message msg){
        if(TextUtils.isEmpty(msg.objs[0].toString())){
            return;
        }
        getModel().requesTaccountStatusUpdate((String)msg.objs[0],true, new BaseObserver<HttpResultBean>() {

            @Override
            public void onNext(HttpResultBean o) {
                msg.what = MainActivity.UPDATE_USER_STATE;
                msg.obj = getUpdateResult(msg.objs[0].toString());
                msg.dispatchToIView();
            }

        });
    }

    private String getUpdateResult(String state){
        String result="";
        switch (Integer.parseInt(state)){
            case PopupWindowUtil.OFF_LINE_STATE:
                result = AppManager.get().getApplication().getResources().getString(R.string.recent_contacts_off_duty);
                break;
            case PopupWindowUtil.ON_LINE_STATE:
                result = AppManager.get().getApplication().getResources().getString(R.string.recent_contacts_free);
                break;
            case PopupWindowUtil.BUSYING_STATE:
                result = AppManager.get().getApplication().getResources().getString(R.string.recent_contacts_busy);
                break;
            case PopupWindowUtil.LEAVE_STATE:
                result = AppManager.get().getApplication().getResources().getString(R.string.recent_contacts_leave);
                break;
        }
        return result;
    }

    public void getUserBeanInfo(final Message message , boolean isCache){
        getModel().getUserBeanInfo(isCache, new BaseObserver<UserBean>() {

            @Override
            public void onNext(UserBean userBean) {
                getModel().setUserBean(userBean);
                getModel().saveUserInfo(userBean);
                message.what = IS_AUTHENTICATED;
                message.obj = userBean;
                message.dispatchToIView();
            }
        });
    }

    public void getLanguageList(){
        CommonBeanManager.getInstance().getLanguageList(new CommonBeanManager.OnLanguageListener() {
            @Override
            public void onGetLanguages(TreeMap<Integer, LanguagesBean> treeMap) {
                getModel().setTreeMap(treeMap);
            }
        });
    }

    public void getUnGrabTransOrderNumber(final IView iView){
        getModel().getUnGrabTransOrderNumber().subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                Message message = Message.obtain(iView);
                message.what = UN_GRAB_ORDER_NUMBER;
                message.arg1 = integer;
                message.dispatchToIView();
            }
        });
    }
}
