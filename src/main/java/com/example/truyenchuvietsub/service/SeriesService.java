package com.example.truyenchuvietsub.service;

import com.example.truyenchuvietsub.SlugGenerator;
import com.example.truyenchuvietsub.dto.series.HotSeries;
import com.example.truyenchuvietsub.dto.SeriesDTO;
import com.example.truyenchuvietsub.dto.ReviewDTO;
import com.example.truyenchuvietsub.dto.UserDTO;
import com.example.truyenchuvietsub.model.*;
import com.example.truyenchuvietsub.model.enums.EnumGenre;
import com.example.truyenchuvietsub.repository.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class SeriesService {
    @Autowired
    private SeriesRepository seriesRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SeriesStateRepository seriesStateRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private UserManager userManager;

    public List<SeriesDTO> getOwnedSeries(int page, int size, String username) {
        User user = (User) userManager.loadUserByUsername(username);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        Query query = new Query(
                Criteria.where("author._id").is(user.getId())
        ).with(pageable);

        return mongoTemplate.find(query, Series.class).stream().map(
                Series -> SeriesDTO.builder()
                        .id(Series.getId())
                        .title(Series.getTitle())
                        .slug(Series.getSlug())
                        .totalChapter(chapterService.countChaptersBySeries_Slug(Series.getSlug()))
                        .genres(Genre.toGenreStrSet(Series.getGenres()))
                        .cover(Series.getCover())
                        .author(UserDTO.from(Series.getAuthor()))
                        .updatedDate(Series.getUpdatedAt())
                        .totalView(countTotalViewBySeriesId(Series.getId()))
                        .totalLike(countTotalLikeBySeriesId(Series.getId()))
                        .reviews(getReviewsBySeriesId(Series.getId()))
                        .build()
        ).toList();
    }

    public int countOwnedSeries(String username) {
        User user = (User) userManager.loadUserByUsername(username);
        Query query = new Query(
                Criteria.where("author._id").is(user.getId())
        );
        return (int) mongoTemplate.count(query, Series.class);
    }

    public Optional<SeriesDTO> getSeriesBySlug(String slug) {
        Series series = seriesRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Series not found"));

        // +1 view count
        int view = series.getView();
        series.setView(view + 1);
        seriesRepository.save(series);
        return seriesRepository.findBySlug(slug).map(
                Series -> SeriesDTO.from(
                        Series,
                        chapterService.countChaptersBySeries_Slug(Series.getSlug()),
                        countTotalViewBySeriesId(Series.getId()),
                        countTotalLikeBySeriesId(Series.getId()),
                        getReviewsBySeriesId(Series.getId())
                )
        );
    }

    public List<SeriesDTO> getTopSeriesWithHighestLikes(int seriesCount) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup("chapters", "chapter.$id", "_id", "chapter"),
                Aggregation.unwind("chapter"),
//                Aggregation.group("chapter.series.$id")
                Aggregation.group("chapter.series.$id")
                        .count().as("totalLikes"),
                Aggregation.sort(Sort.Direction.DESC, "totalLikes"),
                Aggregation.limit(seriesCount)
        );
        AggregationResults<Document> result = mongoTemplate.aggregate(aggregation, "likes", Document.class);
        List<Document> topSeriesWithHighestLikes = result.getMappedResults();
        List<SeriesDTO> seriesDTOS = new ArrayList<>();
        for (Document document : topSeriesWithHighestLikes) {
            Series series = mongoTemplate.findById(document.get("_id"), Series.class);
            assert series != null;
            seriesDTOS.add(SeriesDTO.from(
                    series,
                    0,
                    0,
                    0,
                    null
            ));
        }
        return seriesDTOS;
    }


    public List<SeriesDTO> getTop3SeriesByLikeCount() {
        GroupOperation groupByChapter = Aggregation.group("chapter").count().as("likeCount");
        GroupOperation groupBySeries = Aggregation.group("series").sum("likeCount").as("totalLikes");
        SortOperation sortByLikesDesc = Aggregation.sort(Sort.Direction.DESC, "totalLikes");
        LimitOperation limitTo3 = Aggregation.limit(3);

        Aggregation aggregation = Aggregation.newAggregation(
                groupByChapter,
//                groupBySeries,
                sortByLikesDesc,
                limitTo3
        );

        AggregationResults<HotSeries> aggregationResults = mongoTemplate.aggregate(
                aggregation, "likes", HotSeries.class);

        // Get the top 3 series IDs with highest like counts
        List<HotSeries> top3SeriesLikes = aggregationResults.getMappedResults();
        System.out.println(top3SeriesLikes);
        // sout length
        System.out.println(top3SeriesLikes.size());

        // Fetch Series objects based on the IDs obtained
        List<String> top3SeriesIds = top3SeriesLikes.stream()
                .map(HotSeries::getId)
                .collect(Collectors.toList());
        System.out.println(top3SeriesIds);
        System.out.println();

        List<Series> topHotSeries = mongoTemplate.find(
                Query.query(Criteria.where("_id").in(top3SeriesIds)),
                Series.class
        );
        System.out.println(topHotSeries);

        return null;
    }

    public List<SeriesDTO> getTopRecentCreatedSeries(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Query query = new Query().with(pageable);
        return mongoTemplate.find(query, Series.class).stream().map(
                Series -> SeriesDTO.builder()
                        .id(Series.getId())
                        .title(Series.getTitle())
                        .slug(Series.getSlug())
                        .totalChapter(chapterService.countChaptersBySeries_Slug(Series.getSlug()))
                        .genres(Genre.toGenreStrSet(Series.getGenres()))
                        .cover(Series.getCover())
                        .author(UserDTO.from(Series.getAuthor()))
                        .updatedDate(Series.getUpdatedAt())
                        .build()
        ).toList();

    }

    public SeriesDTO createSeries(SeriesDTO createSeriesRequest) {
        // check if slug is already existed
        String slug = SlugGenerator.toSlug(createSeriesRequest.getTitle());
        if (seriesRepository.findBySlug(slug).isPresent()) {
            throw new RuntimeException("Slug is already existed");
        }
        // get author
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User author = userRepository.findByUsername(auth.getName()).orElseThrow(() -> new RuntimeException("User not found"));

        Series series = new Series(
                createSeriesRequest.getTitle(),
                createSeriesRequest.getDescription(),
                author,
                createSeriesRequest.getCover(),
                seriesStateRepository.findByName("ONGOING").orElseThrow(() -> new RuntimeException("State not found"))
        );

        Set<String> genresStr = createSeriesRequest.getGenres();
        if (genresStr != null) {
            Set<Genre> genres = Genre.toGenreSet(genresStr, genreRepository);
            series.setGenres(genres);
        }
        seriesRepository.save(series);
        return SeriesDTO.from(series, 0, 0, 0, null);
    }


    public void deleteSeriesById(String id, Authentication authentication) {

        Series series = seriesRepository.findById(id).orElseThrow(() -> new RuntimeException("Series not found"));
        // check if user is the author of this Series
        if (!series.getAuthor().getUsername().equals(authentication.getName())) {
            throw new RuntimeException("You are not the author of this Series");
        }
        // get all chapters of this Series
        chapterService.deleteAllBySeriesId(id);
        seriesRepository.deleteById(id);
    }

    public List<SeriesDTO> getSeriesByGenre(String genre) {
        Genre genreObj = genreRepository.findByName(EnumGenre.valueOf(genre.toUpperCase())).orElseThrow(() -> new RuntimeException("Genre not found"));
        return seriesRepository.findSeriesByGenres(genreObj).stream().map(
                Series -> SeriesDTO.from(
                        Series,
                        chapterService.countChaptersBySeries_Slug(Series.getSlug()),
                        countTotalViewBySeriesId(Series.getId()),
                        countTotalLikeBySeriesId(Series.getId()),
                        getReviewsBySeriesId(Series.getId())
                )
        ).toList();
    }
    public List<SeriesDTO> getTopRecentUpdatedSeries(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        Query query = new Query().with(pageable);
        return mongoTemplate.find(query, Series.class).stream().map(
                Series -> SeriesDTO.builder()
                        .id(Series.getId())
                        .title(Series.getTitle())
                        .slug(Series.getSlug())
                        .totalChapter(chapterService.countChaptersBySeries_Slug(Series.getSlug()))
                        .genres(Genre.toGenreStrSet(Series.getGenres()))
                        .cover(Series.getCover())
                        .author(UserDTO.from(Series.getAuthor()))
                        .updatedDate(Series.getUpdatedAt())
                        .build()
        ).toList();
    }

    public Series updateSeriesBySlug(String Serieslug, SeriesDTO createSeriesRequest) {
        Series series = seriesRepository.findBySlug(Serieslug).orElseThrow(() -> new RuntimeException("Series not found"));
        if (!series.getAuthor().getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
            throw new RuntimeException("You are not the author of this Series");
        }
        if (createSeriesRequest.getTitle() != null) {
            series.setTitle(createSeriesRequest.getTitle());
        }
        if (createSeriesRequest.getDescription() != null) {
            series.setDescription(createSeriesRequest.getDescription());
        }
        if (createSeriesRequest.getCover() != null) {
            series.setCover(createSeriesRequest.getCover());
        }
        if (createSeriesRequest.getGenres() != null) {
            Set<String> genresStr = createSeriesRequest.getGenres();
            Set<Genre> genres = Genre.toGenreSet(genresStr, genreRepository);
            series.setGenres(genres);
        }
        seriesRepository.save(series);
        return series;
    }


    public List<SeriesDTO> searchBySlug(String keyword) {
        String slug = SlugGenerator.toSlug(keyword);
        List<Series> seriesList = seriesRepository.findAllBySlugContaining(slug);
        List<SeriesDTO> seriesDTOS = new ArrayList<>();
        for (Series series : seriesList) {
            seriesDTOS.add(SeriesDTO.from(
                    series,
                    countTotalChapterBySeriesId(series.getId()),
                    countTotalViewBySeriesId(series.getId()),
                    countTotalLikeBySeriesId(series.getId()), getReviewsBySeriesId(series.getId()))
            );
        }
        return seriesDTOS;
    }

    public int countTotalViewBySeriesId(String SeriesId) {
        AtomicInteger totalViewCount = new AtomicInteger();
        Query query = new Query();
        query.addCriteria(Criteria.where("Series._id").is(SeriesId));
        mongoTemplate.find(query, Chapter.class).forEach(chapter -> {
            totalViewCount.addAndGet(chapter.getViewCount());
        });

        return totalViewCount.get() + seriesRepository.findById(SeriesId).orElseThrow(() -> new RuntimeException("Series not found")).getView();
    }

    public int countTotalLikeBySeriesId(String SeriesId) {
        // get all chapters of this Series
        List<Chapter> chapters = chapterService.getChaptersBySeries(seriesRepository.findById(SeriesId).orElseThrow(() -> new RuntimeException("Series not found")));
        // get all likes of these chapters
        int totalLikeCount = 0;
        for (Chapter chapter : chapters) {
            totalLikeCount += chapterService.countLikeByChapterId(chapter.getId());
        }
        return totalLikeCount;
    }

    public int countTotalChapterBySeriesId(String SeriesId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("Series.$id").is(SeriesId));
        return (int) mongoTemplate.count(query, "chapters");
    }

    public List<ReviewDTO> getReviewsBySeriesId(String SeriesId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("Series._id").is(SeriesId));
        List<Review> reviews = mongoTemplate.find(query, Review.class);
        List<ReviewDTO> reviewDTOS = new ArrayList<>();
        for (Review review : reviews) {
            reviewDTOS.add(ReviewDTO.from(review));
        }
        return reviewDTOS;
    }
}
