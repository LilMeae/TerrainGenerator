package com.project.noiseTypes;

import com.project.NoiseGenerator;

public class ValueNoise implements NoiseTemplate {

    @Override
    public int[][] generateNoise(long seed, int xWidth, int yWidth, int noiseScale, double persistence, int octaves, double lacunarity){
        return generateNoise(seed, xWidth, yWidth, noiseScale);
    }

    /**
     * Value noise aims to generate heightmap by assigning random value to grid corners, then smoothening everything in between to get smooth terrain that makes sense
     * @param seed the seed
     * @param xWidth how big the x component of the map is
     * @param yWidth how big the y component of the map is
     * @param noiseScale how big is the noise 2d array
     * @return a 2D height map from value noise
     */
    public int[][] generateNoise(long seed, int xWidth, int yWidth, int noiseScale) {
        //this gives us a grid of random values
        //noise scale is how many points are in this grid
        double[][] noiseArray = new double[noiseScale + 1][noiseScale + 1];
        for (int y = 0; y < noiseArray.length; y++) {
            for (int x = 0; x < noiseArray[y].length; x++) {
                noiseArray[y][x] = NoiseGenerator.doubleHash(x * 73856093L ^ y * 19349663L, seed);
            }
        }

        //loop through every point
        int[][] finalArray = new int[yWidth][xWidth];
        for (int y = 0; y < finalArray.length; y++) {
            for (int x = 0; x < finalArray[y].length; x++) {
                //map all the pixles in the heightmap to the noise map
                double xMid = (1.0 * x) / xWidth * noiseScale;
                double yMid = (1.0 * y) / yWidth * noiseScale;

                //the four corners in the noise map that surrond this point
                int x0 = (int) Math.floor(xMid);
                int y0 = (int) Math.floor(yMid);
                int x1 = x0 + 1;
                int y1 = y0 + 1;

                //we are doing this smooth step to make sure that they make sense at grid boundaries
                double tx = NoiseGenerator.smoothstep(xMid - x0);//Amount to interpolate by
                double ty = NoiseGenerator.smoothstep(yMid - y0);//Amount to interpolate by

                //gets the noise at the corners of the compressed coordinate
                double corner00 = noiseArray[x0][y0];
                double corner10 = noiseArray[x1][y0];
                double corner01 = noiseArray[x0][y1];
                double corner11 = noiseArray[x1][y1];

                //gets the interpolated value of our point on the bottom x axis
                double bottomEdgeX = NoiseGenerator.lerp(corner00, corner10, tx);
                //gets the interpolated value of our point on the top x axis
                double topEdgeX = NoiseGenerator.lerp(corner01, corner11, tx);
                //gets the interpolated value of our point on the line between these two points with the given y
                double value = NoiseGenerator.lerp(bottomEdgeX, topEdgeX, ty);

                //scales this into [0, 255] and put that into the height array
                finalArray[y][x] = (int)Math.round((value * 255));
                System.out.print(finalArray[x][y]+" ");
            }
            System.out.println();
        }
        return finalArray;
    }
}
