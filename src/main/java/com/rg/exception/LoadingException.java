package com.rg.exception;

public class LoadingException extends RuntimeException {
    public LoadingException(Exception e){
        super(e);
    }
}
