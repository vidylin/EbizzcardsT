package com.gzligo.ebizzcardstranslator.business.me;

import android.view.View;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;

import java.util.List;

/**
 * Created by ZuoJian on 2017/8/7.
 */

public class LanguageConfirmAdapter extends BaseAdapter<String>{

    public LanguageConfirmAdapter(List<String> infos) {
        super(infos);
    }

    @Override
    public BaseHolder<String> getHolder(View v, int viewType) {
        return new LanguageConfirmHolder(v);
    }

    @Override
    public int getLayoutResId(int viewType) {
        return R.layout.language_confirm_item;
    }
}
