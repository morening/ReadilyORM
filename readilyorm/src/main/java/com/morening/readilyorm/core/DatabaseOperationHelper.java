package com.morening.readilyorm.core;

import android.database.sqlite.SQLiteDatabase;

import com.morening.readilyorm.Operator;
import com.morening.readilyorm.exception.DatabaseOperationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by morening on 2018/9/1.
 */
final public class DatabaseOperationHelper<T> {

    private DatabaseOpenHelper openHelper;
    private Operator operator;

    public DatabaseOperationHelper(DatabaseOpenHelper openHelper, Operator operator){
        this.openHelper = openHelper;
        this.operator = operator;
    }

    public List<T> insertInTx(T... ts) throws DatabaseOperationException {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            for (T t: ts){
                operator.onInsert(t, db);
            }
            db.setTransactionSuccessful();
        } catch (Exception e){
            throw new DatabaseOperationException(e);
        } finally {
            db.endTransaction();
            db.close();
        }

        return Arrays.asList(ts);
    }

    public List<T> insertInTx(List<T> ts) throws DatabaseOperationException {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            for (T t: ts){
                operator.onInsert(t, db);
            }
            db.setTransactionSuccessful();
        } catch (Exception e){
            throw new DatabaseOperationException(e);
        } finally {
            db.endTransaction();
            db.close();
        }

        return ts;
    }

    public List<T> retrieveInTx(T... ts) throws DatabaseOperationException {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        List<T> retrievedList = new ArrayList<>();
        try {
            db.beginTransaction();
            for (T t: ts){
                List<T> retrieved = operator.onRetrieve(t, db);
                if (retrieved != null){
                    retrievedList.addAll(retrieved);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e){
            throw new DatabaseOperationException(e);
        } finally {
            db.endTransaction();
            db.close();
        }

        return retrievedList;
    }

    public List<T> retrieveInTx(List<T> ts) throws DatabaseOperationException {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        List<T> retrievedList = new ArrayList<>();
        try {
            db.beginTransaction();
            for (T t: ts){
                List<T> retrieved = operator.onRetrieve(t, db);
                if (retrieved != null){
                    retrievedList.addAll(retrieved);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e){
            throw new DatabaseOperationException(e);
        } finally {
            db.endTransaction();
            db.close();
        }

        return retrievedList;
    }

    public List<T> updateInTx(T... ts) throws DatabaseOperationException {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            for (T t: ts){
                operator.onUpdate(t, db);
            }
            db.setTransactionSuccessful();
        } catch (Exception e){
            throw new DatabaseOperationException(e);
        } finally {
            db.endTransaction();
            db.close();
        }

        return Arrays.asList(ts);
    }

    public List<T> updateInTx(List<T> ts) throws DatabaseOperationException {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            for (T t: ts){
                operator.onUpdate(t, db);
            }
            db.setTransactionSuccessful();
        } catch (Exception e){
            throw new DatabaseOperationException(e);
        } finally {
            db.endTransaction();
            db.close();
        }

        return ts;
    }

    public List<T> deleteInTx(T... ts) throws DatabaseOperationException {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        List<T> deletedList = new ArrayList<>();
        try {
            db.beginTransaction();
            for (T t: ts){
                List<T> deleted = operator.onDelete(t, db);
                if (deleted != null){
                    deletedList.addAll(deleted);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e){
            throw new DatabaseOperationException(e);
        } finally {
            db.endTransaction();
            db.close();
        }

        return deletedList;
    }

    public List<T> deleteInTx(List<T> ts) throws DatabaseOperationException {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        List<T> deletedList = new ArrayList<>();
        try {
            db.beginTransaction();
            for (T t: ts){
                List<T> deleted = operator.onDelete(t, db);
                if (deleted != null){
                    deletedList.addAll(deleted);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e){
            throw new DatabaseOperationException(e);
        } finally {
            db.endTransaction();
            db.close();
        }

        return deletedList;
    }
}
