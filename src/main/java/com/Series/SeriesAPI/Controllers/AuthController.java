package com.Series.SeriesAPI.Controllers;

import com.Series.SeriesAPI.Auth.Utils.AuthResponse;
import com.Series.SeriesAPI.Auth.Utils.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")    // similar to what we defined in the security config
public class AuthController {


    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest){

    }
}
