package com.project.noiseTypes;

import com.project.NoiseGenerator;

public class ValueNoise implements NoiseTemplate {

    public ValueNoise(){

    };

    public int[][] generateNoise(long seed, int xWidth, int yWidth, int noiseScale) {
        double[][] noiseArray = new double[noiseScale + 1][noiseScale + 1];
        for (int y = 0; y < noiseArray.length; y++) {
            for (int x = 0; x < noiseArray[y].length; x++) {
                noiseArray[y][x] = NoiseGenerator.doubleHash(x + y, seed);
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

                double tx = NoiseGenerator.smoothstep(xMid - x0);
                double ty = NoiseGenerator.smoothstep(yMid - y0);

                double v00 = noiseArray[x0][y0];
                double v10 = noiseArray[x1][y0];
                double v01 = noiseArray[x0][y1];
                double v11 = noiseArray[x1][y1];

                double i1 = NoiseGenerator.lerp(v00, v10, tx);
                double i2 = NoiseGenerator.lerp(v01, v11, tx);
                double value = NoiseGenerator.lerp(i1, i2, ty);

                finalArray[x][y] = (int) (value * 255);
                System.out.print(finalArray[x][y]+" ");
            }
            System.out.println();
        }
        return finalArray;
    }
}
