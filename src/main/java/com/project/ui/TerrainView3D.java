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
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;
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
        int mapSize = heightMap.length;
        int gridN = Math.max(2, Math.min(lod, mapSize));

        TriangleMesh mesh = new TriangleMesh(VertexFormat.POINT_TEXCOORD);

        float[] points    = new float[gridN * gridN * 3];
        float[] texCoords = new float[gridN * gridN * 2];

        double half = WORLD_EXTENT / 2.0;
        double step = WORLD_EXTENT / (gridN - 1);

        for (int row = 0; row < gridN; row++) {
            int sy = (int) Math.round((double) row * (mapSize - 1) / (gridN - 1));
            for (int col = 0; col < gridN; col++) {
                int sx = (int) Math.round((double) col * (mapSize - 1) / (gridN - 1));
                int idx = row * gridN + col;

                double h = (heightMap[sy][sx] / 255.0) * heightScale * WORLD_EXTENT;

                points[idx * 3    ] = (float) (-half + col * step); // x
                points[idx * 3 + 1] = (float) -h;                   // y (negate: -Y is up in JavaFX)
                points[idx * 3 + 2] = (float) (-half + row * step); // z

                texCoords[idx * 2    ] = (float) sx / (mapSize - 1);
                texCoords[idx * 2 + 1] = (float) sy / (mapSize - 1);
            }
        }

        int quadCount = (gridN - 1) * (gridN - 1);
        int[] faces = new int[quadCount * 2 * 6];
        int f = 0;
        for (int row = 0; row < gridN - 1; row++) {
            for (int col = 0; col < gridN - 1; col++) {
                int v00 = row * gridN + col;
                int v10 = row * gridN + col + 1;
                int v01 = (row + 1) * gridN + col;
                int v11 = (row + 1) * gridN + col + 1;

                faces[f++] = v00; faces[f++] = v00;
                faces[f++] = v01; faces[f++] = v01;
                faces[f++] = v10; faces[f++] = v10;

                faces[f++] = v10; faces[f++] = v10;
                faces[f++] = v01; faces[f++] = v01;
                faces[f++] = v11; faces[f++] = v11;
            }
        }

        mesh.getPoints().setAll(points);
        mesh.getTexCoords().setAll(texCoords);
        mesh.getFaces().setAll(faces);

        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(textureImage);
        material.setSpecularColor(Color.BLACK);

        MeshView meshView = new MeshView(mesh);
        meshView.setMaterial(material);
        meshView.setCullFace(CullFace.NONE);

        terrainGroup.getChildren().setAll(meshView);

        double waterY = -(waterLevel * heightScale * WORLD_EXTENT);

        Box water = new Box(WORLD_EXTENT, 0.01, WORLD_EXTENT);
        water.setTranslateY(waterY);

        PhongMaterial waterMat = new PhongMaterial();
        waterMat.setDiffuseColor(Color.rgb(60, 140, 210, 0.55));
        waterMat.setSpecularColor(Color.rgb(200, 220, 240));
        water.setMaterial(waterMat);

        waterGroup.getChildren().setAll(water);
    }

    public void resetCamera() {
        yaw.setAngle(45);
        pitch.setAngle(-30);
        cameraTranslate.setZ(-1.8 * WORLD_EXTENT);
    }
}
