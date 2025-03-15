package com.Series.SeriesAPI.Auth.Utils;

import lombok.Data;

@Data
public class RefreshTokenRequest {

    // this class is used when we want to generate a new access token
    private String refreshToken;
}
