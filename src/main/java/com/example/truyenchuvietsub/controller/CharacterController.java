package com.example.truyenchuvietsub.controller;

import com.example.truyenchuvietsub.dto.CharacterDTO;
import com.example.truyenchuvietsub.model.Character;
import com.example.truyenchuvietsub.model.Series;
import com.example.truyenchuvietsub.repository.SeriesRepository;
import com.example.truyenchuvietsub.security.JwtUtils;
import com.example.truyenchuvietsub.service.CharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/characters")
public class CharacterController {

    @Autowired
    private CharacterService characterService;
    @Autowired
    private SeriesRepository seriesRepository;

    @GetMapping("/get-by-series/{seriesSlug}")
    public ResponseEntity<List<Character>> getCharactersBySeriesSlug(@PathVariable String seriesSlug) {
        List<Character> characters = characterService.getCharactersBySeriesSlug(seriesSlug);
        return new ResponseEntity<>(characters, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Character> createCharacter(@RequestHeader("Authorization") String token, @RequestBody CharacterDTO characterDTO, Authentication authentication) {
        String userId = JwtUtils.extractUserId(token.substring(7));
        assert userId != null;
        Series series = seriesRepository.findBySlug(characterDTO.getSeriesSlug()).orElseThrow();
        String SeriesUserId = series.getAuthor().getId();

        if (!SeriesUserId.equals(userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Character createdCharacter = characterService.createCharacter(characterDTO);

        return new ResponseEntity<>(createdCharacter, HttpStatus.CREATED);
    }
}
