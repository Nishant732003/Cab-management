package com.cabbooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cabbooking.model.BlacklistedToken;
import com.cabbooking.repository.BlacklistedTokenRepository;

@Service
public class LogoutService {

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    public void blacklistToken(String token) {
        // You would extract the actual expiry date from the token
        // and save it with the token to the database.
        BlacklistedToken blacklisted = new BlacklistedToken();
        blacklisted.setToken(token);
        // blacklisted.setExpiryDate(jwtUtil.getExpirationDateFromToken(token));
        blacklistedTokenRepository.save(blacklisted);
    }
}