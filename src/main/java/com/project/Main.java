package com.project;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        int[][] graph = new int[100][100];
        TerrainMap map = new TerrainMap(graph);
        System.out.println(map.getHeight());
    }
}