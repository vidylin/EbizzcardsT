package com.gzligo.ebizzcardstranslator.business.me;

import com.gzligo.ebizzcardstranslator.base.mvp.IModel;
import com.gzligo.ebizzcardstranslator.manager.CommonBeanManager;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;
import com.gzligo.ebizzcardstranslator.persistence.HttpResultBean;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.MyApplicationBean;
import com.gzligo.ebizzcardstranslator.persistence.UserBean;
import com.gzligo.ebizzcardstranslator.utils.RequestBodyUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by ZuoJian on 2017/6/15.
 */

public class PersonalRepository implements IModel{

    public void uploadPortrait(String filePath,boolean isCache,Observer observer){
        MultipartBody.Part part = RequestBodyUtils.getMultipartBody(filePath,"portrait");
        HttpUtils.requestAccountUpdate(null,part,isCache,observer);
    }

    public void uploadNickname(String nickname,boolean isCache,Observer observer){
        Map<String, RequestBody> params = new HashMap<>();
        params.put("nickname", RequestBodyUtils.getRequestBody(nickname));
        HttpUtils.requestAccountUpdate(params,null,isCache,observer);
    }

    public void uploadBirthday(String birthday,boolean isCache,Observer observer){
        Map<String, RequestBody> params = new HashMap<>();
        params.put("birthday", RequestBodyUtils.getRequestBody(birthday));
        HttpUtils.requestAccountUpdate(params,null,isCache,observer);
    }

    public void uploadSex(String sex,boolean isCache,Observer observer) {
        Map<String, RequestBody> params = new HashMap<>();
        params.put("sex", RequestBodyUtils.getRequestBody(sex));
        HttpUtils.requestAccountUpdate(params,null,isCache,observer);
    }

    public void uploadTranslationNum(String num,boolean isCache,Observer observer){
        Map<String, RequestBody> params = new HashMap<>();
        params.put("max_translation_number", RequestBodyUtils.getRequestBody(num));
        HttpUtils.requestAccountUpdate(params,null,isCache,observer);
    }

    public void uploadWorkTime(String timeJson,boolean isCache,Observer observer){
        Map<String, RequestBody> params = new HashMap<>();
        params.put("work_times", RequestBodyUtils.getRequestBody(timeJson));
        HttpUtils.requestAccountUpdate(params,null,isCache,observer);
    }

    public void uploadBaseMessage(String username,String idnum,String education,String profession,String languageJson,boolean isCache,Observer observer){
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("id_number", idnum);
        params.put("profession", profession);
        params.put("language_ids", languageJson);
        HttpUtils.requestAuditBaseInfo(params,Integer.valueOf(education),isCache,observer);
    }

    public void uploadIdCardPic(String id_cards,boolean isCache,Observer observer){
        HttpUtils.requestAuditIdCards(id_cards,isCache,observer);
    }

    public void uploadAuditCers(String languages,String degreeCers,boolean isCache,Observer observer){
        HttpUtils.requestAuditCers(languages,degreeCers,isCache,observer);
    }

    public void uploadAddLanguages(String languages,boolean isCache,Observer observer){
        HttpUtils.requestAddLanguages(languages,isCache,observer);
    }

    public void requestAuditStartup(boolean isCache, Observer observer){
        HttpUtils.requestAuditStartup(isCache,observer);
    }

    public void requestObjectId (String filePath, String fileName, final PersonalCallback personalCallback, boolean isCache){
        HttpUtils.requestUploadGeneral(filePath, fileName, isCache, new Observer<HttpResultBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(HttpResultBean httpResultBean) {
                if (personalCallback!=null) {
                    if (httpResultBean.getError() == 0) {
                        personalCallback.upLoadFileSuccess(httpResultBean.getObj_id());
                    }
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public UserBean getUserBean(){
        return CommonBeanManager.getInstance().getUserBean();
    }

    public void setUserBean(UserBean userBean){
        CommonBeanManager.getInstance().setUserBean(userBean);
    }

    public void setAuditList(List<MyApplicationBean> auditList){
        CommonBeanManager.getInstance().setAuditList(auditList);
    }

    public List<MyApplicationBean> getAuditList(){
        return CommonBeanManager.getInstance().getAuditList();
    }

    public TreeMap<Integer,LanguagesBean> getLanguageMap(){
        return CommonBeanManager.getInstance().getTreeMap();
    }

    public void requestLanguagesKillStatus(String languageId, String status, boolean isCache, Observer observer){
        HttpUtils.requestLanguagesKillStatus(languageId,status,isCache,observer);
    }

    public void requestAudit(boolean isCache,Observer observer){
        HttpUtils.requestAuditHistory(isCache,observer);
    }

    public void getUserBeanInfo(boolean isCache,Observer observer){
        HttpUtils.requestProfileSelf(isCache,observer);
    }
}
