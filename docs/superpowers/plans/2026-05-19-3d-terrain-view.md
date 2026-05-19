# 3D Terrain View Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a JavaFX 3D orbit-camera view of the generated terrain, with a toggle between the existing 2D view and the new 3D view.

**Architecture:** A new class `com.project.ui.TerrainView3D` owns the JavaFX `SubScene`, terrain `MeshView` (built from the heightmap as a `TriangleMesh`), water plane, `PerspectiveCamera` rig, lights, and mouse-driven orbit controls. `UIBuilder` gains a toggle, two new sliders (Height Scale, 3D Detail), and a `StackPane` container holding both the 2D `ImageView` and the 3D SubScene. `MainController` instantiates `TerrainView3D` once and calls `view3D.update(...)` at the end of each `onGenerate()`. The 2D color image is reused as the mesh's diffuse texture so both views remain visually consistent.

**Tech Stack:** Java 17, JavaFX 21 (controls, graphics 3D — SubScene, TriangleMesh, MeshView, PerspectiveCamera, PhongMaterial, AmbientLight, PointLight), Maven.

**Note on testing:** This project has no test framework configured (`pom.xml` has no JUnit). Per the spec, verification is **manual** plus `mvn compile`. Steps below substitute "compile + manual run" for the usual TDD test cycle. A subagent executing this plan should NOT add JUnit; that's explicitly out of scope.

---

## File Structure

- **Create:** `src/main/java/com/project/ui/TerrainView3D.java` — owns SubScene, terrain mesh, water plane, camera rig, lights, orbit handlers. Single responsibility: render and navigate the 3D view.
- **Modify:** `src/main/java/com/project/ui/UIBuilder.java` — add toggle + 2 sliders to control VBox; convert right pane to a `StackPane` containing the existing `ImageView` (2D) and a slot the controller fills with the 3D SubScene; add new getters.
- **Modify:** `src/main/java/com/project/ui/MainController.java` — own a `TerrainView3D` field, attach its node to the right pane, wire the toggle listener, call `view3D.update(...)` after building the 2D image.
- **Modify:** `src/main/java/com/project/ui/MainApp.java` — title bar text change only.

---

## Task 1: Create empty TerrainView3D skeleton (SubScene + camera rig + lights)

Build the class and prove an empty 3D view renders (sky-gray background, camera in place, no mesh yet). This isolates JavaFX 3D setup from mesh logic.

**Files:**
- Create: `src/main/java/com/project/ui/TerrainView3D.java`

- [ ] **Step 1: Create the TerrainView3D class with empty SubScene**

Create `src/main/java/com/project/ui/TerrainView3D.java` with the following exact contents:

```java
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
```

- [ ] **Step 2: Compile**

Run: `mvn -q compile`
Expected: BUILD SUCCESS, no errors.

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/project/ui/TerrainView3D.java
git commit -m "Add TerrainView3D skeleton (empty SubScene with camera and lights)"
```

---

## Task 2: Add toggle, new sliders, and StackPane container to UIBuilder

Surface the new UI controls and create a slot in the right pane the controller can attach the 3D SubScene to.

**Files:**
- Modify: `src/main/java/com/project/ui/UIBuilder.java`

- [ ] **Step 1: Add imports, fields, and getters**

Open `src/main/java/com/project/ui/UIBuilder.java`. At the top, add these imports next to the existing JavaFX imports:

```java
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
```

Add these field declarations alongside the existing fields (e.g., after `private ComboBox<String> noiseTypeSelector;`):

```java
private ToggleGroup viewToggleGroup;
private ToggleButton view2DButton;
private ToggleButton view3DButton;
private Label heightScaleLabel;
private Slider heightScaleSlider;
private Label detailLabel;
private Slider detailSlider;
private StackPane rightPane;
```

Add these getters alongside the existing getters:

```java
public ToggleGroup getViewToggleGroup() { return viewToggleGroup; }
public ToggleButton getView2DButton()   { return view2DButton; }
public ToggleButton getView3DButton()   { return view3DButton; }
public Slider getHeightScaleSlider()    { return heightScaleSlider; }
public Slider getDetailSlider()         { return detailSlider; }
public StackPane getRightPane()         { return rightPane; }
```

- [ ] **Step 2: Build the new controls inside buildLayout()**

In `buildLayout(double width, double height)`, **before** the `VBox control = new VBox();` line, add:

```java
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
```

- [ ] **Step 3: Insert the new controls into the control VBox and convert the right pane to a StackPane**

Find the `control.getChildren().addAll(...)` call. Replace it with:

```java
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
```

Then replace the existing right-pane block:

```java
this.mapView = new ImageView();
mapView.setFitWidth(width * 0.75);
mapView.setFitHeight(height);

