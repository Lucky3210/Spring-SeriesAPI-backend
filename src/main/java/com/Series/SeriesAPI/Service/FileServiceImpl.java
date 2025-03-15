package com.Series.SeriesAPI.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileServiceImpl implements FileService{


    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {

        // get the name of the file
        String fileName = file.getOriginalFilename();

        // get the file path(we append the directory[what we set in the app.prop file] to the file name)
        // File.separator tells SB that the two strings should be concatenated(what we will have as the filePath is poster/fileName)
        String filePath = path + File.separator + fileName;

        // Next we would create a file object and copy the file to the path
        // This file object is responsible to ensure whether the file exist or not(we go further to check if path does not exist and create one )
        File f = new File(path);

        if(!f.exists()){
            f.mkdir();      // it will make directory of posters as defined in our app.prop file
        }

        // Next we copy/upload the file to the path, this requires an inputstream, and the actual file path
        Files.copy(file.getInputStream(), Paths.get(filePath));    // For the last option, if there is a file with the same name, it removes it and upload the current(replacing it)[StandardCopyOption.REPLACE_EXISTING] but we don't want that
        return fileName;
    }

    @Override
    public InputStream getResourceFile(String path, String fileName) throws FileNotFoundException {

        // To get the resource, we need the complete path from where we can get the file
        String filePath = path + File.separator + fileName;

        // Next we need to return an instance of fileinputstream
        return new FileInputStream(filePath);       // This will provide the file in the form of an input stream, such that it can be easily serve to the frontend
    }
}
