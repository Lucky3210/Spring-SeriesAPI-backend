package com.Series.SeriesAPI.Auth.Repository;

import com.Series.SeriesAPI.Auth.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // from the appconfig bean, and even here the userRepository don't have the method of findByUsername, only findById,
    // so we create a function of finding the username(since we are logging in through the username). remember the convention
    Optional<User> findByUsername(String username);
}
