package com.project.noiseTypes;

import com.project.NoiseGenerator;

public abstract class PerlinBase implements NoiseTemplate {

    protected double samplePerlin(long seed, double px, double py) {
        int x0 = (int) Math.floor(px);
        int y0 = (int) Math.floor(py);
        int x1 = x0 + 1;
        int y1 = y0 + 1;

        double tx = NoiseGenerator.fade(px - x0);
        double ty = NoiseGenerator.fade(py - y0);

        double dot00 = NoiseGenerator.dotGradient(seed, x0, y0, px, py);
        double dot10 = NoiseGenerator.dotGradient(seed, x1, y0, px, py);
        double dot01 = NoiseGenerator.dotGradient(seed, x0, y1, px, py);
        double dot11 = NoiseGenerator.dotGradient(seed, x1, y1, px, py);

        return NoiseGenerator.lerp(
            NoiseGenerator.lerp(dot00, dot10, tx),
            NoiseGenerator.lerp(dot01, dot11, tx),
            ty
        );
    }
}
