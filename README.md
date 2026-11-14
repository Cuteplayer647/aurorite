# AuroriteClient

[![License: GPL-3.0](https://img.shields.io/badge/license-GPL--3.0--or--later-blue.svg)](LICENSE)
[![Minecraft 26.3](https://img.shields.io/badge/minecraft-26.3-green.svg)](https://github.com/Cuteplayer647/aurorite)

Minecraft 26.3 的 Fabric 客户端，含 ClickGUI、HUD 模块与 FreeLook。

A lightweight Fabric client for **Minecraft 26.3**, built around a frosted-glass ClickGUI and a small set of
movement and HUD modules.

- Repository: <https://github.com/Cuteplayer647/aurorite>
- Issues: <https://github.com/Cuteplayer647/aurorite/issues>

## Features

| Module | What it does |
| --- | --- |
| **CPS显示** | Left/right clicks per second, with optional dark mode, corner radius and background opacity |
| **按键显示** | WASD + space keystrokes, highlighted while held |
| **Combo** | Consecutive hits on one entity; resets after 5 s, on a new target, or when that entity hits back |
| **血量显示** | Current health as `12.5 ❤`, coloured green / amber / red |
| **FPS显示** | Current framerate |
| **自动疾跑** | Keeps sprinting while the forward key is held |
| **夜视** | Full brightness by raising the gamma value (not a night vision effect) |
| **Zoom** | Hold a key to set the field of view to a chosen value (30–110) |
| **FreeLook** | Hold a key to switch to third person and orbit the camera without turning the body |
| **Watermark** | Draws the client logo on the HUD |

The ClickGUI itself is split into a 70% white module list and a 50% white settings panel over a blurred
background, with scrollable lists, per-module settings and a HUD editor.

## Requirements

- **Java 25**
- **Minecraft 26.3**
- **Fabric Loader 0.20.1+**
- **Fabric API** (any build for 26.3)

## Building

```bash
./gradlew build
```

The mod jar ends up in `build/libs/`.

> **Note:** Minecraft 26.3 ships unobfuscated, so Loom generates **no `remapJar` task** and the jar in
> `build/libs` is already production ready. This is expected, not a missing build step.

## Default keybinds

| Action | Key | Configurable |
| --- | --- | --- |
| Open / close the ClickGUI | `P` | yes, in the ClickGUI settings |
| FreeLook | `Y` | yes |
| Zoom | `C` | yes |

Key binds accept **A–Z and 0–9** only. Everything is changed from inside the ClickGUI: right click a module
to open its settings, then click the key box and press the new key.

## Configuration

Settings are stored in `config/aurorite.json` and are written whenever you change something, so module states,
every setting value and the HUD positions survive a restart.

## Known limitations

- Built against **Minecraft 26.3** only. The client touches several version specific internals (the GUI
  `GuiGraphicsExtractor`, `LocalPlayer.getViewYRot`, `OptionInstance`) and will need work on a new Minecraft
  version.
- FreeLook relies on `LocalPlayer` overriding `getViewYRot`; if a future version changes that, the yaw offset
  stops working.
- The night vision module writes the gamma field directly, because `OptionInstance.set` clamps it to 1.0.
  The original value is restored on exit, and a leftover value is cleaned up on the next launch.
- GUI sizes are computed from the smallest of several window metrics to cope with scaled desktops; on some
  setups the interface may look slightly smaller than it could.
- Key binds are limited to letters and digits by design.

## Artwork

- The menu background (`aurora.png`) was generated with an AI image tool.
- The client logo and the mod icon were generated for this project.

## License

Copyright (C) 2026 Cuteplayer647.

This program is free software: you can redistribute it and/or modify it under the terms of the
**GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
or (at your option) any later version**.

This program is distributed in the hope that it will be useful, but **WITHOUT ANY WARRANTY**; without even the
implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the `LICENSE` file for details.

If you publish a modified version, it must also be released under the GPL.
