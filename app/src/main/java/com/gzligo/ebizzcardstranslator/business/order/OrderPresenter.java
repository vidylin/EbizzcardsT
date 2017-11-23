package com.gzligo.ebizzcardstranslator.business.order;

import com.gzligo.ebizzcardstranslator.base.mvp.BasePresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.mqtt.MqttSystemManager;
import com.gzligo.ebizzcardstranslator.mqtt.callback.MqttSystemCallBack;
import com.gzligo.ebizzcardstranslator.net.retrofit.BaseObserver;
import com.gzligo.ebizzcardstranslator.persistence.ErrorMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.NewTransOrderBean;

import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.gzligo.ebizzcardstranslator.business.order.OrderFragment.NEW_TRANSLATOR_ORDER;
import static com.gzligo.ebizzcardstranslator.business.order.OrderFragment.ORDER_OBTAIN_FAILED;
import static com.gzligo.ebizzcardstranslator.business.order.OrderFragment.ORDER_OBTAIN_ROBBED;
import static com.gzligo.ebizzcardstranslator.business.order.OrderFragment.ORDER_OBTAIN_SUCCESS;

/**
 * Created by Lwd on 2017/6/8.
 */

public class OrderPresenter extends BasePresenter<OrderRepository> implements MqttSystemCallBack {
    private static final int QUERY_ORDER_LIST = 0x16;
    private IView iView;

    public OrderPresenter(OrderRepository model, IView iView) {
        super(model);
        MqttSystemManager.get().registerMqttSystemCallBack(this);
        this.iView = iView;
    }

    public void requestOrderObtain(final Message message, boolean isCache, final int position, final NewTransOrderBean data) {
        getModel().requestOrderObtain((String) message.objs[0], isCache, new BaseObserver<ErrorMessageBean>() {

            @Override
            public void onNext(ErrorMessageBean errorMessageBean) {
                Message msg = Message.obtain(iView);
                msg.arg1 = position;
                msg.obj = data;
                switch (errorMessageBean.getError()) {
                    case 0:
                        msg.what = ORDER_OBTAIN_SUCCESS;
                        break;
                    case 20020302:
                        msg.what = ORDER_OBTAIN_FAILED;
                        break;
                    default:
                        msg.what = ORDER_OBTAIN_ROBBED;
                        break;
                }
                getModel().deleteOrder(data);
                msg.dispatchToIView();
            }
        });
    }

    @Override
    public void handlerSystemMsg(final Object systemMsg) {
        if(systemMsg instanceof NewTransOrderBean){
            Message message = Message.obtain(iView);
            message.what = NEW_TRANSLATOR_ORDER;
            message.obj = systemMsg;
            message.dispatchToIView();
        }
    }

    public void queryOrderList() {
        getModel().queryOrderList().subscribe(new Consumer<List<NewTransOrderBean>>() {
            @Override
            public void accept(@NonNull List<NewTransOrderBean> newTransOrderBeen) throws Exception {
                Message message = Message.obtain(iView);
                message.what = QUERY_ORDER_LIST;
                message.obj = newTransOrderBeen;
                message.dispatchToIView();
            }
        });
    }

    public void deleteOrder(NewTransOrderBean newTransOrderBean) {
        getModel().deleteOrder(newTransOrderBean);
    }
}
