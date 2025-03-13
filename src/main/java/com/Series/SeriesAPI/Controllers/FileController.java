package com.Series.SeriesAPI.Controllers;

import com.Series.SeriesAPI.Service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    // To use the path(directory) set up in the app.prop, we need to define it here and then pass it into the handler method.
    @Value("${project.poster}")     // referencing the path in our app.prop
    private String path;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFileHandler(@RequestPart MultipartFile file) throws IOException {
        String uploadedFileName = fileService.uploadFile(path, file);
        return ResponseEntity.ok("File Uploaded : " + uploadedFileName);
    }

    @GetMapping("/{fileName}")
    public void getFileHandler(@PathVariable String fileName, HttpServletResponse response) throws IOException {

        // from the fileService, we get our inputstream, but we need to serve it well to the client
        InputStream resourceFile = fileService.getResourceFile(path, fileName);
        response.setContentType(MediaType.IMAGE_PNG_VALUE);

        // We want to set the response to a png value, but we still have it as an input stream
        // Let's convert it from input stream to output stream which will be converted to the required response
        StreamUtils.copy(resourceFile, response.getOutputStream());     // this takes in two params, the input stream as defined above, and the conversion to output stream
    }
}
