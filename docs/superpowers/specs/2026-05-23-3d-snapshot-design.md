# 3D Terrain Snapshot — Save as PNG

**Date:** 2026-05-23
**Status:** Design approved, pending implementation plan

## Goal

Extend the existing "Save as PNG" feature so it also works when the 3D view is active. Clicking **Save** should write the currently-displayed 3D rendering (camera angle, zoom, terrain, water plane, lighting) to a PNG file. The 2D save path is unchanged.

## Non-Goals

- No new UI controls; the existing single **Save as PNG** button handles both modes.
- No batch export, no multi-angle export, no animation export.
- No user-configurable resolution; fixed at 1920×1080.
- No tests added; the project has no test harness and this is pure UI plumbing.

## User-Visible Behavior

| User state          | Save action result                                                       |
| ------------------- | ------------------------------------------------------------------------ |
| 2D view active      | Existing behavior — writes the heightmap-resolution PNG from `mapView`.  |
| 3D view active      | Writes a 1920×1080 PNG of the current 3D scene at the live camera pose.  |
| No terrain yet (3D) | No file written; silent no-op.                                           |
| No terrain yet (2D) | No file written; silent no-op (adds a null guard parallel to 3D path).   |
| FileChooser cancel  | No file written; unchanged.                                              |

The 1920×1080 export renders at the target resolution directly (not a scale of the on-screen view), so the PNG shows a wider horizontal FOV than the on-screen 3D pane when the pane's aspect is narrower than 16:9. Vertical FOV (45°) and camera angle/zoom are preserved exactly.

## Architecture

Two files change. No new files, no new dependencies.

### `src/main/java/com/project/ui/TerrainView3D.java`

Add one public method:

```java
public BufferedImage snapshot(int width, int height)
```

Responsibilities:

1. If `terrainGroup.getChildren().isEmpty()`, return `null` (no terrain to capture).
2. Save the SubScene's current width/height.
3. Resize the SubScene to the target dimensions.
4. Build a `SnapshotParameters` with `setFill(Color.rgb(180, 195, 210))` so the sky background matches the on-screen fill.
5. Allocate a `WritableImage(width, height)`.
6. Call `subScene.snapshot(params, fxImage)` (synchronous on the JavaFX Application Thread).
7. Restore the SubScene's original width/height.
8. Return `SwingFXUtils.fromFXImage(fxImage, null)`.

Required new imports:
- `java.awt.image.BufferedImage`
- `javafx.embed.swing.SwingFXUtils`
- `javafx.scene.SnapshotParameters`
- `javafx.scene.image.WritableImage`

The SubScene's camera is already attached, so the snapshot uses it automatically; no `params.setCamera(...)` call is needed.

### `src/main/java/com/project/ui/MainController.java`

Add constants:

```java
private static final int SNAPSHOT_WIDTH = 1920;
private static final int SNAPSHOT_HEIGHT = 1080;
```

Modify `onSave()` to branch by view mode:

```java
private void onSave() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Terrain Map");
    fileChooser.getExtensionFilters().add(new ExtensionFilter("PNG Image", "*.png"));
    File file = fileChooser.showSaveDialog(null);
    if (file == null) return;

    BufferedImage img;
    if (ui.getView3DButton().isSelected()) {
        img = view3D.snapshot(SNAPSHOT_WIDTH, SNAPSHOT_HEIGHT);
    } else {
        javafx.scene.image.Image fxImage = ui.getMapView().getImage();
        if (fxImage == null) return;
        img = SwingFXUtils.fromFXImage(fxImage, null);
    }
    if (img == null) return;

    try {
        ImageIO.write(img, "png", file);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

## Why the temporary resize is invisible

JavaFX only repaints the on-screen window on the next pulse (~16 ms). Steps 2–7 of `snapshot(...)` all execute synchronously on the JavaFX Application Thread within a single button-click handler, which completes before the next pulse. The user never observes the SubScene at 1920×1080.

## Aspect-ratio note

`PerspectiveCamera` is created with `setFieldOfView(45)` (vertical FOV by JavaFX convention). When the SubScene's aspect changes from on-screen (e.g. ~1:1) to 16:9, the horizontal FOV widens accordingly. The captured PNG therefore shows more terrain on the left and right than the on-screen pane, but the camera position, yaw, pitch, and zoom are identical. This is the natural consequence of the user-selected "render at 1920×1080 directly" option and is the intended behavior.

## Error handling

- **Empty scene (3D path):** guarded by the `isEmpty()` check in `TerrainView3D.snapshot`; returns `null`, which the controller treats as a no-op.
- **Null `mapView` image (2D path):** new null guard in `onSave()`. Previous code would have NPE'd here, but the button is only reachable after the window is built, and the existing flow only sets an image after Generate. The guard makes the behavior consistent with the 3D path.
- **IOException on write:** unchanged — `printStackTrace()`. A user-facing dialog is out of scope for this change.
- **FileChooser cancel:** unchanged.

## Testing (manual)

1. Launch app, click **Save** without generating any terrain in either view mode. Expect: no crash, no file written.
2. Generate terrain in 2D view, click **Save**. Expect: PNG at the heightmap resolution (existing behavior unchanged).
3. Switch to 3D view, orbit/zoom, click **Save**. Expect: 1920×1080 PNG matching the current camera angle, with horizontal FOV slightly wider than the on-screen view.
4. Toggle from 3D back to 2D immediately after a 3D save. Expect: no visible flicker, view shows the same camera state as before the save.
5. Generate a new terrain after a 3D save. Expect: 3D view updates correctly; resize/restore did not corrupt SubScene state.
6. Cancel the file dialog in both 2D and 3D modes. Expect: no file written, no errors.

## Out of scope / future work

- User-configurable export resolution (slider or text field).
- Per-mode default filenames (e.g. `terrain-3d-<seed>.png`).
- Higher-quality off-screen rendering at >1920×1080 (would require a detached scene graph).
- Export of the heightmap as raw 16-bit PNG.
