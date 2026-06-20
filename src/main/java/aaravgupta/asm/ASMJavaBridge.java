package aaravgupta.asm;

public final class ASMJavaBridge {
    private ASMJavaBridge() {}

    public static String greeting() {
        return "Hello from Java, called by Kotlin";
    }
}