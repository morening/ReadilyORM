package com.morening.readilyorm.core;

import android.text.TextUtils;

/**
 * Created by morening on 2018/9/15.
 */
class ColumnVersion {

    Class<?> type;

    String nameInDb;

    String typeInDb;

    int version;

    ColumnVersion(Class<?> type, String nameInDb, String typeInDb, int version) {
        this.type = type;
        this.nameInDb = nameInDb;
        this.typeInDb = typeInDb;
        this.version = version;
    }

    @Override
    public boolean equals(Object obj) {
        ColumnVersion other = (ColumnVersion) obj;
        return this.type == other.type
                && TextUtils.equals(this.nameInDb, other.nameInDb);
    }
}
