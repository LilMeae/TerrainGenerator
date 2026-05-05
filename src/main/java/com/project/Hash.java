package com.project;

public class Hash {

public static double hashToDouble(long input, long seed) {
    // 1. Generate the positive 64-bit hash
    long state = (input ^ seed) * 6364136223846793005L + 1442695040888963407L;
    long xorshifted = ((state >>> 18) ^ state) >>> 27;
    int rot = (int)(state >>> 59);
    long hash = Long.rotateRight(xorshifted, rot) & 0x7FFFFFFFFFFFFFFFL;

    // 2. Map to 0.0 - 1.0 range
    // Divide by 2^63 - 1 (the max value of a positive long)
    return (double) hash / 9223372036854775807L;
}
}
