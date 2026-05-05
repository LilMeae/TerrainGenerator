package com.project;

import com.project.noiseTypes.NoiseParent;
import com.project.noiseTypes.WhiteNoise;

public class NoiseGenerator {
    private long seed;
    private NoiseParent noiseParent;

    public NoiseGenerator(long seed){
        this.seed = seed;
    }
    
    public static enum NoiseType{
        WHITE,
        VALUE,
        PERLIN,
        SIMPLEX,
        RANDOM
    }

    public int[][] generateNoise(int height, int width, NoiseType noise){
        if(noise == NoiseType.WHITE){
            noiseParent = new WhiteNoise(seed);
        }
        int[][] heightMap = new int[height][width];
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                heightMap[y][x] = noiseParent.getNoise(x, y);
                System.out.print(heightMap[y][x]+" ");
            }
            System.out.println();
        }
        return heightMap;
    }
}
