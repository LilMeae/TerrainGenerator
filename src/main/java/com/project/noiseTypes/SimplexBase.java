package com.project.noiseTypes;

import com.project.NoiseGenerator;

public abstract class SimplexBase implements NoiseTemplate {
    //this is our shear and un-shear factors. We need a way to change square space into triangle space. We do that by sharing one of the axis to get parallelograms. Then drawing diagnols to get equilateral triangles
    //F takes the equilateral triangle space and maps them into the integer cordinates
    protected static final double F = 0.5 * (Math.sqrt(3) - 1);
    //G takes integer cordinates and maps them into the equilateral triangle space.
    //comes from 6g^2 -6g + 1 = 0, uhh just trust the math i'll write abt this in the report
    protected static final double G = (3.0 - Math.sqrt(3.0)) / 6.0;

    //eight direction vectors, four diagnal, four along axis. All are unit vectors with length 1. Similar to the perlin gradient vectors, just not randomized.
    protected static final double[][] GRADIENTS = {{Math.sqrt(2) / 2,  Math.sqrt(2) / 2}, {-Math.sqrt(2) / 2,  Math.sqrt(2) / 2}, {Math.sqrt(2) / 2, -Math.sqrt(2) / 2}, {-Math.sqrt(2) / 2, -Math.sqrt(2) / 2}, { 1,  0}, {-1,  0}, { 0,  1}, { 0, -1}};

    //solves the issue that sometimes perlin looks boxy because of the square nature of it
    //simplex uses triangles instead of squares. main challenge is to translate from sqare input (x,y) to figuring out which triangle that point is in

    /**
     * Gives the height of a single point using simplex noise
     * @param seed the seed
     * @param x the x cord in cartesian space
     * @param y the y cord in cartesian space
     * @return the height of the point
     */
    protected double sampleSimplex(long seed, double x, double y){
        //applying the f matrix to (x,y), then floor them which gives the bottom left corner of the skewed integer cell this point is in
        double s = (x + y) * F;
        int i = floor(s + x);
        int j = floor(s + y);

        //unskewing that corner back into equilateral triangle space.
        double t = (i + j) * G;

        //this gives the displacement vector from the corner to the point, basically how far you are from the bottom left corner
        //note i - t is the actual place in real space
        double x0 = x - (i - t);
        double y0 = y - (j - t);

        //determining which triangle you are in. This is because the skew turns the square plane into parallelograms, so we have to compare x and y to see if we are in the lower or upper triangle
        int i1 = 0, j1 = 1;
        if (x0 > y0){
            i1 = 1;
            j1 = 0;
        }
        
        //finding the other two displacement vectors from the two other corners
        double x1 = x0 - i1 + G;
        double y1 = y0 - j1 + G;
        double x2 = x0 - 1 + 2 * G;
        double y2 = y0 - 1 + 2 * G;

        //this is what differs from perlin. It doesn't take the average, it lets each corner contribute independently based on distance, then sums
        double n0 = cornerContribution(seed, i, j, x0, y0);
        double n1 = cornerContribution(seed, i + i1, j + j1, x1, y1);
        double n2 = cornerContribution(seed, i + 1, j + 1, x2, y2);
       
        //70 normalizes this to around [-1, 1]
        return 70.0 * (n0 + n1 + n2);
    }
    
    //fast floor function that rounds -0.7 to -1 instead of 0
    protected int floor(double x){
        int x1 = (int) x;
        if (x < x1){
            return x1 - 1;
        }
        return x1;
    }

    /**
     * Gets the contribution a corner has to a point in simplex noise
     * @param seed the seed
     * @param gi the x-cord
     * @param gj the y-cord
     * @param dx the displacent vector x component
     * @param dy the displacement vector y component
     * @return how much the corner contributes
     */
    protected double cornerContribution(long seed, int gi, int gj, double dx, double dy){
        //ok, the 'falloff' curve is basically when r (which is the length of dx dy vector) > 0.5, contribution is 0. It is maxed when dx and dy are 0
        double t = 0.5 - dx * dx - dy * dy;
        //no contribution if outside range
        if (t < 0){
            return 0.0;
        }
        //random gradient selection
        int idx = (int)(NoiseGenerator.doubleHash(gi * 73856093L ^ gj * 19349663L, seed) * 8);
        double[] grad = GRADIENTS[idx];
        //the falloff curve (basically amplitude) * the dot product of the gradient
        return t * t * t * t * (grad[0] * dx + grad[1] * dy);
    }
}
