package com.gzligo.ebizzcardstranslator.db.manager;

import com.gzligo.ebizzcardstranslator.db.BaseDBManager;
import com.gzligo.ebizzcardstranslator.persistence.NewTravelTransOrderBean;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

import greendao.autogen.bean.NewTravelTransOrderBeanDao;

/**
 * Created by Lwd on 2017/9/13.
 */

public class TravelTransOrderManager extends BaseDBManager<NewTravelTransOrderBean,Long> {

    @Override
    public AbstractDao<NewTravelTransOrderBean, Long> getAbstractDao() {
        return daoSession.getNewTravelTransOrderBeanDao();
    }

    public boolean insertOrder(NewTravelTransOrderBean newTravelTransOrderBean){
        WhereCondition cond = NewTravelTransOrderBeanDao.Properties.FromUserId.eq(newTravelTransOrderBean.getFromUserId());
        List<NewTravelTransOrderBean> beanList = getQueryBuilder().whereOr(NewTravelTransOrderBeanDao.Properties.Session_id.eq(newTravelTransOrderBean.getSession_id()),cond).list();
        if (null != beanList && beanList.size() > 0) {
            for (NewTravelTransOrderBean transOrderBean : beanList) {
                delete(transOrderBean);
            }
        }
        boolean result = insert(newTravelTransOrderBean);
        return result;
    }

    public List<NewTravelTransOrderBean> queryOrderList(){
        deleteOutTimeOrder();
        WhereCondition cond = NewTravelTransOrderBeanDao.Properties.EffectTime.gt(System.currentTimeMillis());
        List<NewTravelTransOrderBean> lists = queryRaw(cond);
        return lists;
    }

    public void deleteOutTimeOrder() {
        WhereCondition cond = NewTravelTransOrderBeanDao.Properties.EffectTime.lt(System.currentTimeMillis());
        List<NewTravelTransOrderBean> beanList = getQueryBuilder().where(cond).list();
        if (null != beanList && beanList.size() > 0) {
            for (NewTravelTransOrderBean transOrderBean : beanList) {
                delete(transOrderBean);
            }
        }
    }
}
