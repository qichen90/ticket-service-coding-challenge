package com.qi.support;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Code or Id generator functions.
 */
public class CodeGenerator {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghigklmnopqrstuvwxyz0123456789";
    private static final Random random = new SecureRandom();

    /**
     * Private Constructor.
     * Suppress default constructor for non-instantiability
     */
    private CodeGenerator() {
        throw new AssertionError();
    }

    /**
     * Returns random character.
     *
     * @return character
     */
    private static char randomChar() {
        return ALPHABET.charAt(random.nextInt(ALPHABET.length()));
    }

    /**
     * Returns distance (in meters) between 2 points.
     *
     * @param lengthOfCode must equal to or large than 10
     * @return code in string
     */
    public static String generateCode(int lengthOfCode) {
        StringBuilder code = new StringBuilder();
        for(int i = 0; i < lengthOfCode; i++){
            code.append(randomChar());
        }
        return code.toString();
    }
}
