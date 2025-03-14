package com.Series.SeriesAPI.Auth.Service;

import com.Series.SeriesAPI.Auth.Entities.User;
import com.Series.SeriesAPI.Auth.Entities.UserRole;
import com.Series.SeriesAPI.Auth.Repository.UserRepository;
import com.Series.SeriesAPI.Auth.Utils.AuthResponse;
import com.Series.SeriesAPI.Auth.Utils.LoginRequest;
import com.Series.SeriesAPI.Auth.Utils.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor        // we won't need to create a constructor when we inject a class
public class AuthService {

    // we need to save our password in an encoded format, so we pass in the password encoder bean
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest registerRequest){

        var user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(UserRole.USER)      // by default every user registering will be of role user.
                .build();

        User savedUser = userRepository.save(user);

        // after creating the user object, we need to generate the JWT token(access and refresh token)
        var accessToken = jwtService.generateToken(savedUser);
        var refreshToken = refreshTokenService.createRefreshToken(savedUser.getEmail());

        // then we create an AuthResponse object(by using the builder) and return it
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

    public AuthResponse login(LoginRequest loginRequest){

        // this will authenticate the user, and return authentication object
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // once the user have been authenticated, we generate the JWT tokens, but we will need the user object, so we find the user by the email
        var user = userRepository.findByEmail(loginRequest.getEmail()).
                orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var accessToken = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(loginRequest.getEmail());

        // then we return AuthResponse
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }
}
