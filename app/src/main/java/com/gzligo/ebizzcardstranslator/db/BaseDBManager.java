package com.gzligo.ebizzcardstranslator.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.constants.CommonConstants;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

import greendao.autogen.bean.DaoMaster;
import greendao.autogen.bean.DaoSession;

import static com.gzligo.ebizzcardstranslator.utils.SharedPreferencesUtils.getSharedPreferences;

/**
 * Created by Lwd on 2017/6/20.
 */

public abstract class BaseDBManager<M, K> implements IDatabase<M, K>{
    private static GreenDaoHelper mHelper;
    protected static DaoSession daoSession;

    /**
     * 初始化OpenHelper
     */
    public static void initOpenHelper(@NonNull Context context, @NonNull String dataBaseName) {
        mHelper = getOpenHelper(context, dataBaseName);
        openWritableDb();
    }

    protected static void openWritableDb() throws SQLiteException {
        daoSession = new DaoMaster(getWritableDatabase()).newSession();
    }

    private static SQLiteDatabase getWritableDatabase() {
        return mHelper.getWritableDatabase();
    }

    private static GreenDaoHelper getOpenHelper(@NonNull Context context, @Nullable String dataBaseName) {
        closeDbConnections();
        return new GreenDaoHelper(context, dataBaseName, null);
    }

    /**
     * 关闭数据库
     */
    public static void closeDbConnections() {
        if (mHelper != null) {
            mHelper.close();
            mHelper = null;
        }
        if (daoSession != null) {
            daoSession.clear();
            daoSession = null;
        }
    }

    /**
     * 获取Dao
     */
    public abstract AbstractDao<M, K> getAbstractDao();

    @Override
    public boolean insert(M m) {
        try {
            if (m == null)
                return false;
            openWritableDb();
            getAbstractDao().insert(m);
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(M m) {
        try {
            if (m == null)
                return false;
            openWritableDb();
            getAbstractDao().delete(m);
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteByKey(K key) {
        try {
            if (key.toString().isEmpty())
                return false;
            openWritableDb();
            getAbstractDao().deleteByKey(key);
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteList(List<M> mList) {
        try {
            if (mList == null || mList.size() == 0)
                return false;
            openWritableDb();
            getAbstractDao().deleteInTx(mList);
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteByKeyInTx(K... key) {
        try {
            openWritableDb();
            getAbstractDao().deleteByKeyInTx(key);
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteAll() {
        try{
            openWritableDb();
            getAbstractDao().deleteAll();
        }catch (SQLException e){
            return false;
        }
        return true;
    }

    @Override
    public boolean insertOrReplace(@NonNull M m) {
        try {
            if (m == null){
                return false;
            }
            openWritableDb();
            getAbstractDao().insertOrReplace(m);
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean update(M m) {
        try {
            if (m == null)
                return false;
            openWritableDb();
            getAbstractDao().update(m);
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean updateInTx(M... m) {
        try {
            if (m == null)
                return false;
            openWritableDb();
            getAbstractDao().updateInTx(m);
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean updateList(List<M> mList) {
        try {
            if (mList == null || mList.size() == 0)
                return false;
            openWritableDb();
            getAbstractDao().updateInTx(mList);
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    protected static void openReadableDb() throws SQLiteException {
        daoSession = new DaoMaster(getReadableDatabase()).newSession();
    }

    private static SQLiteDatabase getReadableDatabase() {
        if(null==mHelper){
            String dbName = (String) getSharedPreferences(AppManager.get()
                    .getApplication(), CommonConstants.USER_DB_NAME,"",CommonConstants.USER_INFO_PRE_NAME);
            initOpenHelper(AppManager.get().getApplication(),dbName);
        }
        return mHelper.getReadableDatabase();
    }

    @Override
    public List<M> loadAll() {
        openReadableDb();
        return getAbstractDao().loadAll();
    }

    @Override
    public List<M> loadPages(int page, int number, WhereCondition cond,Property property,WhereCondition... condMore) {
        openReadableDb();
        QueryBuilder<M> queryBuilder = getAbstractDao().queryBuilder().where(cond,condMore).offset(page * number);
        if(null!=property){
            queryBuilder.orderDesc(property);
        }
        return queryBuilder.limit(number).list();
    }

    @Override
    public long getPages(int number,WhereCondition cond,WhereCondition... condMore) {
        long count = getAbstractDao().queryBuilder().where(cond,condMore).count();
        long page = count / number;
        if (page > 0 && count % number == 0) {
            return page - 1;
        }
        return page;
    }

    @Override
    public boolean refresh(M m) {
        try {
            if (m == null)
                return false;
            openWritableDb();
            getAbstractDao().refresh(m);
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    @Override
    public void clearDaoSession() {
        if (daoSession != null) {
            daoSession.clear();
            daoSession = null;
        }
    }

    @Override
    public boolean dropDatabase() {
        try {
            openWritableDb();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void runInTx(Runnable runnable) {
        try {
            openWritableDb();
            daoSession.runInTx(runnable);
        } catch (SQLiteException e) {
        }
    }

    @Override
    public boolean insertList(List<M> list) {
        try {
            if (list == null || list.size() == 0)
                return false;
            openWritableDb();
            getAbstractDao().insertInTx(list);
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean insertOrReplaceList(List<M> list) {
        try {
            if (list == null || list.size() == 0)
                return false;
            openWritableDb();
            getAbstractDao().insertOrReplaceInTx(list);
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    @Override
    public QueryBuilder<M> getQueryBuilder() {
        openReadableDb();
        return getAbstractDao().queryBuilder();
    }

    @Override
    public List<M> queryRaw(WhereCondition cond, WhereCondition... condMore) {
        openReadableDb();
        return getAbstractDao().queryBuilder().where(cond,condMore).build().list();
    }

    @Override
    public M queryOne(WhereCondition cond, WhereCondition... condMore) {
        openReadableDb();
        return getAbstractDao().queryBuilder().where(cond,condMore).build().unique();
    }
}
