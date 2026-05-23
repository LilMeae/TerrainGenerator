package com.project.noiseTypes;

import com.project.NoiseGenerator;

public class FractalPerlinNoise extends PerlinBase{
    
    @Override
    public int[][] generateNoise(long seed, int xWidth, int yWidth, int noiseScale, double persistence, int octaves, double lacunarity) {

        //solves the issue that one layer of perlin only gives smooth big features, lack finer details
        int[][] finalArray = new int[yWidth][xWidth];
        for (int y = 0; y < finalArray.length; y++) {
            for (int x = 0; x < finalArray[y].length; x++) {
                //total for the total height after all iteration
                //amplitude and frequency are starting values. Frequency gets higher and amplitude gets lower every octave
                //maxValue is the sum of all amplitude, keeps the final division between [-1, 1]
                double total = 0;
                double amplitude = 1.0;
                double frequency = 1.0;
                double maxValue = 0;

                //for n octaves
                for (int i = 0; i < octaves; i++) {
                    //compresses it, but multiplies by frequency, this is same thing as sin(x) vs sin(2x), when we have a higher frequency, we see more of the noise map, allowing for more fine small details
                    double px = ((double) x / xWidth) * noiseScale * frequency;
                    double py = ((double) y / yWidth) * noiseScale * frequency;
                    //adds a scaled down version of the sample perlin noise at that location
                    //seed + i to ensure different noise
                    total += samplePerlin(seed + i, px, py) * amplitude;
                    //track total amplitude
                    maxValue += amplitude;
                    //lowers amplitude so smaller features have less weight
                    amplitude *= persistence;
                    //increases frequency for smaller details
                    frequency *= lacunarity;
                }

                //scale this to [0, 255]
                double normalized = total / maxValue;
                int value = (int) Math.round(((normalized + 1) / 2.0) * 255);
                finalArray[y][x] = Math.max(0, Math.min(255, value));
            }
        }
        return finalArray;
    }
}
