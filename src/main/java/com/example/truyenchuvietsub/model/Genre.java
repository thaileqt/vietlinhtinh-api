package com.example.truyenchuvietsub.model;

import com.example.truyenchuvietsub.model.enums.EnumGenre;
import com.example.truyenchuvietsub.repository.GenreRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Document(collection = "genres")
@Getter
@Setter
@NoArgsConstructor
public class Genre {
    @Id
    private String id;
    private EnumGenre name;
    private String shortDescription;
    private String longDescription;

    public static Set<Genre> toGenreSet(Set<String> genresStr, GenreRepository genreRepository) {
        Set<Genre> genres = new HashSet<>();
        for (String genreStr : genresStr) {
            EnumGenre enumGenre = genreRepository.findByName(EnumGenre.valueOf(genreStr.toUpperCase()))
                    .orElseThrow(() -> new RuntimeException("Error: Genre is not found.")).getName();

            if (enumGenre != null) {
                Genre genre = genreRepository.findByName(enumGenre)
                        .orElseThrow(() -> new RuntimeException("Error: Genre is not found."));
                genres.add(genre);
            }
        }
        return genres;
    }

    public static Set<String> toGenreStrSet(Set<Genre> genres) {
        Set<String> genreStr = new HashSet<>();
        for (Genre genre : genres) {
            genreStr.add(String.valueOf(genre.getName()));
        }
        return genreStr;
    }
}
