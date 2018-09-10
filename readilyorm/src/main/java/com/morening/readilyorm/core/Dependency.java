package com.morening.readilyorm.core;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by morening on 2018/9/2.
 */
class Dependency {

    List<Condition> preConditions = null;
    List<Condition> postConditions = null;
    final List<Column> columns = new ArrayList<>();
    Class<?> type;

    Dependency(Class<?> type){
        this.type = type;
    }

    static class Column{
        String nameInDb;
        String typeInDb;
        Class<?> fieldType;
        boolean notNull;

        Column(Class<?> fieldType, String nameInDb, String typeInDb, boolean notNull){
            this.fieldType = fieldType;
            this.nameInDb = nameInDb;
            this.typeInDb = typeInDb;
            this.notNull = notNull;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Column)){
                return false;
            }
            Column other = (Column) obj;
            return TextUtils.equals(this.nameInDb, other.nameInDb);
        }
    }

    static class Condition{
        Class<?> genericType;
        String fieldName;
        String nameInDb;

        Condition(Class<?> genericType, String fieldName, String nameInDb){
            this.genericType = genericType;
            this.fieldName = fieldName;
            this.nameInDb = nameInDb;
        }
    }
}
