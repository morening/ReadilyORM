package com.morening.readilyorm.exception;

/**
 * Created by morening on 2018/9/11.
 */

public class IllegalFieldException extends RuntimeException {

    public IllegalFieldException(String cause){
        super(cause);
    }
}
