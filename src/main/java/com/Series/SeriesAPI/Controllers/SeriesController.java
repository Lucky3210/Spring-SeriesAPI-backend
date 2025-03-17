package com.Series.SeriesAPI.Controllers;


import com.Series.SeriesAPI.Auth.Entities.User;
import com.Series.SeriesAPI.Auth.Repository.UserRepository;
import com.Series.SeriesAPI.DTO.ResetPassword;
import com.Series.SeriesAPI.DTO.SeriesDto;
import com.Series.SeriesAPI.DTO.SeriesPageResponse;
import com.Series.SeriesAPI.Exceptions.EmptyFileException;
import com.Series.SeriesAPI.Service.SeriesService;
import com.Series.SeriesAPI.Utils.AppConstant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/series")
public class SeriesController {

    private final SeriesService seriesService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SeriesController(SeriesService seriesService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.seriesService = seriesService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Convert the string of seriesDto to a JSON object, we use the objectMapper which is part of the jackson library
    // using the readValue method, it takes in first the String content, and then the class for which the conversion
    // will reference as a template.
    private SeriesDto convertToSeriesDto(String seriesDtoObj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(seriesDtoObj, SeriesDto.class);

    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/addSeries")
    public ResponseEntity<SeriesDto> addSeriesHandler(@RequestPart MultipartFile file,
                                                      @RequestPart String seriesDto) throws IOException, EmptyFileException {

        // Handle empty file exception
        if(file.isEmpty()){
            throw new EmptyFileException("File is empty! Please add a file");
        }
        // notice in the argument, we pass in the seriesDto as a string and not of type SeriesDto, this is because we are sending the request
        // with the file being uploaded, if we use the SeriesDto object, we would get an error in stream.
        // But this can't be processed, therefore we convert it into JSON object from the convertToSeriesDto method.
        SeriesDto seriesDtoConvert = convertToSeriesDto(seriesDto);
        return new ResponseEntity<>(seriesService.addSeries(seriesDtoConvert, file), HttpStatus.CREATED);
    }

    @GetMapping("/{seriesId}")
    public ResponseEntity<SeriesDto> getSeriesHandler(@PathVariable Integer seriesId){
        return ResponseEntity.ok(seriesService.getSeries(seriesId));

    }

    @GetMapping("/all-series")
    public ResponseEntity<List<SeriesDto>> getAllSeriesHandler(){
        return ResponseEntity.ok(seriesService.getAllSeries());

    }

    // Update series method handler
    @PutMapping("/update/{seriesId}")
    public ResponseEntity<SeriesDto> updateSeriesHandler(@PathVariable Integer seriesId,
                                                         @RequestPart MultipartFile file,
                                                         @RequestPart String seriesDto) throws IOException {

        // we need to check if file is empty before we call the service that handles this method
        if(file.isEmpty()) file = null;
        SeriesDto seriesDtoConvert = convertToSeriesDto(seriesDto);
        return ResponseEntity.ok(seriesService.updateSeries(seriesDtoConvert, seriesId, file));
    }

    @DeleteMapping("delete/{seriesId}")
    public ResponseEntity<String> deleteSeriesHandler(@PathVariable Integer seriesId) throws IOException {
        return ResponseEntity.ok(seriesService.deleteSeries(seriesId));
    }

    // Pagination and Sorting
    @GetMapping("/allSeriesPage")
    public ResponseEntity<SeriesPageResponse> getSeriesWithPagination(
            @RequestParam(defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstant.PAGE_SIZE, required = false) Integer pageSize){

        return ResponseEntity.ok(seriesService.getAllSeriesWithPagination(pageNumber, pageSize));
    }

    @GetMapping("/allSeriesPageSort")
    public ResponseEntity<SeriesPageResponse> getSeriesWithPaginationAndSorting(
            @RequestParam(defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstant.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = AppConstant.SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConstant.SORT_DIR, required = false) String direction){

        return ResponseEntity.ok(seriesService.getAllSeriesWithPaginationAndSorting(pageNumber, pageSize, sortBy, direction));
    }

    @PostMapping("/resetPassword/{email}")
    public ResponseEntity<String> resetPassword(
            @PathVariable String email,
            @RequestBody ResetPassword resetPassword
    ){

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("You must be logged in"));

        // verify that the inputted oldPassword is actually the password in the db
        // we use the matches method(the first argument is raw password, then the second is encoded)
        boolean passwordMatch = passwordEncoder.matches(resetPassword.oldPassword(), user.getPassword());

        // if password doesn't match we return a message to the user
        if(!passwordMatch) {
            return new ResponseEntity<>("Old password is incorrect", HttpStatus.EXPECTATION_FAILED);
        }

        // if the new password and the repeated one are not the same we also return a message.
        if(!Objects.equals(resetPassword.newPassword(), resetPassword.repeatNewPassword())){
            return new ResponseEntity<>("New Password and repeat New Password Mismatch..", HttpStatus.EXPECTATION_FAILED);
        }

        // If all being equal and true, we need to encode the password and then save it to the user object gotten from the email
        String encodedNewPassword = passwordEncoder.encode(resetPassword.newPassword());
        user.setPassword(encodedNewPassword);

        // save user to User repo and return good.
        userRepository.save(user);
        return ResponseEntity.ok("Password Resetted successfully");
    }
}
