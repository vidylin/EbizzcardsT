package com.gzligo.ebizzcardstranslator.base;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.delegate.IActivity;
import com.gzligo.ebizzcardstranslator.base.delegate.IActivityDelegate;
import com.gzligo.ebizzcardstranslator.base.mvp.IPresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.common.autolayout.AutoAppBarLayout;
import com.gzligo.ebizzcardstranslator.common.autolayout.AutoCardView;
import com.gzligo.ebizzcardstranslator.common.autolayout.AutoCollapsingToolbarLayout;
import com.gzligo.ebizzcardstranslator.common.autolayout.AutoRadioGroup;
import com.gzligo.ebizzcardstranslator.common.autolayout.AutoScrollView;
import com.gzligo.ebizzcardstranslator.common.autolayout.AutoTabLayout;
import com.gzligo.ebizzcardstranslator.common.autolayout.AutoToolbar;
import com.gzligo.ebizzcardstranslator.common.swipeback.SwipeBackHelper;
import com.zhy.autolayout.AutoFrameLayout;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xfast on 2017/5/22.
 */

public abstract class BaseActivity<P extends IPresenter> extends AppCompatActivity implements IActivity<P>, SwipeBackHelper.SlideBackManager, IView {

    private IActivityDelegate mDelegate;
    private P mPresenter;

    private SwipeBackHelper mSwipeBackHelper;
    private Map<Integer, Runnable> allowablePermission = new HashMap<>();

    // adapter for AutolayoutActivity
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        if (FrameLayout.class.getSimpleName().equals(name)) {
            return new AutoFrameLayout(context, attrs);
        } else if (LinearLayout.class.getSimpleName().equals(name)) {
            return new AutoLinearLayout(context, attrs);
        } else if (RelativeLayout.class.getSimpleName().equals(name)) {
            return new AutoRelativeLayout(context, attrs);
        } else if (CardView.class.getSimpleName().equals(name)) {
            return new AutoCardView(context, attrs);
        } else if (Toolbar.class.getSimpleName().equals(name)) {
            return new AutoToolbar(context, attrs);
        } else if (ScrollView.class.getSimpleName().equals(name)) {
            return new AutoScrollView(context, attrs);
        } else if (RadioGroup.class.getSimpleName().equals(name)) {
            return new AutoRadioGroup(context, attrs);
        } else if (TabLayout.class.getSimpleName().equals(name)) {
            return new AutoTabLayout(context, attrs);
        } else if (AppBarLayout.class.getSimpleName().equals(name)) {
            return new AutoAppBarLayout(context, attrs);
        } else if (CollapsingToolbarLayout.class.getSimpleName().equals(name)) {
            return new AutoCollapsingToolbarLayout(context, attrs);
        }
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public P getPresenter() {
        return mPresenter;
    }

    @Override
    public void setPresenter(P presenter) {
        mPresenter = presenter;
    }

    @Override
    public IActivityDelegate getIDelegate() {
        return mDelegate;
    }

    @Override
    public void setIDelegate(IActivityDelegate delegate) {
        mDelegate = delegate;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getCurrentFocus() != null) {
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mSwipeBackHelper == null) {
            mSwipeBackHelper = new SwipeBackHelper(this);
        }
        return mSwipeBackHelper.dispatchTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }

    @Override
    public Activity getSlideActivity() {
        return this;
    }

    @Override
    public boolean supportSlideBack() {
        return true;
    }

    @Override
    public boolean canBeSlideBack() {
        return true;
    }

    @Override
    public void finish() {
        if (mSwipeBackHelper != null) {
            mSwipeBackHelper.finishSwipeImmediately();
            mSwipeBackHelper = null;
        }
        super.finish();
        if (mDelegate != null) {
            mDelegate.onFinish();
        }
    }

    @Override
    public boolean isSupportSwipeBack() {
        return true;
    }

    @Override
    public void handlePresenterCallback(Message message) {
        // Override this method for MVP's P->V interaction
    }

    protected void requestPermission(int id, String[] permissions, Runnable allowableRunnable) {
        if (allowableRunnable == null) {
            throw new IllegalArgumentException("allowableRunnable == null");
        }
        allowablePermission.put(id, allowableRunnable);

        if (Build.VERSION.SDK_INT >= 23) {
            List<String> mPermissionList = new ArrayList<>();
            for(int i=0;i<permissions.length;i++){
                if (ContextCompat.checkSelfPermission(AppManager.get().getApplication(), permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }
            if(!mPermissionList.isEmpty()){
                String[] permissionStr = mPermissionList.toArray(new String[mPermissionList.size()]);
                ActivityCompat.requestPermissions(this, permissionStr, 1);
                return;
            }else{
                allowableRunnable.run();
            }
        } else {
            allowableRunnable.run();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isGetPermissions = false;
        String permissionMark = "";
        for(int i=0;i<grantResults.length;i++){
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                isGetPermissions = true;
            } else {
                isGetPermissions = false;
                switch (permissions[i]){
                    case Manifest.permission.READ_PHONE_STATE:
                        permissionMark = getResources().getString(R.string.login_phone_permission);
                        break;
                    case Manifest.permission.RECORD_AUDIO:
                        permissionMark = getResources().getString(R.string.record_voice_permission);
                        break;
                    case Manifest.permission.CAMERA:
                        permissionMark = getResources().getString(R.string.record_voice_camera_permission);
                        break;
                }
                break;
            }
        }
        if(isGetPermissions){
            Runnable allowRun = allowablePermission.get(requestCode);
            allowRun.run();
        }else{
            Toast.makeText(this,permissionMark,Toast.LENGTH_SHORT).show();
        }

    }
}
