package com.bankingapi.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class AccountNumberGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String PREFIX = "ACC";

    public String generate() {
        long number = (long) (RANDOM.nextDouble() * 9_000_000_000L) + 1_000_000_000L;
        return PREFIX + number;
    }
}
