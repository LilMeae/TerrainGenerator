package com.project.ui;

import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class TerrainView3D {

    static final double WORLD_EXTENT = 256.0;

    private final SubScene subScene;
    private final Group root;
    private final Group terrainGroup;
    private final Group waterGroup;

    private final Rotate yaw   = new Rotate(45,  Rotate.Y_AXIS);
    private final Rotate pitch = new Rotate(-30, Rotate.X_AXIS);
    private final Translate cameraTranslate = new Translate(0, 0, -1.8 * WORLD_EXTENT);

    public TerrainView3D(double width, double height) {
        terrainGroup = new Group();
        waterGroup   = new Group();

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10 * WORLD_EXTENT);
        camera.setFieldOfView(45);

        Group cameraRig = new Group(camera);
        cameraRig.getTransforms().addAll(yaw, pitch, cameraTranslate);

        AmbientLight ambient = new AmbientLight(Color.rgb(80, 80, 90));
        PointLight   sun     = new PointLight(Color.rgb(255, 245, 225));
        sun.setTranslateX( WORLD_EXTENT);
        sun.setTranslateY( WORLD_EXTENT);
        sun.setTranslateZ(-WORLD_EXTENT);

        root = new Group(terrainGroup, waterGroup, ambient, sun, cameraRig);

        subScene = new SubScene(root, width, height, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.rgb(180, 195, 210));
        subScene.setCamera(camera);
    }

    public Node getNode() {
        return subScene;
    }

    public void update(int[][] heightMap, Image textureImage,
                       double waterLevel, double heightScale, int lod) {
        // mesh + water plane are built in later tasks
    }

    public void resetCamera() {
        yaw.setAngle(45);
        pitch.setAngle(-30);
        cameraTranslate.setZ(-1.8 * WORLD_EXTENT);
    }
}
