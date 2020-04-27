package com.dgtd.security.config;

import java.util.concurrent.TimeUnit;

public class Constant {
    private static final String SIGNING_KEY = "MaYzkSjmkzPC57L";
    private static final Long EXPIRATION_TIME = TimeUnit.MILLISECONDS.convert(10, TimeUnit.HOURS);
    private static final String HEADER_REQUEST = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    //Decrytage
    public static final String JSON_CRYPTED_AK = "!cdKinshasaCOD*";        // Clef
    public static final String JSON_CRYPTED_IV = "Dgtd2-Jc";   			// IV

    public static String getSigningKey() {
        return SIGNING_KEY;
    }

    public static Long getExpirationTime() {
        return EXPIRATION_TIME;
    }

    public static String getHeaderRequest() {
        return HEADER_REQUEST;
    }

    public static String getTokenPrefix() {
        return TOKEN_PREFIX;
    }

    public static String getJsonCryptedAk() {
        return JSON_CRYPTED_AK;
    }

    public static String getJsonCryptedIv() {
        return JSON_CRYPTED_IV;
    }
}
