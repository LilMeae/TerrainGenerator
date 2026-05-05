package com.project;


public class TerrainMap {
    private int[][] heightMap;
    public TerrainMap(int[][] heightMap){
        this.heightMap = heightMap;
    }

    //x is width, y is height
    public int getHeight(int x, int y){
        if (!isInBounds(x, y)){
            return Integer.MAX_VALUE;
        }
        return heightMap[x][y];
    }

    public int getWidth(){
        return heightMap.length;
    }

    public int getHeight(){
        return heightMap[0].length;
    }

    public boolean isInBounds(int x, int y){
        if (getWidth() <= x || getHeight() <= y){
            return false;
        }
        return true;
    }

    public void fillRandom(){
        for (int x = 0; x < getWidth(); x++){
            for (int y = 0; y < getHeight(); y++){
                heightMap[x][y] = (int)(256 * Math.random());
            }
        }
    }
}
