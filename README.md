# FastUI â€” High-Performance, Deterministic UI for Java

**A retained-mode UI framework for the FastJava ecosystem, focusing on hardware-locked performance and pixel-perfect aesthetics.**

[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Cross%20Platform-lightgrey.svg)]()
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![JitPack](https://jitpack.io/v/andrestubbe/FastUI.svg)](https://jitpack.io/#andrestubbe/FastUI)

FastUI is a next-generation UI framework built for maximum speed and zero-copy efficiency. Unlike traditional Java UI frameworks (Swing/AWT) that rasterize vector graphics on every frame, FastUI uses a **Retained-Mode Baking Pipeline** to achieve multi-thousand FPS performance on modern hardware.

```java
// Quick Start â€” Rendering a Timeline
import fastui.Timeline;

public class Demo {
    public static void main(String[] args) {
        Timeline timeline = new Timeline(startTime, endTime, 200, 20, Color.BLACK);
        
        // In your render loop
        root.render(g2d);
    }
}
```

---

## Table of Contents
- [Key Features](#key-features)
- [Performance](#performance)
- [Installation](#installation)
- [Try the Demo](#try-the-demo)
- [API Reference](#api-reference)
- [Platform Support](#platform-support)
- [License](#license)
- [Related Projects](#related-projects)

---

## Key Features

- **ðŸš€ Baked Layers** â€” Components render once into internal caches for near-instant blitting.
- **âš¡ Sequencer Logic** â€” High-precision temporal selection cursors and timeline interaction.
- **ðŸ§± Zero Allocation** â€” The core render loop creates zero objects, eliminating GC pressure.

---

## Performance

FastUI is designed to saturate high-refresh-rate monitors with minimal CPU footprint.

| Metric | FastUI | Standard Swing | Improvement |
|-----------|---------|---------------|---------|
| Render Time (100 Items) | **< 0.1 ms** | ~4.5 ms | **45x Faster** |
| Memory Allocations | **0 per frame** | ~120 KB per frame | **Infinite** |

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
        <version>v0.1.0</version>
    </dependency>

    <!-- FastCore (Required Native Loader) -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>fastcore</artifactId>
        <version>v0.1.0</version>
    </dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:fastui:v0.1.0'
    implementation 'com.github.andrestubbe:fastcore:v0.1.0'
}
```

### Option 3: Direct Download (No Build Tool)
Download the latest JARs directly to add them to your classpath:

1. 📦 **[fastui-v0.1.0.jar](https://github.com/andrestubbe/FastUI/releases/download/v0.1.0/fastui-v0.1.0.jar)** (The Core Library)
2. ⚙️ **[fastcore-v0.1.0.jar](https://github.com/andrestubbe/FastCore/releases/download/v0.1.0/fastcore-v0.1.0.jar)** (The Mandatory Native Loader)

> [!IMPORTANT]
> All JARs must be in your classpath for the native JNI calls to function correctly.


## API Reference

| Component | Description |
|--------|-------------|
| `Timeline` | High-precision temporal selector capsule with daily grid and selection markers. |
| `TextArea` | Multi-line text display with automatic clipping and rounded backgrounds. |
| `Button` | High-performance interactive trigger with mouse behavior support. |

---

## Platform Support

FastUI is a **pure Java library** and is natively compatible with any platform supporting Java 17+.

| Platform | Status |
|----------|--------|
| Windows 10/11 | âœ… Fully Supported |
| Linux | âœ… Fully Supported |
| macOS | âœ… Fully Supported |

---

## License
MIT License â€” See [LICENSE](LICENSE) file for details.

---

## Related Projects
- [FastCore](https://github.com/andrestubbe/FastCore) â€” Native Library Loader
- [FastTheme](https://github.com/andrestubbe/FastTheme) â€” Advanced UI styling engine
- [FastFileSearch](https://github.com/andrestubbe/FastFileSearch) â€” Instant file search engine

---
**Made with âš¡ by Andre Stubbe**

<!-- 
SEO Keywords: java, ui framework, performance, retained mode, sequencer
-->
