# 3D Terrain View — Design

**Date:** 2026-05-19
**Status:** Approved (design phase)
**Scope:** Add an orbit-camera 3D view of the generated terrain alongside the existing 2D top-down view, with a toggle between them.

---

## 1. Goal

Today, `MainController.onGenerate()` paints the heightmap as a 2D colored `BufferedImage` shown in an `ImageView`. We want to surface the same heightmap as a 3D mesh with orbit-camera navigation, while keeping the 2D view available via a toggle. Both views must remain visually consistent (same color logic, same heightmap).

## 2. Non-goals

- Shadow mapping, normal maps, water shaders, skybox / atmospheric effects.
- Free-fly or fixed-angle camera modes (orbit only).
- Saving the 3D view as an image (Save button keeps saving the 2D PNG).
- Live updates while dragging sliders. Re-render is gated on the Generate button.
- Automated tests / JUnit setup. Verification is manual + `mvn compile`.

## 3. User-visible changes

- Title bar: `"2D Terrain Generator"` → `"Terrain Generator"`.
- Control panel gains a `2D | 3D` toggle (defaults to 2D).
- Control panel gains two new sliders, always visible:
  - **Height Scale** — range `0.05`–`0.5`, default `0.25`. Multiplies map width to get max terrain altitude.
  - **3D Detail** — range `32`–`512`, default `128`. Mesh grid resolution. `snapToTicks`, `majorTickUnit = 32`.
- Right pane shows either the 2D `ImageView` or the 3D `SubScene` based on the toggle.
- Clicking Generate updates both views from the same heightmap; toggling between them is instant and does not re-render.

## 4. Architecture

```
NoiseGenerator.generateNoise(...) -> int[][] heightMap        (unchanged)

MainController.onGenerate():
    1. Compute heightMap (existing path).
    2. Build colored BufferedImage via getTerrainColor (existing path).
    3. Convert to JavaFX Image once.
    4. view2D.setImage(fxImage)                                (existing)
    5. view3D.update(heightMap, fxImage, water, heightScale, lod)   (new)

UIBuilder:
    - Left VBox: toggle at top, existing sliders, new Height Scale + 3D Detail sliders.
    - Right pane: StackPane containing mapView (ImageView) and view3D.getNode() (SubScene wrapper).
    - Toggle flips visibility (setVisible + setManaged) of the two children.

TerrainView3D (new class, com.project.ui.TerrainView3D):
    Owns: SubScene, root Group, MeshView, water plane, PerspectiveCamera,
          AmbientLight + PointLight, orbit-controller state.
    Public API:
        Node getNode()
        void update(int[][] heightMap, Image textureImage,
                    double waterLevel, double heightScale, int lod)
        void resetCamera()
```

The 2D color image is reused as the 3D mesh's `PhongMaterial.diffuseMap`. One color computation, two views — they will always look consistent.

## 5. Coordinate system & mesh

- Right-handed, Y up. Terrain centered at world origin so orbit pivots around something sensible.
- The mesh has a **fixed world extent** independent of LOD or map size: `WORLD_EXTENT = 256` world units along both x and z.
- `x` from `-WORLD_EXTENT/2` to `+WORLD_EXTENT/2`; same for `z`. Vertex spacing = `WORLD_EXTENT / (lod - 1)`.
- `y` (height) per vertex: `(heightMap[sy][sx] / 255.0) * heightScale * WORLD_EXTENT`.

Keeping the world extent fixed means changing the 3D Detail slider only changes triangle density, not scene scale — camera framing stays consistent and `Height Scale` has predictable units.

**LOD downsampling.**
- Mesh grid is `lod × lod` vertices regardless of `mapSize`.
- Sample heightmap with nearest-neighbor: `sx = round(x * (mapSize-1) / (lod-1))`, same for `sy`.
- Triangles: `2 * (lod-1)^2`.
- At lod=128: ~16K verts, ~32K tris (smooth on any machine).
- At lod=512: ~262K verts, ~520K tris (smooth on mid hardware).
- If user-provided `lod > mapSize`, clamp internally to `min(lod, mapSize)` so we never up-sample fake detail.

