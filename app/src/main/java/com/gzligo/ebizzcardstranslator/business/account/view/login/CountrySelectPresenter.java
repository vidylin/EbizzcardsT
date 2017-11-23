package com.gzligo.ebizzcardstranslator.business.account.view.login;

import android.content.Context;

import com.gzligo.ebizzcardstranslator.base.mvp.BasePresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.persistence.District;

import java.util.ArrayList;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by Lwd on 2017/7/6.
 */

public class CountrySelectPresenter extends BasePresenter<CountrySelectRepository>{
    private static final int GET_DISTRICT_LIST = 0x60;

    public CountrySelectPresenter(CountrySelectRepository model) {
        super(model);
    }

    public void getDistrict(final Message message){
        final Context context = (Context) message.objs[0];
        ArrayList<District> districtArrayList = getModel().getmExistsDistricts();
        if(null==districtArrayList){
            getModel().getCountryList(context).subscribe(new Consumer<ArrayList<District>>() {
                @Override
                public void accept(@NonNull ArrayList<District> o) throws Exception {
                    if(o.size()>0){
                        getModel().setmExistsDistricts(o);
                        message.what = GET_DISTRICT_LIST;
                        message.dispatchToIView();
                    }else{
                        getModel().setCountryList(context).subscribe(new Consumer<ArrayList<District>>() {
                            @Override
                            public void accept(@NonNull ArrayList<District> o) throws Exception {
                                message.what = GET_DISTRICT_LIST;
                                message.dispatchToIView();
                            }
                        });
                    }
                }
            });
        }else{
            message.what = GET_DISTRICT_LIST;
            message.dispatchToIView();
        }
    }

    public ArrayList<District> getDistrictList(){
        return getModel().getmExistsDistricts();
    }
}
