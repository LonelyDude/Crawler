package com.rg.exception;

public class CrawlerInitializationException extends RuntimeException {
    public CrawlerInitializationException(Exception e){
        super(e);
    }
}
