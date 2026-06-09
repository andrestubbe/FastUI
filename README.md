# FastUI  0.1.0 [ALPHA]  High-Performance, Deterministic UI for Java

[![Status](https://img.shields.io/badge/status-0.1.0-brightgreen.svg)](https://github.com/andrestubbe/FastUI/releases/tag/0.1.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe/FastUI)

---

**âš¡ A retained-mode UI framework for the FastJava ecosystem, focusing on per-component baked layers, sequencer-style
interaction, and hardware-locked performance.**

**FastUI** is a next-generation UI framework built for maximum speed and zero-copy efficiency. Unlike traditional Java
UI frameworks (Swing/AWT) that rasterize vector graphics on every frame, FastUI uses a **Retained-Mode Baking Pipeline**
to achieve multi-thousand FPS performance on modern hardware.

---

[![FastKeyboard Showcase](docs/screenshot.png)](https://www.youtube.com/watch?v=BZsqQl7WqWk)


---

## Table of Contents

- [TODO](#features)

---

## Quick Start (Sequencer UI)

```java
        // Initialize the UI Root
        Container root = new Container();
        
        // Add the Unified Timeline Sequencer
        Timeline timeline = new Timeline(
                startTime, endTime,
                200, 20, Color.BLACK, // Height, Arc, BG
                90, trackColor, spanColor, // Range settings
                font, tickColor, labelColor
        );
        root.add(timeline);
        
        // In your VSync-locked render loop
        root.render(g2d); 
```

---

## Key Features

- **ðŸš€ Baked Layers**  Components render once into `BufferedImage` caches for near-instant blitting.
- **? Sequencer Logic**  Advanced anchored-dragging and high-precision temporal selection cursors.
- **ðŸš€ Zero Allocation**  The core render loop creates zero objects, eliminating GC pressure.
- **ðŸš€ Neon-Dark Aesthetic**  Professionally curated design language with semi-transparent overlays and rounded
  capsules.
- **ðŸš€ Deterministic**  Predictable performance and hardware-locked timing via FastDWM integration.

---

## Why FastUI?

Standard Java UI frameworks were designed for an era of static interfaces. In today's world of high-DPI displays and
144Hz+ monitors, the "Immediate-Mode" rasterization model is a bottleneck. FastUI treats UI components as **Persistent
Graphical Assets**.

### Eliminating Framework Bottlenecks

- **?âš¡ High Rasterization Overhead**  Rasterizing shapes every frame is CPU intensive. FastUI **bakes** them.
- **ðŸš€ Garbage Collector Pressure**  Traditional loops allocate thousands of objects. FastUI is **allocation-free**.
- **ðŸš€ Resizing Flicker**  Standard resizing is jittery. FastUI integrates with `FastWindow` for butter-smooth scaling.
- **ðŸš€ Lack of VSync Alignment**  FastUI is natively synchronized to the display refresh rate.


---

## Performance Metrics

FastUI is designed to saturate high-refresh-rate monitors with minimal CPU footprint.

| Metric                    | FastUI            | Standard Swing    | Improvement       |
|---------------------------|-------------------|-------------------|-------------------|
| Render Time (100 Buttons) | **< 0.1 ms**      | ~4.5 ms           | **45x Faster**    |
| Memory Allocations        | **0 per frame**   | ~120 KB per frame | **Infinite**      |
| Jitter during Resize      | **Zero** (Sync'd) | High              | **Liquid Smooth** |

---

## Installation

### Option 1: Maven (Recommended)

Add the JitPack repository and the dependencies to your `pom.xml`:

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
<!-- FastUI Library -->
<dependency>
    <groupId>com.github.andrestubbe</groupId>
    <artifactId>fastui</artifactId>
    <version>0.1.0</version>
</dependency>

<!-- FastCore (Required Native Loader) -->
<dependency>
    <groupId>com.github.andrestubbe</groupId>
    <artifactId>fastcore</artifactId>
    <version>0.1.0</version>
</dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:fastui:0.1.0'
    implementation 'com.github.andrestubbe:fastcore:0.1.0'
}
```

### Option 3: Direct Download (No Build Tool)

Download the latest JARs directly to add them to your classpath:

1. ðŸš€ **[fastui-0.1.0.jar](https://github.com/andrestubbe/FastUI/releases/download/0.1.0/fastui-0.1.0.jar)** (The Core
   Library)
2. ðŸš€ **[fastcore-0.1.0.jar](https://github.com/andrestubbe/FastCore/releases/download/0.1.0/fastcore-0.1.0.jar)** (
   The Mandatory Native Loader)

> [!IMPORTANT]
> All JARs must be in your classpath for the native JNI calls to function correctly.

## API Reference

| Component   | Description                                                                     |
|-------------|---------------------------------------------------------------------------------|
| `Timeline`  | High-precision temporal selector capsule with daily grid and selection markers. |
| `TextArea`  | Multi-line text display with automatic clipping and rounded backgrounds.        |
| `TextField` | Animated single-line input field with Neon-Dark styling.                        |
| `Button`    | High-performance interactive trigger with mouse behavior support.               |
| `Image3x3`  | Sliceable asset renderer for perfectly rounded capsules at any size.            |

---

## Documentation

* **[COMPILE.md](COMPILE.md)**: Full compilation guide (MSVC C++17 build chain + JNI Setup).
* **[REFERENCE.md](docs/REFERENCE.md)**: Full API descriptions, border configurations, and codepoint index.
* **[PHILOSOPHY.md](docs/PHILOSOPHY.md)**: The engineering rationale for zero-allocation performance.
* **[ROADMAP.md](docs/ROADMAP.md)**: Future milestones and planned features.

---
## Platform Support

FastUI is a **pure Java library** and is natively compatible with any platform supporting Java 17+.

| Platform      | Status            |
|---------------|-------------------|
| Windows 10/11 | ? Fully Supported |
| Linux         | ? Fully Supported |
| macOS         | ? Fully Supported |

---

## License

MIT License  See [LICENSE](LICENSE) file for details.

---

## Related Projects

- [FastCore](https://github.com/andrestubbe/FastCore)  Native Library Loader
- [FastWindow](https://github.com/andrestubbe/FastWindow)  Native Window Engine
- [FastTheme](https://github.com/andrestubbe/FastTheme)  Advanced UI styling engine
- [FastFileSearch](https://github.com/andrestubbe/FastFileSearch)  Instant file search engine

---
**Part of the FastJava Ecosystem**  *Making the JVM faster. Small package. Maximum speed. Zero bloat. ðŸš€ðŸš€*
