package com.gzligo.ebizzcardstranslator.business.message;

import android.support.v4.util.ArrayMap;

import com.gzligo.ebizzcardstranslator.base.mvp.BasePresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.mqtt.MqttChatManager;
import com.gzligo.ebizzcardstranslator.mqtt.MqttNotifyManager;
import com.gzligo.ebizzcardstranslator.mqtt.callback.MqttNotifyCallBack;
import com.gzligo.ebizzcardstranslator.mqtt.callback.RecentContactsCallBack;
import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.NewTransOrderBean;
import com.gzligo.ebizzcardstranslator.persistence.TranslatorList;
import com.gzligo.ebizzcardstranslator.persistence.TranslatorSelectedBean;

import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.gzligo.ebizzcardstranslator.business.message.RecentContactsFragment.START_TRANSLATION;

/**
 * Created by Lwd on 2017/6/1.
 */

public class RecentContactsPresenter extends BasePresenter<RecentContactsRepository> implements MqttNotifyCallBack<Object>,
        RecentContactsCallBack<ChatMessageBean> {
    private static final int QUERY_RECENT_CONSTANTS = 0x12;
    private static final int GET_NEW_TRANS_ORDER_BEAN_LIST = 0x13;
    private static final int GET_UN_TRANS_MSG_NUM = 0x14;
    private IView iView;

    public RecentContactsPresenter(RecentContactsRepository model, IView iView) {
        super(model);
        MqttNotifyManager.get().registerMqttNotifyCallBack(this);
        MqttChatManager.get().registerRecentContactsCallBack(this);
        this.iView = iView;
    }

    @Override
    public void handlerNotifyMsg(Object object) {
        if (object instanceof TranslatorSelectedBean) {
            final TranslatorSelectedBean translatorSelectedBean = (TranslatorSelectedBean) object;
            if (translatorSelectedBean.getStatus() == 0) {
                Message message = Message.obtain(iView);
                message.what = START_TRANSLATION;
                message.obj = translatorSelectedBean;
                message.dispatchToIView();
            }
            Message message = Message.obtain(iView);
            message.what = RecentContactsFragment.TRANSLATOR_SELECTED;
            message.dispatchToIView();
        }
    }

    @Override
    public void handlerRecentContactsMsg(final ChatMessageBean chatMessageBean) {
        Message message = Message.obtain(iView);
        message.what = RecentContactsFragment.COMMON_CHAT;
        message.obj = chatMessageBean;
        message.dispatchToIView();
    }

    public void queryRecentConstants() {
        getModel().queryRecentConstants().subscribe(new Consumer<TranslatorList>() {
            @Override
            public void accept(@NonNull TranslatorList selectedBeanList) throws Exception {
                Message message = Message.obtain(iView);
                message.what = QUERY_RECENT_CONSTANTS;
                message.obj = selectedBeanList;
                message.dispatchToIView();
            }
        });
    }

    public List<NewTransOrderBean> getNewTransOrderBeanList(int pos) {
        return getModel().getNewTransOrderBeanList(pos);
    }

    public void getNewTransOrderBeanList(final TranslatorSelectedBean orderBean) {
        getModel().getNewTransOrderBeanList(orderBean).subscribe(new Consumer<List<NewTransOrderBean>>() {
            @Override
            public void accept(@NonNull List<NewTransOrderBean> newTransOrderBeen) throws Exception {
                Message message = Message.obtain(iView);
                message.what = GET_NEW_TRANS_ORDER_BEAN_LIST;
                message.obj = orderBean;
                message.dispatchToIView();
            }
        });
    }

    public List<Boolean> getIsOnlineList() {
        return getModel().getIsOnline();
    }

    public void getUnTranslationMsgNum() {
        getModel().getUnTransMsgNum().subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                Message message = Message.obtain(iView);
                message.what = GET_UN_TRANS_MSG_NUM;
                message.arg1 = integer;
                message.dispatchToIView();
            }
        });
    }

    public ArrayMap<String, Integer> getTranslatorSelectedBeanMap(){
        return getModel().getTranslatorSelectedBeanMap();
    }

    public void setTranslatorSelectedBeanList(int position) {
        getModel().setTranslatorSelectedBeanList(position);
    }

    public List<NewTransOrderBean> getNewTransLists() {
        return getModel().getNewTransLists();
    }

    public void resetTranslatorSelectedBeanList(){
        getModel().resetTranslatorSelectedBeanList();
    }
}
