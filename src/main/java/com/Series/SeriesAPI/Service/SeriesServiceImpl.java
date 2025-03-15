package com.Series.SeriesAPI.Service;

import com.Series.SeriesAPI.DTO.SeriesDto;
import com.Series.SeriesAPI.DTO.SeriesPageResponse;
import com.Series.SeriesAPI.Entity.Series;
import com.Series.SeriesAPI.Exceptions.FilesExistException;
import com.Series.SeriesAPI.Exceptions.SeriesNotFoundException;
import com.Series.SeriesAPI.Repository.SeriesRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class SeriesServiceImpl implements SeriesService{

    private final SeriesRepository seriesRepository;
    private final FileService fileService;

    public SeriesServiceImpl(SeriesRepository seriesRepository, FileService fileService) {
        this.seriesRepository = seriesRepository;
        this.fileService = fileService;
    }

    @Value("${project.poster}")     // referencing the path in our app.prop in order to upload the file(we need path and file)
    private String path;

    @Value("${base.url}")           // referencing the base url in the app.prop
    private String baseUrl;

    @Transactional
    @Override
    public SeriesDto addSeries(SeriesDto seriesDto, MultipartFile file) throws IOException {

        // Instead of replacing existing file, we want to throw an error if the file already exist
        if(Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
            throw new FilesExistException("File with that name already exist!!!");
        }

        // To add a series, we pass in the seriesDto(series objects) and the file to be uploaded
        // Lets handle the file upload first afterward we set the value of the field poster, to the file name(uploading a file return String fileName).
        String uploadedFileName = fileService.uploadFile(path, file);
        seriesDto.setPoster(uploadedFileName);

        // Then, we will map the seriesDto to Series object in order for it to be save into the db.
        Series series = new Series();
        series.setSeriesId(seriesDto.getSeriesId());
        series.setTitle(seriesDto.getTitle());
        series.setDirector(seriesDto.getDirector());
        series.setStudio(seriesDto.getStudio());
        series.setReleaseYear(seriesDto.getReleaseYear());
        series.setSeriesCast(seriesDto.getSeriesCast());
        series.setPoster(seriesDto.getPoster());

        // Series savedSeries = seriesRepository.save(series);
        seriesRepository.save(series);

        /*
         After it is being saved into the db, we want to return the seriesDto as a response object, but in the seriesDto we have a field posterUrl which is an extra field
         therefore we want to generate the posterUrl, such that when a user clicks on the url, it shows the image.
         The posterUrl is synonymous to the url for getting file(in the FileController) - localhost:8080/file/{filename}
        */
        String posterUrl = baseUrl + "/file/" + uploadedFileName;

        // Finally, we map the series object to seriesdto object and return it.
        SeriesDto responseDto = new SeriesDto();
        responseDto.setSeriesId(series.getSeriesId());
        responseDto.setTitle(series.getTitle());
        responseDto.setDirector(series.getDirector());
        responseDto.setSeriesCast(series.getSeriesCast());
        responseDto.setReleaseYear(series.getReleaseYear());
        responseDto.setStudio(series.getStudio());
        responseDto.setPoster(series.getPoster());
        responseDto.setPosterUrl(posterUrl);
        return responseDto;
    }

    @Override
    public SeriesDto getSeries(Integer seriesId) {

        // get series by id from the db
        Series series = seriesRepository.findById(seriesId).orElseThrow(() -> new SeriesNotFoundException("Series with id" + seriesId + " not found"));


        // get the posterUrl from the poster(series.getPoster() returns the poster/image name, from there we can formulate the posterUrl)
        String posterUrl = baseUrl + "/file/" + series.getPoster();

        // add the posterUrl and return it as a dto
        SeriesDto seriesDto = new SeriesDto();
        seriesDto.setSeriesId(series.getSeriesId());
        seriesDto.setTitle(series.getTitle());
        seriesDto.setDirector(series.getDirector());
        seriesDto.setSeriesCast(series.getSeriesCast());
        seriesDto.setReleaseYear(series.getReleaseYear());
        seriesDto.setStudio(series.getStudio());
        seriesDto.setPoster(series.getPoster());
        seriesDto.setPosterUrl(posterUrl);

        return seriesDto;
    }

    @Override
    public List<SeriesDto> getAllSeries() {

        // fetch all series from db
        List<Series> series = seriesRepository.findAll();

        List<SeriesDto> seriesDtos = new ArrayList<>();     // since we are going to return a List we create an instance of it

        // we iterate through the list, in order to generate posterUrl for each series obj, then we map it to a seriesDto obj
        for(Series seriesObj : series){

            // generate posterUrl
            String posterUrl = baseUrl + "/file/" + seriesObj.getPoster();

            // map each series object to seriesDto in order to return it
            SeriesDto seriesDto = new SeriesDto();
            seriesDto.setSeriesId(seriesObj.getSeriesId());
            seriesDto.setTitle(seriesObj.getTitle());
            seriesDto.setDirector(seriesObj.getDirector());
            seriesDto.setSeriesCast(seriesObj.getSeriesCast());
            seriesDto.setReleaseYear(seriesObj.getReleaseYear());
            seriesDto.setStudio(seriesObj.getStudio());
            seriesDto.setPoster(seriesObj.getPoster());
            seriesDto.setPosterUrl(posterUrl);

            seriesDtos.add(seriesDto);      // add the seriesDto to the list and return it
        }
        return seriesDtos;
    }

    @Override
    public SeriesDto updateSeries(SeriesDto seriesDto, Integer seriesId, MultipartFile file) throws IOException {

        // first, we check if the seriesObj exist
        Series series = seriesRepository.findById(seriesId).orElseThrow(() -> new SeriesNotFoundException("Series with id" + seriesId + " not found"));

        // check if file to update already exist(if file is null then nothing to update, but if file is not null
        // then it deletes the existing file associated with the record and upload the new file)
        String fileName = series.getPoster();
        if(file != null){
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path, file);
        }

        // next we set seriesDto poster value according to the above setup
        seriesDto.setPoster(fileName);      // whether the file is null or not null we set it to the fileName

        // map it to series obj, in order to save the series obj to the db
        Series seriesObj = new Series();
        seriesObj.setSeriesId(series.getSeriesId());    // notice we set the id to what we passed in initially
        seriesObj.setTitle(seriesDto.getTitle());
        seriesObj.setDirector(seriesDto.getDirector());
        seriesObj.setStudio(seriesDto.getStudio());
        seriesObj.setReleaseYear(seriesDto.getReleaseYear());
        seriesObj.setSeriesCast(seriesDto.getSeriesCast());
        seriesObj.setPoster(seriesDto.getPoster());

        seriesRepository.save(seriesObj);

        // generate posterUrl and map to seriesDto, then return seriesDto
        String posterUrl = baseUrl + "/file/" + seriesObj.getPoster();

        SeriesDto seriesDtos = new SeriesDto();
        seriesDtos.setSeriesId(seriesObj.getSeriesId());
        seriesDtos.setTitle(seriesObj.getTitle());
        seriesDtos.setDirector(seriesObj.getDirector());
        seriesDtos.setSeriesCast(seriesObj.getSeriesCast());
        seriesDtos.setReleaseYear(seriesObj.getReleaseYear());
        seriesDtos.setStudio(seriesObj.getStudio());
        seriesDtos.setPoster(seriesObj.getPoster());
        seriesDtos.setPosterUrl(posterUrl);

        return seriesDtos;
    }

    @Override
    public String deleteSeries(Integer seriesId) throws IOException {

        // check if series with given ID exist in db
        Series series = seriesRepository.findById(seriesId).orElseThrow(() -> new SeriesNotFoundException("Series with id " + seriesId + " not found"));

        // delete the file associated with the id
        Files.deleteIfExists(Paths.get(path + File.separator + series.getPoster()));

        // delete the series obj
        seriesRepository.delete(series);
        return "Series deleted with ID: "+ seriesId;
    }

    @Override
    public SeriesPageResponse getAllSeriesWithPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);       // returns a pageable object that specifies the pageNumber and pageSize

        // we need this pageable object because when we call the findAll method as below, it takes in an argument of pageable
        Page<Series> seriesPages = seriesRepository.findAll(pageable);     // whatever records or field that are coming from the database will be paginated wrt what was passed as the pageNumber and pageSize constant
        List<Series> series = seriesPages.getContent();     // we get all the paginated series and return it as a list

        // Next we want to return SeriesPageResponse and from the dto package, SeriesPageResponse takes in SeriesDto,
        // therefore we map series to seriesDto by looping through the List of series as returned above.
        List<SeriesDto> seriesDtos = new ArrayList<>();
        for (Series seriesObj : series) {

            // generate posterUrl
            String posterUrl = baseUrl + "/file/" + seriesObj.getPoster();

            // map each series object to seriesDto in order to return it
            SeriesDto seriesDto = new SeriesDto();
            seriesDto.setSeriesId(seriesObj.getSeriesId());
            seriesDto.setTitle(seriesObj.getTitle());
            seriesDto.setDirector(seriesObj.getDirector());
            seriesDto.setSeriesCast(seriesObj.getSeriesCast());
            seriesDto.setReleaseYear(seriesObj.getReleaseYear());
            seriesDto.setStudio(seriesObj.getStudio());
            seriesDto.setPoster(seriesObj.getPoster());
            seriesDto.setPosterUrl(posterUrl);

            seriesDtos.add(seriesDto);
        }
        return new SeriesPageResponse(seriesDtos, pageNumber,
                pageSize, (int) seriesPages.getTotalElements(),
                seriesPages.getTotalPages(), seriesPages.isLast());
    }

        @Override
    public SeriesPageResponse getAllSeriesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String direction) {

        // we are sorting by seriesId(sortBy) in ascending order(direction)
            Sort sort = direction.equalsIgnoreCase("asc")?
                    Sort.by(sortBy).ascending():
                    Sort.by(sortBy).descending();

            // Similar implementation as that of only pagination
            Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);       // returns a pageable object that specifies the pageNumber and pageSize. We add an extra parameter.

            // we need this pageable object because when we call the findAll method as below, it takes in an argument of pageable
            Page<Series> seriesPages = seriesRepository.findAll(pageable);     // whatever records or field that are coming from the database will be paginated
            List<Series> series = seriesPages.getContent();     // returns the list of all the series

            // Next we want to return SeriesPageResponse and from the dto package, SeriesPageResponse takes in SeriesDto,
            // therefore we map series to seriesDto by looping through the List of series as returned above.
            List<SeriesDto> seriesDtos = new ArrayList<>();
            for (Series seriesObj : series) {

                // generate posterUrl
                String posterUrl = baseUrl + "/file/" + seriesObj.getPoster();

                // map each series object to seriesDto in order to return it
                SeriesDto seriesDto = new SeriesDto();
                seriesDto.setSeriesId(seriesObj.getSeriesId());
                seriesDto.setTitle(seriesObj.getTitle());
                seriesDto.setDirector(seriesObj.getDirector());
                seriesDto.setSeriesCast(seriesObj.getSeriesCast());
                seriesDto.setReleaseYear(seriesObj.getReleaseYear());
                seriesDto.setStudio(seriesObj.getStudio());
                seriesDto.setPoster(seriesObj.getPoster());
                seriesDto.setPosterUrl(posterUrl);

                seriesDtos.add(seriesDto);
            }
            return new SeriesPageResponse(seriesDtos, pageNumber,
                    pageSize, (int) seriesPages.getTotalElements(),
                    seriesPages.getTotalPages(), seriesPages.isLast());
    }
}
