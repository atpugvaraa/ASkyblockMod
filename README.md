# A Skyblock Mod

## Setup

For setup instructions, please see the [Fabric Documentation page](https://docs.fabricmc.net/develop/getting-started/creating-a-project#setting-up) related to the IDE that you are using.

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.


# ASM — A Swift Minecraft Mod

A Hypixel Skyblock–oriented Fabric mod for **Minecraft 26.2** that proves something new:

**writing Minecraft mod logic in Swift**, calling live (deobfuscated) Minecraft Java APIs

directly from Swift at runtime — alongside Kotlin and Java in a single mod.

> Status: proof-of-concept. Swift successfully calls `net.minecraft.network.chat.Component.literal(...)`

> at runtime inside Minecraft's JVM and reads the result back. The architecture for "Swift as the

> brain, JVM as the host" is fully validated.

## Why this exists

Minecraft Java Edition became **unobfuscated in 26.1**, and Swift gained **official Android support

+ a mature `swift-java` interop toolchain** in 6.3. Combined, they make it possible to use Swift —

not just Java/Kotlin — to write mod logic. ASM is the first working demonstration of Swift calling

Minecraft's own APIs through `swift-java`.

## Architecture

Three languages, one mod jar:

- **Kotlin / Java** — the Fabric entrypoints and glue (touches Minecraft directly, registers commands).

- **Swift (Java → Swift, FFM)** — `jextract --mode=ffm` exposes Swift functions to the JVM. Used for

  pure-logic calls (e.g. `add(Int64, Int64)`).

- **Swift (Swift → Java, wrap-java)** — `swift-java` wraps Minecraft Java classes as Swift types so

  Swift can call Minecraft APIs. Used for `Component.literal(...)`, etc.

Both interop directions coexist in a **single Swift target (`ASMCore`)** producing one `libASMCore.dylib`.

### The key insight (the part nobody had documented)

`swift-java`'s normal model assumes **Swift owns the JVM** (`JavaVirtualMachine.shared(classpath:)`

starts one). In a Minecraft mod, **Minecraft owns the JVM**. The breakthrough: calling

`JavaVirtualMachine.shared()` (no classpath) from Swift code that is *already running inside

Minecraft's JVM* (entered via the FFM downcall) **adopts the existing JVM** — no second JVM, no

manual `JNIEnv` passing. From there, `JavaClass<WrappedMinecraftType>(environment:)` + the wrapped

method just works.

## Requirements

| Tool | Version | Notes |

|---|---|---|

| Minecraft | 26.2 | Deobfuscated (26.1+) |

| JDK | 25 | Required by MC 26.2 and `swift-java` FFM mode |

| Swift | 6.3.2 | `swift-java` README pins 6.2; 6.3.2 works |

| Fabric Loom | 1.17 | Plugin id `net.fabricmc.fabric-loom` (non-obf; NOT `-remap`) |

| Fabric Loader | 0.19.3 | |

| Fabric API | 0.152.2+26.2 | |

| Fabric Language Kotlin | 1.13.12+kotlin.2.4.0 | |

| Gradle | 9.5.1 | via wrapper |

| swift-java | main (self-published) | Not on Maven Central — publish locally |

Editor: any (this project was built in **Zed**, no IntelliJ required — Gradle does all the work).

## One-time setup

### 1. Toolchain

