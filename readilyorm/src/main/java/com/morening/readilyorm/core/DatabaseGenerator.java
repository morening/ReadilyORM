package com.morening.readilyorm.core;

import android.annotation.TargetApi;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import com.morening.readilyorm.DatabaseVersionChangedListener;
import com.morening.readilyorm.util.Logger;

import java.util.Map;

/**
 * Created by morening on 2018/9/1.
 */
final public class DatabaseGenerator implements DependencyCache.Visitable {

    private DependencyCache dependencyCache;

    private DatabaseVersionChangedListener listener;

    public DatabaseGenerator(DependencyCache dependencyCache, DatabaseVersionChangedListener listener){
        this.dependencyCache = dependencyCache;
        this.listener = listener;
    }

    void create(SQLiteDatabase db) {
        generateTables(db);
    }

    private void generateTables(SQLiteDatabase db){
        dependencyCache.accept(this, db);
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

    @Override
    public void visit(Map<Class<?>, Dependency> dependencies, SQLiteDatabase db) {
        for (Dependency dependency: dependencies.values()){
            generateTable(dependency, db);
        }
    }
}
