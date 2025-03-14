package com.Series.SeriesAPI.Auth.Service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
public class AuthFilterService extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthFilterService(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // we want to get the access token, and we can do that from the http request headers(in this case Authorization)
        final String authHeader = request.getHeader("Authorization");
        String jwt, username;

        // we then check if the auth header has a token or not
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;     // if there is no authHeader, we simply return nothing
        }

        // if the token is present then we extract it
        jwt = authHeader.substring(7);      // the token will start from the 7th character after Bearer(Bearer is 6)

        // extract username from jwt
        username = jwtService.extractUsername(jwt);

        // ensure username is not null and the user is not authenticated
        if((username != null) && SecurityContextHolder.getContext().getAuthentication() == null){

            // if the username is valid but the user isn't authenticated, we need to load the user details(email) and valid the token
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if(jwtService.isTokenValid(jwt, userDetails)){
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities() /* User roles */);

                // build the authentication object along with the access token
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // since the context from SecurityContextHolder was null, we need to set it.
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                // after we set the SecurityContextHolder authentication to this authentication token it means the user is authenticated
            }
        }

        filterChain.doFilter(request, response);
    }
}
