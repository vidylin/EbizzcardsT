package com.gzligo.ebizzcardstranslator.base.lifecycle;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.gzligo.ebizzcardstranslator.base.delegate.FragmentDelegateImpl;
import com.gzligo.ebizzcardstranslator.base.delegate.IFragment;
import com.gzligo.ebizzcardstranslator.base.delegate.IFragmentDelegate;

/**
 * Created by xfast on 2017/5/26.
 */

public class FragmentLifecycleCallbacksImpl extends FragmentManager.FragmentLifecycleCallbacks {
    @Override
    public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
        super.onFragmentAttached(fm, f, context);

        initFragmentDelegate(f);

        IFragmentDelegate delegate = getFragmentDelegate(f);
        if (delegate != null) {
            delegate.onAttach(context);
        }
    }

    @Override
    public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
        super.onFragmentCreated(fm, f, savedInstanceState);
        IFragmentDelegate fragmentDelegate = getFragmentDelegate(f);
        if (fragmentDelegate != null) {
            fragmentDelegate.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
        super.onFragmentViewCreated(fm, f, v, savedInstanceState);
        IFragmentDelegate fragmentDelegate = getFragmentDelegate(f);
        if (fragmentDelegate != null) {
            fragmentDelegate.onCreateView(v, savedInstanceState);
        }
    }

    @Override
    public void onFragmentActivityCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
        super.onFragmentActivityCreated(fm, f, savedInstanceState);
        IFragmentDelegate fragmentDelegate = getFragmentDelegate(f);
        if (fragmentDelegate != null) {
            fragmentDelegate.onActivityCreate(savedInstanceState);
        }
    }

    @Override
    public void onFragmentStarted(FragmentManager fm, Fragment f) {
        super.onFragmentStarted(fm, f);
        IFragmentDelegate fragmentDelegate = getFragmentDelegate(f);
        if (fragmentDelegate != null) {
            fragmentDelegate.onStart();
        }
    }

    @Override
    public void onFragmentResumed(FragmentManager fm, Fragment f) {
        super.onFragmentResumed(fm, f);
        IFragmentDelegate fragmentDelegate = getFragmentDelegate(f);
        if (fragmentDelegate != null) {
            fragmentDelegate.onResume();
        }
    }

    @Override
    public void onFragmentPaused(FragmentManager fm, Fragment f) {
        super.onFragmentPaused(fm, f);
        IFragmentDelegate fragmentDelegate = getFragmentDelegate(f);
        if (fragmentDelegate != null) {
            fragmentDelegate.onPause();
        }
    }

    @Override
    public void onFragmentStopped(FragmentManager fm, Fragment f) {
        super.onFragmentStopped(fm, f);
        IFragmentDelegate fragmentDelegate = getFragmentDelegate(f);
        if (fragmentDelegate != null) {
            fragmentDelegate.onStop();
        }
    }

    @Override
    public void onFragmentSaveInstanceState(FragmentManager fm, Fragment f, Bundle outState) {
        super.onFragmentSaveInstanceState(fm, f, outState);
        IFragmentDelegate fragmentDelegate = getFragmentDelegate(f);
        if (fragmentDelegate != null) {
            fragmentDelegate.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
        super.onFragmentViewDestroyed(fm, f);
        IFragmentDelegate fragmentDelegate = getFragmentDelegate(f);
        if (fragmentDelegate != null) {
            fragmentDelegate.onDestroyView();
        }
    }

    @Override
    public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
        super.onFragmentDestroyed(fm, f);
        IFragmentDelegate fragmentDelegate = getFragmentDelegate(f);
        if (fragmentDelegate != null) {
            fragmentDelegate.onDestroy();
        }
    }

    @Override
    public void onFragmentDetached(FragmentManager fm, Fragment f) {
        super.onFragmentDetached(fm, f);
        IFragmentDelegate fragmentDelegate = getFragmentDelegate(f);
        if (fragmentDelegate != null) {
            fragmentDelegate.onDetach();
            f.getArguments().clear();
        }
    }

    private void initFragmentDelegate(Fragment fragment) {
        if (fragment instanceof IFragment) {
            IFragmentDelegate delegate = getFragmentDelegate(fragment);
            if (delegate == null || !delegate.isAdded()) {
                delegate = new FragmentDelegateImpl((IFragment) fragment);
                ((IFragment) fragment).setIDelegate(delegate);
            }
        }
    }

    private IFragmentDelegate getFragmentDelegate(Fragment fragment) {
        IFragmentDelegate delegate = null;
        if (fragment instanceof IFragment) {
            delegate = ((IFragment) fragment).getIDelegate();
        }
        return delegate;
    }
}
