package com.Series.SeriesAPI.Exceptions;

public class FilesExistException extends RuntimeException{

    public FilesExistException(String message) {
        super(message);
    }
}
