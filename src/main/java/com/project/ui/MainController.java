package com.project.ui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.project.NoiseGenerator;
import com.project.NoiseGenerator.NoiseType;

import javafx.embed.swing.SwingFXUtils;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MainController {
    private UIBuilder ui;
    private final TerrainView3D view3D;
    private String[] specialNoiseTypes = {"Fractal Perlin"};

    public MainController(UIBuilder ui){
        this.ui = ui;
        this.view3D = new TerrainView3D(
            ui.getRightPane().getPrefWidth(),
            ui.getRightPane().getPrefHeight()
        );
        ui.getRightPane().getChildren().add(view3D.getNode());
        view3D.getNode().setVisible(false);
        view3D.getNode().setManaged(false);

        ui.getGenerateButton().setOnAction(e -> onGenerate());
        ui.getSaveButton().setOnAction(e -> onSave());
        ui.getNoiseTypeSelector().setOnAction(e -> onChangeTerrainType());
        ui.getViewToggleGroup().selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT == null && oldT != null) {
                oldT.setSelected(true);
                return;
            }
            onViewToggle();
        });
    }

    private void onGenerate(){
        double water = ui.getWaterSlider().getValue();
        double snow = ui.getSnowSlider().getValue();
        int size = (int)ui.getSizeSlider().getValue();
        long seed = Long.parseLong(ui.getSeedField().getText());
        int noiseScale = (int) ui.getNoiseScaleSlider().getValue();
        String noiseType = ui.getNoiseTypeSelector().getValue();
        double persistance = ui.getPersistenceSlider().getValue();
        int octaves = (int)ui.getOctaveSlider().getValue();
        double lacunarity = ui.getLacunaritySlider().getValue();
        double extremity = ui.getExtremitySlider().getValue();
        double heightScale = ui.getHeightScaleSlider().getValue();
        int lod = (int) ui.getDetailSlider().getValue();
        NoiseType noiseTypeValue;

        noiseTypeValue = switch (noiseType) {
            case "Value" -> NoiseType.VALUE;
            case "Perlin" -> NoiseType.PERLIN;
            case "Simplex" -> NoiseType.SIMPLEX;
            case "Fractal Perlin" -> NoiseType.FRACTAL_PERLIN;
            default -> NoiseType.WHITE;
        };
        int heightMap[][] = NoiseGenerator.generateNoise(size, size, noiseScale,seed, persistance, octaves, lacunarity, extremity, noiseTypeValue);

        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < size; y++){
            for (int x = 0; x < size; x++){
                float value = heightMap[y][x] / 255f;
                Color color = getTerrainColor(value, water, snow);
                img.setRGB(x, y, color.getRGB());
            }
        }

        javafx.scene.image.Image fxImage = SwingFXUtils.toFXImage(img, null);
        ui.getMapView().setImage(fxImage);
        view3D.update(heightMap, fxImage, water, heightScale, lod);
    }

    private Color getTerrainColor(float value, double waterLevel, double snowLevel){
        //water to sand
        if (value < waterLevel) {
            double deepEnd = waterLevel * 0.5;
            if (value < deepEnd) return new Color(10, 50, 150);
            double t = (value - deepEnd) / (waterLevel - deepEnd);
            return blendColors(new Color(10, 50, 150), new Color(60, 140, 210), t);
        }       

        //sand to grass
        double sandEnd = waterLevel + 0.05f;
        if (value < sandEnd) {
            double t = (value - waterLevel) / (sandEnd - waterLevel);
            return blendColors(new Color(210, 180, 140), new Color(210, 180, 140), t);
        }

        //grass to mountain
        double mountainStart = snowLevel - 0.15;
        if (value < mountainStart) {
            double t = (value - sandEnd) / (mountainStart - sandEnd);
            return blendColors(new Color(60, 160, 60), new Color(100, 120, 60), t);
        }

        //mountain to snow
        if (value < snowLevel) {
            double t = (value - mountainStart) / (snowLevel - mountainStart);
            return blendColors(new Color(140, 140, 140), new Color(220, 220, 220), t);
        }

        //snow
        return new Color(255, 255, 255);
    }

    private Color blendColors(Color a, Color b, double t){
        int r = (int)(a.getRed() + t * (b.getRed() - a.getRed()));
        int g = (int)(a.getGreen() + t * (b.getGreen() - a.getGreen()));
        int bl = (int)(a.getBlue() + t * (b.getBlue()  - a.getBlue()));
        return new Color(r, g, bl);
    }

    private void onSave(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Terrain Map");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("PNG Image", "*.png"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null){
            try {
                BufferedImage img = SwingFXUtils.fromFXImage(ui.getMapView().getImage(), null);
                ImageIO.write(img, "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onViewToggle() {
        boolean is3D = ui.getView3DButton().isSelected();
        ui.getMapView().setVisible(!is3D);
        ui.getMapView().setManaged(!is3D);
        view3D.getNode().setVisible(is3D);
        view3D.getNode().setManaged(is3D);
    }

    private void onChangeTerrainType(){
        boolean special = false;
        for(String noise : specialNoiseTypes){
            if(noise.equals(ui.getNoiseTypeSelector().getValue())){
                special = true;
            }
        }
        if(special){
            ui.addSpecialSliders(ui.getLayout());
        }
        else{
            ui.removeSpecialSliders(ui.getLayout());
        }
    }
}
