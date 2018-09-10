package com.morening.readilyorm.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by morening on 2018/9/5.
 */
final public class PrimitiveTypes {

    private static final Class<?> FIELD_TYPE_INTEGER = Integer.class;
    private static final Class<?> FIELD_TYPE_LONG = Long.class;
    private static final Class<?> FIELD_TYPE_FLOAT = Float.class;
    private static final Class<?> FIELD_TYPE_STRING = String.class;

    public static final String COLUMN_TYPE_INTEGER = "INTEGER";
    public static final String COLUMN_TYPE_LONG = "LONG";
    public static final String COLUMN_TYPE_REAL = "REAL";
    public static final String COLUMN_TYPE_TEXT = "TEXT";

    private static final Map<Class<?>, String> primitiveTypes = new HashMap<>();
    static {
        primitiveTypes.put(FIELD_TYPE_INTEGER, COLUMN_TYPE_INTEGER);
        primitiveTypes.put(FIELD_TYPE_LONG, COLUMN_TYPE_LONG);
        primitiveTypes.put(FIELD_TYPE_FLOAT, COLUMN_TYPE_REAL);
        primitiveTypes.put(FIELD_TYPE_STRING, COLUMN_TYPE_TEXT);
    }

    public static boolean isUnsupportPrimitiveType(Class<?> type){
        return type == int.class || type == long.class || type == float.class;
    }

    public static String getColumnType(Class<?> type){
        return primitiveTypes.get(type);
    }
}
