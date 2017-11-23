package com.github.xgfjyw.webrtcclient;

import android.content.Context;
import android.view.View;

/**
 * Created by hongxiliang2 on 9/9/2017.
 */

public interface RTCVideoViewCallback {
    Context getParentContext();
    void videoDidStart(View view);
    void videoDidStop(View view);
}