VBox map = new VBox();
map.setPrefWidth(width * 0.75);
map.setStyle("-fx-background-color: #e4dada83;");
map.getChildren().addAll(mapView);

layout = new HBox(control, map);
```

…with:

```java
this.mapView = new ImageView();
mapView.setFitWidth(width * 0.75);
mapView.setFitHeight(height);

rightPane = new StackPane();
rightPane.setPrefWidth(width * 0.75);
rightPane.setPrefHeight(height);
rightPane.setStyle("-fx-background-color: #e4dada83;");
rightPane.getChildren().add(mapView);

layout = new HBox(control, rightPane);
```

- [ ] **Step 4: Compile**

Run: `mvn -q compile`
Expected: BUILD SUCCESS.

- [ ] **Step 5: Manual smoke test — existing 2D behavior preserved**

Run: `mvn -q javafx:run`
Verify:
- Window opens. Control panel shows the new `2D | 3D` toggle at the top, with **2D selected**.
- Two new sliders `Height Scale` and `3D Detail` appear in the control panel.
- Click `Generate` — the existing 2D terrain image appears in the right pane exactly as before.

Close the window.

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/project/ui/UIBuilder.java
git commit -m "Add view toggle, Height Scale + 3D Detail sliders, StackPane right pane"
```

---

## Task 3: Wire TerrainView3D into MainController and implement the toggle

Attach the (still empty) 3D view and prove the toggle swaps which one is visible.

**Files:**
- Modify: `src/main/java/com/project/ui/MainController.java`

- [ ] **Step 1: Add field, instantiate in constructor, attach to right pane**

Open `src/main/java/com/project/ui/MainController.java`.

Add this field after `private UIBuilder ui;`:

```java
private final TerrainView3D view3D;
```

Replace the constructor with:

```java
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
    ui.getViewToggleGroup().selectedToggleProperty().addListener(
        (obs, oldT, newT) -> onViewToggle()
    );
}
```

- [ ] **Step 2: Add the toggle handler**

Add this method anywhere in the class (e.g., right above `onChangeTerrainType`):

```java
private void onViewToggle() {
    boolean is3D = ui.getView3DButton().isSelected();
    ui.getMapView().setVisible(!is3D);
    ui.getMapView().setManaged(!is3D);
    view3D.getNode().setVisible(is3D);
    view3D.getNode().setManaged(is3D);
}
```

Also: if the user clicks the currently-selected ToggleButton, `viewToggleGroup` would deselect it and leave nothing selected. Prevent that by adding inside the toggle listener (replace the listener line you added in Step 1 with this expanded version):

```java
ui.getViewToggleGroup().selectedToggleProperty().addListener((obs, oldT, newT) -> {
    if (newT == null && oldT != null) {
        oldT.setSelected(true);
        return;
    }
    onViewToggle();
});
```

- [ ] **Step 3: Compile**

Run: `mvn -q compile`
Expected: BUILD SUCCESS.

- [ ] **Step 4: Manual test — toggle swaps views**

