package com.project.noiseTypes;

public interface NoiseParent {
    public void setSeed(long seed);

    public int getNoise(long seed, int x, int y);

    public int getNoise(int x, int y);
}
