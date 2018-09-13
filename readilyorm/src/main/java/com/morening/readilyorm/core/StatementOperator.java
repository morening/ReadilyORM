package com.morening.readilyorm.core;

import android.animation.StateListAnimator;
import android.annotation.TargetApi;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.text.TextUtils;

import com.morening.readilyorm.exception.DatabaseOperationException;
import com.morening.readilyorm.exception.IllegalParameterException;
import com.morening.readilyorm.util.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by morening on 2018/9/12.
 */

public class StatementOperator extends DefaultOperator {

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
                if (nextMap == null){
                    nextMap = new HashMap<>();
                }
                nextMap.put(condition.nameInDb, inserted_id);

                Set_FieldValue_Integer(target, condition.nameInDb, Integer.valueOf(Long.valueOf(inserted_id).intValue()));
            }
        }

        long id = insertLocked(target, db, nextMap);
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

    private <T> long insertLocked(T target, SQLiteDatabase db, Map<String, Long> nextMap) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String sql = buildInsertSql(target, nextMap);
        SQLiteStatement stms = db.compileStatement(sql);

        return stms.executeInsert();
    }

    private <T> String buildInsertSql(T target, Map<String, Long> nextMap) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class<?> targetClass = target.getClass();
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        if (nextMap != null){
            for (Map.Entry entry: nextMap.entrySet()){
                columns.append((String)entry.getKey()).append(",");
                values.append((Long)entry.getValue()).append(",");
            }
        }
        Dependency dependency = cache.getDependency(targetClass);
        for (Dependency.Column column: dependency.columns){
            Class<?> fieldType = column.fieldType;
            String nameInDb = column.nameInDb;
            if (nameInDb.equals("id")){
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
                columns.append(nameInDb).append(",");
                values.append(value).append(",");
            } else if (fieldType == Long.class){
                Long value = 0L;
                if (Get_Value != null){
                    value = (Long)Get_Value;
                } else {
                    Set_FieldValue_Long(target, nameInDb, value);
                }
                Logger.d(this, String.format("[%s] <insertLocked> typeInDb=%s, nameInDb=%s, Get_Value=%s",
                        targetClass.getSimpleName(), column.typeInDb, nameInDb, value));
                columns.append(nameInDb).append(",");
                values.append(value).append(",");
            } else if (fieldType == Float.class){
                Float value = 0F;
                if (Get_Value != null){
                    value = (Float)Get_Value;
                } else {
                    Set_FieldValue_Float(target, nameInDb, value);
                }
                Logger.d(this, String.format("[%s] <insertLocked> typeInDb=%s, nameInDb=%s, Get_Value=%s",
                        targetClass.getSimpleName(), column.typeInDb, nameInDb, value));
                columns.append(nameInDb).append(",");
                values.append(value).append(",");
            } else if (fieldType == String.class){
                String value = "";
                if (Get_Value != null){
                    value = (String)Get_Value;
                } else {
                    Set_FieldValue_String(target, nameInDb, value);
                }
                Logger.d(this, String.format("[%s] <insertLocked> typeInDb=%s, nameInDb=%s, Get_Value=%s",
                        targetClass.getSimpleName(), column.typeInDb, nameInDb, value));
                columns.append(nameInDb).append(",");
                values.append("\"").append(value).append("\"").append(",");
            }
        }
        columns.deleteCharAt(columns.length()-1);
        values.deleteCharAt(values.length()-1);

        String tableName = getTableName(targetClass);
        StringBuilder insert = new StringBuilder("INSERT INTO "+tableName).append(" ")
                .append("(").append(columns).append(")")
                .append(" VALUES ")
                .append("(").append(values).append(")").append(";");

        return insert.toString();
    }

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
        SQLiteStatement stms = db.compileStatement(sql);
        int nRow = stms.executeUpdateDelete();
        Logger.d(this, String.format("<updateLocked> target=%s, affected row number=", target.getClass().getSimpleName(), nRow));

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

    public <T> List<T> onDelete(T target, SQLiteDatabase db) throws DatabaseOperationException {
        try {
            List<T> deletedTargets = onRetrieve(target, db);
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

        SQLiteStatement stms = db.compileStatement(sql);
        int nRow = stms.executeUpdateDelete();
        Logger.d(this, String.format("<deleteLocked> target=%s, affected row number=", target.getClass().getSimpleName(), nRow));

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
                        targetClass.getSimpleName(), column.typeInDb, nameInDb, Get_Value));
                where.append(nameInDb).append("=").append(Get_Value).append(" AND ");
            } else if (fieldType == Long.class){
                Logger.d(this, String.format("[%s] <buildDeleteSql> typeInDb=%s, nameInDb=%s, Get_Value=%s",
                        targetClass.getSimpleName(), column.typeInDb, nameInDb, Get_Value));
                where.append(nameInDb).append("=").append(Get_Value).append(" AND ");
            } else if (fieldType == Float.class){
                Logger.d(this, String.format("[%s] <buildDeleteSql> typeInDb=%s, nameInDb=%s, Get_Value=%s",
                        targetClass.getSimpleName(), column.typeInDb, nameInDb, Get_Value));
                where.append(nameInDb).append("=").append(Get_Value).append(" AND ");
            } else if (fieldType == String.class){
                Logger.d(this, String.format("[%s] <buildDeleteSql> typeInDb=%s, nameInDb=%s, Get_Value=%s",
                        targetClass.getSimpleName(), column.typeInDb, nameInDb, Get_Value));
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
