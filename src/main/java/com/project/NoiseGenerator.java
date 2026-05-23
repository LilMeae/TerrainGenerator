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
            case VALUE -> new ValueNoise().generateNoise(seed, xWidth, yWidth, noiseScale);
            case WHITE -> new WhiteNoise().generateNoise(seed, xWidth, yWidth, noiseScale);
            case PERLIN -> new PerlinNoise().generateNoise(seed, xWidth, yWidth, noiseScale);
            case SIMPLEX -> new SimplexNoise().generateNoise(seed, xWidth, yWidth, noiseScale);
            case FRACTAL_PERLIN -> new FractalPerlinNoise().generateNoise(seed, xWidth, yWidth, noiseScale, persistence, octaves, lacunarity);
            case ERODED_PERLIN -> new ErodedPerlinNoise().generateNoise(seed, xWidth, yWidth, noiseScale, persistence, octaves, lacunarity);
            case FRACTAL_SIMPLEX -> new FractalSimplexNoise().generateNoise(seed, xWidth, yWidth, noiseScale, persistence, octaves, lacunarity);
            case ERODED_SIMPLEX -> new ErodedSimplexNoise().generateNoise(seed, xWidth, yWidth, noiseScale, persistence, octaves, lacunarity);
            default -> new WhiteNoise().generateNoise(seed, xWidth, yWidth, noiseScale);
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
}
