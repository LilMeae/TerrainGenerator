package com.project.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainApp extends Application{
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage){
        double width = Screen.getPrimary().getBounds().getWidth();
        double height = Screen.getPrimary().getBounds().getHeight();

        UIBuilder ui = new UIBuilder();
        ui.buildLayout(width, height);
        HBox layout = ui.getLayout();
        MainController controller = new MainController(ui);

        Scene scene = new Scene(layout, width, height);
        primaryStage.setTitle("2D Terrain Generator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
