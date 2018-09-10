package com.morening.readilyorm.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by morening on 2018/9/5.
 */
public class DependencyCache {

    private final Map<Class<?>, Dependency> dependencyMap = new HashMap<>();
    private Set<Dependency> dependencySet = null;

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

    public Set<Class<?>> getTypeSet(){
        return dependencyMap.keySet();
    }

    public Set<Dependency> getDependencySet(){
        if (dependencySet == null){
            dependencySet = new HashSet<>();
            for (Class<?> type: dependencyMap.keySet()){
                dependencySet.add(dependencyMap.get(type));
            }
        }
        return dependencySet;
    }
}
