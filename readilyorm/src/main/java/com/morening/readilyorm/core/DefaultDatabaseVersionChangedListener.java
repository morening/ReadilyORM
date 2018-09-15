package com.morening.readilyorm.core;

import android.annotation.TargetApi;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import com.morening.readilyorm.DatabaseVersionChangedListener;
import com.morening.readilyorm.util.Logger;

/**
 * Created by morening on 2018/9/15.
 */
public class DefaultDatabaseVersionChangedListener implements DatabaseVersionChangedListener {

    private ColumnVersionCache columnVersionCache;

    public DefaultDatabaseVersionChangedListener(ColumnVersionCache columnVersionCache) {
        this.columnVersionCache = columnVersionCache;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (columnVersionCache == null){
            return;
        }

        Logger.d(this, String.format("[onUpgrade] oldVersion=%d, newVersion=%d", oldVersion, newVersion));

        upgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (columnVersionCache == null){
            return;
        }

        Logger.d(this, String.format("[onDowngrade] oldVersion=%d, newVersion=%d", oldVersion, newVersion));
        Log.d("ReadilyORM", "There is no implementation when downgrading now, please focus on the latest ReadilyORM. Thank you for your support!");
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void upgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        try {
            db.beginTransaction();
            for (ColumnVersion columnVersion: columnVersionCache.getColumnVersions()){
                if (columnVersion.version > oldVersion && columnVersion.version <= newVersion){
                    String sql = buildAddColumnSql(columnVersion.type.getSimpleName(), columnVersion.nameInDb, columnVersion.typeInDb);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        db.validateSql(sql, null);
                    }
                    db.execSQL(sql);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    private String buildAddColumnSql(String table, String nameInDb, String typeInDb){
        StringBuilder addColumn = new StringBuilder("ALTER TABLE ");
        addColumn.append(table).append(" ADD COLUMN ").append(nameInDb).append(" ").append(typeInDb).append(";");

        return addColumn.toString();
    }
}
