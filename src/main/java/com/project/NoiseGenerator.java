package com.project;

import com.project.noiseTypes.ErodedPerlinNoise;
import com.project.noiseTypes.PerlinFractalNoise;
import com.project.noiseTypes.PerlinNoise;
import com.project.noiseTypes.SimplexNoise;
import com.project.noiseTypes.ValueNoise;
import com.project.noiseTypes.WhiteNoise;

public class NoiseGenerator {

    public static enum NoiseType {
        WHITE,
        VALUE,
        PERLIN,
        SIMPLEX,
        FRACTAL_PERLIN,
        ERODED_PERLIN
    }

    public static int[][] generateNoise(int xWidth, int yWidth, int noiseScale, long seed, double persistence, int octaves, double lacunarity, double extremity, NoiseType noise) {
        int[][] heightMap = switch (noise) {
            case VALUE -> new ValueNoise().generateNoise(seed, xWidth, yWidth, noiseScale);
            case WHITE -> new WhiteNoise().generateNoise(seed, xWidth, yWidth, noiseScale);
            case PERLIN -> new PerlinNoise().generateNoise(seed, xWidth, yWidth, noiseScale);
            case SIMPLEX -> new SimplexNoise().generateNoise(seed, xWidth, yWidth, noiseScale);
            case FRACTAL_PERLIN -> new PerlinFractalNoise().generateNoise(seed, xWidth, yWidth, noiseScale, persistence, octaves, lacunarity);
            case ERODED_PERLIN -> new ErodedPerlinNoise().generateNoise(seed, xWidth, yWidth, noiseScale, persistence, octaves, lacunarity);
            default -> new WhiteNoise().generateNoise(seed, xWidth, yWidth, noiseScale);
        };

        double max = 0;

        for (int y = 0; y < yWidth; y++) {
            for (int x = 0; x < xWidth; x++) {
                heightMap[y][x] = (int) Math.pow(heightMap[y][x], extremity);
                if (heightMap[y][x] > max) max = heightMap[y][x];
            }
        }

        if (max > 0) {
            for (int y = 0; y < yWidth; y++) {
                for (int x = 0; x < xWidth; x++) {
                    heightMap[y][x] = (int) Math.round((heightMap[y][x] / max) * 255);
                }
            }
        }
        return heightMap;
    }

    /**
     * Does a pcg hash and returns a pseudo random value between 0.0 and 1.0
     * @param input input number
     * @param seed seed
     * @return double from 0.0 - 1.0
     */
    public static double doubleHash(long input, long seed) {
        long state = (input ^ seed) * 6364136223846793005L + 1442695040888963407L;
        long xorshifted = ((state >>> 18) ^ state) >>> 27;
        int rot = (int) (state >>> 59);
        long hash = Long.rotateRight(xorshifted, rot) & 0x7FFFFFFFFFFFFFFFL;
        return (double) hash / 9223372036854775807L;
    }

    public static double smoothstep(double t) {
    return t * t * (3 - 2 * t);
    }

    public static double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    public static double fade(double t){
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    public static double dotGradient(long seed, int gx, int gy, double px, double py){
        double[] gradient = getGradient(seed, gx, gy);
        double dx = px - gx;
        double dy = py - gy;
        return dx * gradient[0] + dy * gradient[1];
    }

    public static double[] getGradient(long seed, int gx, int gy){
        double angle = NoiseGenerator.doubleHash(gx * 73856093L ^ gy * 19349663L, seed) * 2 * Math.PI;
        return new double[]{Math.cos(angle), Math.sin(angle)};
    }
}
