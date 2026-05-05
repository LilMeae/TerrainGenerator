package com.project;

public class NoiseGenerator {

    public static enum NoiseType {
        WHITE,
        VALUE,
        PERLIN,
        SIMPLEX,
        RANDOM
    }

    public static int[][] generateNoise(int height, int width, long seed, NoiseType noise) {
        return new int[1][1];
    }

    /**
     * Does a pcg hash and returns a pseudo random value between 0.0 and 1.0
     * @param input input number
     * @param seed seed
     * @return double from 0.0 - 1.0
     */
    public static double doubleHash(long input, long seed) {
        long state = (input ^ seed) * 6364136223846793005L + 1442695040888963407L;
        long xorshifted = ((state >>> 18) ^ state) >>> 27;
        int rot = (int) (state >>> 59);
        long hash = Long.rotateRight(xorshifted, rot) & 0x7FFFFFFFFFFFFFFFL;
        return (double) hash / 9223372036854775807L;
    }
}
