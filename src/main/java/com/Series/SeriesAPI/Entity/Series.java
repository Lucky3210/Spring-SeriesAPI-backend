package com.Series.SeriesAPI.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Series {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer seriesId;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Please provide series title")
    private String title;

    @Column(nullable = false)
    @NotBlank(message = "Please provide series director")
    private String director;

    @Column(nullable = false)
    @NotBlank(message = "Please provide series studio")
    private String studio;

    @ElementCollection      // Specifies that this field is a collection of basic types(String) or embeddable objects. So JPA will create a separate table to store the elements of the collection(referencing the series id as a foreign key).
    @CollectionTable(name = "series_cast")      // Specifies the name of the table used to store the collection elements.
    private Set<String> seriesCast;     // Set because all names should be unique

    @Column(nullable = false)
    @NotNull(message = "Enter a year")
    private Integer releaseYear;

    @Column(nullable = false)
    @NotBlank(message = "Please provide series poster")
    private String poster;      // series image to be uploaded(represented by the name of the image)

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
}
