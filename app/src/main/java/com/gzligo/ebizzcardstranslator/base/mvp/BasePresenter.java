package com.gzligo.ebizzcardstranslator.base.mvp;

/**
 * Created by xfast on 2017/5/26.
 */

public class BasePresenter<M extends IModel> implements IPresenter {

    private M mModel;

    public BasePresenter(M model) {
        this.mModel = model;
        onCreate();
    }

    public M getModel() {
        return mModel;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }
}
