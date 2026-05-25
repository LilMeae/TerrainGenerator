package com.project.noiseTypes;

import com.project.NoiseGenerator;

public class PerlinNoise implements NoiseTemplate{
    /**
     * Generates perlin noise
     * All values in the range of [0 - 255]
     */
    @Override
    public int[][] generateNoise(long seed, int xWidth, int yWidth, int noiseScale, double persistence, int octaves, double lacunarity) {
        int[][] finalArray = new int[yWidth][xWidth];
        for (int y = 0; y < finalArray.length; y++) {
            for (int x = 0; x < finalArray[y].length; x++) {
                //same as value noise, compresses the x and y coord into one that fits on the noisescale
                double px = ((double)(x) / xWidth) * noiseScale;
                double py = ((double)(y) / yWidth) * noiseScale;
                double value = NoiseGenerator.samplePerlin(seed, px, py);

                //gets normalized to [0, 255]
                finalArray[y][x] = (int) Math.round(((value + 1) / 2.0) * 255);
                finalArray[y][x] = Math.max(0, Math.min(255, finalArray[y][x]));
            }
        }
        return finalArray;
    }
}
