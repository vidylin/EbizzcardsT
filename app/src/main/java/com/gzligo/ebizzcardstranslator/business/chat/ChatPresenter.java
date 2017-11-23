package com.gzligo.ebizzcardstranslator.business.chat;

import com.gzligo.ebizzcardstranslator.base.mvp.BasePresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.constants.ChatConstants;
import com.gzligo.ebizzcardstranslator.mqtt.MqttChatManager;
import com.gzligo.ebizzcardstranslator.mqtt.callback.MqttChatCallBack;
import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.NewTransOrderBean;
import com.gzligo.ebizzcardstranslator.persistence.TranslationChatResultBean;
import com.gzligo.ebizzcardstranslator.persistence.UnTransMagNumberBean;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.gzligo.ebizzcardstranslator.business.chat.ChatFragment.END_TRANSLATE;
import static com.gzligo.ebizzcardstranslator.business.chat.ChatFragment.NEW_CHAT_COMMON_MSG;
import static com.gzligo.ebizzcardstranslator.business.chat.ChatFragment.NEW_CHAT_PRIVATE_MSG;
import static com.gzligo.ebizzcardstranslator.business.chat.ChatFragment.START_TRANSLATE;

/**
 * Created by Lwd on 2017/6/22.
 */

public class ChatPresenter extends BasePresenter<ChatRepository> implements ChatActivityCallBack, MqttChatCallBack {
    private IView iView;
    private static final int REFRESH_POINT = 0x99;
    private static final int REFRESH_ONLINE_STATUS = 0x97;

    public ChatPresenter(ChatRepository model, IView iView) {
        super(model);
        this.iView = iView;
        MqttChatManager.get().registerChatActivityCallBack(this);
        MqttChatManager.get().registerMqttChatManagerCallBack(this);
    }

