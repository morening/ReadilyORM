package com.morening.readilyorm.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by morening on 2018/9/15.
 */
public class ColumnVersionCache {

    private List<ColumnVersion> columnVersions = new ArrayList<>();

    void addColumnVersion(ColumnVersion columnVersion) {
        if (!columnVersions.contains(columnVersion)){
            columnVersions.add(columnVersion);
        }
    }

    List<ColumnVersion> getColumnVersions(){
        return columnVersions;
    }
}
