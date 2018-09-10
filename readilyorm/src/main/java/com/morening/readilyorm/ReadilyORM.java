package com.morening.readilyorm;

import android.content.Context;
import android.support.annotation.NonNull;

import com.morening.readilyorm.core.DatabaseGenerator;
import com.morening.readilyorm.core.DatabaseOpenHelper;
import com.morening.readilyorm.core.DatabaseOperationHelper;
import com.morening.readilyorm.core.DefaultOperator;
import com.morening.readilyorm.core.DependencyCache;
import com.morening.readilyorm.core.DependencyResolver;
import com.morening.readilyorm.exception.DatabaseOperationException;

import java.util.List;

/**
 * ReadilyORM is a easy, convenient, lightweight ORM.
 * All operations can rollback if failed when processing.
 * ReadilyORM could support from api 16(Android JellyBean) to api 28(Android Pie), it's that almost meet 99% device developing requirements.
 */

/**
 * Created by morening on 2018/9/1.
 */
final public class ReadilyORM {

    private DatabaseOperationHelper operationHelper;

    private ReadilyORM(Context context, String name, int version, Class<?> type, Operator operator, DatabaseVersionChangedListener listener){
        DependencyCache cache = new DependencyCache();
        DependencyResolver resolver = new DependencyResolver(cache);
        try {
            resolver.resolve(type);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        DatabaseGenerator generator = new DatabaseGenerator(cache, listener);
        DatabaseOpenHelper openHelper = new DatabaseOpenHelper.Builder(context)
                .name(name).version(version).generator(generator).build();
        Operator opt = operator;
        if (operator == null){
            opt = new DefaultOperator(cache);
        }
        operationHelper = new DatabaseOperationHelper(openHelper, opt);
    }

    /**
     * Return a list of inserted {@link T} objects
     * There may be only one object or more depends on parameter
     *
     * @param ts One or more T objects
     *           (Object's field could be null except having {@link com.morening.readilyorm.annotations.NotNull},
     *           and if the field value is null, it will be 0 for Integer/Long/Float and null for String by default)
     * @param <T> the type of the inserting object
     * @return A list of inserted objects
     * @throws DatabaseOperationException
     */
    public <T> List<T> insert(@NonNull T... ts) throws DatabaseOperationException {
        if (ts == null || ts.length == 0){
            throw new DatabaseOperationException("Inserting beans should not be null!");
        }
        return operationHelper.insertInTx(ts);
    }

    /**
     * Return a list of inserted {@link T} objects
     * There may be only one object or more depends on parameter
     *
     * @param ts A list of T objects
     *           (Object's field could be null except having {@link com.morening.readilyorm.annotations.NotNull},
     *           and if the field value is null, it will be 0 for Integer/Long/Float and null for String by default)
     * @param <T> the type of the inserting object
     * @return A list of inserted objects
     * @throws DatabaseOperationException
     */
    public <T> List<T> insert(@NonNull List<T> ts) throws DatabaseOperationException{
        if (ts == null || ts.size() == 0){
            throw new DatabaseOperationException("Inserting beans should not be null!");
        }
        return operationHelper.insertInTx(ts);
    }

    /**
     * Return a list of retrieved {@link T} objects
     * There may be only one object or more depends on parameter
     *
     * @param ts ts One or more T objects (Retrieved with any field value)
     * @param <T> the type of the retrieving object
     * @return A list of retrieved objects
     * @throws DatabaseOperationException
     */
    public <T> List<T> retrieve(@NonNull T... ts) throws DatabaseOperationException{
        if (ts == null || ts.length == 0){
            throw new DatabaseOperationException("Retrieving beans should not be null!");
        }
        return operationHelper.retrieveInTx(ts);
    }

    /**
     * Return a list of retrieved {@link T} objects
     * There may be only one object or more depends on parameter
     *
     * @param ts ts A list of T objects (Retrieved with any field value)
     * @param <T> the type of the retrieving object
     * @return A list of retrieved objects
     * @throws DatabaseOperationException
     */
    public <T> List<T> retrieve(@NonNull List<T> ts) throws DatabaseOperationException{
        if (ts == null || ts.size() == 0){
            throw new DatabaseOperationException("Retrieving beans should not be null!");
        }
        return operationHelper.retrieveInTx(ts);
    }

    /**
     * Return a list of updated {@link T} objects
     * There may be only one object or more depends on parameter
     *
     * @param ts One or more T objects
     *           (The object will be updated with id, so all field could be null except id)
     * @param <T> the type of the updating object
     * @return A list of updated objects
     * @throws DatabaseOperationException
     */
    public <T> List<T> update(@NonNull T... ts) throws DatabaseOperationException{
        if (ts == null || ts.length == 0){
            throw new DatabaseOperationException("Updating beans should not be null!");
        }
        return operationHelper.updateInTx(ts);
    }

    /**
     * Return a list of updated {@link T} objects
     * There may be only one object or more depends on parameter
     *
     * @param ts A list of T objects
     *           (The object will be updated with id, so all field could be null except id)
     * @param <T> the type of the updating object
     * @return A list of updated objects
     * @throws DatabaseOperationException
     */
    public <T> List<T> update(@NonNull List<T> ts) throws DatabaseOperationException{
        if (ts == null || ts.size() == 0){
            throw new DatabaseOperationException("Updating beans should not be null!");
        }
        return operationHelper.updateInTx(ts);
    }

    /**
     * Return a list of deleted {@link T} objects
     * There may be only one object or more depends on parameter
     *
     * @param ts One or more T objects
     *           (The object will be deleted with field value;
     *           if the object with null field, all objects saved in database will be removed)
     * @param <T> the type of the deleting object
     * @return A list of deleted objects
     * @throws DatabaseOperationException
     */
    public <T> List<T> delete(@NonNull T... ts) throws DatabaseOperationException{
        if (ts == null || ts.length == 0){
            throw new DatabaseOperationException("Deleting beans should not be null!");
        }
        return operationHelper.deleteInTx(ts);
    }

    /**
     * Return a list of deleted {@link T} objects
     * There may be only one object or more depends on parameter
     *
     * @param ts A list of T objects
     *           (The object will be deleted with field value;
     *           if the object with null field, all objects saved in database will be removed)
     * @param <T> the type of the deleting object
     * @return A list of deleted objects
     * @throws DatabaseOperationException
     */
    public <T> List<T> delete(@NonNull List<T> ts) throws DatabaseOperationException{
        if (ts == null || ts.size() == 0){
            throw new DatabaseOperationException("Deleting beans should not be null!");
        }
        return operationHelper.deleteInTx(ts);
    }

    public static class Builder{

        private Context context;

        private String name = null;

        private int version = 1;

        private Class<?> type = null;

        private Operator operator = null;

        private DatabaseVersionChangedListener listener = null;

        public Builder(Context context){
            this.context = context;
        }

        /**
         * Set the database's name
         *
         * @param name database's name
         * @return Builder
         */
        public Builder name(String name){
            this.name = name;

            return this;
        }

        /**
         * Set the database's version
         *
         * @param version database's version
         * @return Builder
         */
        public Builder version(int version){
            this.version = version;

            return this;
        }

        /**
         * Set the database's type
         *
         * @param type database's type
         * @return Builder
         */
        public Builder type(Class<?> type){
            this.type = type;

            return this;
        }

        /**
         * Set database's operator {@link Operator}
         * If null, ReadilyORM will set a default operator
         *
         * @param operator
         * @return Builder
         */
        public Builder operator(Operator operator){
            this.operator = operator;

            return this;
        }

        /**
         * Set {@link DatabaseVersionChangedListener} to handle database's version change
         *
         * @param listener
         * @return Builder
         */
        public Builder listener(DatabaseVersionChangedListener listener){
            this.listener = listener;

            return this;
        }

        /**
         * Build ReadilyORM instance
         *
         * @return ReadilyORM
         */
        public ReadilyORM build(){

            return new ReadilyORM(context, name, version, type, operator, listener);
        }
    }
}
