package com.project;

import com.project.noiseTypes.ErodedPerlinNoise;
import com.project.noiseTypes.ErodedSimplexNoise;
import com.project.noiseTypes.FractalPerlinNoise;
import com.project.noiseTypes.FractalSimplexNoise;
import com.project.noiseTypes.PerlinNoise;
import com.project.noiseTypes.SimplexNoise;
import com.project.noiseTypes.ValueNoise;
import com.project.noiseTypes.WhiteNoise;

public class NoiseGenerator {

    public static enum NoiseType {
        WHITE,
        VALUE,
        PERLIN,
        SIMPLEX,
        FRACTAL_PERLIN,
        ERODED_PERLIN,
        FRACTAL_SIMPLEX,
        ERODED_SIMPLEX
    }

    public static int[][] generateNoise(int xWidth, int yWidth, int noiseScale, long seed, double persistence, int octaves, double lacunarity, double extremity, NoiseType noise) {
        //generates the height map according to the noise
        int[][] heightMap = switch (noise) {
            case VALUE -> new ValueNoise().generateNoise(seed, xWidth, yWidth, noiseScale, persistence, octaves, lacunarity);
            case WHITE -> new WhiteNoise().generateNoise(seed, xWidth, yWidth, noiseScale, persistence, octaves, lacunarity);
            case PERLIN -> new PerlinNoise().generateNoise(seed, xWidth, yWidth, noiseScale, persistence, octaves, lacunarity);
            case SIMPLEX -> new SimplexNoise().generateNoise(seed, xWidth, yWidth, noiseScale, persistence, octaves, lacunarity);
            case FRACTAL_PERLIN -> new FractalPerlinNoise().generateNoise(seed, xWidth, yWidth, noiseScale, persistence, octaves, lacunarity);
            case ERODED_PERLIN -> new ErodedPerlinNoise().generateNoise(seed, xWidth, yWidth, noiseScale, persistence, octaves, lacunarity);
            case FRACTAL_SIMPLEX -> new FractalSimplexNoise().generateNoise(seed, xWidth, yWidth, noiseScale, persistence, octaves, lacunarity);
            case ERODED_SIMPLEX -> new ErodedSimplexNoise().generateNoise(seed, xWidth, yWidth, noiseScale, persistence, octaves, lacunarity);
            default -> new WhiteNoise().generateNoise(seed, xWidth, yWidth, noiseScale, persistence, octaves, lacunarity);
        };

        double max = 0;

        for (int y = 0; y < yWidth; y++) {
            for (int x = 0; x < xWidth; x++) {
                //applies an extremity curve and finds the new max
                heightMap[y][x] = (int) Math.pow(heightMap[y][x], extremity);
                if (heightMap[y][x] > max) max = heightMap[y][x];
            }
        }

        //normalize everything back to [0,255]
        if (max > 0) {
            for (int y = 0; y < yWidth; y++) {
                for (int x = 0; x < xWidth; x++) {
                    heightMap[y][x] = (int) Math.round((heightMap[y][x] / max) * 255);
                }
            }
        }
        return heightMap;
    }

    /**
     * Does a pcg hash and returns a pseudo random value between 0.0 and 1.0
     * @param input input number
     * @param seed seed
     * @return double from 0.0 - 1.0
     */
    public static double doubleHash(long input, long seed) {
        long state = (input ^ seed) * 6364136223846793005L + 1442695040888963407L;
        long xorshifted = ((state >>> 18) ^ state) >>> 27;
        int rot = (int) (state >>> 59);
        long hash = Long.rotateRight(xorshifted, rot) & 0x7FFFFFFFFFFFFFFFL;
        return (double) hash / 9223372036854775807L;
    }

    /**
     * This is very important. We make this so that when we move from pixel to pixle. When we go from 1 pixel to the next, the t value changes from 1 to 0.
     * While the lerp guarantees that the entire graph of the lines between points is continuous, it may have spiky points since the derivative of lerp is (b-a) which is different in every cell.
     * However, we want them to be smooth, so we need a function f such that f(0) = 0, f(1) = 1, f'(0) = f'(1) = 0. 
     * Solving for this, we get the cubic polynomial 2t^3 - 3t^2
     * @param t the original compressed coordinate
     * @return a smoothened version that slows near the extremities
     */
    public static double smoothstep(double t) {
        return t * t * (3 - 2 * t);
    }

