package com.project.noiseTypes;

import com.project.NoiseGenerator;

public class PerlinNoise implements NoiseTemplate{
    
    public PerlinNoise(){};

    @Override
    public int[][] generateNoise(long seed, int xWidth, int yWidth, int noiseScale) {
        double[][] noiseArray = new double[noiseScale + 1][noiseScale + 1];
        for (int y = 0; y < noiseArray.length; y++) {
            for (int x = 0; x < noiseArray[y].length; x++) {
                noiseArray[y][x] = NoiseGenerator.doubleHash(x * 73856093L ^ y * 19349663L, seed);
            }
        }

        int[][] finalArray = new int[yWidth][xWidth];
        for (int y = 0; y < finalArray.length; y++) {
            for (int x = 0; x < finalArray[y].length; x++) {
                System.out.print(finalArray[x][y]+" ");
            }
            System.out.println();
        }
        return finalArray;
    }
}
