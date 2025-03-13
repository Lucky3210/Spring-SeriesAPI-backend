package com.Series.SeriesAPI.Repository;

import com.Series.SeriesAPI.Entity.Series;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeriesRepository extends JpaRepository<Series, Integer> {
}
