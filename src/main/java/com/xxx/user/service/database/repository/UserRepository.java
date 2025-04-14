package com.xxx.user.service.database.repository;

import com.xxx.user.service.database.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Optional<List<UserEntity>> findAllByUsernameInOrEmailIn(List<String> usernames, List<String> emails, Pageable pageable);
    Optional<UserEntity> findByUsername(String username);
}
