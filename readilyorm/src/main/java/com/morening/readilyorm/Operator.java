package com.morening.readilyorm;

import android.database.sqlite.SQLiteDatabase;

import com.morening.readilyorm.exception.DatabaseOperationException;

import java.util.List;

/**
 * Created by morening on 2018/9/2.
 */

/**
 * Implement this interface to convert between java object and SQL model
 */
public interface Operator {

    /**
     * Insert java object with SQLiteDatabase
     *
     * @param target inserting object
     * @param db SQLiteDatabase
     * @param <T> the type of the inserting object
     * @return the inserted object
     * @throws DatabaseOperationException
     */
    <T> T onInsert(T target, SQLiteDatabase db) throws DatabaseOperationException;

    /**
     * Retrieve java object with SQLiteDatabase
     *
     * @param target retrieving object
     * @param db SQLiteDatabase
     * @param <T> the type of the retrieving object
     * @return the retrieved object
     * @throws DatabaseOperationException
     */
    <T> List<T> onRetrieve(T target, SQLiteDatabase db) throws DatabaseOperationException;

    /**
     * Update java object with SQLiteDatabase
     *
     * @param target updating object
     * @param db SQLiteDatabase
     * @param <T> the type of the updating object
     * @return the updated object
     * @throws DatabaseOperationException
     */
    <T> T onUpdate(T target, SQLiteDatabase db) throws DatabaseOperationException;

    /**
     * Delete java object with SQLiteDatabase
     *
     * @param target deleting object
     * @param db SQLiteDatabase
     * @param <T> the type of the deleting object
     * @return the deleted object
     * @throws DatabaseOperationException
     */
    <T> List<T> onDelete(T target, SQLiteDatabase db) throws DatabaseOperationException;
}
