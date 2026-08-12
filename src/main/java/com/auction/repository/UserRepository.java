package com.auction.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.auction.entity.UserAccount;


@Repository
public interface UserRepository extends JpaRepository<UserAccount, UUID> {

	@Query("""
		    SELECT DISTINCT u
		    FROM UserAccount u
		    LEFT JOIN FETCH u.roles r
		    LEFT JOIN FETCH r.permissions
		    WHERE u.username = :username
		    """)
		Optional<UserAccount> findByUsername(@Param("username") String username);

    boolean existsByUsername(String username);
    
    @Query("""
            SELECT u
            FROM UserAccount u
            LEFT JOIN FETCH u.userProfile
            WHERE u.username = :username
            """)
    Optional<UserAccount> findByUsernameWithProfile(@Param("username") String username);

}