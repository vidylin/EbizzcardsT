package com.gzligo.ebizzcardstranslator.business.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.BaseFragment;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.business.chat.product.ProductDetailActivity;
import com.gzligo.ebizzcardstranslator.common.AudioRecordLayout;
import com.gzligo.ebizzcardstranslator.common.RecycleViewItemActiveCalculator;
import com.gzligo.ebizzcardstranslator.common.refreshlayout.OnRefreshListener;
import com.gzligo.ebizzcardstranslator.common.refreshlayout.RefreshLayout;
import com.gzligo.ebizzcardstranslator.constants.ChatConstants;
import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.ChatMsgProperty;
import com.gzligo.ebizzcardstranslator.persistence.NewTransOrderBean;
import com.gzligo.ebizzcardstranslator.persistence.ProductDetail;
import com.gzligo.ebizzcardstranslator.utils.KeyBoardUtils;
import com.gzligo.ebizzcardstranslator.utils.MediaManager;
import com.scwang.smartrefresh.layout.header.JuhuaHeader;
import com.waynell.videolist.visibility.calculator.SingleListViewItemActiveCalculator;
import com.waynell.videolist.visibility.scroll.RecyclerViewItemPositionGetter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;

/**
 * Created by Lwd on 2017/6/10.
 */

public class ChatFragment extends BaseFragment<ChatFragmentPresenter> implements Serializable{
    public static final int NEW_CHAT_COMMON_MSG = 0x30;
    public static final int NEW_CHAT_PRIVATE_MSG = 0x31;
    public static final int END_TRANSLATE = 0x42;
    public static final int START_TRANSLATE = 0x40;
    public static final int QUERY_CHAT_MSG = 0x43;
    public static final int ON_CHOICE_ITEM_TO_TRANS = 0x44;
    public static final int RE_TRANS_CHAT_MSG = 0x45;
    public static final int PRE_IMAGE = 0x46;
    public static final int PRE_VIDEO = 0x47;
    public static final int STOP_VOICE = 0x48;
    public static final int LONG_CLICK_HEAD_PORTRAIT = 0x32;
    public static final int CLICK_VOICE_MSG = 0x33;
    public static final int UPDATE_COMMON_CHAT_LIST = 0x34;
    public static final int UPDATE_PRIVATE_CHAT_LIST = 0x35;
    private static final int GET_ALL_CHAT_MSG = 0x84;
    private static final int GET_PRODUCT_CHAT_MSG = 0x83;
    @BindView(R.id.audio_record_layout) AudioRecordLayout audioRecordLayout;
    @BindView(R.id.chat_recycler_view) RecyclerView chatRecyclerView;
    @BindView(R.id.audio_record_layout_rl) RefreshLayout mTwinklingRefreshLayout;
    private List<ChatMessageBean> messageBeanList;
    private TreeMap<Integer,Boolean> unTransMsgPositions = new TreeMap<>();
    private ChatAdapter chatAdapter;
    private String fragmentIndex;
    private int translateWhich = -1;
    private SingleListViewItemActiveCalculator mCalculator;
    private RecycleViewItemActiveCalculator recycleViewItemActiveCalculator;
    private String fromId, toId;
    private String comFrom;
    private int mScrollState;
    private int number = 10;
    private int page = 0;
    private int lastPosition;
    private String toName;

    @Override
    public ChatFragmentPresenter createPresenter() {
        return new ChatFragmentPresenter(new ChatFragmentRepository(), this);
    }

