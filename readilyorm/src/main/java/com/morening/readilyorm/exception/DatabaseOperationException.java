package com.morening.readilyorm.exception;

/**
 * Created by morening on 2018/9/4.
 */
public class DatabaseOperationException extends Exception {

    public DatabaseOperationException(String cause){
        super(cause);
    }

    public DatabaseOperationException(Exception e){
        super(e.getMessage(), e.getCause());
        setStackTrace(e.getStackTrace());
    }
}
