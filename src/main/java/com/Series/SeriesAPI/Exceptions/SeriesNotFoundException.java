package com.Series.SeriesAPI.Exceptions;

public class SeriesNotFoundException extends RuntimeException{

    public SeriesNotFoundException(String message) {
        super(message);
    }
}
