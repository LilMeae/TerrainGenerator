package com.project.ui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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
        HBox layout = ui.buildLayout(width, height);
        MainController controller = new MainController(ui);

        Scene scene = new Scene(layout, width, height);
        primaryStage.setTitle("2D Terrain Generator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
