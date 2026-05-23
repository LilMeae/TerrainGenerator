package com.project.noiseTypes;

import com.project.NoiseGenerator;
 
public class WhiteNoise implements NoiseTemplate{

    @Override
    /**
     * Generates white (random) noise
     * All values in the range of [0 - 255]
     */
    public int[][] generateNoise(long seed, int xWidth, int yWidth, int noiseScale, double persistence, int octaves, double lacunarity){
        int[][] finalArray = new int[yWidth][xWidth];
        for(int y = 0; y < finalArray.length; y++){
            for(int x = 0; x < finalArray[y].length; x++){
                //every pixel gets a completely random value
                finalArray[y][x] = (int)Math.round((NoiseGenerator.doubleHash(x^ 1863640L * y * 345345345, seed)*255));//Not random watch out
            }
        }
        return finalArray;
    }
}
