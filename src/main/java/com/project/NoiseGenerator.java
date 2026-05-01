package com.project;

import java.awt.image.BufferedImage;

public class NoiseGenerator {
    private static int imageWidth;
    private static int imageHeight;
    private static int seed;
    
    public enum NoiseTypes{
        PERLIN,
        SIMPLEX,
        RANDOM
    }
}