Run: `mvn -q javafx:run`
Verify:
- Toggle defaults to 2D. Right pane is empty until you click Generate (existing behavior).
- Click `3D` toggle button → right pane shows a **sky-gray empty SubScene** (no mesh yet — that's correct for this task).
- Click `2D` again → 2D view returns.
- Clicking Generate while on 3D fills the (currently invisible) 2D ImageView but the 3D view stays empty. Verify by toggling back to 2D — the image is there.
- Click 2D button when already selected → it stays selected (does not deselect).

Close the window.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/project/ui/MainController.java
git commit -m "Wire TerrainView3D into MainController with view toggle"
```

---

## Task 4: Build the terrain TriangleMesh in TerrainView3D.update()

Turn the heightmap into a textured 3D mesh.

**Files:**
- Modify: `src/main/java/com/project/ui/TerrainView3D.java`

- [ ] **Step 1: Add imports**

In `TerrainView3D.java`, add these imports:

```java
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;
```

- [ ] **Step 2: Replace the empty `update()` body with mesh construction**

Replace the existing `update(...)` method with:

```java
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
}
```

> Note: JavaFX uses **-Y as up** in its default coordinate system (a standard 2D-screen convention extended to 3D). Negating `h` in the y component makes higher terrain appear higher on screen. The camera rig in Task 1 is built consistent with this.

- [ ] **Step 3: Wire MainController.onGenerate() to call view3D.update(...)**

Open `src/main/java/com/project/ui/MainController.java`.

At the top of `onGenerate()`, after the existing local variables but before the `noiseTypeValue = switch` block, add:

```java
double heightScale = ui.getHeightScaleSlider().getValue();
int lod = (int) ui.getDetailSlider().getValue();
```

Find the line:

```java
ui.getMapView().setImage(SwingFXUtils.toFXImage(img, null));
```

Replace it with:

```java
javafx.scene.image.Image fxImage = SwingFXUtils.toFXImage(img, null);
ui.getMapView().setImage(fxImage);
view3D.update(heightMap, fxImage, water, heightScale, lod);
```

- [ ] **Step 4: Compile**

Run: `mvn -q compile`
Expected: BUILD SUCCESS.

- [ ] **Step 5: Manual test — textured 3D mesh appears**

Run: `mvn -q javafx:run`
Verify:
- Click `Generate` at defaults.
- Click `3D` toggle → a textured terrain mesh is visible, colored using the same biome palette as the 2D view.
- Set `Height Scale` to `0.05`, click Generate → nearly flat mesh.
- Set `Height Scale` to `0.5`, click Generate → dramatic peaks.
- Set `3D Detail` to `32`, click Generate → visibly chunky/faceted mesh.
- Set `3D Detail` to `512`, click Generate → smooth mesh.
- Toggle 2D ↔ 3D between Generates — both views stay in sync.

Close the window.

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/project/ui/TerrainView3D.java src/main/java/com/project/ui/MainController.java
git commit -m "Build textured 3D terrain mesh from heightmap"
```

---

## Task 5: Add the water plane

A semi-transparent blue slab at the water level.

**Files:**
- Modify: `src/main/java/com/project/ui/TerrainView3D.java`

- [ ] **Step 1: Add the Box import**

Add the import:

```java
import javafx.scene.shape.Box;
```

- [ ] **Step 2: Build a water plane at the bottom of update()**

At the very end of the `update(...)` method (after `terrainGroup.getChildren().setAll(meshView);`), append:

```java
double waterY = -(waterLevel * heightScale * WORLD_EXTENT);

Box water = new Box(WORLD_EXTENT, 0.01, WORLD_EXTENT);
water.setTranslateY(waterY);

PhongMaterial waterMat = new PhongMaterial();
waterMat.setDiffuseColor(Color.rgb(60, 140, 210, 0.55));
waterMat.setSpecularColor(Color.rgb(200, 220, 240));
water.setMaterial(waterMat);

waterGroup.getChildren().setAll(water);
```

> Note: y is negated for the same reason as the mesh — JavaFX uses -Y as up.

- [ ] **Step 3: Compile**

Run: `mvn -q compile`
Expected: BUILD SUCCESS.

- [ ] **Step 4: Manual test — water plane visible**

Run: `mvn -q javafx:run`
Click Generate, switch to 3D. Verify:
- A semi-transparent blue plane covers the map at the water level. Submerged terrain (deep blue colors) is visible through it.
- Move the Water Level slider to `0.8`, regenerate → water plane is high up, most terrain is submerged.
- Move it to `0.1`, regenerate → water plane is low, most terrain is dry.

Close.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/project/ui/TerrainView3D.java
git commit -m "Add semi-transparent water plane at water level"
```

---

## Task 6: Orbit camera controls

Mouse-driven yaw/pitch/zoom/reset.

**Files:**
- Modify: `src/main/java/com/project/ui/TerrainView3D.java`

- [ ] **Step 1: Add MouseButton import**

Add:

```java
import javafx.scene.input.MouseButton;
```

- [ ] **Step 2: Add mouse-state fields**

Add these fields next to the existing `yaw` / `pitch` / `cameraTranslate` fields:

```java
private double dragLastX;
private double dragLastY;
```

- [ ] **Step 3: Install mouse handlers in the constructor**

At the **end** of the `TerrainView3D` constructor (after `subScene.setCamera(camera);`), add:

```java
subScene.setOnMousePressed(e -> {
    dragLastX = e.getSceneX();
    dragLastY = e.getSceneY();
    if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
        resetCamera();
    }
});

