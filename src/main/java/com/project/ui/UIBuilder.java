package com.project.ui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UIBuilder {
    private Slider waterSlider;
    private Slider snowSlider;
    private TextField seedField;
    private Slider sizeSlider;
    private Button generateButton;
    private Button saveButton;
    private ImageView mapView;

    public Slider getWaterSlider(){
        return waterSlider;
    }
    public Slider getSnowSlider(){
        return snowSlider; 
    }
    public TextField getSeedField(){
        return seedField;
    }
    public Slider getSizeSlider(){
        return sizeSlider;
    }
    public Button getGenerateButton(){
        return generateButton;
    }
    public Button getSaveButton(){
        return saveButton;
    }
    public ImageView getMapView(){
        return mapView;
    }

    public HBox buildLayout(double width, double height){
        Label waterLabel = new Label("Water Level");
        this.waterSlider = new Slider(0, 1, 0.4);
        waterSlider.setShowTickLabels(true);
        waterSlider.setPrefWidth(width * 0.2);

        Label snowLabel = new Label("Snow Level");
        this.snowSlider = new Slider(0, 1, 0.8);
        snowSlider.setShowTickLabels(true);
        snowSlider.setPrefWidth(width * 0.2);

        Label seedLabel = new Label("Seed");
        this.seedField = new TextField(String.valueOf((long)(Math.random() * Long.MAX_VALUE)));
        seedField.setPrefWidth(width * 0.14);

        Button rerollButton = new Button("⟳");
        rerollButton.setOnAction(e -> seedField.setText(String.valueOf((long)(Math.random() * Long.MAX_VALUE))));
        HBox seedRow = new HBox(8, seedField, rerollButton);

        Label sizeLabel = new Label("Map Size");
        this.sizeSlider = new Slider(100, 1000, 500);
        sizeSlider.setShowTickLabels(true);
        sizeSlider.setPrefWidth(width * 0.2);

        this.generateButton = new Button("Generate");
        generateButton.setPrefWidth(width * 0.2);

        this.saveButton = new Button("Save as PNG");
        saveButton.setPrefWidth(width * 0.2);

        VBox control = new VBox();
        control.setPrefWidth(width * 0.25);
        control.setStyle("-fx-background-color: #2f2c2c6f;");
        control.setSpacing(8);
        control.setPadding(new Insets(16));
        control.getChildren().addAll(
            waterLabel, waterSlider,
            snowLabel, snowSlider,
            seedLabel, seedRow,
            sizeLabel, sizeSlider,
            generateButton, saveButton
        );
        
        this.mapView = new ImageView();
        mapView.setFitWidth(width * 0.75);
        mapView.setFitHeight(height);

        VBox map = new VBox();
        map.setPrefWidth(width * 0.75);
        map.setStyle("-fx-background-color: #e4dada83;");
        map.getChildren().addAll(mapView);

        return new HBox(control, map);
    }
}
