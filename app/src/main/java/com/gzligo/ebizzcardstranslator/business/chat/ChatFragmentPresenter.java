package com.gzligo.ebizzcardstranslator.business.chat;

import com.gzligo.ebizzcardstranslator.base.mvp.BasePresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.constants.ChatConstants;
import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.ChatMsgProperty;
import com.gzligo.ebizzcardstranslator.persistence.ProductDetail;

import java.util.List;
import java.util.TreeMap;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.gzligo.ebizzcardstranslator.business.chat.ChatFragment.NEW_CHAT_COMMON_MSG;
import static com.gzligo.ebizzcardstranslator.business.chat.ChatFragment.NEW_CHAT_PRIVATE_MSG;
import static com.gzligo.ebizzcardstranslator.business.chat.ChatFragment.QUERY_CHAT_MSG;
import static com.gzligo.ebizzcardstranslator.business.chat.ChatFragment.UPDATE_COMMON_CHAT_LIST;
import static com.gzligo.ebizzcardstranslator.business.chat.ChatFragment.UPDATE_PRIVATE_CHAT_LIST;

/**
 * Created by Lwd on 2017/6/9.
 */

public class ChatFragmentPresenter extends BasePresenter<ChatFragmentRepository> implements ChatCallBack {
    private static final int GET_ALL_CHAT_MSG = 0x84;
    private static final int GET_PRODUCT_CHAT_MSG = 0x83;
    private IView iView;

    public ChatFragmentPresenter(ChatFragmentRepository model, IView iView) {
        super(model);
        getModel().registerChatCallBack(this);
        this.iView = iView;
    }

    public void sentTranslationChat(ChatMessageBean chatMessageBean) {
        getModel().sentTranslationChat(chatMessageBean);
    }

    @Override
    public void downLoadFileSuccess(ChatMessageBean chatMessageBean) {
        Message msg = Message.obtain(iView);
        msg.what = chatMessageBean.getIsPrivateMessage() ? NEW_CHAT_PRIVATE_MSG : NEW_CHAT_COMMON_MSG;
        msg.obj = chatMessageBean;
        msg.dispatchToIView();
    }

    @Override
    public void upLoadFileSuccess(ChatMessageBean chatMessageBean) {
    }

    public void upLoadFile(ChatMessageBean chatMessageBean) {
        getModel().requestUploadGeneral(chatMessageBean,true);
    }

    public ChatMessageBean buildSendPrivateMsg(int seconds, String fileUrl, ChatMessageBean chatMessageBean, int type,String content) {
        return getModel().buildSendPrivateMsg(seconds, fileUrl, chatMessageBean, type,content);
    }

    public ChatMessageBean buildSendTranslateMsg(int seconds, String fileUrl, ChatMessageBean chatMessageBean, int type) {
        return getModel().buildSendTranslateMsg(seconds, fileUrl, chatMessageBean, type);
    }

    public void queryChatMsg(int number, int currentPage, String fromId, String toId, String index) {
        getModel().queryChatMsg(number, currentPage, fromId, toId, index).subscribe(new Consumer<List<ChatMessageBean>>() {
            @Override
            public void accept(@NonNull List<ChatMessageBean> chatMessageBeanList) throws Exception {
                Message msg = Message.obtain(iView);
                msg.what = QUERY_CHAT_MSG;
                msg.obj = chatMessageBeanList;
                msg.dispatchToIView();
            }
        });
    }

    public List<ChatMessageBean> changeChoiceItem(List<ChatMessageBean> list, String msgId) {
        return getModel().changeChoiceItem(list, msgId);
    }

