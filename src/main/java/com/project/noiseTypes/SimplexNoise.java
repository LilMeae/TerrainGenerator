package com.project.noiseTypes;

import com.project.NoiseGenerator;

public class SimplexNoise implements NoiseTemplate{

    private static final double F2 = 0.5 * (Math.sqrt(3) - 1);
    private static final double G2 = (3.0 - Math.sqrt(3.0)) / 6.0;

    private static final int[][] GRADIENTS = {{ 1,  1}, {-1,  1}, { 1, -1}, {-1, -1}, { 1,  0}, {-1,  0}, { 0,  1}, { 0, -1}};

    @Override
    public int[][] generateNoise(long seed, int xWidth, int yWidth, int noiseScale, double persistence, int octaves, double lacunarity){
        return generateNoise(seed, xWidth, yWidth, noiseScale);
    }

    public int[][] generateNoise(long seed, int xWidth, int yWidth, int noiseScale){
        int[][] finalArray = new int[yWidth][xWidth];
        for(int y = 0; y < finalArray.length; y++){
            for(int x = 0; x < finalArray[y].length; x++){
                double px = ((double) x / xWidth) * noiseScale;
                double py = ((double) y / yWidth) * noiseScale;
                double value = simplexNoise2D(seed, px, py);
                //goes from [-1,1] to [0, 2] then to [0, 255]
                finalArray[y][x] = (int) (Math.round((value + 1.0) / 2 * 255));
                finalArray[y][x] = Math.max(0, Math.min(255, finalArray[y][x]));
            }
        }
        return finalArray;
    }

    private double simplexNoise2D(long seed, double x, double y){
        double s = (x + y) * F2;
        int i = floor(s + x);
        int j = floor(s + y);

        double t = (i + j) * G2;
        double x0 = x - (i - t);
        double y0 = y - (j - t);

        int i1 = 0, j1 = 1;
        if (x0 > y0){
            i1 = 1;
            j1 = 0;
        }
        return 0.0;

    }

    //so that the usual java floor doesn't change -0.7 to 0 instead of -1
    private int floor(double x){
        int x1 = (int) x;
        if (x1 < x){
            return x1 - 1;
        }
        return x1;
    }

}
