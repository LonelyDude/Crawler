package com.rg.exception;

import java.io.IOException;

public class IOConnectionException extends RuntimeException {

    public IOConnectionException(IOException e) {
        super(e);
    }

}
