package com.project.noiseTypes;

import com.project.NoiseGenerator;

public class ErodedPerlinNoise extends PerlinBase{

    private static final double EROSION_STRENGTH = 3.0;
    //solves the issue that steep mountain gets same level of fine detail as smooth flat terrain.
    //goal is to add less detail when terrain is steep
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
                    //math is lim h -> 0 (f(x + h) - f(x)) / h to approximate slope at x
                    //set h to 0.001
                    double h = 1e-3;
                    double v = samplePerlin(seed + i, px, py);
                    double vdx = samplePerlin(seed + i, px + h, py);
                    double vdy = samplePerlin(seed + i, px, py + h);

                    //these two form the gradient vector, which points in the direction of steepest ascent
                    double dndx = (vdx - v) / h; //dnoise/dx
                    double dndy = (vdy - v) / h; //dnoise/dy

                    //lower amplitude gets weighted more
                    //these are declared outside the loop so that the gradient builds up across octaves. Steep areas get steeper, etc.
                    gx += dndx * amplitude;
                    gy += dndy * amplitude;

                    //since i don't care abt direction, i only care abt magnitutde, I take the squared value for a magnitude that is always positive
                    //basically pythogorean theorem
                    double slopeSq = gx * gx + gy * gy;

                    //when slope is not steep (near 0), the erosion scale is high.
                    //vice versa, when slope is high, the erosion scale is lower
                    //this makes steep areas lose finer details
                    double erosionScale = amplitude / (1.0 + EROSION_STRENGTH * slopeSq);
                    total += v * erosionScale;

                    //same as fractal noise
                    maxValue += erosionScale; 
                    amplitude *= persistence;
                    frequency *= lacunarity;
                }

                //normalize the value
                double normalized = total / maxValue;
                int value = (int) Math.round(((normalized + 1.0) / 2.0) * 255);
                finalArray[y][x] = Math.max(0, Math.min(255, value));
            }
        }

        return finalArray;
    }
}