    /**
     * What the weight of a value is between two points, basically an interpolation
     * @param a the lower bound
     * @param b the higher bound
     * @param t the value
     * @return the weighted value
     */
    public static double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    /**
     * This gives you the dot product of between the vector at a corner (gx, gy) and the vector between the corner and the point (px, py)
     * The formula for dot product between two vectors (a,b) and (c,d) is ac + bd.
     * @param seed the seed
     * @param gx the x cord of the corner
     * @param gy the y cord of the corner
     * @param px the x cord of the point
     * @param py the y cord of the point
     * @return a double indicating how much the two vector "agree" in direction
     */
    public static double dotGradient(long seed, int gx, int gy, double px, double py){
        double[] gradient = getGradient(seed, gx, gy);
        double dx = px - gx;
        double dy = py - gy;
        return dx * gradient[0] + dy * gradient[1];
    }

    /**
     * For each corner (gx, gy), creates a random angle and turn that into a unit vector
     * @param seed the seed
     * @param gx the x cord
     * @param gy the y cord
     * @return a unit vector corresponding to that random angle
     */
    public static double[] getGradient(long seed, int gx, int gy){
        //multiplied by 2pi to get a angle in radians on a unit circle with r = 1
        double angle = NoiseGenerator.doubleHash(gx * 73856093L ^ gy * 19349663L, seed) * 2 * Math.PI;
        //this is the x and y component of the vector with magnitude (length) = 1
        return new double[]{Math.cos(angle), Math.sin(angle)};
    }

    /**
     * gives the height of a single point generated by perlin noise
     * @param seed the seed
     * @param px the x cord of the pixel (compressed)
     * @param py the y cord of the pixel (compressed)
     * @return the height
     */
    public static double samplePerlin(long seed, double px, double py) {
        //gets the 4 corners on the noisescale surronding this point
        int x0 = (int) Math.floor(px);
        int y0 = (int) Math.floor(py);
        int x1 = x0 + 1;
        int y1 = y0 + 1;

        //same as value, make sure it is continuous and differentiable
        double tx = smoothstep(px - x0);
        double ty = smoothstep(py - y0);

        //gest the dot product between the current point and each of the corners with their own gradient vector
        double dot00 = dotGradient(seed, x0, y0, px, py);
        double dot10 = dotGradient(seed, x1, y0, px, py);
        double dot01 = dotGradient(seed, x0, y1, px, py);
        double dot11 = dotGradient(seed, x1, y1, px, py);

        //same blending process as value noise, first the top and bottom x cords, then the y cord between the two x cords
        return lerp(lerp(dot00, dot10, tx), lerp(dot01, dot11, tx), ty);

    }

    //this is our shear and un-shear factors. We need a way to change square space into triangle space. We do that by sharing one of the axis to get parallelograms. Then drawing diagnols to get equilateral triangles
    //F takes the equilateral triangle space and maps them into the integer cordinates
    private static final double F = 0.5 * (Math.sqrt(3) - 1);
    //G takes integer cordinates and maps them into the equilateral triangle space.
    //comes from 6g^2 -6g + 1 = 0, uhh just trust the math i'll write abt this in the report
    private static final double G = (3.0 - Math.sqrt(3.0)) / 6.0;

    //eight direction vectors, four diagnal, four along axis. All are unit vectors with length 1. Similar to the perlin gradient vectors, just not randomized.
    private static final double[][] GRADIENTS = {{Math.sqrt(2) / 2,  Math.sqrt(2) / 2}, {-Math.sqrt(2) / 2,  Math.sqrt(2) / 2}, {Math.sqrt(2) / 2, -Math.sqrt(2) / 2}, {-Math.sqrt(2) / 2, -Math.sqrt(2) / 2}, { 1,  0}, {-1,  0}, { 0,  1}, { 0, -1}};

    /**
     * Gives the height of a single point using simplex noise
     * @param seed the seed
     * @param x the x cord in cartesian space
     * @param y the y cord in cartesian space
     * @return the height of the point
     */
    public static double sampleSimplex(long seed, double x, double y){
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
    public static int floor(double x){
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
    public static double cornerContribution(long seed, int gi, int gj, double dx, double dy){
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
