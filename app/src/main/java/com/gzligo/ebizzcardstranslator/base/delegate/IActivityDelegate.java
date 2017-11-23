package com.gzligo.ebizzcardstranslator.base.delegate;

import android.os.Bundle;

/**
 * Created by xfast on 2017/5/25.
 */

public interface IActivityDelegate {

    void onCreate(Bundle savedInstanceState);

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onSaveInstanceState(Bundle outState);

    void onDestroy();

    void onFinish();
}
