package com.gzligo.ebizzcardstranslator.business.me;

import com.gzligo.ebizzcardstranslator.base.mvp.BasePresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.net.retrofit.BaseObserver;
import com.gzligo.ebizzcardstranslator.persistence.ErrorMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.HttpResultBean;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.MyApplicationBean;
import com.gzligo.ebizzcardstranslator.persistence.MyApplicationResultBean;
import com.gzligo.ebizzcardstranslator.persistence.UserBean;

import java.util.List;
import java.util.TreeMap;

import io.reactivex.annotations.NonNull;

/**
 * Created by ZuoJian on 2017/6/15.
 */

public class PersonalPresenter extends BasePresenter<PersonalRepository>{
    private static final int CLOSE_OR_OPEN_LANGUAGE_ERROR = 0x41;
    private static final int CLOSE_OR_OPEN_LANGUAGE_SUCCESS = 0x42;
    private static final int IS_AUTHENTICATED = 0x03;

    public PersonalPresenter(PersonalRepository model) {
        super(model);
    }

    public UserBean getUserBean(){
        return getModel().getUserBean();
    }

    public List<MyApplicationBean> getAuditList(){
        return getModel().getAuditList();
    }

    public void uploadPortrait(final Message message, boolean isCache){
        getModel().uploadPortrait((String) message.objs[0],isCache, new BaseObserver<ErrorMessageBean>() {

            @Override
            public void onNext(ErrorMessageBean errorMessageBean) {
                message.what = MyProfileActivity.UPLOAD_PORTRAIT;
                message.obj = errorMessageBean;
                message.dispatchToIView();
            }

        });
    }

    public void uploadNickname(final Message message, boolean isCache){
        getModel().uploadNickname((String) message.objs[0], isCache, new BaseObserver<ErrorMessageBean>() {

            @Override
            public void onNext(ErrorMessageBean o) {
                message.what = MyProfileActivity.UPLOAD_NICKNAME;
                message.obj = o;
                message.dispatchToIView();
            }

        });
    }

    public void uploadBirthday(final Message message, boolean isCache){
        getModel().uploadBirthday((String) message.objs[0],isCache, new BaseObserver<ErrorMessageBean>() {

            @Override
            public void onNext(ErrorMessageBean errorMessageBean) {
                message.what = MyProfileActivity.UPLOAD_BIRTHDAY;
                message.obj = errorMessageBean;
                message.dispatchToIView();
            }

        });
    }

    public void uploadSex(final Message message, boolean isCache){
        getModel().uploadSex((String) message.objs[0], isCache, new BaseObserver<ErrorMessageBean>() {

            @Override
            public void onNext(ErrorMessageBean errorMessageBean) {
                message.what = MyProfileActivity.UPLOAD_SEX;
                message.obj = errorMessageBean;
                message.dispatchToIView();
            }

        });
    }

    public void uploadTranslationNum(final Message message, boolean isCache){
        getModel().uploadTranslationNum((String) message.objs[0], isCache, new BaseObserver<ErrorMessageBean>() {

            @Override
            public void onNext(ErrorMessageBean errorMessageBean) {
                message.what = OrderNumActivity.UPLOAD_TRANSLATION_NUM;
                message.obj = errorMessageBean;
                message.dispatchToIView();
            }

        });
    }

    public void uploadWorkTime(final Message message, boolean isCache){
        getModel().uploadWorkTime((String) message.objs[0], isCache, new BaseObserver<ErrorMessageBean>() {

            @Override
            public void onNext(ErrorMessageBean errorMessageBean) {
                message.what = WorkTimeActivity.UPLOAD_WORK_TIME;
                message.obj = errorMessageBean;
                message.dispatchToIView();
            }

        });
    }

