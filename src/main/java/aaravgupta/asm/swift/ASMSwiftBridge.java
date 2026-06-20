package aaravgupta.asm.swift;

public final class ASMSwiftBridge {

    static {
        // ASMCore dylib is loaded elsewhere (by the FFM path already running),
        // but ensure it's available for the native method.
        System.loadLibrary("ASMCore");
    }

    // Implemented in Swift via @JavaImplementation.
    // Returns the string that Swift built by calling Minecraft's Component.literal(...).getString()
    public static native String swiftBuildComponentString(String input);
}
