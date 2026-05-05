package com.project.ui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

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
        String seed = ui.getSeedField().getText();

        System.out.println("Water: " + water);
        System.out.println("Snow: " + snow);
        System.out.println("Size: " + size);
        System.out.println("Seed: " + seed);

        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < size; x++){
            for (int y = 0; y < size; y++){
                img.setRGB(x, y, new Color(255, 0, 0).getRGB());
            }
        }

        ui.getMapView().setImage(SwingFXUtils.toFXImage(img, null));
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
