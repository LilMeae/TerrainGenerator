package com.project.noiseTypes;

import com.project.NoiseGenerator;

public class ValueNoise implements NoiseTemplate {

    public ValueNoise(){};

    @Override
    public int[][] generateNoise(long seed, int xWidth, int yWidth, int noiseScale, double persistence, int octaves, double lacunarity){
        return generateNoise(seed, xWidth, yWidth, noiseScale);
    }


    public int[][] generateNoise(long seed, int xWidth, int yWidth, int noiseScale) {
        double[][] noiseArray = new double[noiseScale + 1][noiseScale + 1];
        for (int y = 0; y < noiseArray.length; y++) {
            for (int x = 0; x < noiseArray[y].length; x++) {
                noiseArray[y][x] = NoiseGenerator.doubleHash(x * 73856093L ^ y * 19349663L, seed);
            }
        }

        int[][] finalArray = new int[yWidth][xWidth];
        for (int y = 0; y < finalArray.length; y++) {
            for (int x = 0; x < finalArray[y].length; x++) {
                double xMid = (1.0 * x) / xWidth * noiseScale;
                double yMid = (1.0 * y) / yWidth * noiseScale;

                int x0 = (int) Math.floor(xMid);
                int y0 = (int) Math.floor(yMid);
                int x1 = x0 + 1;
                int y1 = y0 + 1;

                double tx = NoiseGenerator.smoothstep(xMid - x0);//Amount to interpolate by
                double ty = NoiseGenerator.smoothstep(yMid - y0);//Amount to interpolate by

                double corner00 = noiseArray[x0][y0];
                double corner10 = noiseArray[x1][y0];
                double corner01 = noiseArray[x0][y1];
                double corner11 = noiseArray[x1][y1];

                double bottomEdgeX = NoiseGenerator.lerp(corner00, corner10, tx);
                double topEdgeX = NoiseGenerator.lerp(corner01, corner11, tx);
                double value = NoiseGenerator.lerp(bottomEdgeX, topEdgeX, ty);

                finalArray[x][y] = (int)Math.round((value * 255));
                System.out.print(finalArray[x][y]+" ");
            }
            System.out.println();
        }
        return finalArray;
    }
}
