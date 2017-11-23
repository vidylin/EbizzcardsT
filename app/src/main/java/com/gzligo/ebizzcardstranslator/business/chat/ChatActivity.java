package com.gzligo.ebizzcardstranslator.business.chat;


import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.common.ToolActionBar;
import com.gzligo.ebizzcardstranslator.manager.CommonBeanManager;
import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.NewTransOrderBean;
import com.gzligo.ebizzcardstranslator.persistence.TranslatorSelectedBean;
import com.gzligo.ebizzcardstranslator.persistence.UnTransMagNumberBean;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.OnClick;

public class ChatActivity extends BaseActivity<ChatPresenter> {

    @BindView(R.id.login_actionbar) ToolActionBar loginActionbar;
    @BindView(R.id.trans_groups) RecyclerView transGroups;
    @BindView(R.id.chat_group_ll) RelativeLayout chatGroupLl;
    @BindView(R.id.fragment_content) FrameLayout fragmentContent;
    private FragmentManager fragmentManager;
    private ChatFragment[] fragments;
    private String comeFrom;
    private TreeMap<Integer, NewTransOrderBean> treeMap;
    private ChatActivityAdapter chatActivityAdapter;
    private List<NewTransOrderBean> newTransOrderBean;
    private TreeMap<Integer, Integer> integerTreeMap;
    private TreeMap<Integer, LanguagesBean> languagesMap;
    private TreeMap<String, ChatFragment> userChatFragmentMap;
    private List<Boolean> onLineLists;
    private int choiceItem;
    private static final int REFRESH_ONLINE_STATUS = 0x97;
    private static final int REFRESH_POINT = 0x99;
    private static final int QUERY_UN_TRANSLATE_MSG_NUM = 0x98;
    private static final int QUERY_TYPE = 0x96;
    public static final int FRAGMENT_ONE = 0;

    @Override
    public ChatPresenter createPresenter() {
        return new ChatPresenter(new ChatRepository(), ChatActivity.this);
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_chat;
    }

    @Override
    public void initViews() {
        if (treeMap != null) {
            NewTransOrderBean transOrderBean = treeMap.get(0);
            int fromLangId = (null != transOrderBean.getFromLangId()) ? transOrderBean.getFromLangId() : 1;
            int toLangId = (null != transOrderBean.getToLangId()) ? transOrderBean.getToLangId() : 2;
            Drawable drawable = getResources().getDrawable(R.mipmap.chat_language_arrow);
            drawable.setBounds(0, 4, 20, 10);
            loginActionbar.getRight4Txt().setCompoundDrawables(null, null, drawable, null);
            loginActionbar.setRight4Txt(getLangStr(fromLangId));
            loginActionbar.setRight3Txt(getLangStr(toLangId));
            loginActionbar.setTitleTxt(transOrderBean.getFromName() + "、" + transOrderBean.getToName());
            for (int i = 0; i < treeMap.size(); i++) {
                NewTransOrderBean orderBean = treeMap.get(i);
                newTransOrderBean.add(orderBean);
            }
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        transGroups.setLayoutManager(layoutManager);
        getPresenter().queryUnTranslateMsgNum(treeMap, QUERY_UN_TRANSLATE_MSG_NUM);
    }

    @Override
    public void initData() {
        languagesMap = getPresenter().getLanguageMap();
        newTransOrderBean = new ArrayList<>();
        onLineLists = (List<Boolean>) getIntent().getExtras().getSerializable("IS_ONLINE_LISTS");
        List<NewTransOrderBean> newTransOrderBean = (List<NewTransOrderBean>) getActivity().getIntent().getSerializableExtra("NEW_TRANS_ORDER_LIST");
        choiceItem = 0;
        comeFrom = getActivity().getIntent().getExtras().getString("COME_FROM");
        int position = getActivity().getIntent().getExtras().getInt("POSITION");
        treeMap = getPresenter().setMapIndex(newTransOrderBean, position);
        integerTreeMap = new TreeMap<>();
        userChatFragmentMap = new TreeMap<>();
        CommonBeanManager.getInstance().setChatting(true);
        CommonBeanManager.getInstance().readMsg();
    }

    @Override
    public void initEvents() {
        int size = newTransOrderBean.size();
        fragments = new ChatFragment[size];
        init();
    }

    @OnClick({R.id.tv_close})
    void clickView(View view) {
        switch (view.getId()) {
            case R.id.tv_close:
                ChatFragment chatF = fragments[choiceItem];
                if (null != chatF) {
                    chatF.onKeyDown();
                }
                finish();
                break;
        }
    }

    private void init() {
        fragmentManager = getSupportFragmentManager();
        NewTransOrderBean orderBean = treeMap.get(FRAGMENT_ONE);
        showFragment(FRAGMENT_ONE, orderBean);
    }

    private void showFragment(int index, NewTransOrderBean orderBean) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        hideFragment(ft);
        ChatFragment chatFragment = fragments[index];

        if (chatFragment == null) {
            chatFragment = new ChatFragment();
            fragments[index] = chatFragment;
            ft.add(R.id.fragment_content, chatFragment);
            Bundle data = new Bundle();
            data.putString("FRAGMENT_INDEX", String.valueOf(index));
            data.putSerializable("CHAT_USER", treeMap.get(index));
            data.putString("COME_FROM", comeFrom);
            chatFragment.setArguments(data);
        } else {
            chatFragment.initAudioManager();
            ft.show(chatFragment);
        }
        ft.commit();
        String userIdOne = orderBean.getFromUserId() + orderBean.getToUserId();
        String userIdTwo = orderBean.getToUserId() + orderBean.getFromUserId();
        if (!userChatFragmentMap.containsKey(userIdOne) && !userChatFragmentMap.containsKey(userIdTwo)) {
            userChatFragmentMap.put(userIdOne, chatFragment);
            getPresenter().setChatFragment(userChatFragmentMap);
        }
    }

