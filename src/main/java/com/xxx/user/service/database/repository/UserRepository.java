package com.xxx.user.service.database.repository;

import com.xxx.user.service.database.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Optional<List<UserEntity>> findAllByUsernameInOrEmailIn(List<String> usernames, List<String> emails);
    Optional<UserEntity> findByUsername(String username);
    @EntityGraph(attributePaths = "roles")
    @Query("SELECT u FROM UserEntity u WHERE u.username = :username OR u.email = :email")
    Optional<UserEntity> findByUsernameOrEmail(@Param("username") String username,@Param("email") String email);
    @EntityGraph(attributePaths = "roles")
    Optional<List<UserEntity>> findAllByUsernameIn(List<String> usernames);
}
