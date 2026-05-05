package com.project;

import java.awt.image.BufferedImage;

public class NoiseGenerator {
    
    public static enum NoiseType{
        VALUE,
        PERLIN,
        SIMPLEX,
        RANDOM
    }

    public static int[][] generateNoise(int height, int width, NoiseType noise){
        int[][] heightMap = new int[height][width];
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){     
            }
        }
        return heightMap;
    }
}
