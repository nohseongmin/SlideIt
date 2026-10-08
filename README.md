# SlideIT

An Android app for creating, sharing, and storing business cards. Features include simple text editing, gesture-based sharing, search and sorting, OCR, and a dark theme.

Built with Kotlin and Jetpack Compose. Libraries include Room, Coil, ML Kit, Gson, CameraX, and DataStore.

## Canvas editor work

The planned editor adds freeform canvas design alongside the existing text form. Both card types would use a shared renderer and export to images.

The last recorded update is January 21, 2025. Phases 1 and 2 were marked complete with successful builds. Later phases remained pending; the original plan estimated seven-to-nine days in total.

| Phase | Scope | Recorded status | Original estimate |
|---|---|---|---|
| 1 | Data model | Complete | Half a day |
| 2 | Editor selection | Complete | Half a day |
| 3 | Unified rendering | Pending | One day |
| 4 | Canvas editor | Pending | Three-to-five days |
| 5 | Bitmap conversion | Pending | One day |
| 6 | Testing and integration | Pending | One day |

## Completed changes

The BusinessCard model has `editorType`, JSON `canvasData`, and `thumbnailPath`. CardElement defines text, image, and shape elements, with CanvasCardData and JSON converters. The database was updated to version 3.

EditorTypeSelectionDialog offers simple and detailed editing and connects to MainActivity navigation, including OCR and edit-mode selection.

The same update fixed the missing Color import in Theme.kt and deprecated API use in CameraScreen.kt.

## Remaining work

- Implement CardRenderer with SimpleCardRenderer and CanvasCardRenderer, scaling, and integration into sharing and storage screens.
- Build CardCanvasEditorScreen, CardCanvas, and CanvasToolbar, with save and cancel controls.
- Support adding text, images, rectangles, and circles.
- Add movement, pinch resizing, rotation, selection, resize handles, property editing, layer order, and deletion.
- Implement CanvasEditorViewModel, GestureHandler, and BitmapConverter.
- Check editing-mode transitions, rendering quality, sharing, persistence, and performance.

## Source layout

```text
app/src/main/java/com/example/slideit/
  data/model/        BusinessCard and CardElement
  data/dao/          BusinessCardDao
  data/database/     AppDatabase
  data/repository/   BusinessCardRepository
  ui/screens/        Editing, sharing, and storage
  ui/components/     Selection dialog and planned renderers
  ui/theme/          Theme
  viewmodel/         Card and planned canvas state
  util/              OCR and planned bitmap or gesture utilities
  MainActivity.kt    Navigation
```

The next planned step is the unified renderer and its sharing-screen integration. Use repository issues for project questions.
