package com.project.noiseTypes;

import java.util.Random;

public class WhiteNoise implements NoiseParent{
    private long seed;
    private Random random;

    public WhiteNoise(long seed){
        this.seed = seed;
        random = new Random(this.seed);
    }

    public int getNoise(){
        return (int)Math.round(random.nextDouble()*255.0);
    }

    public int getNoise(long seed){
        setSeed(seed);
        return getNoise();
    }

    public void setSeed(long seed){
        this.seed = seed;
    }
}
