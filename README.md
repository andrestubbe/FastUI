# FastUI — High-Performance, Deterministic UI for Java [v0.1.0]

**A retained-mode UI framework for the FastJava ecosystem, focusing on per-component baked layers, sequencer-style interaction, and hardware-locked performance.**

[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows-lightgrey.svg)]()
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![JitPack](https://jitpack.io/v/andrestubbe/FastUI.svg)](https://jitpack.io/#andrestubbe/FastUI)

---

**FastUI** is a next-generation UI framework built for maximum speed and zero-copy efficiency. By using a **Retained-Mode Baking Pipeline**, it bridges the gap between traditional Swing interfaces and the requirements of modern, high-refresh-rate sequencer applications.

---

## Key Features

- **🚀 Baked Layers** — Components render once into high-precision image buffers for near-instant blitting.
- **⚡ Sequencer Logic** — Advanced anchored-dragging and high-precision temporal selection (Timeline).
- **🎨 Neon-Dark Aesthetic** — Professionally curated design language with semi-transparent overlays and rounded capsules.
- **🧱 Pure Java Core** — Lightweight, decoupled codebase optimized for the FastJava monorepo.
- **🔧 Deterministic** — Predictable performance and hardware-locked timing for professional tools.

---

## Why FastUI?

Standard Java UI frameworks (AWT/Swing) rasterize vector graphics on every frame, which becomes a bottleneck on high-DPI displays. FastUI treats UI components as **Persistent Graphical Assets**. Shifting the workload from CPU rasterization to memory-mapped blitting unlocks fluidity previously unreachable in the JVM.

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

// In your VSync-locked loop
root.render(g2d); 
```

---

## Installation (JitPack)

Add the JitPack repository and the dependency to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>io.github.andrestubbe</groupId>
        <artifactId>fastui</artifactId>
        <version>v0.1.0</version>
    </dependency>
</dependencies>
```

---

## API Reference

| Component | Description |
|--------|-------------|
| `Timeline` | High-precision temporal selector capsule with daily grid and selection markers. |
| `TextArea` | Multi-line text display with automatic clipping and rounded backgrounds. |
| `TextField` | Animated single-line input field with Neon-Dark styling. |
| `Button` | High-performance interactive trigger with mouse behavior support. |
| `Image3x3` | Sliceable asset renderer for perfectly rounded capsules at any size. |

---

## Platform Support

FastUI is a **pure Java library** and is natively compatible with any platform supporting Java 17+.

---

## License
MIT License — See [LICENSE](LICENSE) file for details.

---
**Part of the FastJava Ecosystem** — *Making the JVM faster.*
