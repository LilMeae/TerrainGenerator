package com.project.noiseTypes;

public interface NoiseParent {
    public void setSeed(long seed);

    public int getNoise(long seed);

    public int getNoise();
}
