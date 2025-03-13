package com.Series.SeriesAPI.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.FileNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // To let SB know that this is the exception class that should be called when an exception is encountered, we add the ExceptionHandler annotation, then the name of the exception class it should reference
    // The ProblemDetail is helpful in creating custom http responses
    @ExceptionHandler(SeriesNotFoundException.class)
    public ProblemDetail handleSeriesNotFoundException(SeriesNotFoundException seriesNotFoundException){
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, seriesNotFoundException.getMessage());    // this message is the message we passed as "Series not found"
    }

    // Similarly
    @ExceptionHandler(FilesExistException.class)
    public ProblemDetail handleFileExistException(FilesExistException filesExistException){
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, filesExistException.getMessage());
    }

    @ExceptionHandler(EmptyFileException.class)
    public ProblemDetail handleEmptyFileException(EmptyFileException emptyFileException){
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, emptyFileException.getMessage());
    }
}
