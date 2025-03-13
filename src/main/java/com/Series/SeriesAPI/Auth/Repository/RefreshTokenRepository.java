package com.Series.SeriesAPI.Auth.Repository;

import com.Series.SeriesAPI.Auth.Entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
}
