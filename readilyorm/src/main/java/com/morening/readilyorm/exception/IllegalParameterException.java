package com.morening.readilyorm.exception;

/**
 * Created by morening on 2018/9/11.
 */

public class IllegalParameterException extends RuntimeException {

    public IllegalParameterException(String cause){
        super(cause);
    }
}
