package com.example.truyenchuvietsub.service;

import com.example.truyenchuvietsub.dto.CharacterDTO;
import com.example.truyenchuvietsub.model.Character;
import com.example.truyenchuvietsub.repository.CharacterRepository;
import com.example.truyenchuvietsub.repository.SeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CharacterService {

    @Autowired
    private CharacterRepository characterRepository;
    @Autowired
    private SeriesRepository seriesRepository;

    public List<Character> getCharactersBySeriesSlug(String SeriesSlug) {
        return characterRepository.findAllBySeries_Slug(SeriesSlug);
    }

    public Character createCharacter(CharacterDTO characterDTO) {
        // check if request author is Series author
        Character character = new Character();
        character.setName(characterDTO.getName());
        character.setDescription(characterDTO.getDescription());
        character.setSeries(seriesRepository.findBySlug(characterDTO.getSeriesSlug()).orElseThrow());
        character.setCover(characterDTO.getCover());
        return characterRepository.save(character);
    }

}