    public int isExitBean(ChatMessageBean bean) {
        int result = -1;
        TreeMap<String, Integer> treeMapIndex = getModel().getChatMsgIndex();
        String msgId = bean.getMsgId();
        List<ChatMessageBean> allChatMsg = getModel().getAllChatMsg();
        TreeMap<Integer,Boolean> unTransMsgPos = getModel().getUnTransMsgPositions();
        if (null != msgId&&msgId.length()>0) {
            if (treeMapIndex.containsKey(msgId)) {
                result = treeMapIndex.get(msgId);
                ChatMessageBean chatMessageBean = allChatMsg.get(result);
                allChatMsg.remove(result);
                if(!unTransMsgPos.containsKey(result)){
                    if (bean.getMsgId().length() > 0 && !bean.getIsPrivateMessage() && !bean.getMsgIsTrans()
                            && bean.getType() != ChatConstants.COMMON_PRODUCT_CHAT
                            &&(bean.getType()==0||bean.getType()==2)) {
                        if(unTransMsgPos.size()==0){
                            unTransMsgPos.put(result,true);
                            bean.setIsChoiceTranslate(true);
                            getModel().setTranslateWhich(result);
                        }else{
                            unTransMsgPos.put(result,false);
                        }
                        getModel().setUnTransMsgPositions(unTransMsgPos);
                    }
                }else{
                    boolean isChoice = chatMessageBean.getIsChoiceTranslate();
                    bean.setIsChoiceTranslate(isChoice);
                }
                allChatMsg.add(result,bean);
                getModel().setAllChatMsg(allChatMsg);
                return result;
            }else{
                allChatMsg.add(bean);
                if (bean.getMsgId().length() > 0 && !bean.getIsPrivateMessage() && !bean.getMsgIsTrans()
                        && bean.getType() != ChatConstants.COMMON_PRODUCT_CHAT
                        &&(bean.getType()==0||bean.getType()==2)) {
                    if(unTransMsgPos.size()==0){
                        unTransMsgPos.put(allChatMsg.size()-1,true);
                        bean.setIsChoiceTranslate(true);
                        getModel().setTranslateWhich(result);
                    }else{
                        unTransMsgPos.put(allChatMsg.size()-1,false);
                    }
                    getModel().setUnTransMsgPositions(unTransMsgPos);
                }
                treeMapIndex.put(msgId,allChatMsg.size()-1);
                getModel().setChatMsgIndex(treeMapIndex);
            }
        }else{
            allChatMsg.add(bean);
        }
        getModel().setAllChatMsg(allChatMsg);
        return result;
    }

    public void updateChatMsg(ChatMessageBean chatMessageBean){
        getModel().updateChatMsg(chatMessageBean);
    }

    public List<ChatMessageBean> getAllChatMsg(){
        return getModel().getAllChatMsg();
    }

    public TreeMap<Integer,Boolean> getUnTransMsgPositions(){
        return getModel().getUnTransMsgPositions();
    }

    public void updateCommonChatList(ChatMessageBean chatMessageBean,int which){
        if(!chatMessageBean.getIsReTrans()){
            List<ChatMessageBean> allChatMsg = getModel().getAllChatMsg();
            allChatMsg.remove(which);
            allChatMsg.add(which,chatMessageBean);
            getModel().setAllChatMsg(allChatMsg);
            TreeMap<Integer,Boolean> unTransMsgPos = getModel().getUnTransMsgPositions();
            if(unTransMsgPos.containsKey(which)){
                unTransMsgPos.remove(which);
            }
            getModel().setUnTransMsgPositions(unTransMsgPos);
        }
        Message msg = Message.obtain(iView);
        msg.what = UPDATE_COMMON_CHAT_LIST;
        msg.dispatchToIView();
    }

    public void updatePrivateChatList(ChatMessageBean chatMessageBean){
        List<ChatMessageBean> allChatMsg = getModel().getAllChatMsg();
        allChatMsg.add(chatMessageBean);
        TreeMap<String, Integer> treeMapIndex = getModel().getChatMsgIndex();
        treeMapIndex.put(chatMessageBean.getMsgId(),allChatMsg.size()-1);
        getModel().setChatMsgIndex(treeMapIndex);
        getModel().setAllChatMsg(allChatMsg);
        Message msg = Message.obtain(iView);
        msg.what = UPDATE_PRIVATE_CHAT_LIST;
        msg.dispatchToIView();
    }

    public void cleanAllChatMsg(){
        getModel().cleanAllChatMsg();
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

    public List<ChatMsgProperty> getChatMsgProperties() {
        return getModel().getChatMsgProperties();
    }

    public void getProductMsg(final String fromId, final String toId, final String msgId){
        getModel().getProductMsg(fromId,toId,msgId).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                Message message = Message.obtain(iView);
                message.what = GET_PRODUCT_CHAT_MSG;
                message.arg1 = integer;
                message.dispatchToIView();
            }
        });
    }

    public List<List<ProductDetail>> getProductDetails() {
        return getModel().getProductDetails();
    }

    public void setReTranslating(boolean reTranslating) {
        getModel().setReTranslating(reTranslating);
    }

    public boolean isReTranslating() {
        return getModel().isReTranslating();
    }

    public int getChooseItemPosition(){
        if(getModel().isReTranslating()){
            List<ChatMessageBean> chatMsg = getAllChatMsg();
            for(int i=0;i<chatMsg.size();i++){
                ChatMessageBean chatMessageBean = chatMsg.get(i);
                if(chatMessageBean.getIsChoiceTranslate()){
                    return i;
                }
            }
        }else{
            return getModel().getTranslateWhich();
        }
        return -1;
    }
}
