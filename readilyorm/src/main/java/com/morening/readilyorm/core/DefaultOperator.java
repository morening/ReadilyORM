package com.morening.readilyorm.core;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.text.TextUtils;

import com.morening.readilyorm.Operator;
import com.morening.readilyorm.exception.DatabaseOperationException;
import com.morening.readilyorm.exception.IllegalParameterException;
import com.morening.readilyorm.util.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by morening on 2018/9/2.
 */
final public class DefaultOperator implements Operator {

    private DependencyCache cache;

    public DefaultOperator(DependencyCache cache){
        this.cache = cache;
    }

    @Override
    public <T> T onInsert(T target, SQLiteDatabase db) throws DatabaseOperationException {
        try {
            insert(target, db, null);
        } catch (NoSuchMethodException e) {
            throw new DatabaseOperationException(e);
        } catch (IllegalAccessException e){
            throw new DatabaseOperationException(e);
        } catch (InvocationTargetException e){
            throw new DatabaseOperationException(e);
        }

        return target;
    }

    private <T> long insert(T target, SQLiteDatabase db, Map<String, Long> nextMap) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class<?> targetClass = target.getClass();

        ContentValues cv = new ContentValues();
        if (nextMap != null){
            for (String key: nextMap.keySet()){
                cv.put(key, nextMap.get(key));
            }
        }
        Dependency dependency = cache.getDependency(targetClass);
        if (dependency.preConditions != null){
            for (Dependency.Condition condition: dependency.preConditions){
                Class<?> genericType = condition.genericType;
                String fieldName = condition.fieldName;
                Object nextTarget = Get_FieldValue(target, fieldName);
                Logger.d(this, String.format("[%s] <insert> preCondition nameInDb=%s, genericType=%s, fieldName=%s, nextTarget=%s",
                        targetClass.getSimpleName(), condition.nameInDb, genericType.getSimpleName(), fieldName, nextTarget));
                if (nextTarget == null){
                    continue;
                }
                long inserted_id = insert(nextTarget, db, null);
                Logger.d(this, String.format("[%s] <insert> preCondition nameInDb=%s, inserted_id=%s",
                        targetClass.getSimpleName(), condition.nameInDb, inserted_id));
                cv.put(condition.nameInDb, inserted_id);

                Set_FieldValue_Integer(target, condition.nameInDb, Integer.valueOf(Long.valueOf(inserted_id).intValue()));
            }
        }

        long id = insertLocked(target, db, cv);
        Set_FieldValue_Integer(target, "id", Integer.valueOf(Long.valueOf(id).intValue()));

        if (dependency.postConditions != null){
            for (Dependency.Condition condition: dependency.postConditions){
                Class<?> genericType = condition.genericType;
                String fieldName = condition.fieldName;
                List<?> Get_List = (List<?>) Get_FieldValue(target, fieldName);
                Logger.d(this, String.format("[%s] <insert> postCondition nameInDb=%s, genericType=%s, fieldName=%s, Get_List=%s",
                        targetClass.getSimpleName(), condition.nameInDb, genericType.getSimpleName(), fieldName, Get_List));
                if (Get_List != null){
                    for (Object nextTarget: Get_List){
                        Map<String, Long> next = new HashMap<>();
                        next.put(condition.nameInDb, id);
                        insert(nextTarget, db, next);

                        Set_FieldValue_Integer(nextTarget, condition.nameInDb, Integer.valueOf(Long.valueOf(id).intValue()));
                    }
                }
            }
        }

