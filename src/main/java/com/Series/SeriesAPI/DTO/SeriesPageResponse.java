package com.Series.SeriesAPI.DTO;

import java.util.List;

public record SeriesPageResponse(List<SeriesDto> seriesDto,
                                 Integer pageNumber,
                                 Integer pageSize,
                                 int totalElements,
                                 int totalPages,
                                 Boolean isLast) {
}

//     When we use record, there is no need for getters and setters, it already has it built-in.