    @Override
    public View onLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        NewTransOrderBean newTransOrderBean = (NewTransOrderBean) bundle.getSerializable("CHAT_USER");
        fragmentIndex = bundle.getString("FRAGMENT_INDEX");
        comFrom = bundle.getString("COME_FROM");
        if ("OrderFragment".equals(comFrom)) {
            number = 1;
        }
        fromId = newTransOrderBean.getFromUserId();
        toId = newTransOrderBean.getToUserId();
        toName = newTransOrderBean.getToName();
        getPresenter().queryChatMsg(number, page, fromId, toId, fragmentIndex);
        if (messageBeanList == null) {
            messageBeanList = new ArrayList<>();
        }
        if (chatAdapter == null) {
            chatAdapter = new ChatAdapter(messageBeanList, this, chatRecyclerView, newTransOrderBean, ChatConstants.COME_FROM_CHAT);
            chatRecyclerView.setAdapter(chatAdapter);
        }

    }

    @Override
    public void initViews() {
        audioRecordLayout.initAudioManager();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        chatRecyclerView.setLayoutManager(layoutManager);
        mCalculator = new SingleListViewItemActiveCalculator(chatAdapter, new RecyclerViewItemPositionGetter(layoutManager, chatRecyclerView));
        recycleViewItemActiveCalculator = new RecycleViewItemActiveCalculator(chatRecyclerView,chatAdapter);
        chatRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                mScrollState = newState;
                if (newState == RecyclerView.SCROLL_STATE_IDLE && chatAdapter.getItemCount() > 0) {
                    mCalculator.onScrollStateIdle();
                }
                if (null!=getActivity()&&!KeyBoardUtils.isSoftShowing(getActivity())){
                    recycleViewItemActiveCalculator.calculator(messageBeanList);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mCalculator.onScrolled(mScrollState);
                View lastChildView = recyclerView.getLayoutManager().getChildAt(recyclerView.getLayoutManager().getChildCount() - 1);
                lastPosition = recyclerView.getLayoutManager().getPosition(lastChildView);
            }
        });

        chatRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                boolean isVisibility = audioRecordLayout.isVisibility();
                if (KeyBoardUtils.isSoftShowing(getActivity())||isVisibility) {
                    if(translateWhich!=-1){
                        chatRecyclerView.smoothScrollToPosition(translateWhich);
                    }else{
                        chatRecyclerView.smoothScrollToPosition(lastPosition + 1);
                    }
                }
            }
        });
    }

    @Override
    public void initEvents() {
        mTwinklingRefreshLayout.setRefreshHeader(new JuhuaHeader(getActivity()));
        mTwinklingRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                super.onRefresh();
                if (!"OrderFragment".equals(comFrom) && page == 0) {
                    page = 1;
                }
                if("OrderFragment".equals(comFrom) && page == 0){
                    getPresenter().cleanAllChatMsg();
                }
                number = 10;
                getPresenter().queryChatMsg(number, page, fromId, toId, fragmentIndex);
                page++;
            }
        });

        chatRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                audioRecordLayout.hideKB();
                return false;
            }
        });

        audioRecordLayout.setAudioFinishRecorderListener(new AudioRecordLayout.AudioFinishRecorderListener() {

            @Override
            public void requestPermissionListener() {
                ChatActivity chatActivity = (ChatActivity) getActivity();
                chatActivity.requestPermission(ChatFragment.this);
            }

            @Override
            public void getSendTxt(String msg, ChatMessageBean chatMsg,int actionType) {
                switch (actionType) {
                    case NEW_CHAT_COMMON_MSG:
                        if (!TextUtils.isEmpty(msg) && translateWhich != -1) {
                            ChatMessageBean chatMessageBean = messageBeanList.get(translateWhich);
                            chatMessageBean.setTranslateContent(msg);
                            getPresenter().buildSendTranslateMsg(0, null, chatMessageBean, ChatConstants.TXT_PRIVATE);
                            getPresenter().sentTranslationChat(chatMessageBean);
                            getPresenter().updateCommonChatList(chatMessageBean,translateWhich);
                            isShowKeyBroad();
                        }
                        break;
                    case NEW_CHAT_PRIVATE_MSG:
                        chatMsg.setToName(toName);
                        ChatMessageBean chatMsgBean = getPresenter().buildSendPrivateMsg(0, null, chatMsg, ChatConstants.TXT_PRIVATE,msg);
                        getPresenter().sentTranslationChat(chatMsgBean);
                        getPresenter().updatePrivateChatList(chatMsgBean);
                        isShowKeyBroad();
                        audioRecordLayout.setActionType(getPresenter().isReTranslating()?RE_TRANS_CHAT_MSG:NEW_CHAT_COMMON_MSG);
                        break;
                    case RE_TRANS_CHAT_MSG:
                        getPresenter().setReTranslating(false);
                        if (!TextUtils.isEmpty(msg) && translateWhich != -1) {
                            ChatMessageBean chatMessageBean = messageBeanList.get(translateWhich);
                            chatMessageBean.setTranslateContent(msg);
                            chatMessageBean.setIsReTrans(true);
                            getPresenter().buildSendTranslateMsg(0, null, chatMessageBean, ChatConstants.TXT_PRIVATE);
                            getPresenter().sentTranslationChat(chatMessageBean);
                            getPresenter().updateCommonChatList(chatMessageBean,translateWhich);
                            isShowKeyBroad();
                        }
                        audioRecordLayout.setActionType(NEW_CHAT_COMMON_MSG);
                        break;
                }
            }

            @Override
            public void sendVoice(int seconds, String fileUrl, ChatMessageBean chatMsg,int actionType) {
                String msgContent = AppManager.get().getApplication().getString(R.string.chat_msg_voice);
                switch (actionType) {
                    case NEW_CHAT_COMMON_MSG:
                        if (translateWhich != -1) {
                            ChatMessageBean sendVoiceMessage = messageBeanList.get(translateWhich);
                            sendVoiceMessage.setTranslateContent(msgContent);
                            getPresenter().buildSendTranslateMsg(seconds, fileUrl, sendVoiceMessage, ChatConstants.VOICE_PRIVATE);
                            getPresenter().upLoadFile(sendVoiceMessage);
                            getPresenter().updateCommonChatList(sendVoiceMessage,translateWhich);
                            isShowKeyBroad();
                        }
                        break;
                    case NEW_CHAT_PRIVATE_MSG:
                        chatMsg.setToName(toName);
                        ChatMessageBean chatMessageBean = getPresenter().buildSendPrivateMsg(seconds, fileUrl, chatMsg, ChatConstants.VOICE_PRIVATE,msgContent);
                        getPresenter().upLoadFile(chatMessageBean);
                        getPresenter().updatePrivateChatList(chatMessageBean);
                        isShowKeyBroad();
                        audioRecordLayout.setActionType(getPresenter().isReTranslating()?RE_TRANS_CHAT_MSG:NEW_CHAT_COMMON_MSG);
                        break;
                    case RE_TRANS_CHAT_MSG:
                        getPresenter().setReTranslating(false);
                        if (translateWhich != -1) {
                            ChatMessageBean sendVoiceMessage = messageBeanList.get(translateWhich);
                            sendVoiceMessage.setTranslateContent(msgContent);
                            sendVoiceMessage.setIsReTrans(true);
                            getPresenter().buildSendTranslateMsg(seconds, fileUrl, sendVoiceMessage, ChatConstants.VOICE_PRIVATE);
                            getPresenter().upLoadFile(sendVoiceMessage);
                            getPresenter().updateCommonChatList(sendVoiceMessage,translateWhich);
                            isShowKeyBroad();
                        }
                        audioRecordLayout.setActionType(NEW_CHAT_COMMON_MSG);
                        break;
                }
            }

            @Override
            public void isShowKeyBoard() {
                isShowKeyBroad();
            }
        });
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what) {
            case NEW_CHAT_COMMON_MSG://收到新公共的msg
                ChatMessageBean chatMessageBean = (ChatMessageBean) message.obj;
                int pos = getPresenter().isExitBean(chatMessageBean);
                messageBeanList.clear();
                List<ChatMessageBean> allChatMsg = getPresenter().getAllChatMsg();
                messageBeanList.addAll(allChatMsg);
                TreeMap<Integer,Boolean> unTransMsg = getPresenter().getUnTransMsgPositions();
                unTransMsgPositions.clear();
                unTransMsgPositions.putAll(unTransMsg);
                if (pos != -1) {
                    chatAdapter.notifyItemChanged(pos);
                } else {
                    chatAdapter.notifyItemInserted(messageBeanList.size()-1);
                    chatRecyclerView.smoothScrollToPosition(messageBeanList.size()-1);
                }
                translateWhich = getPresenter().getChooseItemPosition();
                isShowKeyBroad();
                break;
            case ON_CHOICE_ITEM_TO_TRANS://选择需要翻译的item
                List<String> payloads = new ArrayList<>();
                if(translateWhich!=-1){
                    ChatMessageBean chatMessage = messageBeanList.get(translateWhich);
                    if(chatMessage.getMsgIsTrans()){
                        payloads.add("CHANGED_RE_TRANS_BG");
                    }else{
                        payloads.add("CHANGED");
                    }
                    chatMessage.setIsChoiceTranslate(false);
                    chatAdapter.notifyItemChanged(translateWhich, payloads);
                }
                audioRecordLayout.setActionType(NEW_CHAT_COMMON_MSG);
                translateWhich = Integer.parseInt((String) message.objs[0]);
                String msgId = (String) message.objs[1];
                ChatMessageBean chatMessage = messageBeanList.get(translateWhich);
                chatMessage.setIsChoiceTranslate(true);
                getPresenter().changeChoiceItem(messageBeanList, msgId);
                chatAdapter.updateChatMessageBeanList(messageBeanList);
                List<String> payload = new ArrayList<>();
                payload.add("CHANGED");
                chatAdapter.notifyItemChanged(translateWhich, payload);
                isShowKeyBroad();
                getPresenter().setReTranslating(false);
                break;
            case NEW_CHAT_PRIVATE_MSG://收到私聊的msg
                ChatMessageBean chatMsg = (ChatMessageBean) message.obj;
                int exitPos = getPresenter().isExitBean(chatMsg);
                messageBeanList.clear();
                allChatMsg = getPresenter().getAllChatMsg();
                messageBeanList.addAll(allChatMsg);
                unTransMsg = getPresenter().getUnTransMsgPositions();
                unTransMsgPositions.clear();
                unTransMsgPositions.putAll(unTransMsg);
                chatAdapter.updateChatMessageBeanList(messageBeanList);
                if (exitPos != -1) {
                    chatAdapter.notifyItemChanged(exitPos);
                } else {
                    int msgNum = messageBeanList.size();
                    chatAdapter.notifyItemInserted(msgNum);
                    chatRecyclerView.smoothScrollToPosition(msgNum);
                }
                isShowKeyBroad();
                break;
            case START_TRANSLATE://收到开始翻译request
                ChatMessageBean chatMessageBean1 = new ChatMessageBean();
                chatMessageBean1.setType(ChatConstants.START_CHAT);
                chatMessageBean1.setMsgTime(System.currentTimeMillis() + "");
                getPresenter().isExitBean(chatMessageBean1);
                messageBeanList.clear();
                allChatMsg = getPresenter().getAllChatMsg();
                messageBeanList.addAll(allChatMsg);
                chatAdapter.updateChatMessageBeanList(messageBeanList);
                chatAdapter.notifyItemInserted(messageBeanList.size());
                chatRecyclerView.smoothScrollToPosition(messageBeanList.size());
                break;
            case END_TRANSLATE://收到结束翻译msg
                ChatMessageBean chatMsgBean = (ChatMessageBean) message.obj;
                getPresenter().isExitBean(chatMsgBean);
                messageBeanList.clear();
                allChatMsg = getPresenter().getAllChatMsg();
                messageBeanList.addAll(allChatMsg);
                chatAdapter.updateChatMessageBeanList(messageBeanList);
                chatAdapter.notifyItemInserted(messageBeanList.size());
                chatRecyclerView.smoothScrollToPosition(messageBeanList.size());
                isShowKeyBroad();
                break;
            case QUERY_CHAT_MSG://上拉加载更多msg
                List<ChatMessageBean> lists = (List<ChatMessageBean>) message.obj;
                if (null != lists && lists.size() > 0) {
                    translateWhich = getPresenter().getChooseItemPosition();
                    messageBeanList.clear();
                    messageBeanList.addAll(lists);
                    chatAdapter.updateChatMessageBeanList(messageBeanList);
                    unTransMsg = getPresenter().getUnTransMsgPositions();
                    unTransMsgPositions.clear();
                    unTransMsgPositions.putAll(unTransMsg);
                    mTwinklingRefreshLayout.finishRefreshing();
                    chatAdapter.notifyDataSetChanged();
                    int count = messageBeanList.size();
                    if (count < 10) {
                        chatRecyclerView.smoothScrollToPosition(count - 1);
                    } else {
                        chatRecyclerView.smoothScrollToPosition(9);
                    }
                } else {
                    mTwinklingRefreshLayout.finishRefreshing();
                }
                if (lists.size() < 10&&page!=0) {
                    mTwinklingRefreshLayout.setEnableRefresh(false);
                }
                isShowKeyBroad();
                break;
            case RE_TRANS_CHAT_MSG://选择重新翻译msg
                if(translateWhich!=-1){
                    chatMessage = messageBeanList.get(translateWhich);
                    if(chatMessage.getMsgIsTrans()){
                        payloads = new ArrayList<>();
                        payloads.add("CHANGED_RE_TRANS_BG");
                    }else{
                        payloads = new ArrayList<>();
                        payloads.add("CHANGED");
                    }
                    chatMessage.setIsChoiceTranslate(false);
                    chatAdapter.notifyItemChanged(translateWhich, payloads);
                }
                translateWhich = message.arg1;
                chatMessage = messageBeanList.get(translateWhich);
                chatMessage.setIsChoiceTranslate(true);
                audioRecordLayout.showKeyBoard();
                audioRecordLayout.setActionType(RE_TRANS_CHAT_MSG);
                chatMsg = messageBeanList.get(translateWhich);
                if(null!=chatMsg){
                    audioRecordLayout.setReTranslateMsgContent(chatMsg);
                }
                getPresenter().setReTranslating(true);
                break;
            case PRE_VIDEO://预览视频
            case PRE_IMAGE://预览图片
                chatMessageBean = messageBeanList.get(message.arg1);
                if(chatMessageBean.getType()==6){
                    getPresenter().getProductMsg(chatMessageBean.getFromId(), chatMessageBean.getToId(), chatMessageBean.getMsgId());
                }else{
                    getPresenter().getAllMsgByClientId(chatMessageBean.getFromId(), chatMessageBean.getToId(), chatMessageBean.getMsgId(), null);
                }
                break;
            case STOP_VOICE://停止语音msg播放
                int position = message.arg1;
                chatAdapter.notifyItemChanged(position);
                break;
            case LONG_CLICK_HEAD_PORTRAIT://长按用户头像私聊
                position = message.arg1;
                chatMessageBean = messageBeanList.get(position);
                Activity activity = getActivity();
                if(activity instanceof ChatActivity){
                    ChatActivity chatActivity = (ChatActivity) activity;
                    boolean isOnline = chatActivity.isOnline(chatMessageBean);
                    if(isOnline){
                        audioRecordLayout.getContentInputEt(chatMessageBean);
                        audioRecordLayout.setActionType(NEW_CHAT_PRIVATE_MSG);
                        audioRecordLayout.showKeyBoard();
                    }
                }
                break;
            case CLICK_VOICE_MSG://播放语音msg
                position = message.arg1;
                chatMessageBean = messageBeanList.get(position);
                chatMessageBean.setIsReadVoice(true);
                getPresenter().updateChatMsg(chatMessageBean);
                break;
            case UPDATE_COMMON_CHAT_LIST://更新公共msg列表
                translateWhich = getPresenter().getChooseItemPosition();
                lists = getPresenter().getAllChatMsg();
                if(translateWhich!=-1){
                    lists.get(translateWhich).setIsChoiceTranslate(true);
                }
                messageBeanList.clear();
                messageBeanList.addAll(lists);
                chatAdapter.updateChatMessageBeanList(messageBeanList);
                chatAdapter.notifyDataSetChanged();
                break;
            case UPDATE_PRIVATE_CHAT_LIST://更新私聊msg
                lists = getPresenter().getAllChatMsg();
                messageBeanList.clear();
                messageBeanList.addAll(lists);
                chatAdapter.updateChatMessageBeanList(messageBeanList);
                chatAdapter.notifyItemInserted(messageBeanList.size()-1);
                break;
            case GET_ALL_CHAT_MSG://获取所有的msg
                pos = message.arg1;
                ArrayList<ChatMsgProperty> chatMsgProperties = (ArrayList<ChatMsgProperty>) getPresenter().getChatMsgProperties();
                Intent intent = new Intent(getActivity(), PreImageActivity.class);
                intent.putExtra("CHAT_MSG_LIST", chatMsgProperties);
                intent.putExtra("POSITION",pos);
                getActivity().startActivity(intent);
                break;
            case GET_PRODUCT_CHAT_MSG://获取所有的产品msg
                pos = message.arg1;
                ArrayList<List<ProductDetail>> productDetails = (ArrayList<List<ProductDetail>>)getPresenter().getProductDetails();
                intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("POSITION", pos);
                intent.putExtra("PRODUCT_DETAILS", productDetails);
                getActivity().startActivity(intent);
                break;
        }
    }

    @Override
    public void onDestroy() {
        if (null != audioRecordLayout) {
            audioRecordLayout.releaseButterKnife();
        }
        super.onDestroy();
    }

    private void isShowKeyBroad() {
        if (translateWhich!=-1) {
            audioRecordLayout.showKeyBoard();
        } else {
            audioRecordLayout.hideKeyBoard();
        }
    }

    public void allowPermission() {
        audioRecordLayout.allowablePermission();
    }

    public void onKeyDown() {
        MediaManager.getInstance().stop();
        int pos = MediaManager.getInstance().getPosition();
        if (pos != -1) {
            chatAdapter.notifyItemChanged(pos);
        }
        audioRecordLayout.cancelRecordVoice();
        isShowKeyBroad();
    }

    public void initAudioManager(){
        audioRecordLayout.initAudioManager();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != chatAdapter) {
            chatAdapter.notifyDataSetChanged();
        }
    }

}
