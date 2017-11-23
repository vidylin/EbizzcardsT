package com.gzligo.ebizzcardstranslator.business.history;

import com.gzligo.ebizzcardstranslator.base.mvp.BasePresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.net.retrofit.BaseObserver;
import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.ChatMsgProperty;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.ProductDetail;
import com.gzligo.ebizzcardstranslator.persistence.TranslatorOrderListBean;

import java.util.List;
import java.util.TreeMap;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by Lwd on 2017/5/27.
 */

public class HistoryOrderPresenter extends BasePresenter<HistoryOrderRepository> {
    private static final int GET_CHAT_HISTORY_MSG = 0x50;
    private static final int GET_ALL_CHAT_MSG = 0x51;
    private static final int GET_PRODUCT_CHAT_MSG = 0x52;
    private IView iView;

    public HistoryOrderPresenter(HistoryOrderRepository model, IView iView) {
        super(model);
        this.iView = iView;
    }

    public void requestTranslatorOrderList(String until, String since, String count, boolean isCache, final int actionType) {
        getModel().requestTranslatorOrderList(until, since, count, isCache,
                new BaseObserver<TranslatorOrderListBean>() {
                    @Override
                    public void onNext(TranslatorOrderListBean translatorOrderListBean) {
                        if (translatorOrderListBean != null) {
                            Message message = Message.obtain(iView);
                            message.what = actionType;
                            message.obj = translatorOrderListBean;
                            message.dispatchToIView();
                        }
                    }
                });
    }

    public void getChatHisToryMsg(final Message message) {
        getModel().getChatHisToryMsg((String) message.objs[0], (String) message.objs[1],
                (String) message.objs[2]).subscribe(new Consumer<List<ChatMessageBean>>() {
            @Override
            public void accept(@NonNull List<ChatMessageBean> chatMessageBeanList) throws Exception {
                message.what = GET_CHAT_HISTORY_MSG;
                message.obj = chatMessageBeanList;
                message.dispatchToIView();
            }
        });
    }

    public TreeMap<Integer, LanguagesBean> getTreeMap() {
        return getModel().getTreeMap();
    }

    public void getAllMsgByClientId(String fromId, String toId, final String msgId, String orderId) {
        getModel().getAllMsgByClientId(fromId, toId, msgId, orderId).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer pos) throws Exception {

                Message message = Message.obtain(iView);
                message.what = GET_ALL_CHAT_MSG;
                message.arg1 = pos;
                message.dispatchToIView();
            }
        });
    }

    public void getProductMsg(final String fromId, final String toId, final String msgId,String orderId){
        getModel().getProductMsg(fromId,toId,msgId,orderId).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                Message message = Message.obtain(iView);
                message.what = GET_PRODUCT_CHAT_MSG;
                message.arg1 = integer;
                message.dispatchToIView();
            }
        });
    }

    public List<ChatMsgProperty> getChatMsgProperties() {
        return getModel().getChatMsgProperties();
    }

    public List<List<ProductDetail>> getProductDetails() {
        return getModel().getProductDetails();
    }
}
