package com.morening.readilyorm.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.morening.readilyorm.util.Logger;

/**
 * Created by morening on 2018/8/31.
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private DatabaseGenerator generator;

    private DatabaseOpenHelper(Context context, String name, int version, DatabaseGenerator generator) {
        super(context, name, null, version);
        this.generator = generator;
        Logger.d(this, String.format("PackageName=%s, databaseName=%s, databaseVersion=%s", context.getPackageName(), name, version));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (generator != null){
            generator.create(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (generator != null){
            generator.upgrade(db, oldVersion, newVersion);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (generator != null){
            generator.downgrade(db, oldVersion, newVersion);
        }
    }

    public static class Builder{

        private Context context;

        private String name = null;

        private int version = 1;

        private DatabaseGenerator generator = null;

        public Builder(Context context){
            this.context = context;
        }

        public Builder name(String name){
            this.name = name;

            return this;
        }

        public Builder version(int version){
            this.version = version;

            return this;
        }

        public Builder generator(DatabaseGenerator generator){
            this.generator = generator;

            return this;
        }

        public DatabaseOpenHelper build(){

            return new DatabaseOpenHelper(context, name, version, generator);
        }
    }
}
