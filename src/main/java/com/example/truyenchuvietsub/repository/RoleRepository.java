package com.example.truyenchuvietsub.repository;

import com.example.truyenchuvietsub.model.Role;
import com.example.truyenchuvietsub.model.enums.EnumRole;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByName(EnumRole name);
  boolean existsByName(EnumRole name);
}
