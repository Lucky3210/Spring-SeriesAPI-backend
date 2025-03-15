package com.Series.SeriesAPI.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface  FileService {

    // path: the path/directory for which we are uploading the file
    // MultipartFile: is used in springboot for files when we are dealing in a restful way
    // The method upload/save the poster image(file) into the server file system
    String uploadFile(String path, MultipartFile file) throws IOException;

    // we need to serve the file, but we cannot directly get the file and serve it, we cannot directly fetch the file from
    //  the server and serve it to the frontend, we need to convert the file to a stream(filestream)
    // the method is responsible for retrieving stored files in the server file system, it takes in the file path
    // and the name of the file, and returns the file in the form of inputstream and generate the url, if path is incorrect or name is invalid it throws an exception
    InputStream getResourceFile(String path, String name) throws FileNotFoundException;
}