```bash

brew install --cask temurin@25

brew install --cask zed

export JAVA_HOME="$(/usr/libexec/java_home -v 25)"   # add to ~/.zshrc

2. Build & publish swift-java locally

‎⁠swift-java⁠‘s SwiftKit libs aren’t on Maven Central; self-publish to ‎⁠~/.m2⁠:mkdir -p ~/Programming/Swift/Packages && cd ~/Programming/Swift/Packages

git clone https://github.com/swiftlang/swift-java.git

cd swift-java

export JAVA_HOME="$(/usr/libexec/java_home -v 25)"

gradle publishToMavenLocal      # produces org.swift.swiftkit:swiftkit-ffm:1.0-SNAPSHOT etc.

3. The mod project

Generated from the Fabric template (‎⁠https://fabricmc.net/develop/template/⁠) with:
Kotlin ON, Split sources ON, Data Gen OFF, Kotlin build script OFF.

Key ‎⁠build.gradle⁠ adjustments:

- ‎⁠repositories { mavenLocal(); mavenCentral() }⁠

- Fabric deps use ‎⁠modImplementation⁠ (not ‎⁠implementation⁠) — Loom’s mod pipeline.

- SwiftKit deps use plain ‎⁠implementation⁠:implementation "org.swift.swiftkit:swiftkit-ffm:1.0-SNAPSHOT"

implementation "org.swift.swiftkit:swiftkit-core:1.0-SNAPSHOT"

- Generated Java from jextract added as a source dir:sourceSets { main { java { srcDir "swift/.build/plugins/outputs/swift/ASMCore/destination/JExtractSwiftPlugin/src/generated/java" } } }

- FFM run args in ‎⁠loom { runs { client { ... } } }⁠:vmArg "--enable-native-access=ALL-UNNAMED"

vmArg "-Djava.library.path=" + file("swift/.build/arm64-apple-macosx/debug").absolutePath + File.pathSeparator + System.getProperty("java.library.path")

The Swift package (‎⁠swift/⁠)

- ‎⁠Package.swift⁠ — dynamic library ‎⁠ASMCore⁠, depends on ‎⁠SwiftJava⁠ + ‎⁠SwiftRuntimeFunctions⁠,
uses three plugins: ‎⁠JExtractSwiftPlugin⁠, ‎⁠JavaCompilerPlugin⁠, ‎⁠SwiftJavaPlugin⁠.

- ‎⁠Sources/ASMCore/swift-java.config⁠ — merged config driving BOTH directions:{

  "javaPackage": "aaravgupta.asm.swift",

  "mode": "ffm",

  "classes": {

    "net.minecraft.network.chat.Component": "MinecraftComponent",

    "net.minecraft.network.chat.MutableComponent": "MinecraftMutableComponent"

  }

}

Wrapping Minecraft classes — the classpath-file side-door

‎⁠swift-java⁠‘s ‎⁠"dependencies"⁠ (Maven) resolution cannot reach Minecraft (its deobf jar lives in
Loom’s local ‎⁠minecraftMaven⁠ repo, not Maven Central — an unimplemented feature, see swift-java #551).
Workaround: hand ‎⁠wrap-java⁠ the jar paths directly via a classpath file.

‎⁠Sources/ASMCore/ASMCore.swift-java.classpath⁠ — colon-separated, single line, no trailing newline:/Users/.../minecraft-common-deobf-26.2.jar:/Users/.../com.mojang/brigadier/.../brigadier-1.3.10.jar

(Brigadier is required because ‎⁠Component⁠ implements ‎⁠com.mojang.brigadier.Message⁠. Add more jars
as you wrap classes that pull in more deps.)

Gotchas we hit (so you don’t)

- ‎⁠Operation not permitted⁠ during ‎⁠swift build⁠ → SwiftPM’s plugin sandbox. Fix: ‎⁠swift build --disable-sandbox⁠.

- Classpath file ‎⁠%0A⁠ errors → newline-separated doesn’t work; use colon-separated, one line.

- ‎⁠NoClassDefFoundError⁠ while wrapping → a transitive dep is missing from the classpath file; add its jar.

- ‎⁠modImplementation⁠ vs ‎⁠implementation⁠ → Fabric mods need ‎⁠modImplementation⁠; plain JVM libs (SwiftKit) use ‎⁠implementation⁠.

- Deobfuscated names → 26.2 uses Mojang names: ‎⁠Component⁠ (not Yarn’s ‎⁠Text⁠), ‎⁠ClientCommands⁠, etc.

Build & runcd swift && swift build --disable-sandbox    # builds dylib + generates Java bindings

cd .. && gradle runClient                    # launches MC 26.2 with ASM

Success: log shows ‎⁠[ASM] Swift built a Minecraft Component, got back: hi from swift!⁠
and ‎⁠/asm⁠ runs in-game.

Roadmap

- Swift pushes messages to the in-game chat (wrap client/player from ‎⁠minecraft-clientonly-deobf⁠).

- ‎⁠/asm⁠ opens an in-game GUI with buttons.

- Wrap more Minecraft classes for real Skyblock features.

- The four-feature experiment: Java / Kotlin / Kotlin+Swift / Pure-Swift features coexisting in one jar.

Built with Swift, Kotlin, Java, and stubbornness.
