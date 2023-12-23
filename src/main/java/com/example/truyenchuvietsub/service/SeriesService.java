package com.example.truyenchuvietsub.service;

import com.example.truyenchuvietsub.SlugGenerator;
import com.example.truyenchuvietsub.dto.series.*;
import com.example.truyenchuvietsub.dto.SeriesDTO;
import com.example.truyenchuvietsub.dto.ReviewDTO;
import com.example.truyenchuvietsub.dto.UserDTO;
import com.example.truyenchuvietsub.model.*;
import com.example.truyenchuvietsub.model.enums.EnumGenre;
import com.example.truyenchuvietsub.repository.*;
import org.bson.Document;
import org.bson.types.ObjectId;
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
        // count method execution time
        long startTime = System.currentTimeMillis();

        User user = (User) userManager.loadUserByUsername(username);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        Query query = new Query(
                Criteria.where("author._id").is(user.getId())
        ).with(pageable);

        List<Series> seriesList = mongoTemplate.find(query, Series.class);
        for (Series series : seriesList) {
            System.out.println(series.getTitle());
        }
        // count chapters which has ref to this Series
        List<SeriesDTO> seriesDTOs = seriesList.stream().map(
                series -> SeriesDTO.from(
                        series,
                        countTotalChapterBySeriesId(series.getId()),
                        countTotalViewBySeriesId(series.getId()),
                        countTotalLikeBySeriesId(series.getId()),
                        getReviewsBySeriesId(series.getId())
                )
        ).toList();
        System.out.println("Execution time: " + (System.currentTimeMillis() - startTime) + "ms");
        return seriesDTOs;
    }

    public int countOwnedSeries(String username) {
        User user = (User) userManager.loadUserByUsername(username);
        Query query = new Query(
                Criteria.where("author._id").is(user.getId())
        );
        return (int) mongoTemplate.count(query, Series.class);
    }

    public SeriesDetail getSeriesBySlug(String slug) {
        Series series = seriesRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Series not found"));
        MatchOperation matchOperation = Aggregation.match(Criteria.where("_id").is(new ObjectId(series.getId())));

        LookupOperation chaptersLookupOperation = LookupOperation.newLookup()
                .from("chapters")
                .localField("_id")
                .foreignField("series.$id")
                .as("chapters");

        LookupOperation likesLookupOperation = LookupOperation.newLookup()
                .from("likes")
                .localField("chapters._id")
                .foreignField("chapter.$id")
                .as("likes");

        LookupOperation reviewsLookupOperation = LookupOperation.newLookup()
                .from("reviews")
                .localField("_id")
                .foreignField("series.$id")
                .as("reviews");

        UnwindOperation unwindChaptersOperation = Aggregation.unwind("chapters", true);
        UnwindOperation unwindLikesOperation = Aggregation.unwind("likes", true);
        UnwindOperation unwindReviewsOperation = Aggregation.unwind("reviews", true);

        // take only 10 lastest chapters
        SortOperation sortByChapterNumberDesc = Aggregation.sort(Sort.Direction.DESC, "chapters.chapterNumber");

        GroupOperation groupOperation = Aggregation.group("_id")
                .first("title").as("title")
                .first("slug").as("slug")
                .first("author").as("author")
                .first("description").as("description")
                .first("cover").as("cover")
                .first("genres").as("genres")
                .first("seriesState").as("seriesState")
                .first("createdAt").as("createdAt")
                .first("updatedAt").as("updatedAt")
                .first("view").as("view")
                .sum("chapters.viewCount").as("totalViews")
                .addToSet("chapters").as("chapters")
                .addToSet("likes").as("likes")
                .addToSet("reviews").as("reviews")
                .avg("reviews.rating").as("averageRating");

        ProjectionOperation projectOperation = Aggregation.project()
                .and("title").as("title")
                .and("slug").as("slug")
                .and("author").as("author")
                .and("description").as("description")
                .and("cover").as("cover")
                .and("genres").as("genres")
                .and("seriesState").as("seriesState")
                .and("createdAt").as("createdAt")
                .and("updatedAt").as("updatedAt")
                .andExpression("totalViews + view").as("totalViews")
                .and("chapters").size().as("totalChapters")
                .and("likes").size().as("totalLikes")
                .and("reviews").size().as("totalReviews")
                .and("averageRating").as("averageRating")
                .and("reviews").as("reviews");

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                chaptersLookupOperation,
                unwindChaptersOperation,
                likesLookupOperation,
                unwindLikesOperation,
                reviewsLookupOperation,
                unwindReviewsOperation,
                groupOperation,
                projectOperation
        );

        return mongoTemplate.aggregate(aggregation, "series", SeriesDetail.class).getUniqueMappedResult();
    }

    public List<HotSeries> getTopSeriesWithHighestLikes(int seriesCount) {

        LookupOperation chaptersLookupOperation = LookupOperation.newLookup()
                .from("chapters")
                .localField("_id")
                .foreignField("series.$id")
                .as("chapters");

        LookupOperation likesLookupOperation = LookupOperation.newLookup()
                .from("likes")
                .localField("chapters._id")
                .foreignField("chapter.$id")
                .as("likes");

        LookupOperation reviewsLookupOperation = LookupOperation.newLookup()
                .from("reviews")
                .localField("_id")
                .foreignField("series.$id")
                .as("reviews");

        UnwindOperation unwindChaptersOperation = Aggregation.unwind("chapters", true);
        UnwindOperation unwindLikesOperation = Aggregation.unwind("likes", true);
        UnwindOperation unwindReviewsOperation = Aggregation.unwind("reviews", true);

        GroupOperation groupOperation = Aggregation.group("_id")
                .first("title").as("title")
                .first("slug").as("slug")
                .first("author").as("author")
                .first("description").as("description")
                .first("cover").as("cover")
                .first("genres").as("genres")
                .first("seriesState").as("seriesState")
                .first("createdAt").as("createdAt")
                .first("updatedAt").as("updatedAt")
                .first("view").as("view")
                .sum("chapters.viewCount").as("totalViews")
                .addToSet("chapters").as("chapters")
                .addToSet("likes").as("likes")
                .addToSet("reviews").as("reviews")
                .avg("reviews.rating").as("averageRating");


        ProjectionOperation projectOperation = Aggregation.project()
                .and("title").as("title")
                .and("slug").as("slug")
                .and("author").as("author")
                .and("cover").as("cover")
                .and("genres").as("genres")
                .and("seriesState").as("seriesState")
                .andExpression("totalViews + view").as("totalViews")
                .and("chapters").size().as("totalChapters")
                .and("likes").size().as("totalLikes")
                .and("averageRating").as("averageRating");

        SortOperation sortByTotalLikes = Aggregation.sort(Sort.Direction.DESC, "totalLikes");


        Aggregation aggregation = Aggregation.newAggregation(
                chaptersLookupOperation,
                unwindChaptersOperation,
                likesLookupOperation,
                unwindLikesOperation,
                reviewsLookupOperation,
                unwindReviewsOperation,
                groupOperation,
                projectOperation,
                sortByTotalLikes,
                Aggregation.limit(seriesCount) // Limit records based on size
        );
        return mongoTemplate.aggregate(aggregation, "series", HotSeries.class).getMappedResults();
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

    public List<RecentCreatedSeries> getTopRecentCreatedSeries(int page, int size) {
        LookupOperation chaptersLookupOperation = LookupOperation.newLookup()
                .from("chapters")
                .localField("_id")
                .foreignField("series.$id")
                .as("chapters");

        UnwindOperation unwindChaptersOperation = Aggregation.unwind("chapters");

        GroupOperation groupOperation = Aggregation.group("_id")
                .first("title").as("title")
                .first("slug").as("slug")
                .first("author").as("author")
                .first("cover").as("cover")
                .first("createdAt").as("createdAt")
                .addToSet("chapters").as("chapters");

        ProjectionOperation projectOperation = Aggregation.project()
                .and("title").as("title")
                .and("slug").as("slug")
                .and("author").as("author")
                .and("cover").as("cover")
                .and("createdAt").as("createdAt")
                .and("chapters").size().as("totalChapters");

        Aggregation aggregation = Aggregation.newAggregation(
                chaptersLookupOperation,
                unwindChaptersOperation,
                groupOperation,
                projectOperation,
                Aggregation.sort(Sort.Direction.DESC, "createdAt"),
                Aggregation.skip((long) (page - 1) * size), // Calculate the offset
                Aggregation.limit(size) // Limit records based on size
        );

        return mongoTemplate.aggregate(aggregation, "series", RecentCreatedSeries.class).getMappedResults();

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
    public List<RecentUpdatedSeries> getTopRecentUpdatedSeries(int page, int size) {
        LookupOperation chaptersLookupOperation = LookupOperation.newLookup()
                .from("chapters")
                .localField("_id")
                .foreignField("series.$id")
                .as("chapters");

        UnwindOperation unwindChaptersOperation = Aggregation.unwind("chapters");

        GroupOperation groupOperation = Aggregation.group("_id")
                .first("title").as("title")
                .first("slug").as("slug")
                .first("author").as("author")
                .first("cover").as("cover")
                .first("description").as("description")
                .first("genres").as("genres")
                // get lastest chapter of this series
                .last("chapters.title").as("chapterTitle")
                .last("chapters.chapterNumber").as("chapterNumber")
                .last("chapters._id").as("chapterId")
                .first("createdAt").as("createdAt")
                .first("updatedAt").as("updatedAt");

        // just take newest chapter associated with each series
        Aggregation aggregation = Aggregation.newAggregation(
                chaptersLookupOperation,
                unwindChaptersOperation,
                groupOperation,
                Aggregation.sort(Sort.Direction.DESC, "updatedAt"),
                Aggregation.skip((long) (page - 1) * size), // Calculate the offset
                Aggregation.limit(size) // Limit records based on size
        );

        return mongoTemplate.aggregate(aggregation, "series", RecentUpdatedSeries.class).getMappedResults();
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

    public int countTotalLikeBySeriesId(String seriesId) {
        // get all chapters of this Series
        List<Chapter> chapters = chapterService.getChaptersBySeries(seriesRepository.findById(seriesId).orElseThrow(() -> new RuntimeException("Series not found")));
        // get all likes of these chapters
        // get all likes of these chapters
        int totalLikeCount = 0;
        for (Chapter chapter : chapters) {
            totalLikeCount += chapterService.countLikeByChapterId(chapter.getId());
        }
        return totalLikeCount;
    }

    public int countTotalChapterBySeriesId(String seriesId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("series.$id").is(seriesId));
        return (int) mongoTemplate.count(query, "chapters");
    }

    public List<ReviewDTO> getReviewsBySeriesId(String seriesId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("series._id").is(seriesId));
        List<Review> reviews = mongoTemplate.find(query, Review.class);
        return reviews.stream().map(ReviewDTO::from).toList();
    }

    public List<UserOwnedSeriesDTO> getSeriesByUsername(String username, int page, int size) {
        // count method execution time
        String userId = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found")).getId();
        MatchOperation matchOperation = Aggregation.match(Criteria.where("author.$id").is(new ObjectId(userId)));

        LookupOperation chaptersLookupOperation = LookupOperation.newLookup()
                .from("chapters")
                .localField("_id")
                .foreignField("series.$id")
                .as("chapters");

        LookupOperation likesLookupOperation = LookupOperation.newLookup()
                .from("likes")
                .localField("chapters._id")
                .foreignField("chapter.$id")
                .as("likes");

        LookupOperation reviewsLookupOperation = LookupOperation.newLookup()
                .from("reviews")
                .localField("_id")
                .foreignField("series.$id")
                .as("reviews");

        UnwindOperation unwindChaptersOperation = Aggregation.unwind("chapters", true);
        UnwindOperation unwindLikesOperation = Aggregation.unwind("likes", true);
        UnwindOperation unwindReviewsOperation = Aggregation.unwind("reviews", true);

        GroupOperation groupOperation = Aggregation.group("_id")
                .first("title").as("title")
                .first("slug").as("slug")
                .first("author").as("author")
                .first("description").as("description")
                .first("cover").as("cover")
                .first("genres").as("genres")
                .first("seriesState").as("seriesState")
                .first("createdAt").as("createdAt")
                .first("updatedAt").as("updatedAt")
                .first("view").as("view")
                .sum("chapters.viewCount").as("totalViews")
                .addToSet("chapters").as("chapters")
                .addToSet("likes").as("likes")
                .addToSet("reviews").as("reviews")
                .avg("reviews.rating").as("averageRating");


        ProjectionOperation projectOperation = Aggregation.project()
                .and("title").as("title")
                .and("slug").as("slug")
                .and("author").as("author")
                .and("description").as("description")
                .and("cover").as("cover")
                .and("genres").as("genres")
                .and("seriesState").as("seriesState")
                .and("createdAt").as("createdAt")
                .and("updatedAt").as("updatedAt")
                .andExpression("totalViews + view").as("totalViews")
                .and("chapters").size().as("totalChapters")
                .and("likes").size().as("totalLikes")
                .and("reviews").size().as("totalReviews")
                .and("averageRating").as("averageRating");

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                chaptersLookupOperation,
                unwindChaptersOperation,
                likesLookupOperation,
                unwindLikesOperation,
                reviewsLookupOperation,
                unwindReviewsOperation,
                groupOperation,
                projectOperation,
                Aggregation.skip((long) (page - 1) * size), // Calculate the offset
                Aggregation.limit(size) // Limit records based on size
        );
        List<UserOwnedSeriesDTO> result = mongoTemplate.aggregate(aggregation, "series", UserOwnedSeriesDTO.class).getMappedResults();
        return result;

    }
}
