package com.Series.SeriesAPI.Service;

import com.Series.SeriesAPI.DTO.SeriesDto;
import com.Series.SeriesAPI.DTO.SeriesPageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SeriesService {

    // Method to add a Series(we need the file too, because we will upload the series image)
    SeriesDto addSeries(SeriesDto seriesDto, MultipartFile file) throws IOException;

    // Method to get a particular series
    SeriesDto getSeries(Integer seriesId);

    // Method to get all series
    List<SeriesDto> getAllSeries();

    // Method to update a series
    SeriesDto updateSeries(SeriesDto seriesDto, Integer seriesId, MultipartFile file) throws IOException;

    String deleteSeries(Integer seriesId) throws IOException;

    // Pagination and Sorting
    SeriesPageResponse getAllSeriesWithPagination(Integer pageNumber, Integer pageSize);

    SeriesPageResponse getAllSeriesWithPaginationAndSorting(Integer pageNumber, Integer pageSize,
                                                            String sortBy, String direction);
    // sortBy denotes the field we want to sort the series by, and the direction is either ascending or descending order
}
