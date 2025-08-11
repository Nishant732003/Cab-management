package com.cabbooking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cabbooking.model.BlacklistedToken;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {

    Optional<BlacklistedToken> findByToken(String token);
}