    public void uploadBaseMessage(final Message message, boolean isCache){
        getModel().uploadBaseMessage((String) message.objs[0], (String) message.objs[1], (String) message.objs[2], (String) message.objs[3],
                (String) message.objs[4], isCache, new BaseObserver<ErrorMessageBean>() {

                    @Override
                    public void onNext(ErrorMessageBean o) {
                        message.what = PersonalBaseMessageActivity.UPLOAD_BASE_MESSAGE;
                        message.obj = o;
                        message.dispatchToIView();
                    }

                });
    }

    public void uploadIdCardPic(final Message message , boolean isCache){
        getModel().uploadIdCardPic((String) message.objs[0], isCache, new BaseObserver<ErrorMessageBean>() {

            @Override
            public void onNext(ErrorMessageBean errorMessageBean) {
                message.what = UploadIdCardActivity.UPLOAD_IDCARDS;
                message.obj = errorMessageBean;
                message.dispatchToIView();
            }

        });
    }

    public void uploadAuditCers(final Message message , boolean isCache){
        getModel().uploadAuditCers((String) message.objs[0], (String) message.objs[1], isCache, new BaseObserver<ErrorMessageBean>() {

            @Override
            public void onNext(ErrorMessageBean errorMessageBean) {
                message.what = UploadCertificateActivity.UPLOAD_AUDIT_CERS;
                message.obj = errorMessageBean;
                message.dispatchToIView();
            }

        });
    }

    public void uploadAddLanguages(final Message message , boolean isCache){
        getModel().uploadAddLanguages((String) message.objs[0], isCache, new BaseObserver<ErrorMessageBean>() {

            @Override
            public void onNext(ErrorMessageBean errorMessageBean) {
                message.what = UploadCertificateActivity.UPLOAD_ADD_LANGUAGES;
                message.obj = errorMessageBean;
                message.dispatchToIView();
            }

        });
    }

    public void requestAuditStartup(final Message message , boolean isCache){
        getModel().requestAuditStartup(isCache, new BaseObserver<ErrorMessageBean>() {

            @Override
            public void onNext(ErrorMessageBean errorMessageBean) {
                message.what = SubmitCompleteActivity.AUDIT_START_UP;
                message.obj = errorMessageBean;
                message.dispatchToIView();
            }

        });
    }

    public void requestObjectId(String filePath,String fileName,PersonalCallback personalCallback, boolean isCache){
        getModel().requestObjectId(filePath,fileName,personalCallback,isCache);
    }

    public void requestAudit(final Message message){
        getModel().requestAudit(true,new BaseObserver<MyApplicationResultBean>(){

            @Override
            public void onNext(@NonNull MyApplicationResultBean bean) {
                if (bean.getError()==0){
                    getModel().setAuditList(bean.getAudits());
                    message.what = MyApplicationActivity.REQUEST_AUDIT;
                }else {
                    message.what = MyApplicationActivity.REQUEST_AUDIT_FAILED;
                }
                message.dispatchToIView();
            }

        });
    }

    public TreeMap<Integer,LanguagesBean> getLanguageList(){
        return getModel().getLanguageMap();
    }

    public void requestLanguagesKillStatus(final Message message){
        getModel().requestLanguagesKillStatus((int) message.objs[0]+"", (int)message.objs[1]+"", true, new BaseObserver<HttpResultBean>() {

            @Override
            public void onNext(HttpResultBean o) {
                if(o.getError()!=0){
                    message.what = CLOSE_OR_OPEN_LANGUAGE_ERROR;
                }else{
                    message.what = CLOSE_OR_OPEN_LANGUAGE_SUCCESS;
                }
                message.dispatchToIView();
            }

            @Override
            public void onError(Throwable e) {
                message.what = CLOSE_OR_OPEN_LANGUAGE_ERROR;
                message.dispatchToIView();
            }

        });
    }

    public void getUserBeanInfo(final Message message , boolean isCache){
        getModel().getUserBeanInfo(isCache, new BaseObserver<UserBean>() {

            @Override
            public void onNext(UserBean userBean) {
                getModel().setUserBean(userBean);
                message.what = IS_AUTHENTICATED;
                message.dispatchToIView();
            }

        });
    }
}
