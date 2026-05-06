package com.project.noiseTypes;

import com.project.NoiseGenerator;

public class SimplexNoise implements NoiseTemplate{
    @Override
    public int[][] generateNoise(long seed, int xWidth, int yWidth, int noiseScale){
        int[][] finalArray = new int[yWidth][xWidth];
        for(int y = 0; y < finalArray.length; y++){
            for(int x = 0; x < finalArray[y].length; x++){
                finalArray[y][x] = (int)Math.round((NoiseGenerator.doubleHash(x^ 1863640L * y * 345345345, seed)*255));//Not random watch out
            }
        }
        return finalArray;
    }
}
