package com.morening.readilyorm.core;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by morening on 2018/9/5.
 */
public class DependencyCache {

    private final Map<Class<?>, Dependency> dependencyMap = new HashMap<>();

    public Dependency getDependency(Class<?> type){
        Dependency dependency = dependencyMap.get(type);
        if (dependency == null){
            dependency = new Dependency(type);
            dependencyMap.put(type, dependency);
        }
        return dependency;
    }

    public void addColumn(Class<?> type, Dependency.Column column){
        Dependency dependency = getDependency(type);
        if (!dependency.columns.contains(column)){
            dependency.columns.add(column);
        }
    }

    public void addPreCondition(Class<?> type, Dependency.Condition condition){
        Dependency dependency = getDependency(type);
        if (dependency.preConditions == null){
            dependency.preConditions = new ArrayList<>();
        }
        dependency.preConditions.add(condition);
    }

    public void addPostCondition(Class<?> type, Dependency.Condition condition){
        Dependency dependency = getDependency(type);
        if (dependency.postConditions == null){
            dependency.postConditions = new ArrayList<>();
        }
        dependency.postConditions.add(condition);
    }

    public void accept(Visitable visitor, SQLiteDatabase db){
        visitor.visit(dependencyMap, db);
    }

    interface Visitable {

        void visit(Map<Class<?>, Dependency> dependencies, SQLiteDatabase db);
    }
}
