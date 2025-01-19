package com.Likun.weatherdashboard.repository;

import com.Likun.weatherdashboard.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    @Query("SELECT a.authority FROM Authority a WHERE a.username = :username")
    List<String> findAuthoritiesByUsername(@Param("username") String username);
}