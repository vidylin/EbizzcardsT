package com.gzligo.ebizzcardstranslator.business.message;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.BaseFragment;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.business.MainActivity;
import com.gzligo.ebizzcardstranslator.business.chat.ChatActivity;
import com.gzligo.ebizzcardstranslator.constants.ChatConstants;
import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.NewTransOrderBean;
import com.gzligo.ebizzcardstranslator.persistence.TranslatorList;
import com.gzligo.ebizzcardstranslator.persistence.TranslatorSelectedBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Lwd on 2017/5/24.
 */

public class RecentContactsFragment extends BaseFragment<RecentContactsPresenter> {
    public static final int TRANSLATOR_SELECTED = 0x00;
    public static final int START_TRANSLATION = 0x11;
    public static final int COMMON_CHAT = 0x10;
    private static final int QUERY_RECENT_CONSTANTS = 0x12;
    private static final int GET_NEW_TRANS_ORDER_BEAN_LIST = 0x13;
    private static final int GET_UN_TRANS_MSG_NUM = 0x14;
    @BindView(R.id.no_recent_contacts) LinearLayout noRecentContacts;
    @BindView(R.id.recent_contacts_rv) RecyclerView recentContactsRv;
    private RecentContactsAdapter recentContactsAdapter;
    private List<TranslatorSelectedBean> selectedBeanList;

    @Override
    public RecentContactsPresenter createPresenter() {
        IView iView = RecentContactsFragment.this;
        return new RecentContactsPresenter(new RecentContactsRepository(), iView);
    }