**TriangleMesh layout.**
- `points`: `float[lod * lod * 3]` — `(x, y, z)` per vertex, row-major.
- `texCoords`: `float[lod * lod * 2]` — `u = sx / (mapSize - 1)`, `v = sy / (mapSize - 1)`. Image stretches across the mesh exactly.
- `faces`: `int[(lod-1) * (lod-1) * 2 * 6]`. Each triangle stores `(pointIdx, texCoordIdx)` pairs as JavaFX requires; here both indices are identical (one texCoord per vertex).
- One `PhongMaterial` with `diffuseMap = textureImage` and `specularColor = Color.BLACK` (terrain shouldn't look glossy).

## 6. Water plane

- Separate `MeshView` (a single quad) at `y = waterLevel * heightScale * WORLD_EXTENT`.
- Spans the full map: `(-WORLD_EXTENT/2, +WORLD_EXTENT/2)` on both x and z.
- `PhongMaterial` with `diffuseColor = Color.rgb(60, 140, 210, 0.55)` (semi-transparent blue).
- Added to the scene graph *after* the terrain mesh so transparency sorts correctly.

## 7. Camera, lights, controls

**SubScene** wraps the 3D content for embedding in the 2D scene graph.
- Size binds to the same dimensions used by the current `mapView`.
- `depthBuffer = true`, `antiAliasing = SceneAntialiasing.BALANCED`.
- Fill color: a neutral sky gray (`Color.rgb(180, 195, 210)`).

**Camera.** `PerspectiveCamera(true)` inside a transform rig:
```
yawGroup (Rotate around Y) -> pitchGroup (Rotate around X) -> cameraTranslate (Translate on Z) -> camera
```
- Initial: `yaw = 45°`, `pitch = -30°`, `cameraTranslate.z = -1.8 * WORLD_EXTENT` (negative so camera sits in front of the origin).
- `nearClip = 0.1`, `farClip = 10 * WORLD_EXTENT`, `fieldOfView = 45`.

**Lights.**
- `AmbientLight(Color.rgb(80, 80, 90))` so shadowed slopes stay legible.
- `PointLight(Color.rgb(255, 245, 225))` positioned at `(WORLD_EXTENT, WORLD_EXTENT, -WORLD_EXTENT)` for directional shading.

**Orbit controls** (mouse handlers on the SubScene):
- **Left-drag**: yaw and pitch deltas based on mouse dx/dy at ~0.3°/px. Pitch clamped to `[-89°, +89°]`.
- **Scroll**: zoom by multiplying `cameraTranslate.z` by `0.9` (zoom in) or `1.1` (zoom out). Clamped to `[-5 * WORLD_EXTENT, -0.2 * WORLD_EXTENT]`.
- **Double-click**: `resetCamera()` restores initial yaw/pitch/distance.
- Right-drag pan is explicitly out of scope.

## 8. Regeneration model

- `Generate` button is the single commit point: it rebuilds the heightmap, the 2D image, and the 3D mesh.
- The new sliders (`Height Scale`, `3D Detail`) take effect on the next Generate click. They do not trigger live updates.
- Toggling 2D ↔ 3D never re-renders; both views are already in sync from the last Generate.

## 9. Code organization

- **New file:** `src/main/java/com/project/ui/TerrainView3D.java` (everything 3D — mesh build, camera rig, light setup, orbit handlers, water plane).
- **Modified:** `UIBuilder` — adds toggle, two new sliders, StackPane container, new getters. The 2D rendering path is left in place.
- **Modified:** `MainController` — owns a `TerrainView3D view3D`, wires the toggle listener, calls `view3D.update(...)` at the end of `onGenerate()`.
- **Modified:** `MainApp` — title bar text change.

This is the "new TerrainView3D class" option, not a pre-refactor of the existing 2D path. The 2D path stays inside `MainController` for now; extracting `TerrainView2D` is deferred.

## 10. Error handling

- `lod > mapSize`: clamped internally to `min(lod, mapSize)`. No user-facing error.
- 3D view selected before any Generate click: SubScene shows empty (sky-gray background, lights, camera). Generate populates it.
- Invalid seed input: existing behavior (Long.parseLong throws). Out of scope to change.

## 11. Verification (manual)

1. `mvn compile` succeeds with no warnings beyond pre-existing ones.
2. `mvn javafx:run` launches; toggle defaults to 2D; existing 2D behavior unchanged.
3. Switch to 3D *before* clicking Generate → empty SubScene, no crash.
4. Click Generate at defaults → textured mesh appears, lit; water plane visible at default water level.
5. Left-drag rotates around terrain center; pitch clamps prevent flipping; scroll zooms smoothly; double-click resets the camera.
6. `Height Scale = 0.05` → near-flat terrain after Generate. `Height Scale = 0.5` → dramatic peaks.
7. `3D Detail = 32` → visibly chunky mesh. `3D Detail = 512` → smooth mesh. Both render without stutter on a fresh Generate.
8. Toggle 2D ↔ 3D round-trip preserves both views; no double-regeneration.
9. Save button still saves the 2D PNG regardless of which view is active.

## 12. Out of scope (explicit YAGNI)

- Shadow mapping, normal maps, water shaders, skybox.
- Free-fly / pan / preset-angle camera modes.
- Saving the 3D view to an image.
- Live updates on slider drag.
- Refactoring the 2D path into its own class.
- Automated tests / JUnit setup.