subScene.setOnMouseDragged(e -> {
    if (e.getButton() != MouseButton.PRIMARY) return;
    double dx = e.getSceneX() - dragLastX;
    double dy = e.getSceneY() - dragLastY;
    dragLastX = e.getSceneX();
    dragLastY = e.getSceneY();

    yaw.setAngle(yaw.getAngle() + dx * 0.3);

    double newPitch = pitch.getAngle() - dy * 0.3;
    if (newPitch >  89) newPitch =  89;
    if (newPitch < -89) newPitch = -89;
    pitch.setAngle(newPitch);
});

subScene.setOnScroll(e -> {
    double factor = (e.getDeltaY() > 0) ? 0.9 : 1.1;
    double newZ = cameraTranslate.getZ() * factor;
    double minZ = -5.0 * WORLD_EXTENT;
    double maxZ = -0.2 * WORLD_EXTENT;
    if (newZ < minZ) newZ = minZ;
    if (newZ > maxZ) newZ = maxZ;
    cameraTranslate.setZ(newZ);
});
```

> Note: We trigger `resetCamera()` on mouse-pressed with `getClickCount() == 2` because JavaFX delivers the second press of a double-click as a press event with click-count 2.

- [ ] **Step 4: Compile**

Run: `mvn -q compile`
Expected: BUILD SUCCESS.

- [ ] **Step 5: Manual test — orbit controls**

Run: `mvn -q javafx:run`
Click Generate, switch to 3D. Verify:
- Left-drag horizontally → terrain rotates around vertical axis (yaw).
- Left-drag vertically → camera tilts up/down (pitch). Camera does **not** flip upside-down at extremes.
- Scroll wheel → zoom in/out, smooth, clamped (can't zoom past terrain center, can't zoom infinitely far).
- Double-click → camera resets to the initial 45°/-30° angle at default distance.

Close.

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/project/ui/TerrainView3D.java
git commit -m "Add orbit camera controls (drag, scroll, double-click reset)"
```

---

## Task 7: Update title and final verification

Title text change + a full spec-driven verification pass.

**Files:**
- Modify: `src/main/java/com/project/ui/MainApp.java`

- [ ] **Step 1: Update the window title**

In `src/main/java/com/project/ui/MainApp.java`, replace:

```java
primaryStage.setTitle("2D Terrain Generator");
```

with:

```java
primaryStage.setTitle("Terrain Generator");
```

- [ ] **Step 2: Compile**

Run: `mvn -q compile`
Expected: BUILD SUCCESS.

- [ ] **Step 3: Full manual verification pass (from spec §11)**

Run: `mvn -q javafx:run`
Walk through every checklist item and confirm each passes:

1. Window title reads `Terrain Generator` (not "2D Terrain Generator").
2. Toggle defaults to 2D; clicking Generate produces the existing 2D output exactly as before.
3. Switching to 3D **before** clicking Generate shows an empty sky-gray SubScene with no crash.
4. Click Generate at defaults → textured mesh visible, lit, with water plane at default water level.
5. Left-drag rotates around terrain center; pitch clamps prevent flipping; scroll zooms smoothly; double-click resets.
6. `Height Scale = 0.05` → near-flat mesh after Generate. `Height Scale = 0.5` → dramatic peaks.
7. `3D Detail = 32` → chunky mesh. `3D Detail = 512` → smooth mesh. Both render without stutter.
8. Toggle 2D ↔ 3D round-trips preserve both views; no re-render on toggle.
9. Save button still saves the 2D PNG regardless of active view.

If any item fails, fix it before committing.

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/project/ui/MainApp.java
git commit -m "Rename window title to 'Terrain Generator'"
```

---

## Done

Verification: all 9 spec checklist items pass on a fresh `mvn javafx:run`. The 2D view behaves identically to the pre-change codebase; the 3D view renders, orbits, zooms, resets; both views derive from the same heightmap and color image.