    @Override
    public View onLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recent_contacts, container, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        if (selectedBeanList == null) {
            selectedBeanList = new ArrayList<>();
        }
        if (recentContactsAdapter == null) {
            recentContactsAdapter = new RecentContactsAdapter(selectedBeanList);
            recentContactsRv.setAdapter(recentContactsAdapter);
        }
        getPresenter().queryRecentConstants();
    }

    @Override
    public void initViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recentContactsRv.setLayoutManager(layoutManager);
    }

    @Override
    public void initEvents() {
        recentContactsAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener<TranslatorSelectedBean>() {
            @Override
            public void onItemClick(View view, int viewType, TranslatorSelectedBean data, int position) {
                ArrayList<NewTransOrderBean> list = (ArrayList) getPresenter().getNewTransOrderBeanList(position);
                ArrayList<Boolean> onLineLists = (ArrayList<Boolean>) getPresenter().getIsOnlineList();
                if (null != list && list.size() > 0) {
                    getPresenter().setTranslatorSelectedBeanList(position);
                    List<Activity> activityList = AppManager.get().getActivities();
                    if(null!=activityList&&activityList.size()>0){
                        for(Activity activity : activityList){
                            if(activity instanceof ChatActivity){
                                ChatActivity chatActivity = (ChatActivity) activity;
                                chatActivity.finish();
                            }
                        }
                    }
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("NEW_TRANS_ORDER_LIST", list);
                    intent.putExtra("IS_ONLINE_LISTS", onLineLists);
                    intent.putExtra("COME_FROM", "RecentContactsFragment");
                    intent.putExtra("TRANSLATOR_BEAN",data);
                    intent.putExtra("POSITION", position);
                    getActivity().startActivity(intent);
                }
            }
        });
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what) {
            case COMMON_CHAT:
                ArrayMap<String,Integer> arrayMap = getPresenter().getTranslatorSelectedBeanMap();
                ChatMessageBean chatMessageBean = (ChatMessageBean) message.obj;
                if(null!=arrayMap&&arrayMap.size()>0&&null!=chatMessageBean){
                    boolean isContain = arrayMap.containsKey(chatMessageBean.getOrderId());
                    if(isContain){
                        int position = arrayMap.get(chatMessageBean.getOrderId());
                        TranslatorSelectedBean translatorSelectedBean = selectedBeanList.get(position);
                        Integer number = translatorSelectedBean.getUnTransMsg();
                        boolean isPrivateMsg = chatMessageBean.getIsPrivateMessage();
                        boolean isTrans = chatMessageBean.getMsgIsTrans();
                        if(!isPrivateMsg){
                            switch (chatMessageBean.getType()){
                                case ChatConstants.COMMON_VIDEO:
                                case ChatConstants.COMMON_PRODUCT_CHAT:
                                    translatorSelectedBean.setTranslatorMsg(chatMessageBean.getContent());
                                    break;
                                default:
                                    if(isTrans){
                                        if(null!=number){
                                            translatorSelectedBean.setUnTransMsg(number-1);
                                        }
                                        translatorSelectedBean.setTranslatorMsg(chatMessageBean.getTranslateContent());
                                    }else{
                                        if(null!=number){
                                            number++;
                                        }else{
                                            number = new Integer(1);
                                        }
                                        translatorSelectedBean.setUnTransMsg(number);
                                        translatorSelectedBean.setTranslatorMsg(chatMessageBean.getContent());
                                    }
                                    break;
                            }
                        }else{
                            translatorSelectedBean.setTranslatorMsg(chatMessageBean.getContent());
                        }
                        String msgTime = chatMessageBean.getMsgTime();
                        translatorSelectedBean.setNotifyTime(null==msgTime?System.currentTimeMillis():Long.parseLong(msgTime));
                        recentContactsAdapter.notificationList(selectedBeanList);
                        List<String> payloads = new ArrayList<>();
                        payloads.add("changed");
                        recentContactsAdapter.notifyItemChanged(position, payloads);
                        getPresenter().getUnTranslationMsgNum();
                    }else{
                        getPresenter().queryRecentConstants();
                    }
                }
                break;
            case TRANSLATOR_SELECTED:
                noRecentContacts.setVisibility(View.GONE);
                recentContactsRv.setVisibility(View.VISIBLE);
                getPresenter().queryRecentConstants();
                break;
            case START_TRANSLATION:
                noRecentContacts.setVisibility(View.GONE);
                recentContactsRv.setVisibility(View.VISIBLE);
                getPresenter().queryRecentConstants();
                TranslatorSelectedBean selectedBean = (TranslatorSelectedBean) message.obj;
                getPresenter().getNewTransOrderBeanList(selectedBean);
                break;
            case QUERY_RECENT_CONSTANTS:
                TranslatorList translatorList = (TranslatorList) message.obj;
                List<TranslatorSelectedBean> translateList = translatorList.getTranslatorSelectedBeen();
                if (null != translateList && translateList.size() > 0) {
                    selectedBeanList.clear();
                    selectedBeanList.addAll(translateList);
                }
                if (!selectedBeanList.isEmpty()) {
                    noRecentContacts.setVisibility(View.GONE);
                    recentContactsRv.setVisibility(View.VISIBLE);
                }
                recentContactsAdapter.notifyDataSetChanged();
                getPresenter().getUnTranslationMsgNum();
                break;
            case GET_NEW_TRANS_ORDER_BEAN_LIST:
                ArrayList<NewTransOrderBean> list = (ArrayList<NewTransOrderBean>) getPresenter().getNewTransLists();
                TranslatorSelectedBean translatorSelectedBean = (TranslatorSelectedBean) message.obj;
                if (null != list && list.size() > 0) {
                    ArrayList<Boolean> onLineLists = (ArrayList<Boolean>) getPresenter().getIsOnlineList();
                    Intent intent = new Intent(AppManager.get().getApplication(), ChatActivity.class);
                    intent.putExtra("COME_FROM", "OrderFragment");
                    intent.putExtra("NEW_TRANS_ORDER_LIST", list);
                    intent.putExtra("IS_ONLINE_LISTS", onLineLists);
                    intent.putExtra("TRANSLATOR_BEAN",translatorSelectedBean);
                    intent.putExtra("POSITION", 0);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    AppManager.get().getApplication().startActivity(intent);
                }
                getPresenter().resetTranslatorSelectedBeanList();
                break;
            case GET_UN_TRANS_MSG_NUM:
                int unTranslationMsgNum = message.arg1;
                List<Activity> activityList = AppManager.get().getActivities();
                if(null!=activityList&&activityList.size()>0){
                    for(Activity activity : activityList){
                        if(activity instanceof MainActivity){
                            MainActivity mainActivity = (MainActivity) activity;
                            mainActivity.showUnTransMsgRedPoint(unTranslationMsgNum);
                        }
                    }
                }
                break;
        }
    }
}
