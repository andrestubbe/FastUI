# FastUI 2.0 - High-Performance Swing Framework

FastUI 2.0 is a modular, high-performance UI framework for Java Swing, designed for building fluid, sequencer-style interfaces with complex temporal interactions. It powers the visual components of the FastJava ecosystem.

## 🚀 Overview

- **Smart Composables**: Pre-configured UI blocks like the `Timeline` and `Range` selector.
- **InteractionManager**: Centralized event routing and state management (hover, active, focus).
- **Pixel-Perfect Rendering**: Native-like aesthetics with rounded corners, semi-transparent overlays, and fluid animations.
- **Sequencer Logic**: Advanced anchored dragging and screen-space filtering for temporal data.

## 📦 Installation (JitPack)

Add the following to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastUI</artifactId>
        <version>v2.0.0</version>
    </dependency>
</dependencies>
```

## 🛠 Project Structure (_BluePrint)

- `fastui.behaviour`: Core interaction logic and reusable mouse/key behaviors.
- `fastui.component`: Primitive UI components (Axis, Image3x3, etc.).
- `fastui.composable`: Higher-level assembly of components into functional widgets.
- `fastui.factory`: Graphics utilities for generating high-quality UI assets (slices, layers).

## 🎨 Aesthetic Guidelines

FastUI follows the **Neon-Dark** design language of the FastJava ecosystem:
- **Primary**: Neon Green (`#20FF80`)
- **Background**: Deep Charcoal (`#0F0F0F`)
- **Typography**: Inter / Sans Serif (Standard 22px for labels)

---
Developed by the FastJava Team.
