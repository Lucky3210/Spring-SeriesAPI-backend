package com.Series.SeriesAPI.DTO;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeriesDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer seriesId;

    @NotBlank(message = "Please provide series title")
    private String title;

    @NotBlank(message = "Please provide series director")
    private String director;

    @NotBlank(message = "Please provide series studio")
    private String studio;

    private Set<String> seriesCast;     // Set because all names should be unique
    private Integer releaseYear;

    @NotBlank(message = "Please provide series poster")
    private String poster;

    @NotBlank(message = "Please provide a poster url")
    private String posterUrl;

    public SeriesDto(Integer seriesId, String title, String director, Set<String> seriesCast, Integer releaseYear, String studio, String poster, String posterUrl) {
    }

    public Integer getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(Integer seriesId) {
        this.seriesId = seriesId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public Set<String> getSeriesCast() {
        return seriesCast;
    }

    public void setSeriesCast(Set<String> seriesCast) {
        this.seriesCast = seriesCast;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
}
