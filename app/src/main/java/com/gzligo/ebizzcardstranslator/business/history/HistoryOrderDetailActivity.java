package com.gzligo.ebizzcardstranslator.business.history;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.business.chat.ChatAdapter;
import com.gzligo.ebizzcardstranslator.business.chat.PreImageActivity;
import com.gzligo.ebizzcardstranslator.business.chat.product.ProductDetailActivity;
import com.gzligo.ebizzcardstranslator.common.RecycleViewItemActiveCalculator;
import com.gzligo.ebizzcardstranslator.common.ToolActionBar;
import com.gzligo.ebizzcardstranslator.constants.ChatConstants;
import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.ChatMsgProperty;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.NewTransOrderBean;
import com.gzligo.ebizzcardstranslator.persistence.ProductDetail;
import com.gzligo.ebizzcardstranslator.persistence.TranslatorOrderList;
import com.waynell.videolist.visibility.calculator.SingleListViewItemActiveCalculator;
import com.waynell.videolist.visibility.scroll.RecyclerViewItemPositionGetter;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.OnClick;

public class HistoryOrderDetailActivity extends BaseActivity<HistoryOrderPresenter> {
    private static final int GET_CHAT_HISTORY_MSG = 0x50;
    private static final int GET_ALL_CHAT_MSG = 0x51;
    private static final int GET_PRODUCT_CHAT_MSG = 0x52;
    private static final int PRE_IMAGE = 0x46;
    private static final int PRE_VIDEO = 0x47;
    private NewTransOrderBean newTransOrderBean;
    @BindView(R.id.login_actionbar) ToolActionBar loginActionbar;
    private ChatAdapter chatAdapter;
    @BindView(R.id.chat_recycler_view) RecyclerView chatRecyclerView;
    private List<ChatMessageBean> messageBeanList;
    private SingleListViewItemActiveCalculator mCalculator;
    private RecycleViewItemActiveCalculator recycleViewItemActiveCalculator;
    private int mScrollState;

    @Override
    public HistoryOrderPresenter createPresenter() {
        return new HistoryOrderPresenter(new HistoryOrderRepository(), this);
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_history_order_detail;
    }

    @Override
    public void initData() {
        TranslatorOrderList translatorOrderList = (TranslatorOrderList) getIntent().getSerializableExtra("TRANSLATE_HISTORY_DATA");
        String fromId = translatorOrderList.getUser1().getUser_id();
        String toId = translatorOrderList.getUser2().getUser_id();
        String orderId = translatorOrderList.getOrder_no() + "";
        int fromLangId = translatorOrderList.getUser1().getLang_id();
        int toLangId = translatorOrderList.getUser2().getLang_id();
        newTransOrderBean = new NewTransOrderBean();
        newTransOrderBean.setToUserId(toId);
        newTransOrderBean.setFromUserId(fromId);
        newTransOrderBean.setToPortraitId(translatorOrderList.getUser2().getPortrait_id());
        newTransOrderBean.setFromPortraitId(translatorOrderList.getUser1().getPortrait_id());
        getPresenter().getChatHisToryMsg(Message.obtain(this, new String[]{fromId, toId, orderId}));
        loginActionbar.setTitleTxt(translatorOrderList.getUser1().getName() + "、" + translatorOrderList.getUser2().getName());
        TreeMap<Integer, LanguagesBean> languageTreeMap = getPresenter().getTreeMap();
        Drawable drawable = getResources().getDrawable(R.mipmap.chat_language_arrow);
        drawable.setBounds(0, 4, 20, 10);
        loginActionbar.getRight4Txt().setCompoundDrawables(null, null, drawable, null);
        loginActionbar.setRight4Txt(languageTreeMap.get(fromLangId).getName());
        loginActionbar.setRight3Txt(languageTreeMap.get(toLangId).getName());
    }

    @OnClick({R.id.tv_close})
    void clickView(View view) {
        switch (view.getId()) {
            case R.id.tv_close:
                finish();
                break;
        }
    }

    @Override
    public void initViews() {
        if (null == messageBeanList) {
            messageBeanList = new ArrayList<>();
            chatAdapter = new ChatAdapter(messageBeanList, HistoryOrderDetailActivity.this, chatRecyclerView, newTransOrderBean, ChatConstants.COME_FROM_HISTORY);
            chatRecyclerView.setAdapter(chatAdapter);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        chatRecyclerView.setLayoutManager(layoutManager);
        recycleViewItemActiveCalculator = new RecycleViewItemActiveCalculator(chatRecyclerView,chatAdapter);
        mCalculator = new SingleListViewItemActiveCalculator(chatAdapter, new RecyclerViewItemPositionGetter(layoutManager, chatRecyclerView));
        chatRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                mScrollState = newState;
                if (newState == RecyclerView.SCROLL_STATE_IDLE && chatAdapter.getItemCount() > 0) {
                    mCalculator.onScrollStateIdle();
                }
                recycleViewItemActiveCalculator.calculator(messageBeanList);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mCalculator.onScrolled(mScrollState);
            }
        });
    }

    @Override
    public void initEvents() {
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what) {
            case GET_CHAT_HISTORY_MSG:
                List<ChatMessageBean> list = (List<ChatMessageBean>) message.obj;
                if (null != list && list.size() > 0) {
                    messageBeanList.clear();
                    messageBeanList.addAll(list);
                    chatAdapter.notifyDataSetChanged();
                }
                break;
            case PRE_VIDEO:
            case PRE_IMAGE:
                ChatMessageBean chatMessageBean = messageBeanList.get(message.arg1);
                if(chatMessageBean.getType()==6){
                    getPresenter().getProductMsg(chatMessageBean.getFromId(), chatMessageBean.getToId(), chatMessageBean.getMsgId(),chatMessageBean.getOrderId());
                }else{
                    getPresenter().getAllMsgByClientId(chatMessageBean.getFromId(), chatMessageBean.getToId(), chatMessageBean.getMsgId(), chatMessageBean.getOrderId());
                }
                break;
            case GET_ALL_CHAT_MSG:
                int pos = message.arg1;
                ArrayList<ChatMsgProperty> chatMsgProperties = (ArrayList<ChatMsgProperty>) getPresenter().getChatMsgProperties();
                Intent intent = new Intent(getActivity(), PreImageActivity.class);
                intent.putExtra("CHAT_MSG_LIST", chatMsgProperties);
                intent.putExtra("POSITION",pos);
                getActivity().startActivity(intent);
                break;
            case GET_PRODUCT_CHAT_MSG:
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
    public void onResume() {
        super.onResume();
        if (null != chatAdapter) {
            chatAdapter.notifyDataSetChanged();
        }
    }
}
