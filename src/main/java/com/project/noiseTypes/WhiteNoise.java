package com.project.noiseTypes;

import com.project.Hash;


public class WhiteNoise implements NoiseParent{
    private long seed;
    
    public WhiteNoise(long seed){
        this.seed = seed;
    }

    public int getNoise(int x, int y){
        return (int)Math.round(Hash.seededPcgHash(x+y, seed)*255.0);
    }

    public int getNoise(long seed, int x, int y){
        setSeed(seed);
        return getNoise(x, y);
    }

    public void setSeed(long seed){
        this.seed = seed;
    }
}
