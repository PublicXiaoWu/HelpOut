package com.gkzxhn.legalconsulting.greendao.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.gkzxhn.legalconsulting.entity.NotificationInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "NOTIFICATION_INFO".
*/
public class NotificationInfoDao extends AbstractDao<NotificationInfo, Long> {

    public static final String TABLENAME = "NOTIFICATION_INFO";

    /**
     * Properties of entity NotificationInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property SessionId = new Property(1, String.class, "sessionId", false, "SESSION_ID");
        public final static Property FromAccount = new Property(2, String.class, "fromAccount", false, "FROM_ACCOUNT");
        public final static Property Time = new Property(3, long.class, "time", false, "TIME");
        public final static Property Content = new Property(4, String.class, "content", false, "CONTENT");
    }


    public NotificationInfoDao(DaoConfig config) {
        super(config);
    }
    
    public NotificationInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"NOTIFICATION_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"SESSION_ID\" TEXT," + // 1: sessionId
                "\"FROM_ACCOUNT\" TEXT," + // 2: fromAccount
                "\"TIME\" INTEGER NOT NULL ," + // 3: time
                "\"CONTENT\" TEXT);"); // 4: content
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"NOTIFICATION_INFO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, NotificationInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String sessionId = entity.getSessionId();
        if (sessionId != null) {
            stmt.bindString(2, sessionId);
        }
 
        String fromAccount = entity.getFromAccount();
        if (fromAccount != null) {
            stmt.bindString(3, fromAccount);
        }
        stmt.bindLong(4, entity.getTime());
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(5, content);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, NotificationInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String sessionId = entity.getSessionId();
        if (sessionId != null) {
            stmt.bindString(2, sessionId);
        }
 
        String fromAccount = entity.getFromAccount();
        if (fromAccount != null) {
            stmt.bindString(3, fromAccount);
        }
        stmt.bindLong(4, entity.getTime());
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(5, content);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public NotificationInfo readEntity(Cursor cursor, int offset) {
        NotificationInfo entity = new NotificationInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // sessionId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // fromAccount
            cursor.getLong(offset + 3), // time
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) // content
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, NotificationInfo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setSessionId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setFromAccount(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTime(cursor.getLong(offset + 3));
        entity.setContent(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(NotificationInfo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(NotificationInfo entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(NotificationInfo entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
