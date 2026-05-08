package com.project.noiseTypes;

public interface NoiseTemplate {
    public abstract int[][] generateNoise(long seed, int xWidth, int yWidth, int noiseScale, double persistence, int octaves, double lacunarity);
}
