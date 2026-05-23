package com.project.noiseTypes;

public interface NoiseTemplate {
    /**
     * Template for noise generation
     * @param seed seed value for randomization
     * @param xWidth x width of the generated noise
     * @param yWidth y width of the generated noise
     * @param noiseScale scale of the noise generated (how much interpolation occurs)
     * @param persistence how much fractal amplitude decreases every octave
     * @param octaves amount of fractals generated
     * @param lacunarity how much noise scale is compressed per fractal
     * @return int[][] of values [0 - 255]
     */
    public abstract int[][] generateNoise(long seed, int xWidth, int yWidth, int noiseScale, double persistence, int octaves, double lacunarity);
}
