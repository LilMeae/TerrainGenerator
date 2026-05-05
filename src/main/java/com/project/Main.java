package com.project;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        NoiseGenerator noiseGenerator = new NoiseGenerator(Math.round(Math.random()*Long.MAX_VALUE));
        noiseGenerator.generateNoise(10, 10, NoiseGenerator.NoiseType.WHITE);
    }
}