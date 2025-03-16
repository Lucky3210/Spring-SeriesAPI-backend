package com.Series.SeriesAPI.Auth.Repository;

import com.Series.SeriesAPI.Auth.Entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // from the appconfig bean, and even here the userRepository don't have the method of findByUsername, only findById,
    // so we create a function of finding the email(since we are logging in through the email). remember the convention
    Optional<User> findByEmail(String username);

    @Transactional
    @Modifying
    @Query("update User u set u.password = ?2 where u.email = ?1")
    void updatePassword(String email, String password);
}
