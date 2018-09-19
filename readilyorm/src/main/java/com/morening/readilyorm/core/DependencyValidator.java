package com.morening.readilyorm.core;

import android.database.sqlite.SQLiteDatabase;

import com.morening.readilyorm.exception.IllegalFieldException;

import java.util.Map;

/**
 * Created by morening on 2018/9/11.
 */

public class DependencyValidator implements DependencyCache.Visitable {

    public void validate(DependencyCache cache) {
        cache.accept(this, null);
    }

    @Override
    public void visit(Map<Class<?>, Dependency> dependencies, SQLiteDatabase db) {
        for (Dependency dependency: dependencies.values()) {
            String typeName = dependency.type.getSimpleName();
            if (dependency.preConditions != null){
                for (Dependency.Condition condition: dependency.preConditions){
                    String nameInDb = condition.nameInDb;
                    if (!dependency.columns.contains(new Dependency.Column(null, nameInDb, null, false))){
                        throw new IllegalFieldException(String.format("%s has no field named '%s'", typeName, nameInDb));
                    }
                }
            }
            if (dependency.postConditions != null){
                for (Dependency.Condition condition: dependency.postConditions){
                    String nameInDb = condition.nameInDb;
                    Class<?> nextType = condition.genericType;
                    Dependency nextDependency = dependencies.get(nextType);
                    if (!nextDependency.columns.contains(new Dependency.Column(null, nameInDb, null, false))){
                        throw new IllegalFieldException(String.format("%s has no field named '%s'", nextType.getSimpleName(), nameInDb));
                    }
                }
            }
        }
    }
}
