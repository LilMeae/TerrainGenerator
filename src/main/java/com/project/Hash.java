package com.project;

public class Hash {

    public static long seededPcgHash(int input, long seed) {
    // Mix the input with the seed first
    long state = (input ^ seed) * 747796405l + 2891336453l;
    
    // Apply the standard PCG permutation
    long word = ((state >>> ((state >>> 28) + 4)) ^ state) * 277803737;
    return (word >>> 22) ^ word;
}
}
