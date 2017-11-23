package com.gzligo.ebizzcardstranslator.business;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.business.history.TranslationRecordFragment;
import com.gzligo.ebizzcardstranslator.business.me.MeFragment;
import com.gzligo.ebizzcardstranslator.business.me.MeMsgActivity;
import com.gzligo.ebizzcardstranslator.business.me.PersonalBaseMessageActivity;
import com.gzligo.ebizzcardstranslator.business.message.RecentContactsFragment;
import com.gzligo.ebizzcardstranslator.business.order.GrabOrderFragment;
import com.gzligo.ebizzcardstranslator.common.ToolActionBar;
import com.gzligo.ebizzcardstranslator.common.indicator.GradualTabsIndicator;
import com.gzligo.ebizzcardstranslator.common.viewpager.FragmentPagerAdapter;
import com.gzligo.ebizzcardstranslator.common.viewpager.ViewPager;
import com.gzligo.ebizzcardstranslator.mqtt.MqttManager;
import com.gzligo.ebizzcardstranslator.mqtt.MqttSystemManager;
import com.gzligo.ebizzcardstranslator.persistence.UserBean;
import com.gzligo.ebizzcardstranslator.push.PushClientHelper;
import com.gzligo.ebizzcardstranslator.utils.PopupWindowUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by xfast on 2017/5/27.
 */

public class MainActivity extends BaseActivity<MainPresenter> implements MainCallBack {
    public static final int IS_AUTHENTICATED = 0x87;
    @BindView(R.id.main_viewpager)
    ViewPager mViewPager;
    @BindView(R.id.main_indicator)
    GradualTabsIndicator mIndicator;
    @BindView(R.id.main_actionbar)
    ToolActionBar mToolbar;
    @BindView(R.id.main_personal_msg_unconfirmed_rl)
    RelativeLayout mPersonalMsgUnconfirmedRl;
    private MeFragment meFragment;
    private TranslationRecordFragment historyOrderFragment;
    private GrabOrderFragment grabOrderFragment;
    private FakeMainAdapter mFakeMainAdapter;

    public static final int POPUP_MSG = 0x89;
    public static final int POPUP_DISMISS = 0x86;
    public static final int UPDATE_USER_STATE = 0x88;
    public static final int UN_GRAB_ORDER_NUMBER = 0x98;

    public static final int INDICATOR_0_MESSAGE = 0;//最近联系人
    public static final int INDICATOR_1_ORDER = 1;  //抢单
    public static final int INDICATOR_2_HISTORY = 2;//历史
    public static final int INDICATOR_3_ME = 3;      //我的

    @Override
    public MainPresenter createPresenter() {
        return new MainPresenter(new MainRepository());
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
//        mIndicator.getTabView(0).showNumber(666);
//        mIndicator.getTabView(3).showPoint();
        MqttSystemManager.get().registerMainCallBack(this);
        getPresenter().getUserBeanInfo(Message.obtain(this),true);
        getPresenter().userStatusUpdate(Message.obtain(MainActivity.this, new String[]{PopupWindowUtil.ON_LINE_STATE+""}));
        getPresenter().getLanguageList();
    }

