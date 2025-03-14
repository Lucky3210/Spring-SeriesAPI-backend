package com.Series.SeriesAPI.Controllers;

import com.Series.SeriesAPI.Auth.Entities.RefreshToken;
import com.Series.SeriesAPI.Auth.Entities.User;
import com.Series.SeriesAPI.Auth.Service.AuthService;
import com.Series.SeriesAPI.Auth.Service.JwtService;
import com.Series.SeriesAPI.Auth.Service.RefreshTokenService;
import com.Series.SeriesAPI.Auth.Utils.AuthResponse;
import com.Series.SeriesAPI.Auth.Utils.LoginRequest;
import com.Series.SeriesAPI.Auth.Utils.RefreshTokenRequest;
import com.Series.SeriesAPI.Auth.Utils.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")    // similar to what we defined in the security config
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest){
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){

        // verify if refresh token is valid
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());
        User user = refreshToken.getUser();     // get user associated with the refresh token

        // we need to generate access token now
        String accessToken = jwtService.generateToken(user);

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build());
    }
}
