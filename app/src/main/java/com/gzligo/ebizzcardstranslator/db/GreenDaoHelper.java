package com.gzligo.ebizzcardstranslator.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.greenrobot.greendao.database.Database;

import greendao.autogen.bean.ChatMessageBeanDao;
import greendao.autogen.bean.DaoMaster;

/**
 * Created by Lwd on 2017/8/3.
 */

public class GreenDaoHelper extends DaoMaster.DevOpenHelper {
    public static final String TAG = "GreenDaoHelper";

    public GreenDaoHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    //数据库字段改动之后，调用以下方法，将有改动的表的Dao class传进去，完成数据库升级
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        Log.w(TAG, "onUpgrade start update db----------------------------> oldVersion: " + oldVersion + ",newVersion: " + newVersion);
        MigrationHelper.migrate(db, ChatMessageBeanDao.class);
        Log.w(TAG, "onUpgrade db update finish----------------------------!");
        DaoMaster.createAllTables(db, true);
    }
}