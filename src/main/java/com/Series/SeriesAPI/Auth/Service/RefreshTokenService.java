package com.Series.SeriesAPI.Auth.Service;

import com.Series.SeriesAPI.Auth.Entities.RefreshToken;
import com.Series.SeriesAPI.Auth.Entities.User;
import com.Series.SeriesAPI.Auth.Repository.RefreshTokenRepository;
import com.Series.SeriesAPI.Auth.Repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(String username){

        User user = userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found with email: " + username));

        // extract refresh token from user object(because in the user entity we define refresh token)
        RefreshToken refreshToken = user.getRefreshToken();

        // we check if the refresh token field is empty or not
        if(refreshToken == null){
            long refreshTokenValidity = 5 * 60* 60 * 10000;

            // if refresh token is null then we build one(before then we add the @Builder to the refresh token entity)
            refreshToken = RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .expirationTime(Instant.now().plusMillis(refreshTokenValidity))
                    .user(user)
                    .build();

            // once we get our refresh token we need to save it to the refresh token repo
            refreshTokenRepository.save(refreshToken);
        }
        // if it is not empty then we return the refresh token either ways
        return refreshToken;
    }

    // Next we want to verify the refresh token
    public RefreshToken verifyRefreshToken(String refreshToken){

        RefreshToken refToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        // after getting the refToken we want to further verify it by comparing its expiration time to the current time
        if(refToken.getExpirationTime().compareTo(Instant.now()) < 0){

            // If the current refresh token is negative(meaning it has expired) then we delete the refresh token
            refreshTokenRepository.delete(refToken);
            throw new RuntimeException("Refresh Token Expired");

        }

        // either ways if we can't find the refresh token in the refresh token repo or the refresh token is expired
        // we want to return the refresh token(meaning it is verified, if it is not verified, it will throw one of the following exception)
        return refToken;
    }
}
