package com.project.noiseTypes;

public class ErodedSimplexNoise extends SimplexBase {

    private double EROSION_STRENGTH = 3.0;

    @Override
    public int[][] generateNoise(long seed, int xWidth, int yWidth, int noiseScale, double persistence, int octaves, double lacunarity){
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

                    //approximate the local gradient by using h value as 0.001
                    double h = 1e-3;
                    double v = sampleSimplex(seed + i, px, py);
                    double vdx = sampleSimplex(seed + i, px + h, py);
                    double vdy = sampleSimplex(seed + i, px, py + h);

                    double dndx = (vdx - v) / h; //dnoise/dx
                    double dndy = (vdy - v) / h; //dnoise/dy

                    //lower amplitude gets weighted more
                    gx += dndx * amplitude;
                    gy += dndy * amplitude;

                    // --- Erosion factor ---
                    // Slope magnitude |G|² grows where terrain is steep.
                    // Dividing amplitude by (1 + k|G|²) suppresses fine
                    // detail on steep faces — eroded terrain loses texture.
                    double slopeSq = gx * gx + gy * gy;
                    double erosionScale = amplitude / (1.0 + EROSION_STRENGTH * slopeSq);

                    total += v * erosionScale;
                    maxValue += erosionScale; // track for normalisation

                    amplitude *= persistence;
                    frequency *= lacunarity;
                }

                // Normalise from Perlin range [-1,1] → [0,255]
                double normalized = (maxValue > 0) ? total / maxValue : 0.0;
                int value = (int) Math.round(((normalized + 1.0) / 2.0) * 255);
                finalArray[y][x] = Math.max(0, Math.min(255, value));
            }
        }

        return finalArray;
    }
}
