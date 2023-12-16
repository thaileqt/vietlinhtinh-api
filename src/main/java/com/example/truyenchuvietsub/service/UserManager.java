package com.example.truyenchuvietsub.service;

import com.example.truyenchuvietsub.model.*;
import com.example.truyenchuvietsub.model.enums.EnumChapterState;
import com.example.truyenchuvietsub.model.enums.EnumGenre;
import com.example.truyenchuvietsub.model.enums.EnumRole;
import com.example.truyenchuvietsub.model.enums.EnumSeriesState;
import com.example.truyenchuvietsub.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

@Service
public class UserManager implements UserDetailsManager {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private ChapterStateRepository chapterStateRepository;
    @Autowired
    private SeriesStateRepository seriesStateRepository;

    @Override
    public void createUser(UserDetails user) {
        ((User) user).setPassword(passwordEncoder.encode(user.getPassword()));
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        // check role exists
        // if not exist then create
        createRoleIfNotExist(EnumRole.ROLE_USER);
        Role role = roleRepository.findByName(EnumRole.ROLE_USER).orElseThrow();
        ((User) user).setRoles(Collections.singleton(role));
        userRepository.save((User) user);
    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {
        if (!userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username not found");
        }
        userRepository.deleteByUsername(username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            throw new RuntimeException("Wrong current password");
        }


    }

    @Override
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        MessageFormat.format("username {0} not found", username)
                ));
    }
    public void createAdmin(UserDetails user) {
        initDatabase();
        createRoleIfNotExist(EnumRole.ROLE_ADMIN);
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        ((User) user).setPassword(passwordEncoder.encode(user.getPassword()));

        Role role = roleRepository.findByName(EnumRole.ROLE_ADMIN).orElseThrow();
        ((User) user).setRoles(Collections.singleton(role));
        userRepository.save((User) user);

        // setting up data genre and status
    }

    public void createRoleIfNotExist(EnumRole role) {
        if (!roleRepository.existsByName(role)) {
            Role newRole = new Role();
            newRole.setName(role);
            roleRepository.save(newRole);
        }
    }

    public void initDatabase() {
        EnumGenre[] enumGenre = EnumGenre.values();
        for (EnumGenre genre : enumGenre) {
            if (!genreRepository.existsByName(genre)) {
                Genre newGenre = new Genre();
                newGenre.setName(genre);
                genreRepository.save(newGenre);
            }
        }

        EnumChapterState[] enumChapterStates = EnumChapterState.values();
        for (EnumChapterState chapterState : enumChapterStates) {
            if (!chapterStateRepository.existsByName(chapterState)) {
                ChapterState newChapterState = new ChapterState();
                newChapterState.setName(chapterState);
                chapterStateRepository.save(newChapterState);
            }
        }

        EnumSeriesState[] enumSeriesStates = EnumSeriesState.values();
        for (EnumSeriesState seriesState : enumSeriesStates) {
            if (!seriesStateRepository.existsByName(seriesState)) {
                SeriesState newSeriesState = new SeriesState();
                newSeriesState.setName(seriesState);
                seriesStateRepository.save(newSeriesState);
            }
        }
    }
}