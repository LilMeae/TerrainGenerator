package com.project.noiseTypes;

public class ErodedSimplexNoise extends SimplexBase {

    private double EROSION_STRENGTH = 3.0;

    @Override
    public int[][] generateNoise(long seed, int xWidth, int yWidth, int noiseScale, double persistence, int octaves, double lacunarity){
        
        //same as eroded perlin noise, but instead of sample perlin we do sample simplex. explanation can be found in ErodedPerlinNoise.java
        int[][] finalArray = new int[yWidth][xWidth];
        
        for (int y = 0; y < yWidth; y++) {
            for (int x = 0; x < xWidth; x++) {
                double total = 0.0;
                double amplitude = 1.0;
                double frequency = 1.0;
                double maxValue = 0.0;

                //gradient accumulator
                double gx = 0.0;
                double gy = 0.0;

                for (int i = 0; i < octaves; i++) {
                    double px = ((double) x / xWidth) * noiseScale * frequency;
                    double py = ((double) y / yWidth) * noiseScale * frequency;

                    double h = 1e-3;
                    double v = sampleSimplex(seed + i, px, py);
                    double vdx = sampleSimplex(seed + i, px + h, py);
                    double vdy = sampleSimplex(seed + i, px, py + h);

                    double dndx = (vdx - v) / h; 
                    double dndy = (vdy - v) / h; 

                    gx += dndx * amplitude;
                    gy += dndy * amplitude;

                    double slopeSq = gx * gx + gy * gy;
                    double erosionScale = amplitude / (1.0 + EROSION_STRENGTH * slopeSq);

                    total += v * erosionScale;
                    maxValue += erosionScale; 

                    amplitude *= persistence;
                    frequency *= lacunarity;
                }

                //normalize the value
                double normalized = (maxValue > 0) ? total / maxValue : 0.0;
                int value = (int) Math.round(((normalized + 1.0) / 2.0) * 255);
                finalArray[y][x] = Math.max(0, Math.min(255, value));
            }
        }

        return finalArray;
    }
}
