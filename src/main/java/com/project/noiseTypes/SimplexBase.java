package com.project.noiseTypes;

import com.project.NoiseGenerator;

public abstract class SimplexBase implements NoiseTemplate {
    protected static final double F2 = 0.5 * (Math.sqrt(3) - 1);
    protected static final double G2 = (3.0 - Math.sqrt(3.0)) / 6.0;

    protected static final int[][] GRADIENTS = {{ 1,  1}, {-1,  1}, { 1, -1}, {-1, -1}, { 1,  0}, {-1,  0}, { 0,  1}, { 0, -1}};

    protected double sampleSimplex(long seed, double x, double y){
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
        
        double x1 = x0 - i1 + G2;
        double y1 = y0 - j1 + G2;
        double x2 = x0 - 1.0 + 2.0 * G2;
        double y2 = y0 - 1.0 + 2.0 * G2;

        double n0 = cornerContribution(seed, i, j, x0, y0);
        double n1 = cornerContribution(seed, i + i1, j + j1, x1, y1);
        double n2 = cornerContribution(seed, i + 1, j + 1, x2, y2);
       
        return 70.0 * (n0 + n1 + n2);
    }
    
    protected int floor(double x){
        int x1 = (int) x;
        if (x < x1){
            return x1 - 1;
        }
        return x1;
    }

    protected double cornerContribution(long seed, int gi, int gj, double dx, double dy){
        double t = 0.5 - dx * dx - dy * dy;
        //no contribution if outside range
        if (t < 0){
            return 0.0;
        }
        int idx = (int)(NoiseGenerator.doubleHash(gi * 73856093L ^ gj * 19349663L, seed) * 8);
        int[] grad = GRADIENTS[idx];
        return t * t * t * t * (grad[0] * dx + grad[1] * dy);
    }
}
