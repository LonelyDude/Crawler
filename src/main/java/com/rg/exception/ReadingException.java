package com.rg.exception;

public class ReadingException extends RuntimeException {
    public ReadingException(Exception e){
        super(e);
    }
}
