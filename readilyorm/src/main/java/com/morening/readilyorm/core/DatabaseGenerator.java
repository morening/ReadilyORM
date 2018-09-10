package com.morening.readilyorm.core;

import android.annotation.TargetApi;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Build;

import com.morening.readilyorm.DatabaseVersionChangedListener;
import com.morening.readilyorm.util.Logger;

import java.util.Set;

/**
 * Created by morening on 2018/9/1.
 */
final public class DatabaseGenerator {

    private DependencyCache cache;

    private DatabaseVersionChangedListener listener;

    public DatabaseGenerator(DependencyCache cache, DatabaseVersionChangedListener listener){
        this.cache = cache;
        this.listener = listener;
    }

    void create(SQLiteDatabase db) {
        generateTables(db);
    }

    private void generateTables(SQLiteDatabase db){
        Set<Dependency> dependencySet = cache.getDependencySet();
        for (Dependency dependency: dependencySet){
            generateTable(dependency, db);
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void generateTable(Dependency dependency, SQLiteDatabase db){
        String tableName = getTableName(dependency.type);
        StringBuilder create = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
                .append(tableName)
                .append("(id INTEGER PRIMARY KEY AUTOINCREMENT,");
        for (Dependency.Column column: dependency.columns){
            if (column.nameInDb.equals("id")){
                continue;
            }
            create.append(column.nameInDb).append(" ").append(column.typeInDb).append(" ");
            if (column.notNull){
                create.append("NOT NULL ");
            }
            create.append(",");
        }
        create.deleteCharAt(create.length()-1);
        create.append(");");
        String sql = create.toString();
        Logger.d(this, sql);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            db.validateSql(sql, null);
        }
        db.execSQL(sql);
    }

    void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (listener != null){
            listener.onUpgrade(db, oldVersion, newVersion);
        }
    }

    void downgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        if (listener != null){
            listener.onDowngrade(db, oldVersion, newVersion);
        }
    }

    private String getTableName(Class<?> type){
        return type.getSimpleName().toLowerCase()+"_table";
    }
}
