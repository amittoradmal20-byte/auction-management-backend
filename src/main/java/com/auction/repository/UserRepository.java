package com.auction.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.auction.entity.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	@Query("""
		    SELECT DISTINCT u
		    FROM User u
		    LEFT JOIN FETCH u.roles r
		    LEFT JOIN FETCH r.permissions
		    WHERE u.username = :username
		    """)
		Optional<User> findByUsername(@Param("username") String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}