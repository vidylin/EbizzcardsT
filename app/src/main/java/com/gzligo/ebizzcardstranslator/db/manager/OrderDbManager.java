package com.gzligo.ebizzcardstranslator.db.manager;

import com.gzligo.ebizzcardstranslator.db.BaseDBManager;
import com.gzligo.ebizzcardstranslator.persistence.NewTransOrderBean;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

import greendao.autogen.bean.NewTransOrderBeanDao;

/**
 * Created by Lwd on 2017/6/20.
 */

public class OrderDbManager extends BaseDBManager<NewTransOrderBean,Long> {

    @Override
    public AbstractDao<NewTransOrderBean, Long> getAbstractDao() {
        return daoSession.getNewTransOrderBeanDao();
    }

    public boolean insertOrder(NewTransOrderBean newTransOrderBean){
        WhereCondition cond = NewTransOrderBeanDao.Properties.FromUserId.eq(newTransOrderBean.getFromUserId());
        WhereCondition cond2 = NewTransOrderBeanDao.Properties.ToUserId.eq(newTransOrderBean.getToUserId());
        WhereCondition whereCondition = getQueryBuilder().and(cond, cond2);
        List<NewTransOrderBean> beanList = getQueryBuilder().whereOr(NewTransOrderBeanDao.Properties.OrderId.eq(newTransOrderBean.getOrderId()),whereCondition).list();
        if (null != beanList && beanList.size() > 0) {
            for (NewTransOrderBean transOrderBean : beanList) {
                delete(transOrderBean);
            }
        }
        boolean result = insert(newTransOrderBean);
        return result;
    }

    public List<NewTransOrderBean> queryOrderList(){
        deleteOutTimeOrder();
        WhereCondition cond = NewTransOrderBeanDao.Properties.EffectiveTime.gt(System.currentTimeMillis());
        List<NewTransOrderBean> lists = queryRaw(cond);
        return lists;
    }


    public void deleteOutTimeOrder() {
        WhereCondition cond = NewTransOrderBeanDao.Properties.EffectiveTime.lt(System.currentTimeMillis());
        List<NewTransOrderBean> beanList = getQueryBuilder().where(cond).list();
        if (null != beanList && beanList.size() > 0) {
            for (NewTransOrderBean transOrderBean : beanList) {
                delete(transOrderBean);
            }
        }
    }
}