    private void hideFragment(FragmentTransaction ft) {
        int num = fragments.length;
        for (int i = 0; i < num; i++) {
            ChatFragment chatFragment = fragments[i];
            if (chatFragment != null) {
                ft.hide(chatFragment);
            }
        }
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what) {
            case REFRESH_POINT:
                UnTransMagNumberBean magNumberBean = (UnTransMagNumberBean) message.obj;
                showPoint(magNumberBean.getIndex(), magNumberBean.getNumber());
                break;
            case QUERY_UN_TRANSLATE_MSG_NUM:
                integerTreeMap = getPresenter().getUnTranslateMsgMap();
                if (null == chatActivityAdapter) {
                    chatActivityAdapter = new ChatActivityAdapter(newTransOrderBean, integerTreeMap, onLineLists, choiceItem);
                    transGroups.setAdapter(chatActivityAdapter);
                    chatActivityAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener<NewTransOrderBean>() {
                        @Override
                        public void onItemClick(View view, int viewType, NewTransOrderBean data, int position) {
                            if (choiceItem != position) {
                                ChatFragment chatF = fragments[choiceItem];
                                if (null != chatF) {
                                    chatF.onKeyDown();
                                }
                                loginActionbar.setTitleTxt(data.getFromName() + "、" + data.getToName());
                                int fromLangId = data.getFromLangId();
                                int toLangId = data.getToLangId();
                                loginActionbar.setRight4Txt(getLangStr(fromLangId));
                                loginActionbar.setRight3Txt(getLangStr(toLangId));
                                showFragment(position, data);
                                List<String> payloads = new ArrayList<>();
                                payloads.add("choice");
                                chatActivityAdapter.notifyItemChanged(position, payloads);
                                List<String> pList = new ArrayList<>();
                                pList.add("unChoice");
                                chatActivityAdapter.notifyItemChanged(choiceItem, pList);
                                choiceItem = position;
                            }
                        }
                    });
                } else {
                    for (int i = 0; i < integerTreeMap.size(); i++) {
                        showPoint(i, integerTreeMap.get(i));
                    }
                }
                break;
            case REFRESH_ONLINE_STATUS:
                Object obj = message.obj;
                if (null != obj) {
                    int position = (int) message.obj;
                    onLineLists.set(position, false);
                    chatActivityAdapter.notifyItemChanged(position);
                }
                break;
            case QUERY_TYPE:
                chatActivityAdapter.setChoiceWhich(choiceItem);
                chatActivityAdapter.notifyDataSetChanged();
                integerTreeMap = getPresenter().getUnTranslateMsgMap();
                for (int i = 0; i < integerTreeMap.size(); i++) {
                    showPoint(i, integerTreeMap.get(i));
                }
                break;
        }
    }

    private void showPoint(int index, int count) {
        List<String> payloads = new ArrayList<>();
        payloads.add(count + "");
        chatActivityAdapter.notifyItemChanged(index, payloads);
    }

    private String getLangStr(int langId) {
        if (null!=languagesMap&&languagesMap.containsKey(langId)) {
            return languagesMap.get(langId).getName();
        }
        return "";
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        List<NewTransOrderBean> orderLists = (List<NewTransOrderBean>) intent.getSerializableExtra("NEW_TRANS_ORDER_LIST");
        List<Boolean> booleanLists = (List<Boolean>) intent.getExtras().getSerializable("IS_ONLINE_LISTS");
        TranslatorSelectedBean translatorSelectedBean = (TranslatorSelectedBean) intent.getExtras().getSerializable("TRANSLATOR_BEAN");
        onLineLists.clear();
        onLineLists.addAll(booleanLists);
        if(null!=chatActivityAdapter){
            chatActivityAdapter.updateOnLineLists(onLineLists);
        }
        if (orderLists.size() != newTransOrderBean.size()) {
            ChatFragment[] chatFragments = new ChatFragment[fragments.length + 1];
            for (int i = 0; i < fragments.length; i++) {
                chatFragments[i + 1] = fragments[i];
            }
            int len = chatFragments.length;
            fragments = new ChatFragment[len];
            for (int i = 0; i < len; i++) {
                fragments[i] = chatFragments[i];
            }
            newTransOrderBean.add(0, orderLists.get(0));
            choiceItem++;
            treeMap = getPresenter().updateMapIndex(newTransOrderBean, choiceItem);
            getPresenter().queryUnTranslateMsgNum(treeMap, QUERY_TYPE);
            showFragment(choiceItem, newTransOrderBean.get(choiceItem));
        } else {
            if (null == chatActivityAdapter) {
                chatActivityAdapter = new ChatActivityAdapter(newTransOrderBean, integerTreeMap, onLineLists, choiceItem);
            } else {
                chatActivityAdapter.notifyDataSetChanged();
            }
        }
        getPresenter().startTranslate(translatorSelectedBean.getFromUserId(), translatorSelectedBean.getToUserId());
    }

    public void requestPermission(final ChatFragment chatFragment) {
        String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA};
        requestPermission(1, permissions, new Runnable() {
            @Override
            public void run() {
                chatFragment.allowPermission();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            ChatFragment chatF = fragments[choiceItem];
            if (null != chatF) {
                chatF.onKeyDown();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean isOnline(ChatMessageBean chatMsg){
        return getPresenter().isOnline(chatMsg,onLineLists);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonBeanManager.getInstance().setChatting(false);
    }
}
