package com.project.noiseTypes;

import com.project.NoiseGenerator;

public class FractalSimplexNoise implements NoiseTemplate {

    /**
     * Generates fractal simplex noise
     * All values in the range of [0 - 255]
     */
    @Override
    public int[][] generateNoise(long seed, int xWidth, int yWidth, int noiseScale, double persistence, int octaves, double lacunarity) {

        //same logic as fractal perlin, but instead of perlin noise sample we do simplex noise sample
        int[][] finalArray = new int[yWidth][xWidth];
        for (int y = 0; y < finalArray.length; y++) {
            for (int x = 0; x < finalArray[y].length; x++) {
                double total = 0;
                double amplitude = 1.0;
                double frequency = 1.0;
                double maxValue = 0;

                for (int i = 0; i < octaves; i++) {
                    double px = ((double) x / xWidth) * noiseScale * frequency;
                    double py = ((double) y / yWidth) * noiseScale * frequency;
                    total += NoiseGenerator.sampleSimplex(seed + i, px, py) * amplitude;
                    maxValue += amplitude;
                    amplitude *= persistence;
                    frequency *= lacunarity;
                }

                double normalized = total / maxValue;
                int value = (int) Math.round(((normalized + 1) / 2.0) * 255);
                finalArray[y][x] = Math.max(0, Math.min(255, value));
            }
        }
        return finalArray;
    }
}
