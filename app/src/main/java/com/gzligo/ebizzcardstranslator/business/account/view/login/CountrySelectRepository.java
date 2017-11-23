package com.gzligo.ebizzcardstranslator.business.account.view.login;

import android.content.Context;
import android.content.res.AssetManager;

import com.gzligo.ebizzcardstranslator.base.mvp.IModel;
import com.gzligo.ebizzcardstranslator.persistence.District;
import com.gzligo.ebizzcardstranslator.utils.FileManager;
import com.gzligo.ebizzcardstranslator.utils.Parser;
import com.gzligo.ebizzcardstranslator.utils.XmlElementNode;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Lwd on 2017/7/6.
 */

public class CountrySelectRepository implements IModel {
    private ArrayList<District> mExistsDistricts;

    public ArrayList<District> getmExistsDistricts() {
        return mExistsDistricts;
    }

    public void setmExistsDistricts(ArrayList<District> mExistsDistricts) {
        this.mExistsDistricts = mExistsDistricts;
    }

    public Observable getCountryList(final Context context) {
        return FileManager.getFileManager(context).readConfigBeanFileObservable(District.class);
    }

    public Observable setCountryList(final Context context) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<District>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<District>> e) throws Exception {
                AssetManager assetManager = context.getAssets();
                XmlElementNode<String, Object> node = Parser.parse(assetManager.open("districts.xml"));
                ArrayList<District> existsDistricts = District.initDistrict(node);
                FileManager.getFileManager(context).saveConfigBeanFile(existsDistricts, District.class.getSimpleName().toLowerCase());
                mExistsDistricts = existsDistricts;
                e.onNext(existsDistricts);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