    @Override
    public void initViews() {
        mFakeMainAdapter = new FakeMainAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mFakeMainAdapter);
        mViewPager.setOffscreenPageLimit(mFakeMainAdapter.getCount());
        mIndicator.setViewPager(mViewPager);
        getPresenter().getUnGrabTransOrderNumber(this);
    }

    @Override
    public void initEvents() {
        mViewPager.addOnPageChangeListener(mFakeMainAdapter);
        mPersonalMsgUnconfirmedRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PersonalBaseMessageActivity.class);
                intent.putExtra(TranslatorConstants.Common.FROM,"main");
                startActivity(intent);
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PushClientHelper.getInstance(getApplication()).registerPushService(new PushClientHelper.OnRegisterPushListener() {
                    @Override
                    public void registerPushFinish(String pushToken) {
                        MqttManager.get().uploadPublishInfo();
                    }
                });
            }
        }, 2000);
    }

    @Override
    public boolean isSupportSwipeBack() {
        return false;
    }

    @Override
    public boolean supportSlideBack() {
        return false;
    }

    @Override
    public void showUnTransMsgRedPoint(int unTransMsgNumber){
        if(unTransMsgNumber>0){
            mIndicator.getTabView(0).showNumber(unTransMsgNumber);
        }else{
            mIndicator.getTabView(0).removeShow();
        }
    }

    @Override
    public void showOrderNumberRedPoint() {
        getPresenter().getUnGrabTransOrderNumber(this);
    }

    private class FakeMainAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

        private List<Fragment> fragments = new ArrayList<>();

        public FakeMainAdapter(FragmentManager fm) {
            super(fm);
            if(null==meFragment){
                meFragment = new MeFragment();
            }
            if(null==historyOrderFragment){
                historyOrderFragment = new TranslationRecordFragment();
            }
            if(null==grabOrderFragment){
                grabOrderFragment = new GrabOrderFragment();
            }
            fragments.add(new RecentContactsFragment());
            fragments.add(grabOrderFragment);
            fragments.add(historyOrderFragment);
            fragments.add(meFragment);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            fakeBadge(position);
            handleToolbar(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private void handleToolbar(int position) {
        mToolbar.reset();
        switch (position) {
            case INDICATOR_0_MESSAGE:
                mToolbar.setTitleTxt(R.string.recent_contacts_actionbar);
                mToolbar.setRight2Txt(R.string.recent_contacts_free);
                mToolbar.getRight2Txt().setTextSize(14);
                Drawable drawable = getResources().getDrawable(R.drawable.arrow_selector);
                drawable.setBounds(0, 0, 50,50);
                mToolbar.getRight2Txt().setCompoundDrawables(null,null,drawable,null);
                mToolbar.setOnRight2ClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Drawable drawable = getResources().getDrawable(R.mipmap.state_down);
                        drawable.setBounds(0, 0, 50,50);
                        mToolbar.getRight2Txt().setCompoundDrawables(null,null,drawable,null);
                        new PopupWindowUtil(MainActivity.this, getActivity(), v);
                    }
                });
                break;
            case INDICATOR_1_ORDER:
                mToolbar.setTitleTxt(R.string.order_actionbar);
                break;
            case INDICATOR_2_HISTORY:
                mToolbar.setTitleTxt(R.string.history_order_actionbar);
                break;
            case INDICATOR_3_ME:
                mToolbar.setTitleTxt(R.string.me_actionbar);
                mToolbar.setRight1Icon(R.drawable.me_msg_selector);
                mToolbar.setOnRight1ClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, MeMsgActivity.class));
                    }
                });
                break;
        }
    }

    private void fakeBadge(int position) {
        if (INDICATOR_0_MESSAGE == position) {
            mIndicator.getTabView(INDICATOR_0_MESSAGE).showNumber(mIndicator.getTabView(INDICATOR_0_MESSAGE).getBadgeNumber());
        } else if (INDICATOR_2_HISTORY == position) {
            mIndicator.getCurrentItemView().removeShow();
        } else if (INDICATOR_3_ME == position) {
//            mIndicator.removeAllBadge();
        }
    }

    @Override
    public void handlePresenterCallback(Message message) {
        super.handlePresenterCallback(message);
        switch (message.what) {
            case POPUP_MSG:
                final String state = message.obj.toString();
                getPresenter().userStatusUpdate(Message.obtain(MainActivity.this, new String[]{state}));
                break;
            case UPDATE_USER_STATE:
                mToolbar.setRight2Txt(message.obj.toString());
                break;
            case IS_AUTHENTICATED:
                UserBean userBean = (UserBean) message.obj;
                int identityStatus = userBean.getIdentity_status();
                if(identityStatus!=0){
                    //用户认证状态 0未申请 1未审核 2已认证 3未通过认证 4审核中
                    mPersonalMsgUnconfirmedRl.setVisibility(View.GONE);
                }else{
                    mPersonalMsgUnconfirmedRl.setVisibility(View.VISIBLE);
                }
                if(null!=meFragment){
                    meFragment.initUserData(userBean);
                }
                break;
            case POPUP_DISMISS:
                Drawable drawable = getResources().getDrawable(R.drawable.arrow_selector);
                drawable.setBounds(0, 0, 50,50);
                mToolbar.getRight2Txt().setCompoundDrawables(null,null,drawable,null);
                break;
            case UN_GRAB_ORDER_NUMBER:
                int totalNum = message.arg1;
                if(totalNum>0){
                    mIndicator.getTabView(1).showNumber(totalNum);
                }else{
                    mIndicator.getTabView(1).removeShow();
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            this.moveTaskToBack(false);
        }
        return super.onKeyDown(keyCode, event);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public void requestPermission(String[] permissions) {
        requestPermission(1,permissions , new Runnable() {
            @Override
            public void run() {
                grabOrderFragment.requestPermission();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        MqttManager.get().retryLogin();
    }

    public static void startMainActivity() {
        Activity activity = AppManager.get().getTopActivity();
        if(null!=activity){
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        }
    }

    public void getUnGrabTransOrderNumber(){
        getPresenter().getUnGrabTransOrderNumber(this);
    }
}
