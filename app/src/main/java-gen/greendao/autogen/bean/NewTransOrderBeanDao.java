package greendao.autogen.bean;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.gzligo.ebizzcardstranslator.persistence.NewTransOrderBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "NEW_TRANS_ORDER_BEAN".
*/
public class NewTransOrderBeanDao extends AbstractDao<NewTransOrderBean, Long> {

    public static final String TABLENAME = "NEW_TRANS_ORDER_BEAN";

    /**
     * Properties of entity NewTransOrderBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property OrderId = new Property(1, String.class, "orderId", false, "ORDER_ID");
        public final static Property Desc = new Property(2, String.class, "desc", false, "DESC");
        public final static Property Duration = new Property(3, Integer.class, "duration", false, "DURATION");
        public final static Property Start = new Property(4, Long.class, "start", false, "START");
        public final static Property FromName = new Property(5, String.class, "fromName", false, "FROM_NAME");
        public final static Property FromUserId = new Property(6, String.class, "fromUserId", false, "FROM_USER_ID");
        public final static Property FromLangId = new Property(7, Integer.class, "fromLangId", false, "FROM_LANG_ID");
        public final static Property FromPortraitId = new Property(8, String.class, "fromPortraitId", false, "FROM_PORTRAIT_ID");
        public final static Property ToName = new Property(9, String.class, "toName", false, "TO_NAME");
        public final static Property ToUserId = new Property(10, String.class, "toUserId", false, "TO_USER_ID");
        public final static Property ToLangId = new Property(11, Integer.class, "toLangId", false, "TO_LANG_ID");
        public final static Property ToPortraitId = new Property(12, String.class, "toPortraitId", false, "TO_PORTRAIT_ID");
        public final static Property EffectiveTime = new Property(13, Long.class, "effectiveTime", false, "EFFECTIVE_TIME");
    }


    public NewTransOrderBeanDao(DaoConfig config) {
        super(config);
    }
    
    public NewTransOrderBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"NEW_TRANS_ORDER_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"ORDER_ID\" TEXT NOT NULL ," + // 1: orderId
                "\"DESC\" TEXT," + // 2: desc
                "\"DURATION\" INTEGER," + // 3: duration
                "\"START\" INTEGER NOT NULL ," + // 4: start
                "\"FROM_NAME\" TEXT," + // 5: fromName
                "\"FROM_USER_ID\" TEXT NOT NULL ," + // 6: fromUserId
                "\"FROM_LANG_ID\" INTEGER," + // 7: fromLangId
                "\"FROM_PORTRAIT_ID\" TEXT," + // 8: fromPortraitId
                "\"TO_NAME\" TEXT," + // 9: toName
                "\"TO_USER_ID\" TEXT NOT NULL ," + // 10: toUserId
                "\"TO_LANG_ID\" INTEGER," + // 11: toLangId
                "\"TO_PORTRAIT_ID\" TEXT," + // 12: toPortraitId
                "\"EFFECTIVE_TIME\" INTEGER NOT NULL );"); // 13: effectiveTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"NEW_TRANS_ORDER_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, NewTransOrderBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getOrderId());
 
        String desc = entity.getDesc();
        if (desc != null) {
            stmt.bindString(3, desc);
        }
 
        Integer duration = entity.getDuration();
        if (duration != null) {
            stmt.bindLong(4, duration);
        }
        stmt.bindLong(5, entity.getStart());
 
        String fromName = entity.getFromName();
        if (fromName != null) {
            stmt.bindString(6, fromName);
        }
        stmt.bindString(7, entity.getFromUserId());
 
        Integer fromLangId = entity.getFromLangId();
        if (fromLangId != null) {
            stmt.bindLong(8, fromLangId);
        }
 
        String fromPortraitId = entity.getFromPortraitId();
        if (fromPortraitId != null) {
            stmt.bindString(9, fromPortraitId);
        }
 
        String toName = entity.getToName();
        if (toName != null) {
            stmt.bindString(10, toName);
        }
        stmt.bindString(11, entity.getToUserId());
 
        Integer toLangId = entity.getToLangId();
        if (toLangId != null) {
            stmt.bindLong(12, toLangId);
        }
 
        String toPortraitId = entity.getToPortraitId();
        if (toPortraitId != null) {
            stmt.bindString(13, toPortraitId);
        }
        stmt.bindLong(14, entity.getEffectiveTime());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, NewTransOrderBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getOrderId());
 
        String desc = entity.getDesc();
        if (desc != null) {
            stmt.bindString(3, desc);
        }
 
        Integer duration = entity.getDuration();
        if (duration != null) {
            stmt.bindLong(4, duration);
        }
        stmt.bindLong(5, entity.getStart());
 
        String fromName = entity.getFromName();
        if (fromName != null) {
            stmt.bindString(6, fromName);
        }
        stmt.bindString(7, entity.getFromUserId());
 
        Integer fromLangId = entity.getFromLangId();
        if (fromLangId != null) {
            stmt.bindLong(8, fromLangId);
        }
 
        String fromPortraitId = entity.getFromPortraitId();
        if (fromPortraitId != null) {
            stmt.bindString(9, fromPortraitId);
        }
 
        String toName = entity.getToName();
        if (toName != null) {
            stmt.bindString(10, toName);
        }
        stmt.bindString(11, entity.getToUserId());
 
        Integer toLangId = entity.getToLangId();
        if (toLangId != null) {
            stmt.bindLong(12, toLangId);
        }
 
        String toPortraitId = entity.getToPortraitId();
        if (toPortraitId != null) {
            stmt.bindString(13, toPortraitId);
        }
        stmt.bindLong(14, entity.getEffectiveTime());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public NewTransOrderBean readEntity(Cursor cursor, int offset) {
        NewTransOrderBean entity = new NewTransOrderBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // orderId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // desc
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // duration
            cursor.getLong(offset + 4), // start
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // fromName
            cursor.getString(offset + 6), // fromUserId
            cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7), // fromLangId
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // fromPortraitId
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // toName
            cursor.getString(offset + 10), // toUserId
            cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11), // toLangId
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // toPortraitId
            cursor.getLong(offset + 13) // effectiveTime
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, NewTransOrderBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setOrderId(cursor.getString(offset + 1));
        entity.setDesc(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDuration(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setStart(cursor.getLong(offset + 4));
        entity.setFromName(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setFromUserId(cursor.getString(offset + 6));
        entity.setFromLangId(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
        entity.setFromPortraitId(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setToName(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setToUserId(cursor.getString(offset + 10));
        entity.setToLangId(cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11));
        entity.setToPortraitId(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setEffectiveTime(cursor.getLong(offset + 13));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(NewTransOrderBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(NewTransOrderBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(NewTransOrderBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
