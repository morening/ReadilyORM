package com.morening.readilyorm.core;

import com.morening.readilyorm.exception.IllegalFieldException;

/**
 * Created by morening on 2018/9/11.
 */

public class DependencyValidator {

    public static void validate(DependencyCache cache) {
        for (Dependency dependency: cache.getDependencySet()) {
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
                    Dependency nextDependency = cache.getDependency(nextType);
                    if (!nextDependency.columns.contains(new Dependency.Column(null, nameInDb, null, false))){
                        throw new IllegalFieldException(String.format("%s has no field named '%s'", nextType.getSimpleName(), nameInDb));
                    }
                }
            }
        }
    }
}
