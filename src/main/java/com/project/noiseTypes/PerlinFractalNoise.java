package com.project.noiseTypes;

import com.project.NoiseGenerator;

public class PerlinFractalNoise implements NoiseTemplate{
    
    public PerlinFractalNoise(){};

    @Override
    public int[][] generateNoise(long seed, int xWidth, int yWidth, int noiseScale) {
        int octaves = 10;
        double persistence = 0.5;
        double lacunarity = 2.0;
        int[][] finalArray = new int[yWidth][xWidth];
        for (int y = 0; y < finalArray.length; y++) {
            for (int x = 0; x < finalArray[y].length; x++) {
                double total = 0;
                double amplitude = 1.0;
                double frequency = 1.0;
                double maxValue = 0;

                for (int i = 0; i < octaves; i++) {
                    double px = ((double) x / xWidth) * noiseScale * frequency;
                    double py = ((double) y / yWidth) * noiseScale * frequency;
                    total += samplePerlin(seed + i, px, py) * amplitude;
                    maxValue += amplitude;
                    amplitude *= persistence;
                    frequency *= lacunarity;
                }

                double normalized = total / maxValue;
                int value = (int) Math.round(((normalized + 1) / 2.0) * 255);
                finalArray[y][x] = Math.max(0, Math.min(255, value));
            }
        }
        return finalArray;
    }

    private double samplePerlin(long seed, double px, double py) {
        int x0 = (int) Math.floor(px);
        int y0 = (int) Math.floor(py);
        int x1 = x0 + 1;
        int y1 = y0 + 1;

        double tx = fade(px - x0);
        double ty = fade(py - y0);

        double dot00 = dotGradient(seed, x0, y0, px, py);
        double dot10 = dotGradient(seed, x1, y0, px, py);
        double dot01 = dotGradient(seed, x0, y1, px, py);
        double dot11 = dotGradient(seed, x1, y1, px, py);

        return NoiseGenerator.lerp(
            NoiseGenerator.lerp(dot00, dot10, tx),
            NoiseGenerator.lerp(dot01, dot11, tx),
            ty
        );
    }

    private double fade(double t){
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private double dotGradient(long seed, int gx, int gy, double px, double py){
        double[] gradient = getGradient(seed, gx, gy);
        double dx = px - gx;
        double dy = py - gy;
        return dx * gradient[0] + dy * gradient[1];
    }

    private double[] getGradient(long seed, int gx, int gy){
        double angle = NoiseGenerator.doubleHash(gx * 73856093L ^ gy * 19349663L, seed) * 2 * Math.PI;
        return new double[]{Math.cos(angle), Math.sin(angle)};
    }
}
