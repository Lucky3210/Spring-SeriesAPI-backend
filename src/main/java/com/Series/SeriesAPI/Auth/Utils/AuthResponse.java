package com.Series.SeriesAPI.Auth.Utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {

    // for all the request(login, register, refreshTokenRequest) we want to our authentication response to be the refresh and access token
    private String accessToken;
    private String refreshToken;
}
