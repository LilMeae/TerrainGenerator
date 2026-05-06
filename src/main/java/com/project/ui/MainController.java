package com.project.ui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.project.NoiseGenerator;
import com.project.NoiseGenerator.NoiseType;
import com.project.noiseTypes.NoiseTemplate;
import com.project.noiseTypes.PerlinNoise;
import com.project.noiseTypes.RandomNoise;
import com.project.noiseTypes.SimplexNoise;
import com.project.noiseTypes.ValueNoise;
import com.project.noiseTypes.WhiteNoise;

import javafx.embed.swing.SwingFXUtils;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MainController {
    private UIBuilder ui;

    public MainController(UIBuilder ui){
        this.ui = ui;
        ui.getGenerateButton().setOnAction(e -> onGenerate());
        ui.getSaveButton().setOnAction(e -> onSave());
    }

    private void onGenerate(){
        double water = ui.getWaterSlider().getValue();
        double snow = ui.getSnowSlider().getValue();
        int size = (int)ui.getSizeSlider().getValue();
        long seed = Long.parseLong(ui.getSeedField().getText());
        int noiseScale = (int) ui.getNoiseScaleSlider().getValue();
        String noiseType = ui.getNoiseTypeSelector().getValue();

        NoiseTemplate noiseGen;
        switch (noiseType) {
            case "Value":
                noiseGen = new ValueNoise();
                break;
            case "Perlin":
                noiseGen = new PerlinNoise();
                break;
            case "Simplex":
                noiseGen = new SimplexNoise();
                break;
            case "White":
                noiseGen = new WhiteNoise();
                break;
            default:
                noiseGen = new RandomNoise();
                break;
        }
        int heightMap[][] = noiseGen.generateNoise(seed, size, size, noiseScale);

        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < size; y++){
            for (int x = 0; x < size; x++){
                float value = heightMap[y][x] / 255f;
                Color color = getTerrainColor(value, water, snow);
                img.setRGB(x, y, color.getRGB());
            }
        }

        ui.getMapView().setImage(SwingFXUtils.toFXImage(img, null));
    }

    private Color getTerrainColor(float value, double waterLevel, double snowLevel){
        if (value < waterLevel - 0.15f) return new Color(10, 30, 100);
        if (value < waterLevel) return new Color(30, 100, 200);
        if (value < waterLevel + 0.05f) return new Color(210, 180, 140);
        if (value > snowLevel) return new Color(255, 255, 255);
        if (value > snowLevel - 0.1f) return new Color(140, 140, 140);
        return new Color(60, 160, 60);
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
}