        return id;
    }

    private <T> long insertLocked(T target, SQLiteDatabase db, ContentValues cv) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> targetClass = target.getClass();
        Dependency dependency = cache.getDependency(targetClass);
        for (Dependency.Column column: dependency.columns){
            Class<?> fieldType = column.fieldType;
            String nameInDb = column.nameInDb;
            if (nameInDb.equals("id")
                    || cv.keySet().contains(nameInDb)){
                continue;
            }

            Object Get_Value = Get_FieldValue(target, nameInDb);
            if (Get_Value == null && column.notNull){
                throw new IllegalParameterException("\""+nameInDb+"\""+" shouldn't be null, Because of NotNull Constraint!");
            }
            if (fieldType == Integer.class){
                Integer value = 0;
                if (Get_Value != null){
                    value = (Integer)Get_Value;
                } else {
                    Set_FieldValue_Integer(target, nameInDb, value);
                }
                Logger.d(this, String.format("[%s] <insertLocked> typeInDb=%s, nameInDb=%s, Get_Value=%s",
                        targetClass.getSimpleName(), column.typeInDb, nameInDb, value));
                cv.put(nameInDb, value);
            } else if (fieldType == Long.class){
                Long value = 0L;
                if (Get_Value != null){
                    value = (Long)Get_Value;
                } else {
                    Set_FieldValue_Long(target, nameInDb, value);
                }
                Logger.d(this, String.format("[%s] <insertLocked> typeInDb=%s, nameInDb=%s, Get_Value=%s",
                        targetClass.getSimpleName(), column.typeInDb, nameInDb, value));
                cv.put(nameInDb, value);
            } else if (fieldType == Float.class){
                Float value = 0F;
                if (Get_Value != null){
                    value = (Float)Get_Value;
                } else {
                    Set_FieldValue_Float(target, nameInDb, value);
                }
                Logger.d(this, String.format("[%s] <insertLocked> typeInDb=%s, nameInDb=%s, Get_Value=%s",
                        targetClass.getSimpleName(), column.typeInDb, nameInDb, value));
                cv.put(nameInDb, value);
            } else if (fieldType == String.class){
                String value = "";
                if (Get_Value != null){
                    value = (String)Get_Value;
                } else {
                    Set_FieldValue_String(target, nameInDb, value);
                }
                Logger.d(this, String.format("[%s] <insertLocked> typeInDb=%s, nameInDb=%s, Get_Value=%s",
                        targetClass.getSimpleName(), column.typeInDb, nameInDb, value));
                cv.put(nameInDb, value);
            }
        }
        String tableName = getTableName(targetClass);
        Logger.d(this, String.format("<insertLocked> tableName=%s, cv=%s", tableName, cv.toString()));

        return db.insert(tableName, null, cv);
    }

    @Override
    public <T> List<T> onRetrieve(T target, SQLiteDatabase db) throws DatabaseOperationException{
        try {
            return retrieve(target, db, null);
        } catch (NoSuchMethodException e) {
            throw new DatabaseOperationException(e);
        } catch (InstantiationException e){
            throw new DatabaseOperationException(e);
        } catch (IllegalAccessException e){
            throw new DatabaseOperationException(e);
        } catch (InvocationTargetException e){
            throw new DatabaseOperationException(e);
        }
    }

    private <T> List<T> retrieve(T target, SQLiteDatabase db, Map<String, Integer> nextCondition) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        List<T> retrievedTargets = retrieveLocked(target, db, nextCondition);
        if (retrievedTargets == null){
            return null;
        }

        Class<?> targetClass = target.getClass();
        Dependency dependency = cache.getDependency(targetClass);
        for (T retrievedTarget: retrievedTargets){
            if (dependency.preConditions != null){
                for (Dependency.Condition condition: dependency.preConditions){
                    String fieldName = condition.fieldName;
                    String nameInDb = condition.nameInDb;
                    Class<?> nextClass = condition.genericType;
                    Object nextInstance = nextClass.newInstance();

                    Object Get_Value = Get_FieldValue(retrievedTarget, nameInDb);
                    Logger.d(this, String.format("[%s] <retrieve> nextClass=%s, fieldName=%s, nameInDb=%s, Get_Value=%s",
                            targetClass.getSimpleName(), nextClass.getSimpleName(), fieldName, nameInDb, (int) Get_Value));

                    Map<String, Integer> next = new HashMap<>();
                    next.put("id", (int) Get_Value);
                    List<?> retrievedNext = retrieve(nextInstance, db, next);
                    Logger.d(this, String.format("[%s] <retrieve> type=%s", targetClass.getSimpleName(), nextClass.getSimpleName()));
                    if (retrievedNext == null || retrievedNext.size() == 0){
                        continue;
                    }
                    Set_FieldValue_Object(retrievedTarget, fieldName, retrievedNext.get(0), nextClass);
                }
            }
            if (dependency.postConditions != null){
                for (Dependency.Condition condition: dependency.postConditions){
                    String fieldName = condition.fieldName;
                    String nameInDb = condition.nameInDb;
                    Class<?> nextClass = condition.genericType;
                    Object nextInstance = nextClass.newInstance();

                    Object Get_Value = Get_FieldValue(retrievedTarget, "id");
                    Logger.d(this, String.format("[%s] <retrieve> nextClass=%s, fieldName=%s, nameInDb=%s, Get_Value=%s",
                            targetClass.getSimpleName(), nextClass.getSimpleName(), fieldName, nameInDb, (int) Get_Value));

                    Map<String, Integer> next = new HashMap<>();
                    next.put(nameInDb, (int) Get_Value);
                    List<?> list = retrieve(nextInstance, db, next);
                    if (list == null || list.size() == 0){
                        continue;
                    }
                    Logger.d(this, String.format("[%s] <retrieve> type=%s, size=%d", targetClass.getSimpleName(), nextClass.getSimpleName(), list.size()));
                    Set_FieldValue_Object(retrievedTarget, fieldName, list, List.class);
                }
            }
        }

        return retrievedTargets;
    }

    @TargetApi(Build.VERSION_CODES.N)
    private <T> List<T> retrieveLocked(T target, SQLiteDatabase db, Map<String, Integer> nextCondition) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String sql = buildRetrieveSql(target, nextCondition);
        Logger.d(this, String.format("[%s]<retrieveLocked> sql=%s", target.getClass().getSimpleName(), sql));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            db.validateSql(sql, null);
        }
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()){
            List<T> ts = new ArrayList<>();
            Class<T> targetClass = (Class<T>) target.getClass();
            Dependency dependency = cache.getDependency(targetClass);
            do {
                T retrievedTarget = modelize(cursor, targetClass, dependency);
                ts.add(retrievedTarget);
            } while (cursor.moveToNext());

            return ts;
        }

        return null;
    }

    private <T> T modelize(Cursor cursor, Class<T> targetClass, Dependency dependency) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        T targetInstance = targetClass.newInstance();
        for (Dependency.Column column : dependency.columns) {
            String nameInDb = column.nameInDb;
            Class<?> fieldType = column.fieldType;
            int index = cursor.getColumnIndex(nameInDb);
            if (fieldType == Integer.class) {
                Integer value = cursor.getInt(index);
                Logger.d(this, String.format("[%s] <modelize> typeInDb=%s, nameInDb=%s, value=%s",
                        targetClass.getSimpleName(), column.typeInDb, nameInDb, value));
                Set_FieldValue_Integer(targetInstance, nameInDb, value);
            } else if (fieldType == Long.class) {
                Long value = cursor.getLong(index);
                Logger.d(this, String.format("[%s] <modelize> typeInDb=%s, nameInDb=%s, value=%s",
                        targetClass.getSimpleName(), column.typeInDb, nameInDb, value));
                Set_FieldValue_Long(targetInstance, nameInDb, value);
            } else if (fieldType == Float.class) {
                Float value = cursor.getFloat(index);
                Logger.d(this, String.format("[%s] <modelize> typeInDb=%s, nameInDb=%s, value=%s",
                        targetClass.getSimpleName(), column.typeInDb, nameInDb, value));
                Set_FieldValue_Float(targetInstance, nameInDb, value);
            } else if (fieldType == String.class) {
                String value = cursor.getString(index);
                Logger.d(this, String.format("[%s] <modelize> typeInDb=%s, nameInDb=%s, value=%s",
                        targetClass.getSimpleName(), column.typeInDb, nameInDb, value));
                Set_FieldValue_String(targetInstance, nameInDb, value);
            }
        }
        return targetInstance;
    }

    private <T> String buildRetrieveSql(T target, Map<String, Integer> nextCondition) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> targetClass = target.getClass();
        Dependency dependency = cache.getDependency(targetClass);
        String tableName = getTableName(targetClass);
        StringBuilder query = new StringBuilder("SELECT * FROM ").append(tableName);
        StringBuilder where = new StringBuilder();
        if (nextCondition == null){
            for (Dependency.Column column: dependency.columns){
                Class<?> fieldType = column.fieldType;
                String nameInDb = column.nameInDb;
                Object Get_Value = Get_FieldValue(target, nameInDb);
                if (Get_Value == null){
                    continue;
                }
                if (fieldType == Integer.class){
                    Logger.d(this, String.format("[%s] <buildRetrieveSql> typeInDb=%s, nameInDb=%s, Get_value=%s",
                            targetClass.getSimpleName(), column.typeInDb, nameInDb, (Integer)Get_Value));
                    where.append(nameInDb).append("=").append((Integer)Get_Value).append(" AND ");
                } else if (fieldType == Long.class){
                    Logger.d(this, String.format("[%s] <buildRetrieveSql> typeInDb=%s, nameInDb=%s, Get_value=%s",
                            targetClass.getSimpleName(), column.typeInDb, nameInDb, (Long)Get_Value));
                    where.append(nameInDb).append("=").append((Long)Get_Value).append(" AND ");
                } else if (fieldType == Float.class){
                    Logger.d(this, String.format("[%s] <buildRetrieveSql> typeInDb=%s, nameInDb=%s, Get_value=%s",
                            targetClass.getSimpleName(), column.typeInDb, nameInDb, (Float)Get_Value));
                    where.append(nameInDb).append("=").append((Float)Get_Value).append(" AND ");
                } else if (fieldType == String.class){
                    Logger.d(this, String.format("[%s] <buildRetrieveSql> typeInDb=%s, nameInDb=%s, Get_value=%s",
                            targetClass.getSimpleName(), column.typeInDb, nameInDb, (String)Get_Value));
                    where.append(nameInDb).append("=").append("\"").append((String)Get_Value).append("\"").append(" AND ");
                }
            }
        } else {
            for (String key: nextCondition.keySet()){
                where.append(key).append("=").append(nextCondition.get(key)).append(" AND ");
            }
        }
        if (where.length() > 0){
            query.append(" WHERE ").append(where.substring(0, where.lastIndexOf(" AND ")));
        }
        query.append(";");

        return query.toString();
    }

    @Override
    public <T> T onUpdate(T target, SQLiteDatabase db) throws DatabaseOperationException {
        try {
            return update(target, db);
        } catch (NoSuchMethodException e) {
            throw new DatabaseOperationException(e);
        } catch (IllegalAccessException e){
            throw new DatabaseOperationException(e);
        } catch (InvocationTargetException e){
            throw new DatabaseOperationException(e);
        }
    }

    private <T> T update(T target, SQLiteDatabase db) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (target == null){
            return null;
        }
        Class<?> targetClass = target.getClass();
        Dependency dependency = cache.getDependency(targetClass);

        updateLocked(target, db);

        if (dependency.preConditions != null){
            for (Dependency.Condition condition: dependency.preConditions){
                Object nextTarget = Get_FieldValue(target, condition.fieldName);
                Logger.d(this, String.format("[%s] <update> (preConditions) genericType=%s, fieldName=%s, nextTarget=%s",
                        targetClass.getSimpleName(), condition.genericType.getSimpleName(), condition.fieldName, nextTarget));
                update(nextTarget, db);
            }
        }

        if (dependency.postConditions != null){
            for (Dependency.Condition condition: dependency.postConditions){
                List Get_List = (List) Get_FieldValue(target, condition.fieldName);
                Logger.d(this, String.format("[%s] <update> (postConditions) genericType=%s, fieldName=%s, Get_List=%s",
                        targetClass.getSimpleName(), condition.genericType.getSimpleName(), condition.fieldName, Get_List));
                if (Get_List != null){
                    for (int k=0; k<Get_List.size(); k++){
                        update(Get_List.get(k), db);
                    }
                }
            }
        }

        return target;
    }

    @TargetApi(Build.VERSION_CODES.N)
    private <T> T updateLocked(T target, SQLiteDatabase db) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String sql = buildUpdateSql(target);
        Logger.d(this, "<updateLocked> sql="+sql);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            db.validateSql(sql, null);
        }
        db.execSQL(sql);

        return target;
    }

    private <T> String buildUpdateSql(T target) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> targetClass = target.getClass();
        Logger.d(this, "<buildUpdateSql> target="+targetClass.getSimpleName());
        String tableName = getTableName(targetClass);
        Dependency dependency = cache.getDependency(targetClass);

        StringBuilder update = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
        for (Dependency.Column column: dependency.columns){
            String nameInDb = column.nameInDb;
            if (TextUtils.equals(nameInDb, "id")){
                continue;
            }
            Object Get_Value = Get_FieldValue(target, nameInDb);
            Class<?> fieldType = column.fieldType;
            if (fieldType == Integer.class){
                Integer value = 0;
                if (Get_Value != null){
                    value = (Integer)Get_Value;
                }
                Logger.d(this, String.format("[%s] <buildUpdateSql> typeInDb=%s, nameInDb=%s, Get_Value=%s",
                        targetClass.getSimpleName(), column.typeInDb, nameInDb, value));
                update.append(nameInDb).append("=").append(value).append(",");
            } else if (fieldType == Long.class){
                Long value = 0L;
                if (Get_Value != null){
                    value = (Long)Get_Value;
                }
                Logger.d(this, String.format("[%s] <buildUpdateSql> typeInDb=%s, nameInDb=%s, Get_Value=%s",
                        targetClass.getSimpleName(), column.typeInDb, nameInDb, value));
                update.append(nameInDb).append("=").append(value).append(",");
            } else if (fieldType == Float.class){
                Float value = 0F;
                if (Get_Value != null){
                    value = (Float)Get_Value;
                }
                Logger.d(this, String.format("[%s] <buildUpdateSql> typeInDb=%s, nameInDb=%s, Get_Value=%s",
                        targetClass.getSimpleName(), column.typeInDb, nameInDb, value));
                update.append(nameInDb).append("=").append(value).append(",");
            } else if (fieldType == String.class){
                String value = "";
                if (Get_Value != null){
                    value = (String)Get_Value;
                }
                Logger.d(this, String.format("[%s] <buildUpdateSql> typeInDb=%s, nameInDb=%s, Get_Value=%s",
                        targetClass.getSimpleName(), column.typeInDb, nameInDb, value));
                update.append(nameInDb).append("=").append("\"").append(value).append("\"").append(",");
            }
        }
        update.deleteCharAt(update.length()-1);

        Object Get_Value = Get_FieldValue(target, "id");
        if ((int)Get_Value > 0){
            StringBuilder where = new StringBuilder();
            where.append("id").append("=").append((int)Get_Value);
            update.append(" WHERE ").append(where);
        }
        update.append(";");

        return update.toString();
    }

    @Override
    public <T> List<T> onDelete(T target, SQLiteDatabase db) throws DatabaseOperationException {
        try {
            List<T> deletedTargets = retrieve(target, db, null);
            if (deletedTargets != null){
                for (T deletedTarget: deletedTargets){
                    delete(deletedTarget, db);
                }
            }
            return deletedTargets;
        } catch (NoSuchMethodException e) {
            throw new DatabaseOperationException(e);
        } catch (IllegalAccessException e){
            throw new DatabaseOperationException(e);
        } catch (InvocationTargetException e){
            throw new DatabaseOperationException(e);
        } catch (InstantiationException e){
            throw new DatabaseOperationException(e);
        }
    }

    private <T> void delete(T target, SQLiteDatabase db) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (target == null){
            return;
        }
        Class<?> targetClass = target.getClass();
        Dependency dependency = cache.getDependency(targetClass);

        deleteLocked(target, db);

        if (dependency.preConditions != null){
            for (Dependency.Condition condition: dependency.preConditions){
                Object nextTarget = Get_FieldValue(target, condition.fieldName);
                Logger.d(this, String.format("[%s] <delete> (preConditions) genericType=%s, fieldName=%s, nextTarget=%s",
                        targetClass.getSimpleName(), condition.genericType.getSimpleName(), condition.fieldName, nextTarget));
                delete(nextTarget, db);
            }
        }

        if (dependency.postConditions != null){
            for (Dependency.Condition condition: dependency.postConditions){
                List<?> Get_List = (List<?>) Get_FieldValue(target, condition.fieldName);
                Logger.d(this, String.format("[%s] <delete> (postConditions) genericType=%s, fieldName=%s, Get_List=%s",
                        targetClass.getSimpleName(), condition.genericType.getSimpleName(), condition.fieldName, Get_List));
                if (Get_List != null){
                    for (int k=0; k<Get_List.size(); k++){
                        delete(Get_List.get(k), db);
                    }
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private <T> T deleteLocked(T target, SQLiteDatabase db) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String sql = buildDeleteSql(target);
        Logger.d(this, "<deleteLocked> sql="+sql);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            db.validateSql(sql, null);
        }
        db.execSQL(sql);

        return target;
    }

    private <T> String buildDeleteSql(T target) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> targetClass = target.getClass();
        Logger.d(this, "<buildDeleteSql> target="+targetClass.getSimpleName());
        String tableName = getTableName(targetClass);
        Dependency dependency = cache.getDependency(targetClass);

        StringBuilder delete = new StringBuilder("DELETE").append(" FROM ").append(tableName);
        StringBuilder where = new StringBuilder();
        for (Dependency.Column column: dependency.columns){
            String nameInDb = column.nameInDb;
            Object Get_Value = Get_FieldValue(target, nameInDb);
            if (Get_Value == null){
                continue;
            }
            Class<?> fieldType = column.fieldType;
            if (fieldType == Integer.class){
                Logger.d(this, String.format("[%s] <buildDeleteSql> typeInDb=%s, nameInDb=%s, Get_Value=%s",
                        targetClass.getSimpleName(), column.typeInDb, nameInDb, (Integer)Get_Value));
                where.append(nameInDb).append("=").append((Integer)Get_Value).append(" AND ");
            } else if (fieldType == Long.class){
                Logger.d(this, String.format("[%s] <buildDeleteSql> typeInDb=%s, nameInDb=%s, Get_Value=%s",
                        targetClass.getSimpleName(), column.typeInDb, nameInDb, (Long)Get_Value));
                where.append(nameInDb).append("=").append((Long)Get_Value).append(" AND ");
            } else if (fieldType == Float.class){
                Logger.d(this, String.format("[%s] <buildDeleteSql> typeInDb=%s, nameInDb=%s, Get_Value=%s",
                        targetClass.getSimpleName(), column.typeInDb, nameInDb, (Float)Get_Value));
                where.append(nameInDb).append("=").append((Float)Get_Value).append(" AND ");
            } else if (fieldType == String.class){
                Logger.d(this, String.format("[%s] <buildDeleteSql> typeInDb=%s, nameInDb=%s, Get_Value=%s",
                        targetClass.getSimpleName(), column.typeInDb, nameInDb, (String)Get_Value));
                where.append(nameInDb).append("=").append("\"").append((String)Get_Value).append("\"").append(" AND ");
            }
        }
        if (where.length() > 0){
            delete.append(" WHERE ").append(where.substring(0, where.lastIndexOf(" AND ")));
        }
        delete.append(";");

        return delete.toString();
    }

    private void Set_FieldValue_Integer(Object target, String fieldName, Integer value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String Set_FieldName = SetMethodName(fieldName);
        Method Set_FieldMethod = target.getClass().getMethod(Set_FieldName, Integer.class);
        Set_FieldMethod.invoke(target, value);
    }

    private void Set_FieldValue_Long(Object target, String fieldName, Long value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String Set_FieldName = SetMethodName(fieldName);
        Method Set_FieldMethod = target.getClass().getMethod(Set_FieldName, Long.class);
        Set_FieldMethod.invoke(target, value);
    }

    private void Set_FieldValue_Float(Object target, String fieldName, Float value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String Set_FieldName = SetMethodName(fieldName);
        Method Set_FieldMethod = target.getClass().getMethod(Set_FieldName, Float.class);
        Set_FieldMethod.invoke(target, value);
    }

    private void Set_FieldValue_String(Object target, String fieldName, String value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String Set_FieldName = SetMethodName(fieldName);
        Method Set_FieldMethod = target.getClass().getMethod(Set_FieldName, String.class);
        Set_FieldMethod.invoke(target, value);
    }

    private void Set_FieldValue_Object(Object target, String fieldName, Object object, Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String Set_FieldName = SetMethodName(fieldName);
        Method Set_FieldMethod = target.getClass().getMethod(Set_FieldName, clazz);
        Set_FieldMethod.invoke(target, object);
    }

    private Object Get_FieldValue(Object target, String fieldName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String Get_FieldName = GetMethodName(fieldName);
        Method Get_FieldMethod = target.getClass().getMethod(Get_FieldName);
        return Get_FieldMethod.invoke(target);
    }

    private String getTableName(Class<?> target){
        return target.getSimpleName().toLowerCase()+"_table";
    }

    private String GetMethodName(String fieldName){
        return new StringBuilder("get")
                .append(Character.toUpperCase(fieldName.charAt(0)))
                .append(fieldName.substring(1)).toString();
    }

    private String SetMethodName(String fieldName){
        return new StringBuilder("set")
                .append(Character.toUpperCase(fieldName.charAt(0)))
                .append(fieldName.substring(1)).toString();
    }
}