    public void queryUnTranslateMsgNum(TreeMap<Integer, NewTransOrderBean> treeMap, final int type) {
        getModel().queryUnTranslateMsgNum(treeMap).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean integerTreeMap) throws Exception {
                Message msg = Message.obtain(iView);
                msg.what = type;
                msg.dispatchToIView();
            }
        });
    }

    @Override
    public void getUnTranslateMsg(ChatMessageBean chatMsg) {
        if (!chatMsg.getIsPrivateMessage() && chatMsg.getMsgId().length() > 0
                && chatMsg.getType() != 6&&chatMsg.getType()!=ChatConstants.COMMON_VIDEO) {
            int countNum;
            if (chatMsg.getMsgIsTrans() == false) {
                countNum = 1;
            } else {
                countNum = -1;
            }
            getModel().updateUnTransPointNum(chatMsg.getFromId(), chatMsg.getToId(), countNum).subscribe(new Consumer<UnTransMagNumberBean>() {
                @Override
                public void accept(@NonNull UnTransMagNumberBean unTransMagNumberBean) throws Exception {
                    Message msg = Message.obtain(iView);
                    msg.what = REFRESH_POINT;
                    msg.obj = unTransMagNumberBean;
                    msg.dispatchToIView();
                }
            });
        }
    }

    @Override
    public void recordVoicePermission() {
    }

    public TreeMap<Integer, NewTransOrderBean> setMapIndex(List<NewTransOrderBean> list, int pos) {
        return getModel().setMapIndex(list, pos);
    }

    public TreeMap<Integer, NewTransOrderBean> updateMapIndex(List<NewTransOrderBean> list, int pos) {
        return getModel().updateMapIndex(list, pos);
    }

    public TreeMap<Integer, Integer> getUnTranslateMsgMap() {
        return getModel().getUnTranslateMsgMap();
    }

    public TreeMap<Integer, LanguagesBean> getLanguageMap() {
        return getModel().getLanguageMap();
    }

    public void setChatFragment(TreeMap<String, ChatFragment> chatFragmentsTreeMap) {
        getModel().setUserChatFragmentMap(chatFragmentsTreeMap);
    }

    @Override
    public void handlerChatMsg(ChatMessageBean chatMsg) {
        String usersId = chatMsg.getFromId() + chatMsg.getToId();
        String usersIdTwo = chatMsg.getToId() + chatMsg.getFromId();
        TreeMap<String, ChatFragment> chatFragmentTreeMap = getModel().getUserChatFragmentMap();
        if (chatFragmentTreeMap.containsKey(usersId)) {
            ChatFragment chatFragment = chatFragmentTreeMap.get(usersId);
            Message msg = Message.obtain(chatFragment);
            msg.what = chatMsg.getIsPrivateMessage() ? NEW_CHAT_PRIVATE_MSG : NEW_CHAT_COMMON_MSG;
            msg.obj = chatMsg;
            msg.dispatchToIView();
        } else if (chatFragmentTreeMap.containsKey(usersIdTwo)) {
            ChatFragment chatFragment = chatFragmentTreeMap.get(usersIdTwo);
            Message msg = Message.obtain(chatFragment);
            msg.what = chatMsg.getIsPrivateMessage() ? NEW_CHAT_PRIVATE_MSG : NEW_CHAT_COMMON_MSG;
            msg.obj = chatMsg;
            msg.dispatchToIView();
        }
    }

    @Override
    public void handlerChatResultMsg(TranslationChatResultBean translationChatResultBean) {
    }

    @Override
    public void endTranslateMsg(ChatMessageBean chatMsg) {
        if (chatMsg.getType() == ChatConstants.END_CHAT) {
            String usersId = chatMsg.getFromId() + chatMsg.getToId();
            String usersIdTwo = chatMsg.getToId() + chatMsg.getFromId();
            TreeMap<String, ChatFragment> chatFragmentTreeMap = getModel().getUserChatFragmentMap();
            Map<String, Integer> map = getModel().getMapIndex();
            if (null != map) {
                if (map.containsKey(usersId)) {
                    int pos = map.get(usersId);
                    Message msg = Message.obtain(iView);
                    msg.what = REFRESH_ONLINE_STATUS;
                    msg.obj = pos;
                    msg.dispatchToIView();
                } else if (map.containsKey(usersIdTwo)) {
                    int pos = map.get(usersIdTwo);
                    Message msg = Message.obtain(iView);
                    msg.what = REFRESH_ONLINE_STATUS;
                    msg.obj = pos;
                    msg.dispatchToIView();
                }
            }
            if (chatFragmentTreeMap.containsKey(usersId)) {
                ChatFragment chatFragment = chatFragmentTreeMap.get(usersId);
                Message msg = Message.obtain(chatFragment);
                msg.what = END_TRANSLATE;
                msg.obj = chatMsg;
                msg.dispatchToIView();
            } else if (chatFragmentTreeMap.containsKey(usersIdTwo)) {
                ChatFragment chatFragment = chatFragmentTreeMap.get(usersIdTwo);
                Message msg = Message.obtain(chatFragment);
                msg.what = END_TRANSLATE;
                msg.obj = chatMsg;
                msg.dispatchToIView();
            }
        }
    }

    public void startTranslate(String fromId, String toId) {
        String usersId = fromId + toId;
        String usersIdTwo = toId + fromId;
        TreeMap<String, ChatFragment> chatFragmentTreeMap = getModel().getUserChatFragmentMap();
        if (chatFragmentTreeMap.containsKey(usersId)) {
            ChatFragment chatFragment = chatFragmentTreeMap.get(usersId);
            Message msg = Message.obtain(chatFragment);
            msg.what = START_TRANSLATE;
            msg.dispatchToIView();
        } else if (chatFragmentTreeMap.containsKey(usersIdTwo)) {
            ChatFragment chatFragment = chatFragmentTreeMap.get(usersIdTwo);
            Message msg = Message.obtain(chatFragment);
            msg.what = START_TRANSLATE;
            msg.dispatchToIView();
        }
    }

    public boolean isOnline(ChatMessageBean chatMsg,List<Boolean> onLineLists){
        String usersId = chatMsg.getFromId() + chatMsg.getToId();
        String usersIdTwo = chatMsg.getToId() + chatMsg.getFromId();
        Map<String, Integer> map = getModel().getMapIndex();
        if (null != map&&null!=onLineLists) {
            if (map.containsKey(usersId)) {
                int value = map.get(usersId);
                if(onLineLists.size()>=value){
                    return onLineLists.get(value);
                } else{
                    return false;
                }
            } else if (map.containsKey(usersIdTwo)) {
                int value = map.get(usersIdTwo);
                if(onLineLists.size() >= value){
                    return onLineLists.get(value);
                } else{
                    return false;
                }
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
}
