package com.project;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        NoiseGenerator noiseGenerator = new NoiseGenerator(Math.round(Math.random()*Long.MAX_VALUE));
        noiseGenerator.generateNoise(10, 10, NoiseGenerator.NoiseType.WHITE);
        int[][] graph = new int[100][100];
        TerrainMap map = new TerrainMap(graph);
        System.out.println(map.getHeight());
    }
}