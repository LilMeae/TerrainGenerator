package com.project.noiseTypes;

public class SimplexNoise extends SimplexBase{
    @Override
    public int[][] generateNoise(long seed, int xWidth, int yWidth, int noiseScale, double persistence, int octaves, double lacunarity){
                int[][] finalArray = new int[yWidth][xWidth];
        for(int y = 0; y < finalArray.length; y++){
            for(int x = 0; x < finalArray[y].length; x++){
                double px = ((double) x / xWidth) * noiseScale;
                double py = ((double) y / yWidth) * noiseScale;
                double value = sampleSimplex(seed, px, py);
                //goes from [-1,1] to [0, 2] then to [0, 255]
                finalArray[y][x] = (int) (Math.round((value + 1.0) / 2 * 255));
                finalArray[y][x] = Math.max(0, Math.min(255, finalArray[y][x]));
            }
        }
        return finalArray;
    }
}
