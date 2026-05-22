package com.project.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class UIBuilder {
    private Label waterLabel;
    private Slider waterSlider;
    private Label snowLabel;
    private Slider snowSlider;
    private Label seedLabel;
    private TextField seedField;
    private Label sizeLabel;
    private Slider sizeSlider;
    private Button generateButton;
    private Button saveButton;
    private ImageView mapView;
    private Label noiseScaleLabel;
    private Slider noiseScaleSlider;
    private Label extremityLabel;
    private Slider extremitySlider;
    private Label persistenceLabel;
    private Slider persistenceSlider;
    private Label octaveLabel;
    private Slider octaveSlider;
    private Label lacunarityLabel;
    private Slider lacunaritySlider;
    private Label noiseTypeLabel;
    private ComboBox<String> noiseTypeSelector;
    private ToggleGroup viewToggleGroup;
    private ToggleButton view2DButton;
    private ToggleButton view3DButton;
    private Label heightScaleLabel;
    private Slider heightScaleSlider;
    private Label detailLabel;
    private Slider detailSlider;
    private StackPane rightPane;
    private HBox layout;

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
    public Slider getNoiseScaleSlider(){
        return noiseScaleSlider;
    }

    public Slider getPersistenceSlider(){
        return persistenceSlider;
    }

    public Slider getOctaveSlider(){
        return octaveSlider;
    }

    public Slider getLacunaritySlider(){
        return lacunaritySlider;
    }

    public ComboBox<String> getNoiseTypeSelector(){
        return noiseTypeSelector;
    }

    public HBox getLayout(){
        return layout;
    }

    public Slider getExtremitySlider(){
        return extremitySlider;
    }

    public ToggleGroup getViewToggleGroup() { return viewToggleGroup; }
    public ToggleButton getView2DButton()   { return view2DButton; }
    public ToggleButton getView3DButton()   { return view3DButton; }
    public Slider getHeightScaleSlider()    { return heightScaleSlider; }
    public Slider getDetailSlider()         { return detailSlider; }
    public StackPane getRightPane()         { return rightPane; }

    public void buildLayout(double width, double height){
        waterLabel = new Label("Water Level");
        this.waterSlider = new Slider(0, 1, 0.4);
        waterSlider.setShowTickLabels(true);
        waterSlider.setPrefWidth(width * 0.2);

        snowLabel = new Label("Snow Level");
        this.snowSlider = new Slider(0, 1, 0.8);
        snowSlider.setShowTickLabels(true);
        snowSlider.setPrefWidth(width * 0.2);

        seedLabel = new Label("Seed");
        this.seedField = new TextField(String.valueOf((long)(Math.random() * Long.MAX_VALUE)));
        seedField.setPrefWidth(width * 0.14);

        Button rerollButton = new Button("⟳");
        rerollButton.setOnAction(e -> seedField.setText(String.valueOf((long)(Math.random() * Long.MAX_VALUE))));
        HBox seedRow = new HBox(8, seedField, rerollButton);

        sizeLabel = new Label("Map Size");
        this.sizeSlider = new Slider(100, 1000, 500);
        sizeSlider.setShowTickLabels(true);
        sizeSlider.setPrefWidth(width * 0.2);

        noiseScaleLabel = new Label("Noise Scale");
        this.noiseScaleSlider = new Slider(1, 20, 8);
        noiseScaleSlider.setShowTickLabels(true);
        noiseScaleSlider.setPrefWidth(width * 0.2);
        
        extremityLabel = new Label ("Extremity");
        this.extremitySlider = new Slider(1, 10, 1);
        extremitySlider.setShowTickLabels(true);
        extremitySlider.setPrefWidth(width*0.2);

        persistenceLabel = new Label("Persistence");
        this.persistenceSlider = new Slider(0, 1, 0.05);
        persistenceSlider.setShowTickLabels(true);
        persistenceSlider.setPrefHeight(width * 0.2);

        octaveLabel = new Label("Octaves");
        this.octaveSlider = new Slider(1, 100, 1);
        octaveSlider.setShowTickLabels(true);
        octaveSlider.setPrefHeight(width * 0.2);

        lacunarityLabel = new Label("Lacunarity");
        this.lacunaritySlider = new Slider(0, 1, 0.05);
        lacunaritySlider.setShowTickLabels(true);
        lacunaritySlider.setPrefHeight(width * 0.2);

        this.generateButton = new Button("Generate");
        generateButton.setPrefWidth(width * 0.2);

        this.saveButton = new Button("Save as PNG");
        saveButton.setPrefWidth(width * 0.2);

        noiseTypeLabel = new Label("Noise Type");
        this.noiseTypeSelector = new ComboBox<>();
        noiseTypeSelector.getItems().addAll("Value", "Perlin", "White", "Simplex", "Fractal Perlin", "Eroded Perlin", "Fractal Simplex", "Eroded Simplex");
        noiseTypeSelector.setValue("Fractal Perlin");
        noiseTypeSelector.setPrefWidth(width * 0.2);

        viewToggleGroup = new ToggleGroup();
        view2DButton = new ToggleButton("2D");
        view3DButton = new ToggleButton("3D");
        view2DButton.setToggleGroup(viewToggleGroup);
        view3DButton.setToggleGroup(viewToggleGroup);
        view2DButton.setSelected(true);
        HBox viewToggleRow = new HBox(8, view2DButton, view3DButton);

        heightScaleLabel = new Label("Height Scale");
        this.heightScaleSlider = new Slider(0.05, 0.5, 0.25);
        heightScaleSlider.setShowTickLabels(true);
        heightScaleSlider.setPrefWidth(width * 0.2);

        detailLabel = new Label("3D Detail");
        this.detailSlider = new Slider(32, 512, 128);
        detailSlider.setShowTickLabels(true);
        detailSlider.setSnapToTicks(true);
        detailSlider.setMajorTickUnit(32);
        detailSlider.setMinorTickCount(0);
        detailSlider.setPrefWidth(width * 0.2);

        VBox control = new VBox();
        control.setId("control");
        control.setPrefWidth(width * 0.25);
        control.setStyle("-fx-background-color: #2f2c2c6f;");
        control.setSpacing(4);
        control.setPadding(new Insets(16));
        control.getChildren().addAll(
            viewToggleRow,
            waterLabel, waterSlider,
            snowLabel, snowSlider,
            seedLabel, seedRow,
            sizeLabel, sizeSlider,
            noiseScaleLabel, noiseScaleSlider,
            noiseTypeLabel, noiseTypeSelector,
            heightScaleLabel, heightScaleSlider,
            detailLabel, detailSlider,
            generateButton, saveButton,
            extremityLabel, extremitySlider,
            persistenceLabel, persistenceSlider,
            lacunarityLabel, lacunaritySlider,
            octaveLabel, octaveSlider
        );
        
        this.mapView = new ImageView();
        mapView.setFitWidth(width * 0.75);
        mapView.setFitHeight(height);

        rightPane = new StackPane();
        rightPane.setPrefWidth(width * 0.75);
        rightPane.setPrefHeight(height);
        rightPane.setStyle("-fx-background-color: #e4dada83;");
        rightPane.getChildren().add(mapView);

        layout = new HBox(control, rightPane);
    }

    public void removeSpecialSliders(HBox box){
        VBox vbox = (VBox) box.getChildren().stream()
         .filter(node -> node instanceof VBox && "control".equals(node.getId()))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Control vBox not found"));

        vbox.getChildren().removeAll(
            persistenceLabel, persistenceSlider,
            lacunarityLabel, lacunaritySlider,
            octaveLabel, octaveSlider
        );
    }

    public void addSpecialSliders (HBox box){
        VBox vbox = (VBox) box.getChildren().stream()
         .filter(node -> node instanceof VBox && "control".equals(node.getId()))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Control vBox not found"));
        vbox.getChildren().addAll(
            persistenceLabel, persistenceSlider,
            lacunarityLabel, lacunaritySlider,
            octaveLabel, octaveSlider
        );
    }
}
