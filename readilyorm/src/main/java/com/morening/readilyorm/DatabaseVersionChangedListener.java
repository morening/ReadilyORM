package com.morening.readilyorm;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by morening on 2018/9/9.
 */

/**
 * If upgrade/downgrade, need to implement this interface
 */
public interface DatabaseVersionChangedListener {

    /**
     * When upgrading the database version
     *
     * @param db SQLiteDatabase
     * @param oldVersion
     * @param newVersion
     */
    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    /**
     * When downgrading the database version
     *
     * @param db SQLiteDatabase
     * @param oldVersion
     * @param newVersion
     */
    void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion);
}
