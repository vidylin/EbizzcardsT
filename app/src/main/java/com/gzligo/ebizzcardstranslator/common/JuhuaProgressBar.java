package com.gzligo.ebizzcardstranslator.common;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.scwang.smartrefresh.layout.internal.ProgressDrawable;

/**
 * Created by Lwd on 2017/11/2.
 */

public class JuhuaProgressBar extends ProgressBar {
    public JuhuaProgressBar(Context context) {
        super(context);
        init();
    }

    public JuhuaProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setIndeterminateDrawable(new ProgressDrawable());
    }

}
